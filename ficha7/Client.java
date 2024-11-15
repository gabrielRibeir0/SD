import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {

    public static Contact parseLine(String userInput) {
        String[] tokens = userInput.split(" ");

        if (tokens[3].equals("null")) tokens[3] = null;

        return new Contact(
                tokens[0],
                Integer.parseInt(tokens[1]),
                Long.parseLong(tokens[2]),
                tokens[3],
                new ArrayList<>(Arrays.asList(tokens).subList(4, tokens.length)));
    }


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        ContactList list2 = ContactList.deserialize(dis);
        System.out.println("Lista de contactos:");
        for (Contact contact : list2) {
            System.out.println(contact);
        }

        String userInput;
        while ((userInput = in.readLine()) != null) {
            Contact newContact = parseLine(userInput);

            newContact.serialize(out);
            out.flush();

            ContactList list = ContactList.deserialize(dis);
            System.out.println("Lista de contactos:");
            for (Contact contact : list) {
                System.out.println(contact);
            }
        }

        socket.close();
    }
}
