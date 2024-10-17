import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Warehouse {
    private Map<String, Product> map =  new HashMap<String, Product>();
    private Lock lock = new ReentrantLock();

    private class Product {
        Lock lock = new ReentrantLock();
        Condition outOfStock = lock.newCondition();
        int quantity = 0;
    }

    private Product get(String item) {
        this.lock.lock();
        try {
            Product p = map.get(item);
            if (p != null) return p;
            p = new Product();
            map.put(item, p);
            return p;
        }
        finally {
            this.lock.unlock();
        }
    }

    public void supply(String item, int quantity) {
        Product p = get(item);
        p.lock.lock();
        try {
            int oldQuantity = p.quantity;
            p.quantity += quantity;
            if (oldQuantity == 0){
                for(int i = 0; i < quantity; i++) {
                    p.outOfStock.signal();
                }
            }
        }
        finally {
            p.lock.unlock();
        }
    }

    // Errado se faltar algum produto...
    public void consume(Set<String> items) throws InterruptedException {
        for (String s : items) {
            Product p = get(s);
            p.lock.lock();
            try {
                if (p.quantity <= 0) {
                    while (p.quantity <= 0) {
                        p.outOfStock.await();
                    }
                } else {
                    p.quantity--;
                }
            }
            finally {
                p.lock.unlock();
            }
        }
    }

}
