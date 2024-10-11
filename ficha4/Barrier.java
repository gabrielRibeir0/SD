import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private final int N;
    private int count = 0;
    private int returnedThreads = 0;

    public Barrier(int N) {
        this.N = N;
    }

    //adiar o return
    void await() throws InterruptedException {
        lock.lock();

        try{
            //se uma thread adquirir o await muito rapido apos sair, espera até todas terem retornado
            //podia se usar outra condition, mas isto so acontece quando as outras threads estão para retornar não bloqueadas
            while(returnedThreads > 0){
                condition.await();
            }

            count++;
            if(count < N) {
                // é preciso um while para re-testar a condição
                // pois o await pode retornar espontaneamente mesmo sem ser chamado um signal()
                while (count < N) {
                    condition.await();
                }
            }
            //só a última chama o signalAll(), não havia problema em ser todas a chamar, mas os sinais perdem-se
            else {
                condition.signalAll();
            }

            /*
            Uma variante possivel, signal() em cascata, onde uma thread acorda com o signal que chama outro signal que acorda outra thread
            A última thread envia um signal() que é desperdiçado
                while (count < N) {
                    condition.await();
                }
               condition.signal()
            */

            returnedThreads++;
            if(returnedThreads == N) {
                count = 0;
                returnedThreads = 0;
                //acordar threads presas no primeiro while
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}
