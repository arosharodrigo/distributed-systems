package org.zk.election;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;

import static org.apache.zookeeper.CreateMode.EPHEMERAL_SEQUENTIAL;

public class LeaderElector {
    private final String dir;
    private final String name;
    private final ZooKeeper zookeeper;
    private final byte[] data = new byte[0];
    private String path;
    private String leader;
    private List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
    private ElectionEventListener eventListener;

    public LeaderElector(ZooKeeper zookeeper, String dir, ElectionEventListener listener, String name) {
        this.dir = dir;
        this.name = name;
        this.zookeeper = zookeeper;
        this.eventListener = listener;
    }


    public void start() throws InterruptedException, KeeperException {
        createZNodeIfNotExists();
        String prefix = getZNodePrefix();
        this.path = createOffer(prefix);
        leader = findLeader();

        System.out.println(name + " - path[" + path + "] leader[" + leader + "]");
        if(path.equals(leader)) {
            eventListener.onElected();
            System.out.println("[" + name + "] Elected as leader");
        } else {
            System.out.println("[" + name + "] Watching leader [" + leader + "]");
            Stat stat = zookeeper.exists(dir + "/" + leader, new WatcherImpl());
            if (null == stat) {
                eventListener.onElected();
                System.out.println("[" + name + "] Elected as leader");
            }
        }

    }

    private class WatcherImpl implements Watcher {

        public void process(WatchedEvent event) {
            eventListener.onLeaderChanged(event.getPath());
        }
    }

    private void createZNodeIfNotExists() {
        try {
            Stat stat = zookeeper.exists(dir, false);
            if (stat == null) {
                zookeeper.create(dir, data, acl, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getZNodePrefix() {
        return "n-" + zookeeper.getSessionId() + "-";
    }

    private String createOffer(String prefix) throws InterruptedException, KeeperException {
        String path = null;
        List<String> names = zookeeper.getChildren(dir, false);
        for (String name : names) {
            if (name.startsWith(prefix)) {
                path = dir + "/" + name;
                break;
            }
        }

        if (null == path) {
            path = zookeeper.create(dir + "/" + prefix, data,acl, EPHEMERAL_SEQUENTIAL);
            System.out.println("[" + name + "] Path created [" + path + "]");
        }
        return path;
    }

    private String findLeader() throws InterruptedException, KeeperException {
        int lockSequence = findSequenceNo(path);
        List<String> pathList = zookeeper.getChildren(dir, false);

        int previousSeq = -1;
        String previousPath = "";
        for(String p : pathList) {
            int seq = findSequenceNo(p);
            if (seq < lockSequence && seq > previousSeq) {
                previousSeq = seq;
                previousPath = p;
            }
        }

        if (-1 == previousSeq) {
            return path;
        }

        return previousPath;
    }

    private int findSequenceNo(String lockPath) {
        return Integer.parseInt(lockPath.substring(lockPath.lastIndexOf('-') + 1));
    }
}
