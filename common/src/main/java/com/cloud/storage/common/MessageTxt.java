package com.cloud.storage.common;

import java.io.Serializable;

public class MessageTxt extends Message implements Serializable {
    private static final long serialVersionUID = 6193392663743561680L;
    public MessageTxt(CommandsInt command,String[] msg) {
        this.command = command;
        this.data = msg;
    }
    public MessageTxt(CommandsInt command,String guid,String[] msg) {
        this.command = command;
        this.guid = guid;
        this.data = msg;
    }
}
