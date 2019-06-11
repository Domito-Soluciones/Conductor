package cl.domito.dmttransfer.dominio;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;

public class Conductor {

    private static Conductor instance;

    public String id;
    public String nombre;
    public String nick;
    public String servicioActual;
    public String servicioActualRuta;
    public String pasajeroActual;
    public int estado;
    public boolean activo;
    public boolean recordarSession;
    public JSONArray servicios;
    public Location location;
    public Location locationDestino;
    public JSONArray servicio;
    public boolean navegando;
    public int cantidadPasajeros;
    public GoogleApiClient googleApiClient;
    public Context context;
    public boolean volver;
    public boolean zarpeIniciado = false;
    public boolean pasajeroRecogido = false;
    public boolean pasajeroRepartido = false;

    public static synchronized Conductor getInstance(){
        if(instance == null){
            instance = new Conductor();
        }
        return instance;
    }

}

