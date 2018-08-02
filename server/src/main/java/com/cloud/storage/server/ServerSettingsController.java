package com.cloud.storage.server;

import com.cloud.storage.common.Answers;
import com.cloud.storage.common.Message;
import com.cloud.storage.common.Settings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;


public class ServerSettingsController {
    @FXML
    TextField txtPort;
    @FXML
    TextField txtSource;
    @FXML
    TextField txtLogin;
    @FXML
    PasswordField txtPassword;
    @FXML
    Button bTestConnect;
    @FXML
    TextField txtPathFiles;
    @FXML
    Button bPathFiles;
    @FXML
    TextField txtPathLogs;
    @FXML
    Button bPathLogs;
    @FXML
    Button bOk;
    @FXML
    Button bCancel;

    @FXML
    private void initialize(){
       loadSettings();
    }

    public void loadSettings(){
        Settings.ServerSettings settings = Settings.getServerSettings();
        txtPort.setText(settings.port);
        txtLogin.setText(settings.login);
        txtPassword.setText(settings.password);
        txtSource.setText(settings.source);
        txtPathFiles.setText(settings.pathFiles);
        txtPathLogs.setText(settings.pathLogs);
    }

    public void clickTestConnect(ActionEvent actionEvent) {
        if (testConnection()){
            showMessage(Alert.AlertType.INFORMATION,"Соединение с базой данных",
                    "Соединение с базой данных установлено");
        }else{
            showMessage(Alert.AlertType.ERROR,"Соединение с базой данных",
                    "Ошибка установки соединения с базой данных");
        }
    }

    public void showMessage(Alert.AlertType type,String title,String msg){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public boolean testConnection(){
        Session session = null;
        try {
            Configuration configuration = new Configuration()
                    .setProperty("hibernate.connection.url",txtSource.getText())
                    .setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver")
                    .setProperty("hibernate.connection.username",txtLogin.getText())
                    .setProperty("hibernate.connection.password",txtPassword.getText())
                    .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect")
                    .setProperty("hbm2ddl.auto", "update")
                    .addResource("UsersEntity.hbm.xml")
                    .addResource("FilesEntity.hbm.xml")
                    .configure();
            SessionFactory ourSessionFactory = configuration.buildSessionFactory();
            session = ourSessionFactory.openSession();
            session.close();
            ourSessionFactory.close();
         } catch (Throwable ex) {
            return false;
        }finally {
            if (session != null) {
                session.close();

            }
        }
        return true;
    }

    public void clickChoosePathFiles(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку хранения файлов");
        File directory = directoryChooser.showDialog(bPathFiles.getScene().getWindow());
        if (directory != null){
            txtPathFiles.setText(directory.getPath());
        }
    }

    public void clickChoosePathLogs(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку хранения логов");
        File directory = directoryChooser.showDialog(bPathLogs.getScene().getWindow());
        if (directory != null){
            txtPathLogs.setText(directory.getPath());
        }
    }

    public void clickOk(ActionEvent actionEvent) {
        saveProperties();
        exit();
    }

    public void saveProperties(){
        FileOutputStream fos = null;
        Properties properties = new Properties();
        try {
            fos = new FileOutputStream("server.properties");
            properties.setProperty("port",txtPort.getText());
            properties.setProperty("source",txtSource.getText());
            properties.setProperty("login",txtLogin.getText());
            properties.setProperty("password",txtPassword.getText());
            properties.setProperty("pathfiles",txtPathFiles.getText());
            properties.setProperty("pathlogs",txtPathLogs.getText());
            properties.store(fos,"");
        } catch (FileNotFoundException e) {
            Log.loggingEvent(Level.INFO,"ServerSettingsController",e.getMessage());
        }catch(IOException e){
            Log.loggingEvent(Level.INFO,"ServerSettingsController",e.getMessage());
        }
    }

    public void clickCancel(ActionEvent actionEvent) {
        exit();
    }

    public void exit(){
        Stage stage = (Stage) bCancel.getScene().getWindow();
        stage.close();
    }

    public void keyTyped(KeyEvent keyEvent) {
        txtPort.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    txtPort.setText(oldValue);
                }
            }
        });
    }
}
