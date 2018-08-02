package com.cloud.storage.common;

import java.io.Serializable;

public abstract class Message implements Serializable {
    private static final long serialVersionUID = 5193392663743561680L;

    public CommandsInt command;
    public String guid;
    public Object[] data;

    public CommandsInt getCommand() {
        return command;
    }

    public Object[] getData() {
        return data;
    }
}
