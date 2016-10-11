package com.example.vale.migpslocation;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;



public class MainActivity extends AppCompatActivity /*implements LocationListener */ {

    private LocationManager locationManager;
    private String provider;
    private MyLocationListener myLocationListener;
    private final static int COD_PETICION_PERMISOS = 103;


    private void pedirPermisos() {
        Log.d(getClass().getCanonicalName(), "Pidiendo permiso...");
        try {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    COD_PETICION_PERMISOS); //codigo arbitrario, que identifica la peticion

        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), "ERRORAZO al pedir permisos", e);
        }


    }

    private void mostrarLocalizacionTrasAprobacion() {

        Location location = null;

        try {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//"gps"
            mostrarLocalizacion(location);
        } catch (SecurityException se) {
            Log.e(MainActivity.class.getCanonicalName(), "Error al mostrar localización fina tras aprobación", se);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == COD_PETICION_PERMISOS) {
            // Si el usaurio cancela, el array no tiene ninguna posición--Chequear
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.d(getClass().getCanonicalName(), "PERMISO DE LOCALIZACIÓN FINA CONCECIDO en ejecución");
                mostrarLocalizacionTrasAprobacion();

            } else {

                Log.d(getClass().getCanonicalName(), "PERMISO DE LOCALIZACIÓN DENEGADO en ejecución");

            }
        }

    }

    public static void mostrarLocalizacion(Location location) {
        double lat = 0;
        double lng = 0;
        double alt = 0;

        if (null != location) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            alt = location.getAltitude();

            Log.d(MainActivity.class.getCanonicalName(), "LATITUD = " + lat);
            Log.d(MainActivity.class.getCanonicalName(), "LONGITUD = " + lng);
            Log.d(MainActivity.class.getCanonicalName(), "ALTITUD = " + alt);

            Log.d(MainActivity.class.getCanonicalName(), "Proveedor = " + location.getProvider());

        } else {
            Log.d(MainActivity.class.getCanonicalName(), "LOCALIZACIÓN null ");
        }


    }


    private void solicitarActivarLocalizacion ()

    {

        FragmentManager fm = this.getFragmentManager();
        DialogoGPS dialogo = new DialogoGPS();
        dialogo.show(fm, "Aviso");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Location location_inicial = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(getClass().getCanonicalName(), "Entramos por oncreate ...");


        try {

            //ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION); //si incluyo esta línea, aunque no haga nada con el valor, A studio deja de dar la vara con la SecurityException

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            myLocationListener = new MyLocationListener();
            provider = LocationManager.GPS_PROVIDER;

            PermisoLocalizacion permisoLocalizacion = PermisoLocalizacion.obtenerPermisos(this);

            switch (permisoLocalizacion) {
                case AMBOS:
                    Log.d(getClass().getCanonicalName(), "AMBOS permisos aparecen en el manifest , fueron aprobados en la instalación y/o en la ejecución");
                case FINO: //luego usamos el GPS como proveedor

                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Log.d(getClass().getCanonicalName(), "EL Acceso fino por GPS está habiliadto");
                        location_inicial = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);//"gps"
                        mostrarLocalizacion(location_inicial);

                    }
                        else
                        {
                            Log.d(getClass().getCanonicalName(), "Pidiendo que habilite el acceso");
                            solicitarActivarLocalizacion();


                        }
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        Log.d(getClass().getCanonicalName(), "EL Acceso grueso por Network está habiliadto");
                        location_inicial = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);//"netwrok"
                        mostrarLocalizacion(location_inicial);
                    }
                    if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                        Log.d(getClass().getCanonicalName(), "Acceso por PassiveProvider habilitado");
                        location_inicial = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);//"passive"
                        mostrarLocalizacion(location_inicial);
                    }
                    //mostrarLocalizacion(location_inicial);

                    break;
                case GRUESO:
                    Log.d(getClass().getCanonicalName(), "Permiso GRUESO habilitado");
                    //luego usamos el NETWORK como proveedor

                    location_inicial = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);//"network"
                    mostrarLocalizacion(location_inicial);


                    break;
                case NINGUNO:
                    Log.d(getClass().getCanonicalName(), "Permisos inhabilitados (aún presentes en Manifest) debido a :\n" +
                            "1.- Que es un permiso peligroso, estamos en API 23 o superior y no fue aceptado en ejecución previamente\n" +
                            "2.- Que el usuario, habiéndolo concecido, lo revocó en ajustes-Aplicaciones");
                    //TODO PEDIRLO

                    pedirPermisos();
                    break;
            }
        } catch (SecurityException se) {
            Log.d(getClass().getCanonicalName(), "Ha ocurrido una excepción de seguridad. No había permisos suficientes y no fueron chequeados", se);//A studio deteceta que no hay una línea de código ActivityCompat.checkSelfPermission en el método y es por ello que obliga a incluir esta excecpión. Hemos usado ActivityCompat.checkSelfPermission en otra clase, pero él no llega  ver el camino.
        }

     }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d(getClass().getCanonicalName(), "La aplicación entra (o vuelve a entrar) a primer plano, ACTIVO");


        try {
            locationManager.requestLocationUpdates(provider, 5000, 0, myLocationListener);
        } catch (SecurityException se) {
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(getClass().getCanonicalName(), "La aplicación entra en pausa (deja de estar visible), paro para optimizar");

        try {
            locationManager.removeUpdates(myLocationListener);

        } catch (SecurityException se) {
            Log.e(getClass().getCanonicalName(), "Sin permisos");
        }
    }


}