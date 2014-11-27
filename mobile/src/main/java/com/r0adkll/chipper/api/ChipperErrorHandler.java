package com.r0adkll.chipper.api;

import android.content.Context;

import com.r0adkll.chipper.R;
import com.r0adkll.chipper.api.model.ChipperError;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Custom Chipper API Error handler
 *
 * Created by r0adkll on 11/26/14.
 */
public class ChipperErrorHandler implements ErrorHandler {

    private Context mCtx;

    /**
     * Constructor
     * @param ctx
     */
    public ChipperErrorHandler(Context ctx){
        mCtx = ctx;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        String errorDescription;

        if (cause.getKind() == RetrofitError.Kind.NETWORK) {
            errorDescription = mCtx.getString(R.string.error_network);
        } else {
            if (cause.getResponse() == null) {
                errorDescription = mCtx.getString(R.string.error_no_response);
            } else {

                // Error message handling - return a simple error to Retrofit handlers..
                try {
                    ChipperError errorResponse = (ChipperError) cause.getBodyAs(ChipperError.class);
                    errorDescription = errorResponse.readable;

                    // Log the technical
                    Timber.e("API Error: %s", errorResponse.technical);
                } catch (Exception ex) {
                    try {
                        errorDescription = mCtx.getString(R.string.error_network_http_error, cause.getResponse().getStatus());
                    } catch (Exception ex2) {
                        Timber.e("handleError: " + ex2.getLocalizedMessage());
                        errorDescription = mCtx.getString(R.string.error_unknown);
                    }
                }
            }
        }

        return new Exception(errorDescription);
    }

}
