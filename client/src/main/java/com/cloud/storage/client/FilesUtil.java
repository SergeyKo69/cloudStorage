package com.cloud.storage.client;

import com.cloud.storage.common.*;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.math.BigInteger;
import java.util.logging.Level;

public class FilesUtil {
    private static final int SIZE_PART = 1024000; // 1 мб.

    public static MessageTxt uploadFile(String path,int part){
        try {
            RandomAccessFile f = new RandomAccessFile(path, "r");
            int aParts;
            String guid = "";
            if ((int)f.length() <= SIZE_PART){
                int off = 0;
                aParts = 1;
                int length = (int)f.length();
                DataFile file = createDataFile(f,off,length,aParts,0,path,guid);
                MessageData msg = new MessageData(Commands.UPLOAD,UserUtils.guid,file);
                Message answ = TransferData.writeObject(msg);
                if (answ.command != Answers.ERROR){
                    guid = (String) answ.data[0];
                }else{
                    return Messages.errorMessage("Ошибка отправки файла!");
                }
            }else{
                aParts = (int)f.length()/SIZE_PART;
                if ((int)f.length() - (aParts*SIZE_PART) > 0){
                    aParts++;
                }
                for (int i = 0; i < aParts; i++) {
                    int off = i*SIZE_PART;
                    int length;
                    if ((off + SIZE_PART) > (int)f.length()){
                        length = (int)f.length() - off;
                    }else{
                        length = SIZE_PART;
                    }
                    DataFile file = createDataFile(f,off,length,aParts,i,path,guid);
                    MessageData msg = new MessageData(Commands.UPLOAD,UserUtils.guid,file);
                    Message answ = TransferData.writeObject(msg);
                    if (answ.command != Answers.ERROR){
                        guid = (String) answ.data[0];
                    }else{
                        return Messages.errorMessage("Ошибка отправки файла!");
                    }
                }
            }


            return new MessageTxt(Answers.OK,guid,new String[]{"Файл успешно загружен"});
         } catch (FileNotFoundException e) {
            Log.loggingEvent(Level.INFO,"FilesUtil",e.getMessage());
        }catch (IOException e) {
            Log.loggingEvent(Level.INFO,"FilesUtil",e.getMessage());
        }
        return null;
    }

    public static DataFile createDataFile(RandomAccessFile f, int off, int length, int aParts, int part, String path, String guid) {
        byte[] temp = null;
        byte[] b = new byte[length];
        DataFile file = null;
        try {
            temp = new byte[(int)f.length()];
            f.read(temp, off, length);
            System.arraycopy(temp,off,b,0,length);
            file = new DataFile();
            file.setSize(BigInteger.valueOf(f.length()));
            file.setName(FilenameUtils.getName(path));
            file.setGuid(guid);
            file.setExt(FilenameUtils.getExtension(file.getName()));
            file.setPart(part);
            file.setParts(--aParts);
            file.setSizePackage(length);
            file.setData(b);
        } catch (IOException e) {
            Log.loggingEvent(Level.INFO,"FilesUtil",e.getMessage());
        }
        return file;
    }

    public static Message getFileList(){
        MessageTxt msg = new MessageTxt(Commands.REFRESH,UserUtils.guid,new String[]{""});
        return TransferData.writeObject(msg);
    }

    public static Message renameFile(String newName, DataFile dataFile){
        Message msg = new MessageTxt(Commands.RENAME,UserUtils.guid,new String[]{newName,dataFile.getGuid()});
        return TransferData.writeObject(msg);
    }

    public static Message deleteFile(String guid){
        Message msg = new MessageTxt(Commands.DELETE,UserUtils.guid,new String[]{guid});
        return TransferData.writeObject(msg);
    }

    public static Message downloadFile(String guid,String path){
        File f = new File(path);
        if (f.exists()){
            f.delete();
        }
        Message msg = new MessageTxt(Commands.DOWNLOAD,UserUtils.guid,new String[]{guid});
        Message answ = TransferData.writeObject(msg);
        if (answ.command == Answers.OK){
            //Получение количества частей файла;
            int aParts = Integer.parseInt((String) answ.data[0]);
            if (aParts == 0){
                msg = new MessageTxt(Commands.DOWNLOAD, UserUtils.guid, new String[]{guid,"0"});
                answ = TransferData.writeObject(msg);
                if (answ.command == Answers.ERROR) {
                    return Messages.errorMessage("Ошибка получения файла");
                }
                DataFile file = (DataFile) answ.data[0];
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(path, true);
                    fos.write(file.getData());
                    return Messages.okMessage("Файл сохранен на диск");
                } catch (FileNotFoundException e) {
                    Log.loggingEvent(Level.INFO,"FilesUtil",e.getMessage());
                    Messages.errorMessage("Ошибка получения файла");
                } catch (IOException e) {
                    Log.loggingEvent(Level.INFO,"FilesUtil",e.getMessage());
                    Messages.errorMessage("Ошибка получения файла");
                }
            }else {
                FileOutputStream fos = null;
                for (int i = 0; i < aParts; i++) {
                    msg = new MessageTxt(Commands.DOWNLOAD, UserUtils.guid, new String[]{guid, "" + i});
                    answ = TransferData.writeObject(msg);
                    if (answ.command == Answers.ERROR) {
                        return Messages.errorMessage("Ошибка получения файла");
                    }
                    DataFile file = (DataFile) answ.data[0];
                    try {
                        fos = new FileOutputStream(path, true);
                        fos.write(file.getData());
                        fos.flush();
                    } catch (FileNotFoundException e) {
                        Log.loggingEvent(Level.INFO,"FilesUtil",e.getMessage());
                        Messages.errorMessage("Ошибка получения файла");
                    } catch (IOException e) {
                        Log.loggingEvent(Level.INFO,"FilesUtil",e.getMessage());
                        Messages.errorMessage("Ошибка получения файла");
                    }

                }
                return Messages.okMessage("Файл сохранен на диск");
            }
        } else {
            return answ;
        }
        return Messages.errorMessage("Ошибка получения файла");
    }
}
