package com.cloud.storage.common;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

public class DataFile implements Serializable {
    private static final long serialVersionUID = 9193392663743561680L;

    int id;
    Date date;
    String guid;
    String name;
    String ext;
    int part;
    int parts;
    BigInteger size;
    int sizePackage;
    byte[] data;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSizePackage() {
        return sizePackage;
    }

    public void setSizePackage(int sizePackage) {
        this.sizePackage = sizePackage;
    }

    public int getParts() {
        return parts;
    }

    public void setParts(int parts) {
        this.parts = parts;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public BigInteger getSize() {
        return size;
    }

    public void setSize(BigInteger size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
