import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ManagerImpl implements Manager{
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private Condition maxRaids = lock.newCondition();
    private int R = 10;
    private RaidImpl currentRaid = new RaidImpl();
    List<RaidImpl> pending = new ArrayList<>();
    private int running = 0;

    private class RaidImpl implements Raid{
        private Lock lock = new ReentrantLock();
        private Condition condition = lock.newCondition();
        private int maxMins = 0;
        private int left = 0;
        List<String> players = new ArrayList<>();

        public List<String> players() {
            return players;
        }

        public void waitStart() throws InterruptedException {

        }

        void start(){
            lock.lock();
            try{

            }
            finally{
                lock.unlock();
            }
        }

        public void leave() {
            lock.lock();
            try{
                left += 1;
                if(left == players.size()){
                    finished();
                }
            }
            finally {
                lock.unlock();
            }
        }
    }

    private void finished(){
        lock.lock();
        try{
            if(pending.isEmpty()){
                running--;
            }
            else{
                pending.remove(0).start();
            }
        }
        finally {
            lock.unlock();
        }
    }

    public Raid join(String name, int minPlayers) throws InterruptedException {
        lock.lock();

        try{
            RaidImpl myRaid = currentRaid;
            myRaid.players.add(name);
            myRaid.maxMins = Math.max(myRaid.maxMins, minPlayers);

            if(myRaid.players.size() < myRaid.maxMins){
                while(currentRaid == myRaid){
                    condition.await();
                }
            }
            else{
                myRaid.players = List.of(myRaid.players.toArray(new String[0])); // quando a raid começa torna a lista imutável
                currentRaid = new RaidImpl();
                condition.signalAll();
            }

            return myRaid;
        }
        finally {
            lock.unlock();
        }
    }
}
