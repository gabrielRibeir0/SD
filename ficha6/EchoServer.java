import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Register{
    Lock lock = new ReentrantLock();

    int sum = 0;
    int count = 0;

    void add(int n){
        lock.lock();
        try{
            sum += n;
            count++;
        }
        finally{
            lock.unlock();
        }
    }

    int average(){
        lock.lock();
        try{
            if(count == 0){
                return 0;
            }

            return sum/count;
        }
        finally{
            lock.unlock();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private Register r;

    public ClientHandler(Socket socket, Register r) {
        this.socket = socket;
        this.r = r;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            String line;
            int sum = 0;
            while ((line = in.readLine()) != null) {
                int value = Integer.parseInt(line);
                sum += value;
                r.add(value);

                out.println(sum);
                out.flush();
            }

            out.println(r.average());
            out.flush();

            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class EchoServer {

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345);
            Register r = new Register();

            while (true) {
                Socket socket = ss.accept();

                new Thread(new ClientHandler(socket, r)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
