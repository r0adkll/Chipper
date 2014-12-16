package com.r0adkll.chipper.api;

import com.r0adkll.chipper.api.model.Chiptune;
import com.r0adkll.chipper.api.model.Device;
import com.r0adkll.chipper.api.model.Playlist;
import com.r0adkll.chipper.api.model.ServerTime;
import com.r0adkll.chipper.api.model.User;
import com.r0adkll.chipper.api.model.Vote;
import com.r0adkll.chipper.data.Historian;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * API Definitions for the new Java Chipper API
 *
 * Created by r0adkll on 11/1/14.
 */
public interface ChipperService {

    /**
     * Get server's time endpoint that reports the servers time and version
     *
     * @param cb    the callback
     */
    @GET("/time")
    void time(Callback<ServerTime> cb);

    /**
     * The Login/Create endpoint for authorizing the chipper api
     *
     * @param cb    the auth callback
     */
    @FormUrlEncoded
    @POST("/auth")
    void auth(@Field("email") String email,
              @Field("token") String gPlusToken,
              Callback<User> cb);

    /**
     * TODO: Create this endpoint in the API
     *
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
     * TODO: Create this endpoint in the API
     *
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
     * Register a new device, or update an existing one with a matching device_id
     *
     * @param deviceId      the unique id of the device
     * @param model         the model of the device ({@link android.os.Build#MANUFACTURER} + {@link android.os.Build#PRODUCT})
     * @param sdk           the sdk int version of this device
     * @param tablet        whether this device is a tablet or not
     * @param cb            the callback
     */
    @FormUrlEncoded
    @POST("/user/devices")
    void registerDevice(@Field("device_id") String deviceId,
                        @Field("model") String model,
                        @Field("sdk") int sdk,
                        @Field("tablet") boolean tablet,
                        Callback<Device> cb);

    /**
     * Verify a PlayStore purchase against the server
     * so we can give this account premium if possible.
     *
     * @param userId            the user id
     * @param purchaseToken     the purchase token to verify
     */
    @FormUrlEncoded
    @POST("/user/{id}/premium")
    void verifyPlayStorePurchase(@Path("id") String userId,
                                 @Field("purchase_token") String purchaseToken,
                                 Callback cb);

    /**
     * Get a list of devices for the provided user
     *
     * @param userId        the user id for whose devices to get
     * @param cb            the callback
     */
    @GET("/user/{id}/devices")
    void getUsersDevices(@Path("id") String userId,
                         Callback<List<Device>> cb);

    /**
     * Get a single device
     *
     * @param userId        the id of the user the device belongs to
     * @param deviceId      the id of the device to get
     * @param cb            the callback
     */
    @GET("/user/{id}/devices/{deviceId}")
    void getDevice(@Path("id") String userId,
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
    void registerPushToken(@Path("id") String userId,
                           @Path("deviceId") String deviceId,
                           @Field("push_token") String pushToken,
                           Callback<Device> cb);

    /**
     * Delete a specified device
     *
     * @param userId        the id of the user the device belongs to
     * @param deviceId      the id of the device to delete
     * @param cb            the callback
     */
    @DELETE("/user/{id}/devices/{deviceId}")
    void deleteDevice(@Path("id") String userId,
                      @Path("deviceId") String deviceId,
                      Callback cb);

    /**
     * Get all of the user's playlists that he owns
     *
     * @param userId        the id of the user the playlists belong to
     * @param cb            the callback
     */
    @GET("/user/{id}/playlists")
    void getPlaylists(@Path("id") String userId,
                      Callback<List<Playlist>> cb);

    /**
     * Synchronously get all the user's playlists stored on the server
     *
     * @param userId        the id of the user the playlists belong to
     * @return              the list of playlists, null/empty array
     */
    @GET("/user/{id}/playlists")
    List<Playlist> getPlaylistsSync(@Path("id") String userId);

    /**
     * Update/Create a playlist on the server under the user's account
     *
     * @param userId        the id of the user the playlist will belong to
     * @param body          the playlist update/create json
     */
    @POST("/user/{id}/playlists")
    void createPlaylist(@Path("id") String userId,
                        @Body Map<String, Object> body,
                        Callback<Playlist> cb);

    /**
     * Get a specified playlist
     *
     * @param userId        the id of the user the playlist belongs to
     * @param playlistId    the id of the playlist to get
     * @param cb            the callback
     */
    @GET("/user/{id}/playlists/{pid}")
    void getPlaylist(@Path("id") String userId,
                     @Path("pid") String playlistId,
                     Callback<Playlist> cb);

    /**
     * Update an existing playlist
     *
     * @param userId            the id of the user this playlist belongs to
     * @param playlistId        the id of the playlist to update
     * @param body              the playlist update body
     * @param cb                the callback
     */
    @POST("/user/{id}/playlists/{pid}")
    void updatePlaylist(@Path("id") String userId,
                        @Path("pid") String playlistId,
                        @Body Map<String, Object> body,
                        Callback<Playlist> cb);

    /**
     * Synchronously update an existing playlist with the server
     *
     * @param userId        the user of the playlist
     * @param playlistId    the playlist identifier
     * @param body          the update operations body
     * @return              the resulting playlist from the server
     */
    @POST("/user/{id}/playlists/{pid}")
    Playlist updatePlaylistSync(@Path("id") String userId,
                                @Path("pid") String playlistId,
                                @Body Map<String, Object> body);

    /**
     * Delete a specified playlist
     *
     * @param userId        the id of the user the playlist belongs to
     * @param playlistId    the id of the playlist to delete
     * @param cb            the callback
     */
    @DELETE("/user/{id}/playlists/{pid}")
    void deletePlaylist(@Path("id") String userId,
                        @Path("pid") String playlistId,
                        Callback<Map<String, String>> cb);

    /**
     * Share a playlist
     *
     * @param userId        the id of the user the playlist belongs to
     * @param playlistId    the id of the playlist to share
     * @param permission    (OPTIONAL) the permission of the now shared playlist
     * @param cb            the callback
     */
    @FormUrlEncoded
    @POST("/user/{id}/playlists/{pid}/share")
    void sharePlaylist(@Path("id") String userId,
                       @Path("pid") String playlistId,
                       @Field("permission") String permission,
                       Callback<Map<String, String>> cb);

    /**
     * Get a list of playlists that are shared with you
     *
     * @param userId    the id of the user the playlists are shared with
     * @param cb        the callback
     */
    @GET("/user/{id}/shared/playlists")
    void getSharedPlaylists(@Path("id") String userId,
                            Callback<List<Playlist>> cb);

    /**
     * Get a specific shared playlist by it's id
     *
     * @param userId              the id of the user that the playlist is shared with
     * @param sharedPlaylistId    the id of the shared playlist
     * @param cb                  the callback
     */
    @GET("/user/{id}/shared/playlists/{spid}")
    void getSharedPlaylist(@Path("id") String userId,
                           @Path("spid") String sharedPlaylistId,
                           Callback<Playlist> cb);

    /**
     * Update a playlist that is shared with you, but only if the owner has set the permission
     * on the playlist to 'write' or 'full'
     *
     * @param userId                the id of the user that the playlist is shared with
     * @param sharedPlaylistId      the id of the shared playlist
     * @param cb                    the callback
     */
    @POST("/user/{id}/shared/playlists/{spid}")
    void updateSharedPlaylist(@Path("id") String userId,
                              @Path("spid") String sharedPlaylistId,
                              Callback<Playlist> cb);


    /**
     * Remove your link to this shared playlist. This does NOT delete the playlist itself
     * but merely makes you unable to see it (unless you re-redeem it)
     *
     * @param userId                the id of the user that the playlist is shared with
     * @param sharedPlaylistId      the id of the shared playlist
     * @param cb                    the callback
     */
    @DELETE("/user/{id}/shared/playlists/{spid}")
    void removeSharedPlaylist(@Path("id") String userId,
                              @Path("spid") String sharedPlaylistId,
                              Callback cb);

    /**
     * Redeem a shared playlists' token that was received from one of the specially
     * generated links sent by the owning user.
     *
     * @param userId    the id of the user
     * @param token     the shared playlist token used to redeem the playlist
     * @param cb        the callback
     */
    @FormUrlEncoded
    @POST("/user/{id}/shared/redeem")
    void redeemSharedPlaylist(@Path("id") String userId,
                              @Field("token") String token,
                              Callback cb);

    /**
     * Return the map of all the user's vote values
     *
     * @param userId    the id of the user
     * @param cb        the callback
     */
    @GET("/user/{id}/votes")
    void getUserVotes(@Path("id") String userId,
                      Callback<List<Vote>> cb);

    /**
     * Vote on a chiptune
     *
     * @param userId        the id of the user
     * @param voteType      the type of vote i.e. 'up' or 'down'
     * @param tuneId        the id of the chiptune to vote upon
     * @param cb            the callback
     */
    @POST("/user/{id}/vote/{type}/{tuneId}")
    void vote(@Path("id") String userId,
              @Path("type") String voteType,
              @Path("tuneId") String tuneId,
              Callback<Map<String, Object>> cb);

    /**
     * Batch vote on several chiptunes at once
     *
     * @param userId        the id of the user
     * @param body          the body containing the vote params
     * @param cb            the callback
     */
    @POST("/user/{id}/vote/batch")
    void batchVote(@Path("id") String userId,
                   @Body List<Map<String, Object>> body,
                   Callback cb);


    /**
     * TODO: Create this endpoint in the API
     *
     * Post a play stats to the server so the API can collect play statistics to give
     * useful feedback to the users. i.e. System Wide Most Played, or Popular this week, or
     * Most often completed, etc.
     *
     * @param userId        the the id of the user
     * @param chiptuneId    the id of the chiptune that stat is about
     * @param statsType     the stats increment type, see {@link com.r0adkll.chipper.data.Historian.Chronicle}
     * @param cb            the callback
     */
    @POST("/user/{id}/stats/{chiptuneId}/{type}")
    void postStats(@Path("id") String userId,
                   @Path("chiptuneId") String chiptuneId,
                   @Path("type") String statsType,
                   Callback<Historian.Chronicle> cb);

    /**
     * Get the current featured playlist
     *
     * @param cb        the callback
     */
    @GET("/general/featured")
    void getFeaturedPlaylist(Callback<Playlist> cb);

    /**
     * Get a map of all the collective vote values of all
     * the songs available.
     *
     * @param cb    the callback
     */
    @GET("/general/votes")
    void getVotes(Callback<Map<String, Integer>> cb);

    /**
     * Get the master list of chiptunes that give the name, title, stream url
     * lenght of play time, etc.
     *
     * @param cb        the callback
     */
    @GET("/general/chiptunes")
    void getChiptunes(Callback<List<Chiptune>> cb);

    /**
     * Get the most played chiptunes from the server with a given
     * limit (DEFAULT 5)
     *
     * @param limit     the # of most played chiptunes to return (DEFAULT: 5)
     * @param cb        the callback
     */
    @GET("/general/mostplayed")
    void getMostPlayed(@Query("limit") int limit,
                       Callback<List<Historian.Chronicle>> cb);

    /**
     * Get teh most skipped chiptunes from the server with a given limit
     *
     * @param limit     the # of most skipped chiptunes to return (DEFAULT: 5)
     * @param cb        the callback
     */
    @GET("/general/mostskipped")
    void getMostSkipped(@Query("limit") int limit,
                        Callback<List<Historian.Chronicle>> cb);

    /**
     * Get the most completed chiptunes from the server with a given limit
     *
     * @param limit     the # of completely played chiptunes to return (DEFAULT: 5)
     * @param cb        the callback
     */
    @GET("/general/mostcompleted")
    void getMostCompleted(@Query("limit") int limit,
                          Callback<List<Historian.Chronicle>> cb);

    /**
     * If you are an administrator, you can upload featured playlists
     * to the server. This will only update (or create if non-existant) the
     * feature playlist and send a push message to every registered device notifying it
     * that it has updated.
     *
     * @param body      the featured playlist body
     * @param cb        the callback
     */
    @POST("/admin/featured")
    void updateFeaturePlaylist(@Body Map<String, Object> body,
                               Callback<Playlist> cb);

}
