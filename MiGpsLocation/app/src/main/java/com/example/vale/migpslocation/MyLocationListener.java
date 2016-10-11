package com.example.vale.migpslocation;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener
{
    @Override
    public void onLocationChanged(Location location) {

        Log.d(getClass().getCanonicalName(), " Localización cambiada");
        MainActivity.mostrarLocalizacion (location);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        switch(status){
            case LocationProvider.AVAILABLE:
                Log.d(getClass().getCanonicalName(), "Proveedor " + provider + " DISPONIBLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d(getClass().getCanonicalName(), "Proveedor " + provider + " FUERA DE SERVICIO ");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d(getClass().getCanonicalName(), "Proveedor " + provider + " TEMPORALMENTE NO DISPONIBLE");
                break;

        }

    }

    @Override
    public void onProviderEnabled(String provider) {
//este método también es invocado al volver de la actividad de los ajustes de localización
        Log.d(getClass().getCanonicalName(), "Proveedor " + provider + " ACTIVADO");



    }

    @Override
    public void onProviderDisabled(String provider) {
//este método también es invocado al volver de la actividad de los ajustes de localización
        Log.d(getClass().getCanonicalName(), "Proveedor " + provider + " DESACTIVADO");

    }
}
