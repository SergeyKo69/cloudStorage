package com.cloud.storage.common;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class TransferData {
    public static Message writeObject(Message msg) {
        ObjectEncoderOutputStream oeos = null;
        ObjectDecoderInputStream odis = null;
        Socket connection = Connect.getConnect();
        if (connection == null){
            return Messages.errorMessage("Нет соединения с сервером!");
        }
        try {
            oeos = new ObjectEncoderOutputStream(connection.getOutputStream());
            oeos.writeObject(msg);
            oeos.flush();
            odis = new ObjectDecoderInputStream(connection.getInputStream());
            return  (Message) odis.readObject();
         } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        } finally {
            try {
                oeos.close();
                odis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
