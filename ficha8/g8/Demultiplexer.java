package g8;

public class Demultiplexer implements AutoCloseable {
    public Demultiplexer(TaggedConnection conn) {}
    public void start() { }
    public void send(Frame frame) throws IOException { }
    public void send(int tag, byte[] data) throws IOException { }
    public byte[] receive(int tag) throws IOException, InterruptedException { }
    public void close() throws IOException { }
}
