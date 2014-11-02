package com.r0adkll.chipper.core.api.model;

import java.util.List;

/**
 * Created by r0adkll on 11/2/14.
 */
public class Playlist {

    public String id;
    public User owner;
    public String name;
    public long updated;
    public User updated_by_user;
    public String token;
    public List<String> permissions;
    public List<Integer> tunes;

}
