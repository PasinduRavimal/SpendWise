package com.spendWise.models;

public class UserAccountModel {
    
    private String username;
    private String displayName;
    private String password;

    public UserAccountModel(String username, String displayName, String password){
        this.username = username;
        this.displayName = displayName;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
