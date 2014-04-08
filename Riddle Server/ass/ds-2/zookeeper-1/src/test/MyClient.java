package test;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.recipes.lock.LockListener;
import org.apache.zookeeper.recipes.lock.WriteLock;

import java.io.IOException;

/**
 */
public class MyClient {
    public static void main(String[] args) {
        new MyClient().process();

    }

    private void process() {
        try {
            ZooKeeper zk = new ZooKeeper("localhost:2181", 10000, new MyWatcher());
            WriteLock lock = new WriteLock(zk, "/loc", null, new MyLockListener());
            boolean locked = lock.lock();
            System.out.println("Locking...");
            System.out.println("Locked - " + locked);
            if (locked) {
                Thread.sleep(10000);
                System.out.println("Unlocking...");
                lock.unlock();
                System.out.println("Unlocked");
            } else {
                Thread.sleep(15000);
                System.out.println("Have lock :" + lock.isOwner());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    class MyLockListener implements LockListener {

        public void lockAcquired() {
            System.out.println("Lock acquired");
        }

        public void lockReleased() {
            System.out.println("Lock released");
        }
    }

    private class MyWatcher implements Watcher {
        public void process(WatchedEvent event) {
            // lets either become the leader or watch the new/updated node
            System.out.println("Watcher fired on path: " + event.getPath() + " state: " +
                    event.getState() + " type " + event.getType());
//            try {
//                lock();
//            } catch (Exception e) {
//                LOG.warn("Failed to acquire lock: " + e, e);
//            }
        }
    }

}
