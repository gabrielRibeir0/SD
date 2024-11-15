package g8;

import java.io.*;
import java.net.*;

public class FramedConnection implements AutoCloseable {
    private Socket socket;

    public FramedConnection(Socket socket) throws IOException {
        this.socket = socket;
    }

    public void send(byte[] data) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.write(data);
        out.flush();
    }

    public byte[] receive() throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        return in.readAllBytes();
    }

    public void close() throws IOException {
        socket.close();
    }
}