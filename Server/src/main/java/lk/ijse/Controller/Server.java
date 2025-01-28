package lk.ijse.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Server implements Initializable {

    @FXML
    private Button btnSend;

    @FXML
    private TextArea txtAreaServer;

    @FXML
    private TextField txtServer;

    private ServerSocket serverSocket;
    private List<ClientHandler> clients;


    DataOutputStream dtout;
    DataInputStream dataInputStream;

    static   String reply="";
    static String message="";

    @FXML
    void btnSendOnAction(ActionEvent event) throws IOException {
        String message = txtServer.getText();

        if (!message.isEmpty()) {
            broadcastMessagebyAdmin("System", message);
            txtServer.clear();
        }
    }

    @FXML
    void txtServerOnAction(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clients = new ArrayList<>();

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(3003);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    System.out.println(clients);
                    clientHandler.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Platform.runLater(() -> {
            Stage stage = (Stage) txtAreaServer.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                event.consume(); // Consume the event to prevent the default close operation

                // Display a confirmation dialog
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Server Connection");
                alert.setHeaderText("Are you sure you want to close the server?");
                alert.setContentText("The connection of the chat application will be lost");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    stage.close(); // Close the window
                }
            });
        });
    }

    private void broadcastMessagebyAdmin(String sender, String message) {

        txtAreaServer.appendText(sender + ": " + message + "\n");
        for (ClientHandler client : clients) {
            client.sendMessage(sender, message);
        }

    }

    private void broadcastMessagebyClients(String sender, String message, Socket socket) {
        txtAreaServer.appendText(sender + ": " + message + "\n");
        for (ClientHandler client : clients) {
            if (client.clientSocket != socket) {
                client.sendMessage(sender, message);
            }
        }

    }

    private void broadcastImagesbyClients(String user_name, String path, Socket socket) {
        txtAreaServer.appendText(user_name + " sent an image\n");
        for (ClientHandler client : clients) {
            if (client.clientSocket != socket) {
                client.sendImage(user_name, path);
            }
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private DataInputStream dtin;
        private DataOutputStream dtout;

        public ClientHandler(Socket socket) {
            clientSocket = socket;
            try {
                this.dtin = new DataInputStream(clientSocket.getInputStream());
                dtout = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String name = dtin.readUTF();
                broadcastMessagebyClients("System", name + "  joined the chat.", clientSocket);

                while (true) {
                    String message = dtin.readUTF();
                    if (message.equals("pass-qpactk3i5710-xkdwisq@ee358fyndvndla98r478t35-jvvhjfv94r82@")) {
                        break;
                    } else if (message.startsWith("image")) {
                        String user_name = dtin.readUTF();
                        String path = dtin.readUTF();
                        broadcastImagesbyClients(user_name, path, clientSocket);
                    } else {
                        broadcastMessagebyClients(name, message, clientSocket);
                    }
                }

                clients.remove(this);
                System.out.println(clients);
                clientSocket.close();
                broadcastMessagebyClients("System", name + "  left the chat.", clientSocket);

                // TextFlow textFlow1 = new TextFlow();
                // textFlow1.setStyle("-fx-background-color:#939393;" + "-fx-background-radius: 20px;" + "-fx-font-size: 17px;");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendImage(String user_name, String path) {
            try {
                dtout.writeUTF("image");
                dtout.writeUTF(user_name);
                dtout.writeUTF(path);
                dtout.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String sender, String message) {
            try {
                if (sender.equals("System")) {
                    dtout.writeUTF(sender + ":" + message);
                    dtout.flush();

                } else {
                    dtout.writeUTF(message);
                    dtout.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
