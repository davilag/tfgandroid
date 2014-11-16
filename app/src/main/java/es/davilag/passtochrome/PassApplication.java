package es.davilag.passtochrome;

import android.app.Application;

/**
 * Created by davilag on 10/11/14.
 */
public class PassApplication extends Application {
    private int nveces;

    public int getNveces() {
        return nveces;
    }

    public void setNveces(int nveces) {
        this.nveces = nveces;
    }
}
