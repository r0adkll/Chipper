package com.r0adkll.chipper.push.model;

import android.os.Bundle;

/**
 * This is the main interface for handling incoming push messages
 * from the GcmIntentService and properly handling the push
 * notifications. This will be implemented by an implementation that
 * wil then be Dagger injected into the intentservice and used to
 * handle the notifications
 *
 * Created by r0adkll on 12/17/14.
 */
public interface PushNode {

    public void onPushMessage(String type, Bundle extras);

    public void onPushSendError(Bundle extras);

    public void onPushDeleted(Bundle extras);

}
