package com.cloud.storage.common;

import java.io.Serializable;

public class MessageData extends Message implements Serializable {
    private static final long serialVersionUID = 7193392663743561680L;

    public MessageData(CommandsInt command,String guid ,DataFile dataFile) {
        this.command = command;
        this.guid = guid;
        this.data = new DataFile[1];
        this.data[0] = dataFile;
    }

    public MessageData(CommandsInt command,String guid ,DataFile[] dataFile) {
        this.command = command;
        this.guid = guid;
        this.data = dataFile;
    }
}
