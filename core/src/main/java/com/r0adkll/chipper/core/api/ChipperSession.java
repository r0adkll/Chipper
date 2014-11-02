package com.r0adkll.chipper.core.api;

import android.util.Pair;

import com.google.gson.Gson;
import com.r0adkll.chipper.core.api.model.User;
import com.r0adkll.chipper.core.utils.Tools;
import com.r0adkll.deadskunk.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;

/**
 * This class is used to denote a Chipper API session for
 * making authenticated requests
 *
 * Created by r0adkll on 11/1/14.
 */
public class ChipperSession extends RealmObject {

    private final Gson GSON = new Gson();

    private User user;
    private String publicKey;
    private String privateKey;

    /***********************************************************************************************
     *
     * Helper Methods
     *
     */

    /**
     * Generate the auth params and keystore hash
     * @return
     */
    public String generateAuthParam(){
        Map<String, Object> auth = new HashMap<>();

        // Add auth params
        auth.put("user_id", user.getId());
        auth.put("public_key", publicKey);
        auth.put("timestamp", System.currentTimeMillis()/1000);

        // Now generate the hash
        String params = GSON.toJson(auth);
        String hash = Tools.sha256(params.concat(privateKey));
        auth.put("hash", hash);

        return GSON.toJson(auth);
    }

    /***********************************************************************************************
     *
     * Getters and Setters
     *
     */

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
