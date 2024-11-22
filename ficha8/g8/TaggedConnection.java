package g8;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable {
    public static class Frame {
        public final int tag;
        public final byte[] data;

        public Frame(int tag, byte[] data) {
            this.tag = tag;
            this.data = data;
        }
    }

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Lock writeLock = new ReentrantLock();
    private Lock readLock = new ReentrantLock();

    public TaggedConnection(Socket socket) throws IOException {
        this.socket = socket;
        in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
    }

    public void send(Frame frame) throws IOException {
        send(frame.tag, frame.data);
    }

    public void send(int tag, byte[] data) throws IOException {
        writeLock.lock();
        try {
            out.writeInt(4 + data.length);
            out.writeInt(tag);
            out.write(data);
            out.flush();
        }
        finally {
            writeLock.unlock();
        }
    }

    public Frame receive() throws IOException {
        readLock.lock();
        try{
            int length = in.readInt();
            int tag = in.readInt();
            byte[] data = new byte[length - 4];
            in.readFully(data);
            return new Frame(tag, data);
        }
        finally {
            readLock.unlock();
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}