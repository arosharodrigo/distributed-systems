package algo;

import syssim.Participant;
import syssim.SimSystem;
import util.Command;
import util.Role;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Byzantine {

    public static class ByzantineListener implements Participant.EventListener{

        private int participantCount;
        private int participantIndex;

        public ByzantineListener(int participantCount, int participantIndex){
            this.participantCount = participantCount;
            this.participantIndex = participantIndex;
        }

        @Override
        public void participantStarted(SimSystem simSystem, Participant participant) {

            // If participant is a commander it should send to all other participants the selected command.
            // Here selected command is "ATTACK"
            Command selectedCmd = Command.ATTACK;

            if (participant.getRole() == Role.COMMANDER) {
                for(int id : simSystem.getAllParticipantIds()) {
                    if (id != participant.getID()) {
                        simSystem.sendMessage(id, new String[]{String.valueOf(participant.getID()), selectedCmd.name()});
                    }
                }
            }

        }
        @Override
        public void eventReceived(SimSystem simSystem, String[] event, int selfId) {
            System.out.println(selfId + " received " + Arrays.toString(event));

            Participant participant = simSystem.getParticipant(selfId);
            Integer senderId = Integer.valueOf(event[0]);
            String msg = event[1];

            participant.getMessages().put(senderId, msg);

            if (senderId == participant.getCommanderId()) {
                if (participant.getRole() == Role.FAULTY) {
                    msg = getRandomCommand(Command.valueOf(msg)).name();
                }
                sendToOtherParticipants(simSystem, selfId, participant, msg);
            }

            if (simSystem.getParticipantCount() - 1 == participant.getMessages().size()) {
                System.out.println("Received messages by " + selfId + ": " + participant.getMessages().toString());
                System.out.println(selfId + " is selected to execute command: " + getMaxReceivedCommand(participant.getMessages().values()));
                participant.getMessages().clear();
            }

        }

        private void sendToOtherParticipants(SimSystem simSystem, int selfId, Participant participant, String msg) {
            for(int id : simSystem.getAllParticipantIds()) {
                if (id != participant.getCommanderId() && id != selfId) {
                    simSystem.sendMessage(id, new String[]{String.valueOf(participant.getID()), msg});
                }
            }
        }

        private Command getRandomCommand(Command current) {
            // Due to limited commands randomization is done by getting opposite command for simulation purpose.
            if (current == Command.ATTACK) {
                return Command.RETREAT;
            } else {
                return Command.ATTACK;
            }
        }

        public static String getMaxReceivedCommand(Collection<String> values) {

            int attackCount = Collections.frequency(values, Command.ATTACK.name());
            int retreatCount = Collections.frequency(values, Command.RETREAT.name());

            // Equality also lead to 'RETREAT' command for safety purpose.
            if (attackCount > retreatCount) {
                return Command.ATTACK.name();
            }
            else {
                return Command.RETREAT.name();
            }
        }

    }


    public static void main(String[] args) throws Exception {
        final SimSystem system = new SimSystem();

        final int participantCount = 4;     // 4, 10
        final int faultyCount = 1;          // 1, 2

        Participant commander = null;

        for(int i = 0; i < 1; i++) {
            final int participantIndex = i;
            commander = system.createParticipant(new ByzantineListener(participantCount, participantIndex), Role.COMMANDER, 0);
        }

        if (commander != null) {
            for(int i = 0; i < faultyCount; i++) {
                final int participantIndex = i;
                Participant faultyParticipant = system.createParticipant(new ByzantineListener(participantCount, participantIndex), Role.FAULTY, commander.getID());
            }

            for(int i = 0; i < (participantCount - faultyCount - 1); i++) {
                final int participantIndex = i;
                Participant participant = system.createParticipant(new ByzantineListener(participantCount, participantIndex), Role.NORMAL, commander.getID());
            }
        }

        system.bootUp();
    }

}
