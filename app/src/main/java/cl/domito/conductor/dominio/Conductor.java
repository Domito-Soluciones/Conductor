package cl.domito.conductor.dominio;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    public int indicePasajeroActual = 0;
    public boolean recogidaFinalizada = false;
    public boolean zarpeIniciado = false;

    public static synchronized Conductor getInstance(){
        if(instance == null){
            instance = new Conductor();
        }
        return instance;
    }

}

