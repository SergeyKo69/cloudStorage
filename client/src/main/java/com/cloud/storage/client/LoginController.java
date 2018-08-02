package com.cloud.storage.client;

import com.cloud.storage.common.Answers;
import com.cloud.storage.common.Commands;
import com.cloud.storage.common.MessageTxt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;

public class LoginController {
    @FXML
    VBox boxLogin;
    @FXML
    VBox boxRegistration;
    @FXML
    VBox boxInformation;
    @FXML
    TextField txtLogin;
    @FXML
    PasswordField txtPassword;
    @FXML
    TextField txtRLogin;
    @FXML
    TextField txtUserName;
    @FXML
    PasswordField txtRPassword;
    @FXML
    PasswordField txtRePassword;
    @FXML
    Button bExit;
    @FXML
    Label lblInformation;

    @FXML
    private void initialize(){
        boxInformation.setVisible(false);
    }

    public void clickSingIn(ActionEvent actionEvent) {
        singIn();
    }

    public void singIn(){
        if (login()){
            openMainForm();
            exit();
        }
    }

    private void openMainForm(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/sample.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
             stage.setScene(new Scene(root));
            stage.show();
        }catch (IOException e){
            Log.loggingEvent(Level.INFO,"LoginController",e.getMessage());
        }
    }

    public void clickRegistration(ActionEvent actionEvent) {
        boxRegistration.setVisible(true);
        boxLogin.setVisible(false);
    }

    private boolean registration(){
        boxLogin.setVisible(false);
        boxRegistration.setVisible(true);
        String uName = txtUserName.getText();
        String login = txtRLogin.getText();
        String password = txtRPassword.getText();
        String rePassword = txtRePassword.getText();
        if (uName.equals("") | login.equals("") | password.equals("")) {
            boxInformation.setVisible(true);
            lblInformation.setText("Введите, пожалуйста логин,пароль и имя пользователя!");
            return false;
        }
        if (!password.equals(rePassword)){
            boxInformation.setVisible(true);
            lblInformation.setText("Введеные пароли не совпадают!");
            return false;
        }
        password = UserUtils.encryptString(password);
        if(password.equals("")){
            boxInformation.setVisible(true);
            lblInformation.setText("Ошибка шифрования, повторите попытку!");
            return false;
        }
        MessageTxt msg = new MessageTxt(Commands.REGISTRATION, new String[]{uName, login, password});
        MessageTxt answ = UserUtils.login(msg);
        if (answ.command == Answers.ERROR) {
            boxInformation.setVisible(true);
            lblInformation.setText((String) answ.getData()[0]);
            return false;
        }
        return true;
    }

    public void clickExit(ActionEvent actionEvent) {
       exit();
    }

    public void exit(){
        Stage stage = (Stage) bExit.getScene().getWindow();
        stage.close();
    }

    public void clickApply(ActionEvent actionEvent) {
        if (registration()){
            boxRegistration.setVisible(false);
            boxLogin.setVisible(true);
            txtLogin.setText(txtRLogin.getText());
            txtPassword.setText(txtRPassword.getText());
        }
    }

    public void clickLast(ActionEvent actionEvent) {
        boxRegistration.setVisible(false);
        boxLogin.setVisible(true);
    }

    public boolean login() {
        boxInformation.setVisible(false);
        String login = txtLogin.getText();
        String password = txtPassword.getText();
        if (login.equals("") | password.equals("") ){
            boxInformation.setVisible(true);
            lblInformation.setText("Не указан логин/пароль! Введите, пожалуйста логин и пароль!");
            return false;
        }
        password = UserUtils.encryptString(password);
        if(password.equals("")){
            boxInformation.setVisible(true);
            lblInformation.setText("Ошибка шифрования, повторите попытку!");
            return false;
        }
        MessageTxt msg = new MessageTxt(Commands.LOGIN, new String[]{login, password});
        MessageTxt answ = UserUtils.login(msg);
        if (answ.command == Answers.ERROR){
            boxInformation.setVisible(true);
            lblInformation.setText((String)answ.getData()[0]);
            return false;
        }
        return true;
    }

    public void clickPasswordPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            singIn();
        }
    }

    public void clickSettings(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ClientSettings.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root,600,300));
            stage.show();
        }catch (IOException e){
            Log.loggingEvent(Level.INFO,"LoginController",e.getMessage());
        }
    }
}
