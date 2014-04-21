package byzantine;

import syssim.Participant;
import syssim.SimSystem;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by user on 4/21/14.
 */
public class ByzantineImpl
{
    public static class MessageListener implements Participant.EventListener
    {
        private int participantID;
        final HashMap<Integer, String> messagesHashMap = new HashMap<Integer, String>();


        @Override
        public void participantStarted( SimSystem simSystem )
        {
            //            System.out.println(  "ParticipantId " + participantID );
            Participant participantObj = simSystem.getParticipantMap().get( participantID );
            boolean traitor = participantObj.isTraitor();

            if( simSystem.getCommander() <= 0 )
            {
                simSystem.setCommander( participantID );

                if( traitor )
                {
                    simSystem.broadcastMessage( new String[]{String.valueOf( participantID ), Util.getRandomCommand()} );
                }
                else
                {
                    simSystem.broadcastMessage( new String[]{String.valueOf( participantID ), Util.COMMAND_ATTACK} );
                }

                try
                {
                    Thread.sleep( 1000 );
                }
                catch( InterruptedException e )
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Collection<Participant> participantCollection = simSystem.getParticipantMap().values();

                for( Participant participant : participantCollection )
                {
                    if( ( simSystem.getCommander() != participant.getID() ) && ( participantID != participant.getID() ) )
                    {
                        String commanderMessage = messagesHashMap.get( simSystem.getCommander() );

                        if( commanderMessage != null )
                        {
                            if( !traitor )
                            {
                                simSystem.sendMessage( participant.getID(), new String[]{String.valueOf( participantID ), commanderMessage} );
                            }
                            else
                            {
                                simSystem.sendMessage( participant.getID(), new String[]{String.valueOf( participantID ), Util.getOppositeCommand( commanderMessage )} );
                            }
                        }
                    }
                }
            }

        }

        @Override
        public void eventReceived( SimSystem simSystem, String[] event )
        {
            System.out.println( participantID + " received " + Arrays.toString( event ) );

            Integer messengerID = Integer.valueOf( event[0] );
            String message = event[1];

            messagesHashMap.put( messengerID, message );

            if( simSystem.getParticipantMap().size() - 1 == messagesHashMap.size() )
            {
                System.out.println( "Messages of " + participantID + messagesHashMap.toString() );
                System.out.println( "######## " + participantID + " selected " + Util.getMaxOccurrenceCmd( messagesHashMap.values() ) + " ########" );
            }
        }

        public void setParticipantID( int participantID )
        {
            this.participantID = participantID;
        }
    }

    /**
     *  For convenience, each node is identified by its participant port number
     *
     * @param args
     * @throws Exception
     */
    public static void main( String[] args ) throws Exception
    {
        final SimSystem system = new SimSystem();
        final int participantCount = 10;                  //4 , 10
        final int traitorCount = 2;                      // 1 , 2

        for( int i = 0; i < ( participantCount - traitorCount ); i++ )
        {
            MessageListener messageListener = new MessageListener();
            Participant participant = system.createParticipant( messageListener );
            messageListener.setParticipantID( participant.getID() );
        }

        for( int i = 0; i < traitorCount; i++ )
        {
            MessageListener messageListener = new MessageListener();
            Participant traitorParticipant = system.createTraitorParticipant( messageListener );
            messageListener.setParticipantID( traitorParticipant.getID() );
        }

        system.bootUp();
    }
}
