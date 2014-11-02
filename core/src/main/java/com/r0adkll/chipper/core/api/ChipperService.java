package com.r0adkll.chipper.core.api;

import com.r0adkll.chipper.core.api.model.Device;
import com.r0adkll.chipper.core.api.model.Playlist;
import com.r0adkll.chipper.core.api.model.User;

import java.util.List;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * API Definitions
 *
 * Created by r0adkll on 11/1/14.
 */
public interface ChipperService {

    /**
     * The Login/Create endpoint for authorizing the chipper api
     *
     * @param cb    the auth callback
     */
    @POST("/auth")
    void auth(Callback<User> cb);

    /**
     * Verify a PlayStore purchase against the server
     * so we can give this account premium if possible.
     *
     * @param userId            the user id
     * @param auth              the auth body
     * @param purchaseToken     the purchase token to verify
     */
    @POST("/user/{id}/premium")
    void verifyPlayStorePurchase(@Path("id") String userId, @Field("auth") String auth, @Field("purchase_token") String purchaseToken, Callback cb);

    /**
     * Get a list of devices for the provided user
     *
     * @param userId        the user id for whose devices to get
     * @param cb            the callback
     */
    @GET("/user/{id}/devices")
    void getUsersDevices(@Path("id") String userId, Callback<List<Device>> cb);

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
    @POST("/user/{id}/devices")
    void registerDevice(@Path("id") String userId,
                        @Field("auth") String auth,
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
    void getDevice(@Path("id") String userId,
                   @Field("auth") String auth,
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
    @POST("/user/{id}/devices/{deviceId}")
    void registerPushToken(@Path("id") String userId,
                           @Path("deviceId") String deviceId,
                           @Field("auth") String auth,
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
    void deleteDevice(@Path("id") String userId,
                      @Path("deviceId") String deviceId,
                      @Field("auth") String auth,
                      Callback cb);

    /**
     * Get all of the user's playlists that he owns
     *
     * @param userId        the id of the user the playlists belong to
     * @param cb            the callback
     */
    @GET("/user/{id}/playlists")
    void getPlaylists(@Path("id") String userId,
                      @Field("auth") String auth,
                      Callback<List<Playlist>> cb);

    /**
     * Update/Create a playlist on the server under the user's account
     *
     * @param userId        the id of the user the playlist will belong to
     * @param auth          the auth body
     * @param body          the playlist update/create json
     */
    @POST("/user/{id}/playlists")
    void createPlaylist(@Path("id") String userId,
                        @Field("auth") String auth,
                        @Field("body") String body,
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
    void getPlaylist(@Path("id") String userId,
                     @Path("pid") String playlistId,
                     @Field("auth") String auth,
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
    void updatePlaylist(@Path("id") String userId,
                        @Path("pid") String playlistId,
                        @Field("auth") String auth,
                        @Field("body") String body,
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
    void deletePlaylist(@Path("id") String userId,
                        @Path("pid") String playlistId,
                        @Field("auth") String auth,
                        Callback cb);



}
