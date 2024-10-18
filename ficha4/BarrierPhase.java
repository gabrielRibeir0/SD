import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BarrierPhase {
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private final int N;
    private int count = 0;
    private int phase = 0;

    public BarrierPhase(int N) {
        this.N = N;
    }

    //adiar o return
    void await() throws InterruptedException {
        lock.lock();

        try{
            int phase = this.phase;
            this.count++;

            if(this.count < N) {
                while (this.phase == phase) {
                    condition.await();
                }
            }
            else {
                condition.signalAll();
                this.count = 0;
                this.phase++;
            }
        } finally {
            lock.unlock();
        }
    }
}
