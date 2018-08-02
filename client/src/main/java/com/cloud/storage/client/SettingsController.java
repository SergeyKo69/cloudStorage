package com.cloud.storage.client;

import com.cloud.storage.common.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;

public class SettingsController {
    @FXML
    TextField txtHost;
    @FXML
    TextField txtPort;
    @FXML
    Button bTestConnection;
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

    public void loadSettings() {
        Settings.ClientSettings settings = Settings.getClientSettings();
        txtPort.setText(settings.port);
        txtHost.setText(settings.host);
        txtPathFiles.setText(settings.pathFiles);
        txtPathLogs.setText(settings.pathLogs);
    }

    public void clickTestConnection(ActionEvent actionEvent) {
        if (testConnection()){
            showMessage(Alert.AlertType.INFORMATION,"Тест соединения","Соединение установлено");
        }else{
            showMessage(Alert.AlertType.ERROR,"Тест соединения","Ошибка соединения с сервером");
        }
    }

    public boolean testConnection(){
        try {
            Socket connect = new Socket(txtHost.getText(),Integer.parseInt(txtPort.getText()));
            if (connect == null){
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void showMessage(Alert.AlertType type, String title, String msg){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void clickChoosePathFiles(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку хранения файлов");
        File directory = directoryChooser.showDialog(bPathFiles.getScene().getWindow());
        if (directory != null){
            txtPathFiles.setText(directory.getPath());
        }
    }

    public void clickPathLogs(ActionEvent actionEvent) {
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
            fos = new FileOutputStream("client.properties");
            properties.setProperty("port",txtPort.getText());
            properties.setProperty("host",txtHost.getText());
            properties.setProperty("pathfiles",txtPathFiles.getText());
            properties.setProperty("pathlogs",txtPathLogs.getText());
            properties.store(fos,"");
        } catch (FileNotFoundException e) {
            Log.loggingEvent(Level.INFO,"SettingsController",e.getMessage());
        }catch(IOException e){
            Log.loggingEvent(Level.INFO,"SettingsController",e.getMessage());
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
