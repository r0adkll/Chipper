package com.r0adkll.chipper.tv.ui.login;

/**
 * Created by r0adkll on 12/7/14.
 */
public interface TVLoginPresenter {

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
