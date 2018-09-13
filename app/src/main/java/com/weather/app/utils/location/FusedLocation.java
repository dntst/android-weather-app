package com.weather.app.utils.location;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FusedLocation extends AbstractLocation {

    private Activity context;

    public FusedLocation(Activity context) {
        this.context = context;
    }

    @Override
    public void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationClient.getLastLocation().addOnSuccessListener(context, (android.location.Location location) -> {
                if (location != null) {
                    String locationName = getLocationName(location);
                    callback.onLocationFound(new Location(location.getLatitude(), location.getLongitude(), location.getAccuracy(), locationName));
                } else {
                    callback.onError();
                }
            }).addOnFailureListener((Exception e) -> callback.onError())
                    .addOnCanceledListener(() -> callback.onError());
        } else {
            callback.onPermissionRequired();
        }
    }

    private String getLocationName(android.location.Location location) {
        String name = "";

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                name = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return name;
    }
}
