import java.util.ArrayList;

public class Main {

    static class BankRunner implements Runnable {
        Bank bank = new Bank();

        public BankRunner(Bank bank) {
            this.bank = bank;
        }

        public void run(){
            final int I = 1000;
            final int V = 100;

            for(int i = 0; i < I; i++) {
                this.bank.deposit(V);
            }
        }
    }

    private static void ex1() throws InterruptedException{
        int N = 10;
        var threads = new ArrayList<Thread>();

        for(int i = 0; i < N; i++){
            var t = new Thread(new Increment());
            threads.add(t);
        }

        threads.forEach(Thread::start);

        threads.forEach(t -> {
            try {
                t.join(); //wait em SO
            } catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        });

        System.out.println("fim");
    }

    //Não garante a atomicidade das operações e o saldo final não é necessáriamente 1000000 (varia dependendo da execução)
    //Isto é corrigido com os locks na própria class Bank
    private static void ex2() throws InterruptedException{
        int N = 10;
        var bank = new Bank();
        var threads = new ArrayList<Thread>();

        for(int i = 0; i < N; i++){
            threads.add(new Thread(new BankRunner(bank)));
        }

        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try{
                t.join();
            } catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        });

        System.out.println("O saldo é " + bank.balance());
    }

    public static void main(String[] args) {
        try {
            //ex1();
            ex2();
        }
        catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }
}
