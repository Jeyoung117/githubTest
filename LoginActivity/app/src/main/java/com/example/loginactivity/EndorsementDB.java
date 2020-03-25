package com.example.loginactivity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class EndorsementDB extends RealmObject {

    @PrimaryKey
    String userId;
    String strEndorsement;

    public EndorsementDB() {

    }

    public EndorsementDB(String userId, String strEndorsement) {
        this.userId = userId;
        this.strEndorsement = strEndorsement;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getStrEndorsement() {
        return this.strEndorsement;
    }

    public void setStrEndorsement(String strEndorsement) {
        this.strEndorsement = strEndorsement;
    }
}
