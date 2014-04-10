package algo;

import repo.MapResource;
import syssim.Participant;
import syssim.ResourceParticipant;
import syssim.SimSystem;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 4/10/14
 * Time: 5:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class DecentralizedDistributedLocks {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(50);

    public enum COMMANDS {
        GIVE_PERMISSION("GIVE_PERMISSION"),
        END_PERMISSION("END_PERMISSION"),
        PERMISSION_GRANTED("PERMISSION_GRANTED"),
        PERMISSION_DENIED("PERMISSION_DENIED");

        private String command;

        private COMMANDS(String command) {
            this.command = command;
        }
    }

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
//            System.out.println(participantIndex + " received "+ Arrays.toString(event));
            ResourceParticipant participant = simSystem.getParticipant(participantIndex+4444+1);
            String prefix = event[0];
            String senderId = event[1];
            String resourceName = event[2];

            if(prefix.equals(COMMANDS.GIVE_PERMISSION.command)) {
                System.out.println("Receiver:[" + participant.getID() + "] Permission request message received from " + senderId);
                if(participant.grantPermission(resourceName, String.valueOf(senderId))) {
                    simSystem.sendMessage(Integer.parseInt(senderId), new String[]{COMMANDS.PERMISSION_GRANTED.command, String.valueOf(participant.getID()), resourceName});
                    System.out.println("Permission grant message sent from " + participant.getID());
                } else {
                    simSystem.sendMessage(Integer.parseInt(senderId), new String[]{COMMANDS.PERMISSION_DENIED.command, String.valueOf(participant.getID()), resourceName});
                    System.out.println("Permission denied message sent from " + participant.getID());
                }
            } else if(prefix.equals(COMMANDS.END_PERMISSION.command)){
                participant.releasePermission(resourceName, senderId);
                System.out.println("Receiver:[" + participant.getID() + "] Permission release from " + senderId);
            } else if(prefix.equals(COMMANDS.PERMISSION_GRANTED.command)) {
                participant.addToResultedPermissions(resourceName, senderId, "GRANTED");
                System.out.println("Receiver:[" + participant.getID() + "] Permission granted message received from " + senderId);
            } else if(prefix.equals(COMMANDS.PERMISSION_DENIED.command)) {
                participant.addToResultedPermissions(resourceName, senderId, "NOT-GRANTED");
                System.out.println("Receiver:[" + participant.getID() + "] Permission denied message received from " + senderId);
            }

        }
    }

    public static void main(String[] args) throws Exception{
        final SimSystem system = new SimSystem();
        DecentralizedDistributedLocks locks = new DecentralizedDistributedLocks();

        Map<String, String> r1map = new HashMap<String, String>();
        r1map.put("1", "ONE");
        MapResource r1 = new MapResource("r1", r1map);

        List<String> r1Locations = new ArrayList<String>();
        r1Locations.add("4445");
        r1Locations.add("4446");
        r1Locations.add("4447");
        r1Locations.add("4448");

        final int resourceParticipantCount = 4;
        List<ResourceParticipant> resourceParticipants = new ArrayList<ResourceParticipant>();
        for(int i = 0; i< resourceParticipantCount; i++){
            final int participantIndex = i;
            ResourceParticipant participant = system.createParticipant(new DistributedLocksListener(resourceParticipantCount, participantIndex));
            participant.addResource(r1);

            Map<String, List<String>> resourceLocations = participant.getResourceLocations();
            resourceLocations.put(r1.getName(), r1Locations);
        }

        final int normalParticipantCount = 7;
        for(int i = 4; i< normalParticipantCount; i++){
            final int participantIndex = i;
            ResourceParticipant participant = system.createParticipant(new DistributedLocksListener(normalParticipantCount, participantIndex));
            Map<String, List<String>> resourceLocations = participant.getResourceLocations();
            resourceLocations.put(r1.getName(), r1Locations);
            locks.simulateResourceHungry(system, participant, "r1");
            resourceParticipants.add(participant);
        }
    }

    private void simulateResourceHungry(final SimSystem system, final ResourceParticipant participant, final String resourceName) {
        final long delay = Math.abs(new Random().nextLong() % 500);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                system.broadcastMessage(new String[]{COMMANDS.GIVE_PERMISSION.command, String.valueOf(participant.getID()), resourceName});
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {

                }
                if (participant.getResultedPermisssions().containsKey(resourceName)) {
                    List<String> list = participant.getResultedPermisssions().get(resourceName);
                    if(list != null) {
                        int totalCoordinatorCount = participant.getResourceLocations().get("r1").size();
                        int grantedCoordinatorCount = countGrantedCoordinators(list);
                        if(grantedCoordinatorCount > (totalCoordinatorCount / 2)) {
                            System.out.println("Acquired resource [" + resourceName + "] by " + participant.getID() + " and using it for 1 second");
                            try {
                                Thread.sleep(1000);
                                System.out.println("Releasing the resource [" + resourceName + "] after using by " + participant.getID());
                                list.clear();
                                system.broadcastMessage(new String[]{COMMANDS.END_PERMISSION.command, String.valueOf(participant.getID()), resourceName});
                            } catch (InterruptedException e) {

                            }
                        } else {
                            system.broadcastMessage(new String[]{COMMANDS.END_PERMISSION.command, String.valueOf(participant.getID()), resourceName});
                            list.clear();
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                    }
                }
            }
        }, 200, 100, TimeUnit.MILLISECONDS);
    }

    private int countGrantedCoordinators(List<String> list) {
        int count = 0;
        for(String s : list) {
            if(s.equals("GRANTED")) {
                count++;
            }
        }
        return count;
    }

}
