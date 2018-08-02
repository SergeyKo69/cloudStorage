package com.cloud.storage.common;

public class Messages {
    public static MessageTxt errorMessage(String str){
        return new MessageTxt(Answers.ERROR,new String[]{str});
    }

    public static MessageTxt okMessage(String str){
        return new MessageTxt(Answers.OK,new String[]{str});
    }
}
