package maged.csi5230.edu.tictactoegame;

import android.app.Application;

import maged.csi5230.edu.tictactoegame.utils.CrashHandler;

/**
 * Created by dragonlayout on 2017/11/12.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(getApplicationContext());
    }
}
