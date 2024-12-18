import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Agreement {
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private final int N;
    private int count = 0;

    private static class Instance{
        int maxValue = Integer.MIN_VALUE;
    }
    private Instance current = new Instance();

    public Agreement(int N) {
        this.N = N;
    }

    int propose(int choice) throws InterruptedException {
        lock.lock();

        try{
            Instance my = this.current;
            my.maxValue = Math.max(my.maxValue, choice);
            this.count++;

            if(this.count < N) {
                while (this.current == my) {
                    condition.await();
                }
            }
            else {
                condition.signalAll();
                this.count = 0;
                this.current = new Instance();
            }

            return my.maxValue;
        } finally {
            lock.unlock();
        }
    }
}
