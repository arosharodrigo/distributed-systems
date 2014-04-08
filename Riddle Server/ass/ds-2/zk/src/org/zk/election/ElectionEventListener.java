package org.zk.election;

public interface ElectionEventListener {

    /**
     * Invoked when leader is changed
     * @param newLeaderPath
     */
    void onLeaderChanged(String newLeaderPath);

    /**
     * Invoked when consumer is appointed as new leader
     */
    void onElected();

}
