package com.cloud.storage.server;

import com.cloud.storage.common.*;
import com.cloud.storage.server.entity.FilesEntity;

import java.io.*;
import java.math.BigInteger;
import java.util.logging.Level;

public class FileUtils {
    private static final int SIZE_PART = 1024000; // 1 мб.
    private static final String PATH_FOLDER = Settings.getServerSettings().pathFiles;

    public static Message uploadFile(Message msg){
        if (checkGUID(msg.guid) == Answers.ERROR){
            return errorAuthorizationMsg();
        }
        try {
            DataFile file = (DataFile) msg.data[0];
            int userId = DataBaseUtils.getIdUserByGUID(msg.guid);
            if (userId == 0){
                return Messages.errorMessage("Ошибка передачи файла!");
            }
            String path = PATH_FOLDER + "/" + userId + "_" + file.getName();
            FileOutputStream fos = new FileOutputStream(path, true);
            fos.write(file.getData());
            fos.flush();
            String guid = DataBaseUtils.saveOrUpdateFile(userId, msg, PATH_FOLDER + "/" + userId + "_" + file.getName());
            if (guid.equals("")) {
                return Messages.errorMessage("Ошибка передачи файла!");
            }

            return new MessageTxt(Answers.OK,new String[]{guid});
        } catch (FileNotFoundException e) {
            Log.loggingEvent(Level.INFO,"FileUtils",e.getMessage());
            return Messages.errorMessage("Ошибка передачи файла!");
        }catch (IOException e){
            Log.loggingEvent(Level.INFO,"FileUtils",e.getMessage());
            return Messages.errorMessage("Ошибка передачи файла!");
        }
     }

    public static boolean deleteFile(String path){
        File file = new File(path);
        if (file.exists()){
            return file.delete();
        }
        return false;
    }

    public static Message deleteFile(Message msg){
        if (checkGUID(msg.guid) == Answers.ERROR){
            return errorAuthorizationMsg();
        }
        return DataBaseUtils.deleteFile(msg);
    }

    public static Message renameFile(Message msg){
        if (checkGUID(msg.guid) == Answers.ERROR){
            return errorAuthorizationMsg();
        }
        return DataBaseUtils.renameFile(msg);
    }

    public static Message downloadFile(Message msg){
        if (checkGUID(msg.guid) == Answers.ERROR){
            return errorAuthorizationMsg();
        }
        if (msg.data.length == 1){
            FilesEntity file = DataBaseUtils.getFileByGUID((String)msg.data[0]);
            if (file != null){
                try {
                    RandomAccessFile f = new RandomAccessFile(file.getPath(), "r");
                    long aParts = 0;
                    if (f.length() <= SIZE_PART){
                        aParts = 0;
                    }else{
                        aParts = (int)f.length()/SIZE_PART;
                        if ((int)f.length() - (aParts*SIZE_PART) > 0){
                            aParts++;
                        }
                    }
                    return new MessageTxt(Answers.OK,new String[]{""+ aParts});
                } catch (FileNotFoundException e) {
                    Log.loggingEvent(Level.INFO,"FileUtils",e.getMessage());
                    return Messages.errorMessage("Ошибка получения файла");
                }catch (IOException e){
                    Log.loggingEvent(Level.INFO,"FileUtils",e.getMessage());
                    return Messages.errorMessage("Ошибка получения файла");
                }
            }else{
                return Messages.errorMessage("Ошибка получения файла");
            }
        }else if(msg.data.length == 2){
            try {
                FilesEntity file = DataBaseUtils.getFileByGUID((String)msg.data[0]);
                RandomAccessFile f = new RandomAccessFile(file.getPath(), "r");
                int part = Integer.parseInt((String) msg.data[1]);
                int length;
                int off = SIZE_PART*part;
                if ((off + SIZE_PART) > f.length()){
                    length = (int)f.length() - off;
                }else{
                    length = SIZE_PART;
                }
                byte[] temp = null;
                byte[] b = new byte[length];
                DataFile dataFile = null;
                try {
                    temp = new byte[(int)f.length()];
                    f.seek(off);
                    f.read(temp,off,length);
                    System.arraycopy(temp,off,b,0,length);
                    dataFile = new DataFile();
                    dataFile.setData(b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new MessageData(Answers.OK,msg.guid,dataFile);
            } catch (FileNotFoundException e) {
                Log.loggingEvent(Level.INFO,"FileUtils",e.getMessage());
                return Messages.errorMessage("Ошибка получения файла");
            }catch (IOException e){
                Log.loggingEvent(Level.INFO,"FileUtils",e.getMessage());
                return Messages.errorMessage("Ошибка получения файла");
            }
        }
        return okMessage("Файл отправлен");
    }

    public static Message refresh(Message msg){
        if (checkGUID(msg.guid) == Answers.ERROR){
            return errorAuthorizationMsg();
        }
        return DataBaseUtils.getFiles(msg.guid);
    }

    public static MessageTxt errorAuthorizationMsg(){
        return new MessageTxt(Answers.ERROR, new String[]{"Ошибка авторизации! Пожалуйста авторизируйтесь!"});
    }

    public static MessageTxt okMessage(String message){
        return new MessageTxt(Answers.OK, new String[]{message});
    }

    public static Answers checkGUID(String str){
        if (DataBaseUtils.isGUID(str)){
            return Answers.OK;
        }
        return Answers.ERROR;
    }
}
