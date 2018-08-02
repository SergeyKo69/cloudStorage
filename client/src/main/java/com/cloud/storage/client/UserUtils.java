package com.cloud.storage.client;

import com.cloud.storage.common.*;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

public class UserUtils {
    public static String guid;
    public static String userName;

    public static MessageTxt login(MessageTxt msg){
        MessageTxt answ = (MessageTxt) TransferData.writeObject(msg);
        if (answ == null){
            return Messages.errorMessage("Ошибка соединения с сервером");
        }
        guid = answ.guid;
        userName = (String)answ.getData()[0];
        return answ;
    }

    public static MessageTxt registration(MessageTxt msg){
        MessageTxt answ = (MessageTxt) TransferData.writeObject(msg);
        if (answ == null){
            return Messages.errorMessage("Ошибка соединения с сервером");
        }
        guid = answ.guid;
        userName = (String)answ.getData()[0];
        return answ;
    }

    public static String encryptString(String str){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.reset();
            md.update(str.getBytes("UTF-8"));
            return new BigInteger(1,md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            Log.loggingEvent(Level.INFO,"UserUtils",e.getMessage());
        }catch (UnsupportedEncodingException e){
            Log.loggingEvent(Level.INFO,"UserUtils",e.getMessage());
        }
        return "";
    }
}
