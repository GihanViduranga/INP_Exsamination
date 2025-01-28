package lk.ijse;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Login {


    @FXML
    private Button btnJoin;

    @FXML
    private TextField txtUserName;

    public  static  String name;

    @FXML
    void btnJoinOnAction(ActionEvent event) throws IOException {
        name=txtUserName.getText();
        txtUserName.clear();

        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/View/Client1ChatForm.fxml"))));
        stage.setTitle("Chat Room");
        stage.show();
    }

    @FXML
    void txtUserNameOnAction(ActionEvent event) {

    }

}
