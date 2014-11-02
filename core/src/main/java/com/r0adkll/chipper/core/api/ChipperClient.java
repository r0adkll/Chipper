package com.r0adkll.chipper.core.api;

import com.r0adkll.chipper.core.utils.GSON;
import com.r0adkll.chipper.core.utils.Tools;

import java.util.HashMap;
import java.util.Map;

/**
 * This will be the main chipper api client used to make all the request
 *
 * Created by r0adkll on 11/1/14.
 */
public class ChipperClient {

    /***********************************************************************************************
     *
     * Variables
     *
     */

    private ChipperSession session;

    /**
     * Default Constructor
     *
     */
    public ChipperClient(){}

    /**
     * Constructor
     * @param session       the chipper auth session
     */
    public ChipperClient(ChipperSession session){
        this.session = session;
    }

    /***********************************************************************************************
     *
     * API Methods
     *
     */

    /**
     * Generate the auth params and keystore hash
     * @return
     */
    public static String generateAuthParam(ChipperSession session){
        Map<String, Object> auth = new HashMap<>();

        // Add auth params
        auth.put("user_id", session.getUser().getId());
        auth.put("public_key", session.getPublicKey());
        auth.put("timestamp", System.currentTimeMillis()/1000);

        // Now generate the hash
        String params = GSON.getGson().toJson(auth);
        String hash = Tools.sha256(params.concat(session.getPrivateKey()));
        auth.put("hash", hash);

        return GSON.getGson().toJson(auth);
    }

}
