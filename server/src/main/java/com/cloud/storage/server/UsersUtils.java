package com.cloud.storage.server;

import com.cloud.storage.common.Answers;
import com.cloud.storage.common.Message;
import com.cloud.storage.common.MessageTxt;
import com.cloud.storage.common.Messages;

public class UsersUtils {
    public static Message autorization(Message msg){
        return DataBaseUtils.autorization(msg);
    }

    public static Message registration(Message msg){
        if (!DataBaseUtils.isRegistredUser((String) msg.data[0])){
            return DataBaseUtils.registrationUser(msg);
        }else{
            return Messages.errorMessage("Ошибка регистрации, такой пользователь уже существует");
        }
    }

    public static Message logout(Message msg){
        return new MessageTxt(Answers.OK, new String[]{""});
    }
}
