package syssim;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SimSystem {
	AtomicInteger participantIndexCounter = new AtomicInteger();
	private static ConcurrentHashMap<Integer, ResourceParticipant> participantMap = new ConcurrentHashMap<Integer, ResourceParticipant>();
	private static ConcurrentHashMap<Integer, EventClient> eventClientMap = new ConcurrentHashMap<Integer, EventClient>();
	
    private ExecutorService pool = Executors.newFixedThreadPool(50);


	
	public ResourceParticipant createParticipant(ResourceParticipant.EventListener listener) throws SysSimException{
		int participantIndex = participantIndexCounter.incrementAndGet();
        ResourceParticipant participant = new ResourceParticipant(listener, this, 4444+ participantIndex);
		pool.submit(participant);
		participantMap.put(participant.getID(), participant);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new SysSimException("Waiting until node server start is interrupted.", e);
        }
        eventClientMap.put(participant.getID(), new EventClient("127.0.0.1", participant.getPort()));
		return participant;
	}
	
	
	public void bootUp(){
		Iterator<ResourceParticipant> iterator = participantMap.values().iterator();
		while(iterator.hasNext()){
			iterator.next().getListener().participantStarted(this);
		}

	}
	

	public void sendMessage(int pid, String[] message){
		eventClientMap.get(pid).sendMessage(message);
	}
	
	public void broadcastMessage(String[] message) {
		Iterator<EventClient> iterator = eventClientMap.values().iterator();
		while(iterator.hasNext()){
			EventClient participant = iterator.next();
			//System.out.println("Send " + message);
			participant.sendMessage(message);
		}
	}

    public ResourceParticipant getParticipant(int participantId) {
        return participantMap.get(participantId);
    }

    public static ConcurrentHashMap<Integer, ResourceParticipant> getParticipantMap() {
        return participantMap;
    }

    public static void setParticipantMap(ConcurrentHashMap<Integer, ResourceParticipant> participantMap) {
        SimSystem.participantMap = participantMap;
    }

    public static ConcurrentHashMap<Integer, EventClient> getEventClientMap() {
        return eventClientMap;
    }

    public static void setEventClientMap(ConcurrentHashMap<Integer, EventClient> eventClientMap) {
        SimSystem.eventClientMap = eventClientMap;
    }
}
