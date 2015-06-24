package com.r0adkll.chipper.ui.screens.login;

/**
 * Project: Chipper
 * Package: com.r0adkll.chipper.ui.screens.login
 * Created by drew.heavner on 11/12/14.
 */
public interface LoginPresenter {

    /**
     * Create a user account that is not associated with G+
     */
    public void createUserAccount(String email, String password);

    /**
     * Login to an account using email/password combo
     */
    public void loginToAccount(String email, String password);

    /**
     * Creates/Logins to a user account on the server using the G+ access token
     * to authorize the account.
     *
     * @param accessToken       the Google+ access token from loggin in
     */
    public void authorizeUserAccount(String email, String accessToken);

}
