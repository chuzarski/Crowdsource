package net.chuzarski.crowdednews.utils;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

public class ProductionTree extends Timber.HollowTree {

    @Override
    public void i(String message, Object... args) {
        //nothing
    }

    @Override
    public void d(String message, Object... args) {
        //nothing
    }

    @Override
    public void e(String message, Object... args) {
        Crashlytics.log("ERROR: " + String.format(message, args));
    }

    @Override
    public void v(String message, Object... args) {
        super.v(message, args);
    }

    @Override
    public void w(String message, Object... args) {
        super.w(message, args);
    }
}
