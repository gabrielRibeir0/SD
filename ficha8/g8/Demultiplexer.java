package g8;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.Map;

import g8.TaggedConnection.Frame;

public class Demultiplexer implements AutoCloseable {
    private class Dequeue{
        private Condition condition;
        private Queue<byte[]> queue;
        
        Dequeue(Condition condition){
            this.condition = condition;
            this.queue = new LinkedList<>();
        }
    }
    private TaggedConnection tagConn;
    private Map<Integer, Dequeue> storage = new HashMap<>();

    public Demultiplexer(TaggedConnection conn) {
        this.tagConn = conn;
    }

    public void start() {
        while (true) {
            try {
                tagConn.receive();

            }
            catch (IOException e) {
                System.out.println("Erro a ler da conexão");
            }
        }
    }

    public void send(Frame frame) throws IOException { }

    public void send(int tag, byte[] data) throws IOException { }

    public byte[] receive(int tag) throws IOException, InterruptedException {
        return null;
    }

    public void close() throws IOException {
        tagConn.close();
    }
}
