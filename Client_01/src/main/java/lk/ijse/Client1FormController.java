package lk.ijse;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.EventObject;
import java.util.Optional;
import java.util.ResourceBundle;

public class Client1FormController implements Initializable {

    @FXML
    private Button btnSendClient;

    @FXML
    private ImageView emoIcon;

    @FXML
    private FlowPane emojiCategoryPane;

    @FXML
    private FlowPane emojiContainer;

    @FXML
    private ImageView icnCamera;

    @FXML
    private Text lblName;

    @FXML
    private VBox mainVbox;

    @FXML
    private AnchorPane paneChat;

    @FXML
    private TextField sendTxtAreaClient;

    @FXML
    private ScrollPane spaneForFlowPane;

    Socket socket;
    private Socket clientSocket;
    private DataInputStream din;
    private DataOutputStream dout;
    static String user_name;
    public static Image image;

    @FXML
    void btnSendOnAction(ActionEvent event) {
        String massage = sendTxtAreaClient.getText();

        if (!massage.isEmpty()){
            try {
                Platform.runLater(()->{
                    HBox hbox = new HBox();
                    hbox.setPadding(new Insets(5, 15, 5, 15));
                    hbox.setStyle("-fx-background-color: #ffff; -fx-text-fill: black;-fx-background-radius: 10");
                    hbox.setAlignment(Pos.BASELINE_RIGHT);
                    Label label = new Label(massage+"\n");
                    label.setMaxWidth(300);
                    label.setWrapText(true);
                    hbox.getChildren().add(label);
                    hbox.setMaxWidth(Region.USE_PREF_SIZE);
                    hbox.setMaxHeight(Region.USE_PREF_SIZE);
                    hbox.setMinWidth(Region.USE_PREF_SIZE);
                    hbox.setMinHeight(Region.USE_PREF_SIZE);
                    StackPane stackPane = new StackPane(hbox);
                    stackPane.setAlignment(Pos.BASELINE_RIGHT);
                    mainVbox.getChildren().add(stackPane);
                });
                dout.writeUTF(lblName.getText() + ":" + sendTxtAreaClient.getText());
                dout.flush();
                sendTxtAreaClient.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        spaneForFlowPane.setVisible(false);
    }

    @FXML
    void emoIconOnAction(MouseEvent event) {

    }

    @FXML
    void icnCameraOnMouseClicked(MouseEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) icnCamera.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String imagePath = selectedFile.getAbsolutePath();
            System.out.println("Selected image path: " + imagePath);
            dout.writeUTF("image");
            dout.writeUTF(lblName.getText() );
            dout.writeUTF(imagePath);
            dout.flush();

            ImageView imageView = new ImageView(new Image("file:" + imagePath));
            imageView.setFitWidth(192);
            imageView.setPreserveRatio(true);

            imageView.setOnMouseClicked(event1 -> {
                try {
                    File file = new File(imagePath);
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Platform.runLater(() -> {
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.BASELINE_RIGHT);
                hbox.getChildren().add(imageView);
                mainVbox.getChildren().add(hbox);
            });
        }

        try {
            EventObject actionEvent = null;
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image");
            File filePath = fileChooser.showOpenDialog(stage);
            dout.writeUTF(lblName.getText()+ "::" + "img" + filePath.getPath());
            dout.flush();
        }catch (NullPointerException e){
            System.out.println(e);
            System.out.println("Image not Selected!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void txtFieldClientOnAction(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblName.setText(Login.name);

        try {
            clientSocket = new Socket("localhost",3003);
            din = new DataInputStream(clientSocket.getInputStream());
            dout = new DataOutputStream(clientSocket.getOutputStream());

            new Thread(()-> {
                try {
                    while (true){
                        String massage = din.readUTF();

                        if (massage.startsWith("image")){
                            String sender = din.readUTF();
                            Label senderLabel = new Label(sender+ ": ");
                            String path = din.readUTF();

                            ImageView imageView = new ImageView(new Image("file:" + path));
                            imageView.setFitWidth(192);
                            imageView.setPreserveRatio(true);

                            imageView.setOnMouseClicked(even ->{

                                try {
                                    File file = new File(path);
                                    Desktop.getDesktop().open(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                            Platform.runLater(() ->{
                                mainVbox.getChildren().add(senderLabel);
                            });

                            Platform.runLater(()->{
                                mainVbox.getChildren().add(imageView);
                            });
                        }else {
                            if (massage.startsWith("System")){
                                Label label = new Label(massage);
                                Platform.runLater(()->{
                                    mainVbox.getChildren().add(label);
                                });
                            }else {
                                Platform.runLater(()->{
                                    HBox hBox = new HBox();
                                    hBox.setPadding(new Insets(5,15,5,15));
                                    hBox.setStyle("-fx-background-color: #039dfc; -fx-text-fill: #ffff;-fx-background-radius: 14");
                                    hBox.setAlignment(Pos.BASELINE_LEFT);
                                    Label label = new Label(massage);
                                    label.setTextFill(Color.WHITE);
                                    label.setMaxWidth(300);
                                    label.setWrapText(true);
                                    hBox.getChildren().add(label);
                                    hBox.setMaxWidth(Region.USE_PREF_SIZE);
                                    hBox.setMaxHeight(Region.USE_PREF_SIZE);
                                    hBox.setMinHeight(Region.USE_PREF_SIZE);
                                    hBox.setMinWidth(Region.USE_PREF_SIZE);
                                    StackPane stackPane = new StackPane(hBox);
                                    stackPane.setAlignment(Pos.BASELINE_LEFT);
                                    mainVbox.getChildren().add(stackPane);
                                });
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }).start();


        } catch (IOException e) {
            System.out.println(e);

        }
        Platform.runLater(()->{
//
            Stage stage = (Stage) mainVbox.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                event.consume();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(user_name);
                alert.setHeaderText("Are you sure you want to leave the chat?");
                alert.setContentText("Your data will be lost if you leave the chat application now");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK){
                    try {
                        dout.writeUTF("pass-qpactk3i5710-xkdwisq@ee358fyndvndla98r478t35-jvvhjfv94r82@");
                        dout.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    stage.close();
                }
            });

        });

    }

}
