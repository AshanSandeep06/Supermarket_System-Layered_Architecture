package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.pos.bo.BOFactory;
import lk.ijse.pos.bo.custom.LoginBO;
import lk.ijse.pos.dto.LoginDTO;
import lk.ijse.pos.util.ValidationUtil;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class LoginFormController {
    public TextField txtUserName;
    public PasswordField txtPwd;
    public JFXButton btnLogin;
    public AnchorPane context;
    LinkedHashMap<TextField, Pattern> map = new LinkedHashMap<>();

    //Property Injection(DI)
    private final LoginBO loginBO = (LoginBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.LOGIN);

    public void initialize() {
        btnLogin.setDisable(true);
        storeValidations();
    }

    private void storeValidations() {
        Pattern userNamePattern = Pattern.compile("^[A-z0-9]{5,10}$");
        Pattern pwdPattern = Pattern.compile("^[A-z]{3,8}[0-9]{3,5}$");
        map.put(txtUserName, userNamePattern);
        map.put(txtPwd, pwdPattern);
    }

    private boolean checkIsEmpty() {
        return txtUserName.getText().isEmpty() || txtPwd.getText().isEmpty();
    }

    public void txtUserNameOnAction(ActionEvent event) {
        txtPwd.requestFocus();
    }

    public void txtPwdOnAction(ActionEvent event) {
        //btnLogin.fire();
    }

    public void loginOnAction(ActionEvent event) throws IOException {
        try {
            if (checkIsEmpty()) {
                Notifications notificationBuilder = Notifications.create()
                        .title("Login Failed..!")
                        .text("Please enter the username & password..!")
                        .graphic(new ImageView(new Image("lk/ijse/pos/assets/icons8-help-100.png")))
                        .hideAfter(Duration.seconds(4))
                        .position(Pos.CENTER);
                notificationBuilder.darkStyle();
                notificationBuilder.show();
            } else {

                LoginDTO loginDTO = loginBO.getLoginAccess(txtUserName.getText(), txtPwd.getText());

                if (loginDTO != null) {
                    if (loginDTO.getJobRole().equals("Admin")) {
                        // Redirect to Admin DashBoard
                        Stage stage = (Stage) context.getScene().getWindow();
                        stage.setTitle("Admin DashBoard");
                        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/AdminDashBoardForm.fxml"))));
                        stage.show();
                        loginSuccessful();
                    } else {
                        // Redirect to Cashier DashBoard
                        Stage window = (Stage) context.getScene().getWindow();
                        window.close();
                        Stage stage = new Stage();
                        stage.setResizable(false);
                        stage.setTitle("Cashier DashBoard");
                        stage.setFullScreen(true);
                        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/CashierDashBoardForm.fxml"))));
                        stage.show();
                        loginSuccessful();
                    }
                } else {
                    Notifications notificationBuilder = Notifications.create()
                            .title("Login Failed..!")
                            .text("username or password is incorrect..!")
                            .graphic(new ImageView(new Image("lk/ijse/pos/assets/Invalid Password.png")))
                            .hideAfter(Duration.seconds(4))
                            .position(Pos.CENTER);
                    //notificationBuilder.showConfirm();
                    notificationBuilder.darkStyle();
                    notificationBuilder.show();
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void keyReleasedOnAction(KeyEvent keyEvent) {
        Object validate = ValidationUtil.validate(map, btnLogin);

        if (KeyCode.ENTER == keyEvent.getCode()) {
            if (validate instanceof TextField) {
                TextField textField = (TextField) validate;
                textField.requestFocus();
            } else {
                btnLogin.fire();
            }
        }
    }

    private void loginSuccessful() {
        Notifications notificationBuilder = Notifications.create()
                .title("Login Successfully..!")
                .text("You have Successfully login to the System..!")
                .graphic(new ImageView(new Image("lk/ijse/pos/assets/icons8-ok-100.png")))
                .hideAfter(Duration.seconds(4))
                .position(Pos.BOTTOM_RIGHT);
        notificationBuilder.darkStyle();
        notificationBuilder.show();
    }
}
