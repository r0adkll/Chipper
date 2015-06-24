package com.r0adkll.chipper.tv.ui.login;

import com.r0adkll.chipper.ui.model.IView;

/**
 * Created by r0adkll on 12/7/14.
 */
public interface TVLoginView extends IView{

    public void showErrorMessage(String message);

    public void reset();

    public void close();

}
