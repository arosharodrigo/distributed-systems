package algo;

import repo.MapResource;
import syssim.Participant;
import syssim.ResourceParticipant;
import syssim.SimSystem;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 4/10/14
 * Time: 5:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class DecentralizedDistributedLocks {

    public static class DistributedLocksListener implements Participant.EventListener {
        final List<Integer> tokens = new ArrayList<Integer>();
        private int participantCount;
        private int participantIndex;

        public DistributedLocksListener(int participantCount, int participantIndex){
            this.participantCount = participantCount;
            this.participantIndex = participantIndex;
        }

        @Override
        public void participantStarted(SimSystem simSystem) {
//            simSystem.broadcastMessage(new String[]{String.valueOf(participantIndex)});
        }
        @Override
        public void eventReceived(SimSystem simSystem, String[] event) {
            System.out.println(participantIndex + " received "+ Arrays.toString(event));
            ResourceParticipant participant = simSystem.getParticipant(participantIndex);
            String prefix = event[0];
            String receiverIndex = event[1];
            String resourceName = event[2];

            if(prefix.equals("GIVE_PERMISSION")) {
                if(participant.grantPermission(resourceName, String.valueOf(receiverIndex))) {
                    simSystem.sendMessage(Integer.parseInt(receiverIndex), new String[]{"PERMISSION_GRANTED", String.valueOf(participantIndex), resourceName});
                } else {
                    simSystem.sendMessage(Integer.parseInt(receiverIndex), new String[]{"PERMISSION_DENIED", String.valueOf(participantIndex), resourceName});
                }
            } else if(prefix.equals("PERMISSION_GRANTED")) {
                participant.addToResultedPermisssions(resourceName, receiverIndex, "GRANTED");
            } else if(prefix.equals("PERMISSION_DENIED")) {
                participant.addToResultedPermisssions(resourceName, receiverIndex, "NOT-GRANTED");
            }

        }
    }


    public static void main(String[] args) throws Exception{
        final SimSystem system = new SimSystem();

        Map<String, String> r1map = new HashMap<String, String>();
        r1map.put("1", "ONE");
        MapResource r1 = new MapResource("r1", r1map);

        List<String> r1Locations = new ArrayList<String>();
        r1Locations.add("0");
        r1Locations.add("1");
        r1Locations.add("2");
        r1Locations.add("3");

        final int resourceParticipantCount = 4;
        for(int i =0;i< resourceParticipantCount; i++){
            final int participantIndex = i;
            ResourceParticipant participant = system.createParticipant(new DistributedLocksListener(resourceParticipantCount, participantIndex) );
            participant.addResource(r1);

            Map<String, List<String>> resourceLocations = participant.getResourceLocations();
            resourceLocations.put(r1.getName(), r1Locations);
        }
        system.bootUp();
    }

}
