import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BancoMultiplasContas {

    private static class Account {
        private int balance;
        private Lock lock = new ReentrantLock();
        Account (int balance) { this.balance = balance; }
        int balance () {
            return balance;
        }

        boolean deposit (int value) {
            balance += value;
            return true;
        }

        boolean withdraw (int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    // Bank slots and vector of accounts
    private final int slots;
    private Account[] av;
    // só até ex 2
    // private Lock lock = new ReentrantLock();

    public BancoMultiplasContas (int n) {
        slots=n;
        av=new Account[slots];
        for (int i=0; i<slots; i++)
            av[i]=new Account(0);
    }


    // Account balance
    public int balance (int id) {
        av[id].lock.lock();
        try {
            if (id < 0 || id >= slots)
                return 0;
            return av[id].balance();
        }
        finally {
            av[id].lock.unlock();
        }
    }

    // Deposit
    public boolean deposit (int id, int value) {
        av[id].lock.lock();
        try {
            if (id < 0 || id >= slots)
                return false;
            return av[id].deposit(value);
        }
        finally {
            av[id].lock.unlock();
        }
    }

    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw (int id, int value) {
        av[id].lock.lock();
        try {
            if (id < 0 || id >= slots)
                return false;
            return av[id].withdraw(value);
        }
        finally {
            av[id].lock.unlock();
        }
    }

    // Transfer
    public boolean transfer (int from, int to, int value) {
        if (from < 0 || from >= this.slots || to < 0 || to >= this.slots)
            return false;

        //max e min para a ordem ser sempre igual e não existir deadlocks
        av[Math.min(from, to)].lock.lock();
        av[Math.max(from, to)].lock.lock();
        try {
            if (!withdraw(from, value))
                return false;

            deposit(to, value);
            return true;
        }
        finally {
            av[from].lock.unlock();
            av[to].lock.unlock();
        }
    }

    // TotalBalance
    public int totalBalance () {
        int total = 0;

        for(int i = 0; i < this.slots; i++){
            av[i].lock.lock();
        }

        try {
            for (int i = 0; i < this.slots; i++) {
                total += balance(i);
            }
            return total;
        }
        finally {
            for(int i = 0; i < this.slots; i++){
                av[i].lock.unlock();
            }
        }
    }
}

