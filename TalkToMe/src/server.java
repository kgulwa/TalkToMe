import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345; // Specify your port number here
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started, listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client.java connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);

                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private int userId;

        private class ClientHandler implements Runnable {
            private Socket clientSocket;
            private PrintWriter out;
            private BufferedReader in;
            private int userId;

            public ClientHandler(Socket socket) {
                this.clientSocket = socket;
            }

            @Override
            public void run() {
                try {
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    // Step 1: Receive login credentials from client.java
                    String loginMessage = in.readLine();  // Expected format: "LOGIN username password"
                    String[] loginData = loginMessage.split(" ");
                    if (loginData.length == 3 && "LOGIN".equals(loginData[0])) {
                        String username = loginData[1];
                        String password = loginData[2];

                        // Step 2: Authenticate user
                        userId = authenticateUser(username, password); // Assuming this method returns user ID or 0 if failed

                        if (userId != 0) {
                            out.println("AUTH_SUCCESS");
                            System.out.println("User " + username + " authenticated successfully.");

                            // Now allow further messaging
                            String message;
                            while ((message = in.readLine()) != null) {
                                System.out.println("Received from user " + userId + ": " + message);
                                broadcastMessage(userId, message); // Send to other clients
                            }
                        } else {
                            out.println("AUTH_FAIL");
                            System.out.println("Authentication failed for user " + username);
                            closeConnection();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }

            private void closeConnection() {
                try {
                    if (clientSocket != null && !clientSocket.isClosed()) {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        private int authenticateUser( String username, String password) {
            int userID = -1;
            String url = "jdbc:mysql://localhost:3306/yourDatabase";
            String dbUser =  "yourDbUser";
            String dbPassword = "yourDbPassword";

            try(Connection connection DriverManager.getConnection(url, dbUser, dbPassword)){
                String query = "SELECT id FROM users WHERE username = ? AND password = ?";
                PreparedStatement statement = connection.prepareStatement(query);
            }
                PreparedStatement statement = connection.prepareStatement(query);
                statement. setString(1, username);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    userId = resultSet.getInt("id");
                }
            catch (Excpetion e){
                    e.printStackTrace();
            }
                return userId;

            // Perform database authentication or registration here, returning the user ID
            // Example: You might prompt for username and password, validate, and return the ID
            return 0; // Placeholder user ID; replace with actual user ID from the database
        }

        private void broadcastMessage(int senderId, String message) {
            for (ClientHandler client : clients) {
                if (client.userId != senderId) {
                    client.out.println("User " + senderId + ": " + message);
                }
            }
        }
    }
}
