package g8;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FramedConnection implements AutoCloseable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Lock writeLock = new ReentrantLock();
    private Lock readLock = new ReentrantLock();

    public FramedConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void send(byte[] data) throws IOException {
        writeLock.lock();
        try {
            this.out.writeInt(data.length);
            this.out.write(data);
            this.out.flush();
        }
        finally {
           writeLock.unlock();
        }
    }

    public byte[] receive() throws IOException {
        readLock.lock();
        try {
            int length = in.readInt();
            byte[] data = new byte[length];
            this.in.readFully(data);
            return data;
        }
        finally {
            readLock.unlock();
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}