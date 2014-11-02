package com.r0adkll.chipper.core.api.model;

import io.realm.RealmObject;

/**
 * Created by r0adkll on 11/1/14.
 */
public class User extends RealmObject{

    private String id;
    private String email;
    private boolean premium;

    /**
     * Constructor
     */
    public User(){}

    /**
     * Constructor
     *
     * @param id        the user's id
     * @param email     the user's email
     * @param premium   the user's premium status
     */
    public User(String id, String email, boolean premium){
        this.id = id;
        this.email = email;
        this.premium = premium;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
