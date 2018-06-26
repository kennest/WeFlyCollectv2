package com.wefly.wecollect.services;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.mapzen.android.lost.api.FusedLocationProviderApi;
import com.mapzen.android.lost.api.LocationListener;
import com.mapzen.android.lost.api.LocationRequest;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;
import com.wefly.wecollect.presenters.BaseActivity;
import com.wefly.wecollect.presenters.BaseService;
import com.wefly.wecollect.utils.AppController;

/**
 * Created by Obrina.KIMI on 1/10/2018.
 */

public class LocationProviderService extends BaseService implements LostApiClient.ConnectionCallbacks, LocationListener {
    private LostApiClient lostApiClient;
    private LocationRequest mLocationRequest;
    AppController appController=AppController.getInstance();
    private final String TAG = getClass().getSimpleName();

    Intent intent;
    public static String str_receiver = "gps.service.receiver";
    private FusedLocationProviderApi mLocationProvider;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        intent = new Intent(str_receiver);

        lostApiClient = new LostApiClient.Builder(this).addConnectionCallbacks(this).build();

        mLocationProvider = LocationServices.FusedLocationApi;

        mLocationRequest = LocationRequest.create()
                .setInterval(0)
                .setFastestInterval(100)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lostApiClient.connect();
        return START_STICKY;
    }

    private void RequestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationProvider.requestLocationUpdates(lostApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        fn_update(location);
    }

    private void fn_update(Location location){
        intent.putExtra("latitude",location.getLatitude()+"");
        intent.putExtra("longitude",location.getLongitude()+"");

        // update All activity
        appController.latitude=location.getLatitude();
        appController.longitude=location.getLongitude();

        BaseActivity.setuLatitude(location.getLatitude());
        BaseActivity.setuLongitude(location.getLongitude());
        sendBroadcast(intent);
    }

    private void turnOff(){
        try {
            mLocationProvider.removeLocationUpdates(lostApiClient, this);
            lostApiClient.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        turnOff();
        super.onDestroy();
    }

    @Override
    public void onConnected() {
        RequestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended() {
        stopSelf();
    }
}
