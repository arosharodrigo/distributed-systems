package org.zk.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;

import static org.apache.zookeeper.CreateMode.EPHEMERAL_SEQUENTIAL;

public class ReadWriteLock {

    private final String dir;
    private final String name;
    private final ZooKeeper zookeeper;
    private final byte[] data = new byte[0];
    private String lockPath;
    private String previousLock;
    private LockListener listener;
    private List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;

    public ReadWriteLock(ZooKeeper zookeeper, String dir, LockListener listener, String name) {
        this.dir = dir;
        this.name = name;
        this.zookeeper = zookeeper;
        this.listener = listener;
    }

    public boolean writeLock() throws InterruptedException, KeeperException {
        createZNodeIfNotExists();
        String writeLockPrefix = getWriteLockPrefix();
        String lockPath = createLockPath(writeLockPrefix);
        this.lockPath = lockPath;
        previousLock = findPreviousLock(getLockPrefix());

        if(lockPath.equals(previousLock)) {
            listener.lockAcquired();
            System.out.println("[" + name + "] Lock acquired");
            return true;
        } else {
            System.out.println("[" + name + "] Watching previous path [" + previousLock + "]");
            Stat stat = zookeeper.exists(dir + "/" + previousLock, new WriteLockWatcherImpl());
            if (null != stat) {
                return false;
            } else {
                listener.lockAcquired();
                System.out.println("[" + name + "] Lock acquired");
                return true;
            }
        }

    }

    public boolean readLock() throws InterruptedException, KeeperException {
        createZNodeIfNotExists();
        String readLockPrefix = getReadLockPrefix();
        String lockPath = createLockPath(readLockPrefix);
        this.lockPath = lockPath;
        previousLock = findPreviousLock(getWriteLockPrefix());

        if(lockPath.equals(previousLock)) {
            listener.lockAcquired();
            System.out.println("[" + name + "] Lock acquired");
            return true;
        } else {
            System.out.println("[" + name + "] Watching previous path [" + previousLock + "]");
            Stat stat = zookeeper.exists(dir + "/" + previousLock, new ReadLockWatcherImpl());
            if (null != stat) {
                return false;
            } else {
                listener.lockAcquired();
                System.out.println("[" + name + "] Lock acquired");
                return true;
            }
        }
    }

    public void unLock() throws InterruptedException, KeeperException {
        System.out.println("[" + name + "] Deleting path [" + lockPath + "]");
        zookeeper.delete(lockPath, -1);
    }

    private class WriteLockWatcherImpl implements Watcher {

        public void process(WatchedEvent event) {
            System.out.println("[" + name + "] Event received [" + event + "]");
            try {
                writeLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReadLockWatcherImpl implements Watcher {

        public void process(WatchedEvent event) {
            try {
                readLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
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

    private String findPreviousLock(String pathPrefix) throws InterruptedException, KeeperException {
        int lockSequence = findSequenceNo(lockPath);
        List<String> pathList = zookeeper.getChildren(dir, false);

        int previousSeq = -1;
        String previousPath = "";
        for(String path : pathList) {
            if (path.startsWith(pathPrefix)) {
                int seq = findSequenceNo(path);
                if (seq < lockSequence && seq > previousSeq) {
                    previousSeq = seq;
                    previousPath = path;
                }
            }
        }

        if (-1 == previousSeq) {
            return lockPath;
        }

        return previousPath;
    }

    private int findSequenceNo(String lockPath) {
        return Integer.parseInt(lockPath.substring(lockPath.lastIndexOf('-') + 1));
    }

    private String createLockPath(String prefix) throws InterruptedException, KeeperException {
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

    private String getLockPrefix() {
        return "lock-";
    }

    private String getWriteLockPrefix() {
        return getLockPrefix() + "write-" + zookeeper.getSessionId() + "-";
    }

    private String getReadLockPrefix() {
        return getLockPrefix() + "read-" + zookeeper.getSessionId() + "-";
    }
}
