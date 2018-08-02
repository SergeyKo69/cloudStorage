package com.cloud.storage.server;

import com.cloud.storage.common.*;
import com.cloud.storage.server.entity.FilesEntity;
import com.cloud.storage.server.entity.UsersEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DataBaseUtils {
    private static final SessionFactory ourSessionFactory;

    static class While{
        public String name;
        public String value;
        public String type;
        public TypeVal typeVal;

        public While(String name, String value, String type,TypeVal typeVal){
            this.name = name;
            this.value = value;
            this.type = type;
            this.typeVal = typeVal;
        }
    }

    static enum TypeVal{
        STRING,
        NUMER
    }

    static {
        try {
            Settings.ServerSettings settings = Settings.getServerSettings();
            Configuration configuration = new Configuration()
                    .setProperty("hibernate.connection.url",settings.source)
                    .setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver")
                    .setProperty("hibernate.connection.username", settings.login)
                    .setProperty("hibernate.connection.password", settings.password)
                    .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect")
                    .setProperty("hbm2ddl.auto", "update")
                    .addResource("UsersEntity.hbm.xml")
                    .addResource("FilesEntity.hbm.xml")
                    .configure();
            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            Log.loggingEvent(Level.INFO,"DataBaseUtils",ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
       return ourSessionFactory.openSession();
    }

    public static boolean isRegistredUser(String userName){
        Session session = getSession();
        String query = getSqlQuery(new String[]{"id"},"USERS",
                       new While[]{new While("username",userName,"=",TypeVal.STRING)});
        try {
            List<UsersEntity> list = (List<UsersEntity>) session.createSQLQuery(query).
                    addEntity(UsersEntity.class).
                    list();
            return (list.size() > 0);
        }catch (Exception e){
            Log.loggingEvent(Level.INFO,"DataBaseUtils",e.getMessage());
            return true;
        }finally {
            session.close();
        }
    }

    public static MessageTxt registrationUser(Message msg){
        Session session = getSession();
        try {
            session.beginTransaction();
            UsersEntity user = new UsersEntity();
            user.setUsername((String) msg.data[0]);
            user.setLogin((String)msg.data[1]);
            user.setPassword((String)msg.data[2]);
            user.setGuid(UUID.randomUUID().toString());
            session.save(user);
            session.getTransaction().commit();
            return new MessageTxt(Answers.OK,user.getGuid(),new String[]{user.getUsername()});
        }catch (Exception e){
            Log.loggingEvent(Level.INFO,"DataBaseUtils",e.getMessage());
            e.printStackTrace();
        }finally {
            session.close();
        }
        return Messages.errorMessage("Ошибка регистрации");
    }

    public static MessageTxt autorization(Message msg){
        Session session = getSession();
        if (session == null){
            return Messages.errorMessage("Ошибка входа!");
        }
        String query = getSqlQuery(new String[]{"id","username","login","password","guid"},"USERS",
                new While[]{new While("login",(String) msg.data[0],"=",TypeVal.STRING),
                            new While("password",(String) msg.data[1],"=",TypeVal.STRING)});
        try {
            List<UsersEntity> list = (List<UsersEntity>) session.createSQLQuery(query).
                    addEntity(UsersEntity.class).
                    list();
            if (list.size() > 0){
                session.beginTransaction();
                UsersEntity user = list.get(0);
                String guid = UUID.randomUUID().toString();
                user.setGuid(guid);
                session.update(user);
                session.getTransaction().commit();
                return new MessageTxt(Answers.OK,guid,new String[]{user.getUsername()});
            }
        }catch (Exception e){
            Log.loggingEvent(Level.INFO,"DataBaseUtils",e.getMessage());
            return Messages.errorMessage("Ошибка входа!");
        }finally {
            session.close();
        }
        return Messages.errorMessage("Ошибка входв! Нет такого пользователя или пароля!");
    }

    private static String getSqlQuery(String[] field,String table ,While[] whiles){
        String query = "SELECT ";
        for (int i = 0; i < field.length; i++) {
            query = query + field[i];
            if (i < field.length - 1){
                query = query + ", ";
            }else{
                query = query + " ";
            }
        }
        query = query + "FROM " + table + " ";
        if (whiles.length > 0) {
            query = query + " WHERE ";
            for (int i = 0; i < whiles.length; i++) {
                query = query + whiles[i].name + " " +
                        whiles[i].type + getTypeVal(whiles[i].typeVal) + whiles[i].value +
                        getTypeVal(whiles[i].typeVal);
                if (i < whiles.length - 1){
                    query = query + " and ";
                }else{
                    query = query + " ";
                }
            }
        }
        return query;
    }

    public static boolean isGUID(String guid){
        Session session = getSession();
        session.beginTransaction();
        String query = getSqlQuery(new String[]{"id","guid","username","login","password"},"USERS",
                new While[]{new While("guid",guid,"=",TypeVal.STRING)});
        try {
            List<UsersEntity> list = (List<UsersEntity>) session.createSQLQuery(query).
                    addEntity(UsersEntity.class).
                    list();
            session.getTransaction().commit();
            return (list.size() > 0);
        }catch (Exception e){
            Log.loggingEvent(Level.INFO,"DataBaseUtils",e.getMessage());
            return false;
        }finally {
            session.close();
        }
    }

    public static int getIdFileByGUOD(String guid){
        Session session = getSession();
        String query = getSqlQuery(new String[]{"*"},"FILES",
                new While[]{new While("guid",guid,"=",TypeVal.STRING)});
        try {
            List<FilesEntity> list = (List<FilesEntity>) session.createSQLQuery(query).
                    addEntity(FilesEntity.class).
                    list();
            if (list.size() > 0){
                return list.get(0).getId();
            }
        }catch (Exception e){
            Log.loggingEvent(Level.INFO,"DataBaseUtils",e.getMessage());
            return 0;
        }finally {
            session.close();
        }
        return 0;
    }

    public static FilesEntity getFileByGUID(String guid){
        Session session = getSession();
        String query = getSqlQuery(new String[]{"*"},"FILES",
                new While[]{new While("guid",guid,"=",TypeVal.STRING)});
        try {
            List<FilesEntity> list = (List<FilesEntity>) session.createSQLQuery(query).
                    addEntity(FilesEntity.class).
                    list();
            if (list.size() > 0){
                return list.get(0);
            }
        }catch (Exception e){
            Log.loggingEvent(Level.INFO,"DataBaseUtils",e.getMessage());
            return null;
        }finally {
            session.close();
        }
        return null;
    }

    public static int getIdUserByGUID(String guid){
        Session session = getSession();
        String query = getSqlQuery(new String[]{"id","guid","username","login","password"},"USERS",
                new While[]{new While("guid",guid,"=",TypeVal.STRING)});
        try {
            List<UsersEntity> list = (List<UsersEntity>) session.createSQLQuery(query).
                    addEntity(UsersEntity.class).
                    list();
            if (list.size() > 0){
                return list.get(0).getId();
            }
        }catch (Exception e){
            Log.loggingEvent(Level.INFO,"DataBaseUtils",e.getMessage());
            return 0;
        }finally {
            session.close();
        }
        return 0;
    }

    public static Message getFiles(String guid){
        int userId = getIdUserByGUID(guid);
        Session session = getSession();
        String query = getSqlQuery(new String[]{"*"},"FILES",
                new While[]{new While("userid",String.valueOf(userId),"=",TypeVal.NUMER)});
        try {
            List<FilesEntity> list = (List<FilesEntity>) session.createSQLQuery(query).
                    addEntity(FilesEntity.class).
                    list();
            DataFile[] dataFiles = new DataFile[list.size()];

            if (list.size() > 0){
                for (int i = 0; i < list.size(); i++) {
                    dataFiles[i] = new DataFile();
                    dataFiles[i].setPart(list.get(i).getPart());
                    dataFiles[i].setParts(list.get(i).getParts());
                    dataFiles[i].setName(list.get(i).getFilename());
                    dataFiles[i].setId(list.get(i).getId());
                    dataFiles[i].setExt(list.get(i).getType());
                    dataFiles[i].setGuid(list.get(i).getGuid());
                    dataFiles[i].setDate(list.get(i).getDate());
                    dataFiles[i].setSize(list.get(i).getSize());
                }
            }
            return new MessageData(Answers.OK,guid,dataFiles);
        }catch (Exception e){
            Log.loggingEvent(Level.INFO,"DataBaseUtils",e.getMessage());
            return Messages.errorMessage("Ошибка получения списка файлов");
        }finally {
            session.close();
        }
    }

    public static String saveOrUpdateFile(int userId,Message msg,String path){
        Session session = getSession();
        try {
            session.beginTransaction();
            FilesEntity file = new FilesEntity();
            Date date = new Date();
            file.setDate(new Timestamp(date.getTime()));
            file.setUserid(userId);
            DataFile dataFile = (DataFile)msg.data[0];
            System.out.println(""+dataFile.getPart());
            file.setPart(dataFile.getPart());
            file.setParts(dataFile.getParts());
            file.setFilename(dataFile.getName());
            file.setPath(path);
            file.setType(dataFile.getExt());
            file.setSize(dataFile.getSize());
            String guid = dataFile.getGuid();
            file.setId(getIdFileByGUOD(guid));
            if (guid.equals("")){
                guid = UUID.randomUUID().toString();
                file.setGuid(guid);
            }else{
                file.setGuid(guid);
            }
            session.saveOrUpdate(file);
            session.getTransaction().commit();
            return guid;
        }catch (Exception e){
            Log.loggingEvent(Level.INFO,"DataBaseUtils",e.getMessage());
            return "";
        }finally {
            session.close();
        }
    }

    private static String getTypeVal(TypeVal typeVal){
        switch (typeVal){
            case STRING:
                return "'";
            case NUMER:
                return " ";
            default:
                return "";
        }
    }

    public static Message renameFile(Message msg){
        if (msg.data.length == 2) {
            FilesEntity file = getFileByGUID((String) msg.data[1]);
            Session session = getSession();
            try {
                session.beginTransaction();
                file.setFilename((String)msg.data[0]);
                session.update(file);
                session.getTransaction().commit();
                return Messages.okMessage("Файл переименован");
            }catch (Exception e){
                Log.loggingEvent(Level.INFO,"DataBaseUtils",e.getMessage());
                return Messages.errorMessage("Ошибка переименования файла");
            }finally {
                session.close();
            }
        }else{
            return Messages.errorMessage("Переданы не верные данные");
        }
    }

    public static Message deleteFile(Message msg){
        if (msg.data.length == 1){
            FilesEntity file = getFileByGUID((String) msg.data[0]);
            Session session = getSession();
            try {
                session.beginTransaction();
                if (FileUtils.deleteFile(file.getPath())) {
                    session.delete(file);
                    session.getTransaction().commit();
                    return Messages.okMessage("Файл удален");
                }
                return Messages.errorMessage("При удалений файла произошла ошибка");
            }catch (Exception e){
                Log.loggingEvent(Level.INFO,"DataBaseUtils",e.getMessage());
                return Messages.errorMessage("Ошибка удаления файла");
            }
        }else{
            return Messages.errorMessage("Переданы не верные данные");
        }
    }

    public static Message testConnection(Message msg){
        return null;
    }
}
