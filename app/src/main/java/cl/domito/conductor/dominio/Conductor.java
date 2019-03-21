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

    private String id;
    private String nombre;
    private String nick;
    private String servicioActual;
    private String pasajeroActual;
    private List<String> listaDestinos = new ArrayList();
    private int estado;
    private boolean activo;
    private boolean recordarSession;
    private JSONArray servicios;
    private boolean ocupado;
    private Location location;
    private Location locationDestino;
    private boolean servicioAceptado;
    private JSONArray servicio;
    private int indiceViaje = 0;
    private boolean navegando;
    private int cantidadPasajeros;
    private GoogleApiClient googleApiClient;
    private Context context;
    private boolean volver;
    private boolean rutaDibujada;

    public static synchronized Conductor getInstance(){
        if(instance == null){
            instance = new Conductor();
        }
        return instance;
    }

    public String getPasajeroActual() {
        return pasajeroActual;
    }

    public void setPasajeroActual(String pasajeroActual) {
        this.pasajeroActual = pasajeroActual;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }


    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isRecordarSession() {
        return recordarSession;
    }

    public void setRecordarSession(boolean recordarSession) {
        this.recordarSession = recordarSession;
    }

    public JSONArray getServicios() {
        return servicios;
    }

    public void setServicios(JSONArray servicios) {
        this.servicios = servicios;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public Location getLocationDestino() {
        return locationDestino;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getServicioActual() {
        return servicioActual;
    }

    public void setServicioActual(String servicioActual) {
        this.servicioActual = servicioActual;
    }

    public boolean isServicioAceptado() {
        return servicioAceptado;
    }

    public void setServicioAceptado(boolean servicioAceptado) {
        this.servicioAceptado = servicioAceptado;
    }

    public JSONArray getServicio() {
        return servicio;
    }

    public void setServicio(JSONArray servicio) {
        this.servicio = servicio;
    }

    public int getIndiceViaje() {
        return indiceViaje;
    }

    public void setIndiceViaje(int indiceViaje) {
        this.indiceViaje = indiceViaje;
    }

    public boolean isNavegando() {
        return navegando;
    }

    public void setNavegando(boolean navegando) {
        this.navegando = navegando;
    }

    public int getCantidadPasajeros() {
        return cantidadPasajeros;
    }

    public void setCantidadPasajeros(int cantidadPasajeros) {
        this.cantidadPasajeros = cantidadPasajeros;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<String> getListaDestinos() {
        return listaDestinos;
    }

    public boolean isVolver() {
        return volver;
    }

    public void setVolver(boolean volver) {
        this.volver = volver;
    }

    public boolean isRutaDibujada() {
        return rutaDibujada;
    }

    public void setRutaDibujada(boolean rutaDibujada) {
        this.rutaDibujada = rutaDibujada;
    }
}

