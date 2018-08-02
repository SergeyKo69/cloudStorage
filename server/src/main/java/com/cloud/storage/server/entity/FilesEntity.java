package com.cloud.storage.server.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Table(name = "files", schema = "cloudstorage", catalog = "utf8_bin")
public class FilesEntity implements Serializable {
    private int id;
    private int userid;
    private Integer part;
    private Integer parts;
    private String filename;
    private String path;
    private Timestamp date;
    private String type;
    private BigInteger size;
    private String guid;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "userid", nullable = false)
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    @Basic
    @Column(name = "part", nullable = true)
    public Integer getPart() {
        return part;
    }

    public void setPart(Integer part) {
        this.part = part;
    }

    @Basic
    @Column(name = "parts", nullable = true)
    public Integer getParts() {
        return parts;
    }

    public void setParts(Integer parts) {
        this.parts = parts;
    }

    @Basic
    @Column(name = "filename", nullable = false, length = 255)
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Basic
    @Column(name = "path", nullable = false, length = 255)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Basic
    @Column(name = "guid", nullable = false, length = 36)
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "date", nullable = false)
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Basic
    @Column(name = "type", nullable = true, length = 45)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Basic
    @Column(name = "size", nullable = true, precision = 0)
    public BigInteger getSize() {
        return size;
    }

    public void setSize(BigInteger size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilesEntity that = (FilesEntity) o;

        if (id != that.id) return false;
        if (userid != that.userid) return false;
        if (part != null ? !part.equals(that.part) : that.part != null) return false;
        if (parts != null ? !parts.equals(that.parts) : that.parts != null) return false;
        if (filename != null ? !filename.equals(that.filename) : that.filename != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (size != null ? !size.equals(that.size) : that.size != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userid;
        result = 31 * result + (part != null ? part.hashCode() : 0);
        result = 31 * result + (parts != null ? parts.hashCode() : 0);
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        return result;
    }
}
