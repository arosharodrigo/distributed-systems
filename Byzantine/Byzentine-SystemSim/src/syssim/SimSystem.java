package syssim;

import util.Role;

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
	
    private ExecutorService pool = Executors.newFixedThreadPool( 50 );

	public Participant createParticipant(Participant.EventListener listener, Role role, int commanderId) throws SysSimException {
		int participantIndex = participantIndexCounter.incrementAndGet();
		Participant participant = new Participant(listener, this, 4444+ participantIndex, role, commanderId);
		pool.submit(participant);
        try {
            // wait until port properly startup
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        participantMap.put(participant.getID(), participant);
		eventClientMap.put(participant.getID(), new EventClient("127.0.0.1", participant.getPort()));
		return participant;
	}


	public void bootUp(){
		Iterator<Participant> iterator = participantMap.values().iterator();
		while(iterator.hasNext()){
            Participant participant = iterator.next();
            participant.getListener().participantStarted(this, participant);
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

    public Participant getParticipant(int id) {
        return participantMap.get(id);
    }

    public int getParticipantCount() {
        return participantMap.size();
    }

    public Set<Integer> getAllParticipantIds() {
        return participantMap.keySet();
    }

}
