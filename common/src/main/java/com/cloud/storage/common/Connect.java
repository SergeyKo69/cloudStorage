package com.cloud.storage.common;

import java.net.Socket;

import java.io.IOException;

public class Connect extends Socket {
    private static Socket instance;

    private Connect() {

    }

    public static Socket getConnect(){
        if (instance == null || instance.isClosed()){
            try {
                Settings.ClientSettings settings = Settings.getClientSettings();
                instance = new java.net.Socket(settings.host, Integer.parseInt(settings.port)); // далее будет подтягиваться из файла конфигурации.
            } catch (IOException e) {
                e.printStackTrace();
            }
            return instance;
        }else
            return instance;
    }
}
