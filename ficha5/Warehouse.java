import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Warehouse {
    private Map<String, Product> map =  new HashMap<String, Product>();
    private Lock lock = new ReentrantLock();

    private class Product {
        Condition outOfStock = lock.newCondition();
        int quantity = 0;
    }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) return p;
        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) {
        lock.lock();
        try {
            Product p = get(item);
            p.quantity += quantity;
            p.outOfStock.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    // Errado se faltar algum produto...
    public void consumeEgoista(Set<String> items) throws InterruptedException {
        //versão egoísta
        lock.lock();
        try {
            for (String s : items) {
                Product p = get(s);
                while (p.quantity <= 0) {
                    p.outOfStock.await();
                }
                p.quantity--;
            }
        }
        finally {
            lock.unlock();
        }
    }

    private Product missing(Product[] a) throws InterruptedException {
        for (Product p : a) {
            if (p.quantity <= 0) {
                return p;
            }
        }
        return null;
    }

    public void consumeCooperativa(Set<String> items) throws InterruptedException {
        //versão cooperativa
        lock.lock();
        try {
            Product[] a = new Product[items.size()];
            int i = 0;
            for(String item : items) {
                a[i++] = get(item);
            }

            while (true){
                Product m = missing(a);
                if (m == null) {
                    break;
                }

                m.outOfStock.await();
            }
            /* Alternativa sem função auxiliar
            for(int i = 0; i < a.length; i++){
                if(a[i].quantity <= 0) {
                    a[i].outOfStock.await();
                    i = -1; // para a primeira posição no reset ser lida (incrementa e vai para 0)
                }
            }*/

            for (Product p : a) {
                p.quantity--;
            }
        }
        finally {
            lock.unlock();
        }
    }

}
