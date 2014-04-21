package syssim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SimSystem {
    AtomicInteger participantIndexCounter = new AtomicInteger();
    private static ConcurrentHashMap<Integer, Participant> participantMap = new ConcurrentHashMap<Integer, Participant>();
    private static ConcurrentHashMap<Integer, EventClient> eventClientMap = new ConcurrentHashMap<Integer, EventClient>();

    private static int commander;

    private ExecutorService pool = Executors.newFixedThreadPool( 50 );



    public Participant createParticipant(Participant.EventListener listener) throws SysSimException{
        int participantIndex = participantIndexCounter.incrementAndGet();
		Participant participant = new Participant(listener, this, 4444+ participantIndex);
		pool.submit(participant);
		participantMap.put(participant.getID(), participant);
		eventClientMap.put(participant.getID(), new EventClient("127.0.0.1", participant.getPort()));
        return participant;
    }

    public Participant createTraitorParticipant(Participant.EventListener listener) throws SysSimException
    {
        int participantIndex = participantIndexCounter.incrementAndGet();
        Participant participant = new Participant( listener, this, 4444 + participantIndex );
        participant.setTraitor( true );
        pool.submit( participant );
        participantMap.put( participant.getID(), participant );
        eventClientMap.put( participant.getID(), new EventClient( "127.0.0.1", participant.getPort() ) );
        return participant;
    }

    public void bootUp(){
        Iterator<Participant> iterator = participantMap.values().iterator();
		while(iterator.hasNext()){
            iterator.next().getListener().participantStarted(this);
        }

    }


	public void sendMessage(int pid, String[] message){
		eventClientMap.get(pid).sendMessage(message);
    }

    public void broadcastMessage(String[] message){
        Iterator<EventClient> iterator = eventClientMap.values().iterator();
		while(iterator.hasNext()){
            EventClient participant = iterator.next();
            //System.out.println("Send " + message);
			participant.sendMessage(message);
        }
    }

    public static ConcurrentHashMap<Integer, Participant> getParticipantMap()
    {
        return participantMap;
    }

    public static void setParticipantMap( ConcurrentHashMap<Integer, Participant> participantMap )
    {
        SimSystem.participantMap = participantMap;
    }

    public int getCommander()
    {
        return commander;
    }

    public static void setCommander( int commander )
    {
        SimSystem.commander = commander;
    }
}
