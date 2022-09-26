package util;

import android.app.Application;

//Making this class singleton class
public class JournalUser extends Application {

    private String userName;
    private String userId;

    private static JournalUser instance = null;

    //This method will create only one instance for the journalUser class
    public static JournalUser getInstance(){

        if(instance == null){
            instance = new JournalUser();

        }

        return instance;
    }

    //Default constructor
    public JournalUser(){

    }
    //Getters & Setters

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
