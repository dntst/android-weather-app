package com.weather.app.utils.location;

public abstract class AbstractLocation {

    Callback callback;

    public interface Callback {
        void onLocationFound(Location location);
        void onPermissionRequired();
        void onError();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public abstract void getCurrentLocation();
}
