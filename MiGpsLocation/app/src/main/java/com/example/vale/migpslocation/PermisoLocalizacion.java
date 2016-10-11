package com.example.vale.migpslocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;


public enum PermisoLocalizacion {GRUESO, FINO, AMBOS, NINGUNO;

    public static PermisoLocalizacion obtenerPermisos (Context contexto)
    {
        PermisoLocalizacion permisoLocalizacion = null;
        int flag_permiso_fino = -5;
        int flag_permiso_grueso = -5;


            flag_permiso_fino = ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION);
            flag_permiso_grueso = ActivityCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (flag_permiso_grueso == PackageManager.PERMISSION_GRANTED)
                if (flag_permiso_fino == PackageManager.PERMISSION_GRANTED)
                    permisoLocalizacion = PermisoLocalizacion.AMBOS;
                else permisoLocalizacion = PermisoLocalizacion.GRUESO;
            else if (flag_permiso_fino == PackageManager.PERMISSION_DENIED)
                    permisoLocalizacion = PermisoLocalizacion.NINGUNO;
                else permisoLocalizacion = PermisoLocalizacion.FINO;

        return permisoLocalizacion;
    }
}
