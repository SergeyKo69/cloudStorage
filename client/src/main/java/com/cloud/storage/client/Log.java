package com.cloud.storage.client;

import java.io.IOException;
import java.util.logging.*;

public class Log {
    static {
        try {
            LogManager.getLogManager().readConfiguration(Log.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void loggingEvent(Level level, String className, String event){
        Logger logger = Logger.getLogger(className);
        FileHandler fh = null;
        try {
            fh = new FileHandler("logClient.txt",true);

            logger.addHandler(fh);
            SimpleFormatter sf = new SimpleFormatter();
            fh.setFormatter(sf);
            logger.setUseParentHandlers(false);
            logger.log(level,event);
        } catch (IOException e) {
            e.printStackTrace();
        }
   }

}
