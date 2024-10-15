import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Agreement {
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private final int N;
    private int count = 0;
    private int returnedThreads = 0;
    private int maxValue = Integer.MIN_VALUE;

    public Agreement(int N) {
        this.N = N;
    }

    int propose(int choice) throws InterruptedException {
        lock.lock();

        try{
            while(returnedThreads > 0){
                condition.await();
            }

            maxValue = Math.max(maxValue, choice);
            count++;

            if(count < N) {
                while (count < N) {
                    condition.await();
                }
            }
            else {
                condition.signalAll();
            }

            returnedThreads++;
            if(returnedThreads == N) {
                count = 0;
                returnedThreads = 0;
                condition.signalAll();
            }

            return maxValue;
        } finally {
            lock.unlock();
        }
    }
}
