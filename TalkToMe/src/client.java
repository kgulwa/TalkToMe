import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 5000;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client() {
        try {
            socket = new Socket(SERVER_ADDRESS, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // thread to listen for incoming messages from the server
            new Thread(new IncomingMessageHandler()).start();

            // Read messages from the console and send them to the server
            Scanner scanner = new Scanner(System.in);
            System.out.println("Connected to the server. You can start messaging.");
            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String message) {
        out.println(message);  // Send the message to the server
    }

    private class IncomingMessageHandler implements Runnable {
        @Override
        public void run() {
            String incomingMessage;
            try {
                while ((incomingMessage = in.readLine()) != null) {
                    System.out.println("Message from server: " + incomingMessage);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
