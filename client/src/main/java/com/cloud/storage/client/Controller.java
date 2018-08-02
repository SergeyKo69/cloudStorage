package com.cloud.storage.client;

import com.cloud.storage.common.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;

public class Controller {
    private ObservableList<DataFile> tableData = FXCollections.observableArrayList();

    @FXML
    TableView tableFiles;
    @FXML
    TableColumn<DataFile, Date> date;
    @FXML
    TableColumn<DataFile, String> name;
    @FXML
    TableColumn<DataFile, Integer> size;
    @FXML
    TableColumn<DataFile, Integer> id;
    @FXML
    TableColumn<DataFile, String> guid;
    @FXML
    TableColumn<DataFile, String> ext;
    @FXML
    HBox boxOperations;
    @FXML
    Label lblStatus;
    @FXML
    HBox boxRename;
    @FXML
    TextField txtRename;
    @FXML
    Button bRename;
    @FXML
    Button bDelete;
    @FXML
    Button bDownload;
    @FXML
    Label lblUserName;
    @FXML
    Button bExit;

    @FXML
    private void initialize(){
        date.setCellValueFactory(new PropertyValueFactory<DataFile, Date>("date"));
        name.setCellValueFactory(new PropertyValueFactory<DataFile, String>("name"));
        size.setCellValueFactory(new PropertyValueFactory<DataFile, Integer>("size"));
        id.setCellValueFactory(new PropertyValueFactory<DataFile, Integer>("id"));
        guid.setCellValueFactory(new PropertyValueFactory<DataFile, String>("guid"));
        ext.setCellValueFactory(new PropertyValueFactory<DataFile, String>("ext"));
        boxOperations.setVisible(true);
        lblUserName.setText(UserUtils.userName);
        refreshFileTable();
   }

    public void refreshFileTable(){
        tableFiles.getItems().clear();
        Message answ = FilesUtil.getFileList();
        if (answ.command == Answers.OK){
            if (answ.data.length > 0){
                for (int i = 0; i < answ.data.length; i++) {
                    tableData.add((DataFile) answ.data[i]);
                }
                tableFiles.setItems(tableData);
            }
        }else{
            showMessage(Alert.AlertType.ERROR,"Ошибка получения списка файлов",(String)answ.getData()[0]);
        }
    }

    public void btnClickUpload(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор файла");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(Main.primaryStage);
        if (file != null) {
            Message answ = FilesUtil.uploadFile(file.getPath(),0);
            if (answ.command == Answers.ERROR){
                showMessage(Alert.AlertType.ERROR,"Ошибка передачи файла",(String)answ.getData()[0]);
            }
        }
        refreshFileTable();
    }

    public void btnClickRename(ActionEvent actionEvent) {
        boxRename.setVisible(true);
    }

    public void btnClickDelete(ActionEvent actionEvent) {
        DataFile dataFile = (DataFile) tableFiles.getSelectionModel().getSelectedItem();
        Message answ = FilesUtil.deleteFile(dataFile.getGuid());
        if (answ.command == Answers.ERROR){
            showMessage(Alert.AlertType.ERROR,"Ошибка удаления файла",(String)answ.getData()[0]);
        }
        refreshFileTable();
    }

    public void btnClickDownload(ActionEvent actionEvent) {
        Settings.ClientSettings settings = Settings.getClientSettings();
        DataFile dataFile = (DataFile) tableFiles.getSelectionModel().getSelectedItem();
        if (settings.pathFiles.equals("") || !new File(settings.pathFiles).exists()) {
            //Если не задана папка в настройках или она не существует, то открываем
            // окно выбора папки сохранения файла.
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Сохранить файл");
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter(dataFile.getExt() + " files (*." + dataFile.getExt() + ")", "*." + dataFile.getExt());
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(Main.primaryStage);
            if (file != null) {
                FilesUtil.downloadFile(dataFile.getGuid(), file.getPath());
            }
        }else{
            //Иначе сохраняем в указанное место.
            File file = new File(settings.pathFiles + "/" + dataFile.getName());
            if (file.exists()){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Окно подтверждения");
                alert.setHeaderText("Файл существует");
                alert.setContentText("Такой файл уже существует! Зменить ?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    file.delete();
                    FilesUtil.downloadFile(dataFile.getGuid(), settings.pathFiles + "/" + dataFile.getName());
                    showMessage(Alert.AlertType.INFORMATION,"Загрузка файла","Файл успешно сохранен на диск");
                }
            }else{
                FilesUtil.downloadFile(dataFile.getGuid(), settings.pathFiles + "/" + dataFile.getName());
                showMessage(Alert.AlertType.INFORMATION,"Загрузка файла","Файл успешно сохранен на диск");
            }
        }
    }

    public void showMessage(Alert.AlertType type,String title,String msg){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void btnClickOKRename(ActionEvent actionEvent) {
        boxRename.setVisible(false);
        DataFile dataFile = (DataFile) tableFiles.getSelectionModel().getSelectedItem();
        Message answ = FilesUtil.renameFile(txtRename.getText(),dataFile);
        if (answ.command == Answers.ERROR){
            showMessage(Alert.AlertType.ERROR,"Ошибка переименования",(String)answ.getData()[0]);
        }
        refreshFileTable();
        boxRename.setVisible(false);
    }

    public void btnClickCancelRename(ActionEvent actionEvent) {
        boxRename.setVisible(false);
    }

    public void setStatus(String status){
        lblStatus.setText("Status: " + status);
    }

    public void ClickTableFiles(MouseEvent mouseEvent) {
        bRename.setDisable(false);
        bDelete.setDisable(false);
        bDownload.setDisable(false);
    }

    public void btnClickLogOut(MouseEvent mouseEvent) {
        UserUtils.guid = "";
        UserUtils.userName = "";
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        }catch (IOException e){
            Log.loggingEvent(Level.INFO,"Controller",e.getMessage());
        }
        Stage s = (Stage)bExit.getScene().getWindow();
        s.close();
    }
}
