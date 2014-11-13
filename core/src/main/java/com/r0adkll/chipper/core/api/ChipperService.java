package com.r0adkll.chipper.core.api;

import com.r0adkll.chipper.core.api.model.Chiptune;
import com.r0adkll.chipper.core.api.model.Device;
import com.r0adkll.chipper.core.api.model.Playlist;
import com.r0adkll.chipper.core.api.model.User;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * API Definitions for the new Java Chipper API
 *
 * Created by r0adkll on 11/1/14.
 */
public interface ChipperService {

    /**
     * The Login/Create endpoint for authorizing the chipper api
     *
     * @param cb    the auth callback
     */
    @FormUrlEncoded
    @POST("/auth")
    void auth(@Field("email") String email,
              @Field("token") String gPlusToken ,
              Callback<User> cb);

    /**
     * This is the login endpoint used to login in using their
     * username and password combination.
     *
     * @param email         the email address they wish to use
     * @param password      the password to use that is hashed on the server
     * @param cb            the auth callback
     */
    @FormUrlEncoded
    @POST("/auth/login")
    void login(@Field("email") String email,
               @Field("password") String password,
               Callback<User> cb);

    /**
     * Create an account that doesn't use Google+ auth
     *
     * @param email         the email address they wish to use
     * @param password      their password to create an account with
     * @param cb            the callback with the user object on the server
     */
    @FormUrlEncoded
    @POST("/auth/create")
    void create(@Field("email") String email,
                @Field("password") String password,
                Callback<User> cb);

    /**
     * Verify a PlayStore purchase against the server
     * so we can give this account premium if possible.
     *
     * @param userId            the user id
     * @param auth              the auth body
     * @param purchaseToken     the purchase token to verify
     */
    @FormUrlEncoded
    @POST("/user/{id}/premium")
    void verifyPlayStorePurchase(@Header("auth") String auth,
                                 @Path("id") String userId,
                                 @Field("purchase_token") String purchaseToken,
                                 Callback cb);

    /**
     * Get a list of devices for the provided user
     *
     * @param userId        the user id for whose devices to get
     * @param cb            the callback
     */
    @GET("/user/{id}/devices")
    void getUsersDevices(@Header("auth") String auth,
                         @Path("id") String userId,
                         Callback<List<Device>> cb);

    /**
     * Register a new device, or update an existing one with a matching device_id
     *
     * @param userId        the id of the user
     * @param deviceId      the unique id of the device
     * @param model         the model of the device ({@link android.os.Build#MANUFACTURER} + {@link android.os.Build#PRODUCT})
     * @param sdk           the sdk int version of this device
     * @param tablet        whether this device is a tablet or not
     * @param cb            the callback
     */
    @FormUrlEncoded
    @POST("/user/{id}/devices")
    void registerDevice(@Header("auth") String auth,
                        @Path("id") String userId,
                        @Field("device_id") String deviceId,
                        @Field("model") String model,
                        @Field("sdk") int sdk,
                        @Field("tablet") boolean tablet,
                        Callback<Device> cb);

    /**
     * Get a single device
     *
     * @param userId        the id of the user the device belongs to
     * @param deviceId      the id of the device to get
     * @param cb            the callback
     */
    @GET("/user/{id}/devices/{deviceId}")
    void getDevice(@Header("auth") String auth,
                   @Path("id") String userId,
                   @Path("deviceId") String deviceId,
                   Callback<Device> cb);

    /**
     * Register a push token to this device
     *
     * @param userId        the id of the user the device belongs to
     * @param deviceId      the id of the device to update
     * @param pushToken     the push token to update
     * @param cb            the callback
     */
    @FormUrlEncoded
    @POST("/user/{id}/devices/{deviceId}")
    void registerPushToken(@Header("auth") String auth,
                           @Path("id") String userId,
                           @Path("deviceId") String deviceId,
                           @Field("push_token") String pushToken,
                           Callback cb);

    /**
     * Delete a specified device
     *
     * @param userId        the id of the user the device belongs to
     * @param deviceId      the id of the device to delete
     * @param cb            the callback
     */
    @DELETE("/user/{id}/devices/{deviceId}")
    void deleteDevice(@Header("auth") String auth,
                      @Path("id") String userId,
                      @Path("deviceId") String deviceId,
                      Callback cb);

    /**
     * Get all of the user's playlists that he owns
     *
     * @param userId        the id of the user the playlists belong to
     * @param cb            the callback
     */
    @GET("/user/{id}/playlists")
    void getPlaylists(@Header("auth") String auth,
                      @Path("id") String userId,
                      Callback<List<Playlist>> cb);

    /**
     * Update/Create a playlist on the server under the user's account
     *
     * @param userId        the id of the user the playlist will belong to
     * @param auth          the auth body
     * @param body          the playlist update/create json
     */
    @POST("/user/{id}/playlists")
    void createPlaylist(@Header("auth") String auth,
                        @Path("id") String userId,
                        @Body Map<String, Object> body,
                        Callback<Playlist> cb);

    /**
     * Get a specified playlist
     *
     * @param userId        the id of the user the playlist belongs to
     * @param playlistId    the id of the playlist to get
     * @param auth          the auth body
     * @param cb            the callback
     */
    @GET("/user/{id}/playlists/{pid}")
    void getPlaylist(@Header("auth") String auth,
                     @Path("id") String userId,
                     @Path("pid") String playlistId,
                     Callback<Playlist> cb);

    /**
     * Update an existing playlist
     *
     * @param userId            the id of the user this playlist belongs to
     * @param playlistId        the id of the playlist to update
     * @param auth              the auth body
     * @param body              the playlist update body
     * @param cb                the callback
     */
    @POST("/user/{id}/playlists/{pid}")
    void updatePlaylist(@Header("auth") String auth,
                        @Path("id") String userId,
                        @Path("pid") String playlistId,
                        @Body Map<String, Object> body,
                        Callback<Playlist> cb);

    /**
     * Delete a specified playlist
     *
     * @param userId        the id of the user the playlist belongs to
     * @param playlistId    the id of the playlist to delete
     * @param auth          the auth body
     * @param cb            the callback
     */
    @DELETE("/user/{id}/playlists/{pid}")
    void deletePlaylist(@Header("auth") String auth,
                        @Path("id") String userId,
                        @Path("pid") String playlistId,
                        Callback cb);

    /**
     * Share a playlist
     *
     * @param auth          the auth header used to authenticate the response
     * @param userId        the id of the user the playlist belongs to
     * @param playlistId    the id of the playlist to share
     * @param permission    (OPTIONAL) the permission of the now shared playlist
     * @param cb            the callback
     */
    @FormUrlEncoded
    @POST("/user/{id}/playlists/{pid}/share")
    void sharePlaylist(@Header("auth") String auth,
                       @Path("id") String userId,
                       @Path("pid") String playlistId,
                       @Field("permission") String permission,
                       Callback cb);

    /**
     * Get a list of playlists that are shared with you
     *
     * @param auth      the auth header used to authenticate the response
     * @param userId    the id of the user the playlists are shared with
     * @param cb        the callback
     */
    @GET("/user/{id}/shared/playlists")
    void getSharedPlaylists(@Header("auth") String auth,
                            @Path("id") String userId,
                            Callback<List<Playlist>> cb);

    /**
     * Get a specific shared playlist by it's id
     *
     * @param auth                the auth header used to authenticate the response
     * @param userId              the id of the user that the playlist is shared with
     * @param sharedPlaylistId    the id of the shared playlist
     * @param cb                  the callback
     */
    @GET("/user/{id}/shared/playlists/{spid}")
    void getSharedPlaylist(@Header("auth") String auth,
                           @Path("id") String userId,
                           @Path("spid") String sharedPlaylistId,
                           Callback<Playlist> cb);

    /**
     * Update a playlist that is shared with you, but only if the owner has set the permission
     * on the playlist to 'write' or 'full'
     *
     * @param auth                  the auth header used to authenticate the response
     * @param userId                the id of the user that the playlist is shared with
     * @param sharedPlaylistId      the id of the shared playlist
     * @param cb                    the callback
     */
    @POST("/user/{id}/shared/playlists/{spid}")
    void updateSharedPlaylist(@Header("auth") String auth,
                              @Path("id") String userId,
                              @Path("spid") String sharedPlaylistId,
                              Callback<Playlist> cb);


    /**
     * Remove your link to this shared playlist. This does NOT delete the playlist itself
     * but merely makes you unable to see it (unless you re-redeem it)
     *
     * @param auth                  the auth header used to authenticate the response
     * @param userId                the id of the user that the playlist is shared with
     * @param sharedPlaylistId      the id of the shared playlist
     * @param cb                    the callback
     */
    @DELETE("/user/{id}/shared/playlists/{spid}")
    void removeSharedPlaylist(@Header("auth") String auth,
                              @Path("id") String userId,
                              @Path("spid") String sharedPlaylistId,
                              Callback cb);

    /**
     * Redeem a shared playlists' token that was received from one of the specially
     * generated links sent by the owning user.
     *
     * @param auth      the auth header used to authenticate the response
     * @param userId    the id of the user
     * @param token     the shared playlist token used to redeem the playlist
     * @param cb        the callback
     */
    @FormUrlEncoded
    @POST("/user/{id}/shared/redeem")
    void redeemSharedPlaylist(@Header("auth") String auth,
                              @Path("id") String userId,
                              @Field("token") String token,
                              Callback cb);

    /**
     * Return the map of all the user's vote values
     *
     * @param auth      the auth header used to authenticate the response
     * @param userId    the id of the user
     * @param cb        the callback
     */
    @GET("/user/{id}/votes")
    void getUserVotes(@Header("auth") String auth,
                      @Path("id") String userId,
                      Callback<Map<String, Integer>> cb);

    /**
     * Vote on a chiptune
     *
     * @param auth          the auth header used to authenticate the response
     * @param userId        the id of the user
     * @param voteType      the type of vote i.e. 'up' or 'down'
     * @param tuneId        the id of the chiptune to vote upon
     * @param cb            the callback
     */
    @POST("/user/{id}/vote/{type}/{tuneId}")
    void vote(@Header("auth") String auth,
              @Path("id") String userId,
              @Path("type") String voteType,
              @Path("tuneId") int tuneId,
              Callback cb);

    /**
     * Batch vote on several chiptunes at once
     *
     * @param auth          the auth header used to authenticate the response
     * @param userId        the id of the user
     * @param body          the body containing the vote params
     * @param cb            the callback
     */
    @POST("/user/{id}/vote/batch")
    void batchVote(@Header("auth") String auth,
                   @Path("id") String userId,
                   @Body List<Map<String, Object>> body,
                   Callback cb);

    /**
     * Get the current featured playlist
     *
     * @param auth      the the auth header used to authenticate the response
     * @param cb        the callback
     */
    @GET("/playlists/featured")
    void getFeaturedPlaylist(@Header("auth") String auth,
                             Callback<Playlist> cb);

    /**
     * Get a map of all the collective vote values of all
     * the songs available.
     *
     * @param auth  the auth header used to authenticate the response
     * @param cb    the callback
     */
    @GET("/votes")
    void getVotes(@Header("auth") String auth,
                  Callback cb);

    /**
     * Get the master list of chiptunes that give the name, title, stream url
     * lenght of play time, etc.
     *
     * @param auth      the the auth header used to authenticate the response
     * @param cb        the callback
     */
    @GET("/chiptunes")
    void getChiptunes(@Header("auth") String auth,
                      Callback<List<Chiptune>> cb);

}
