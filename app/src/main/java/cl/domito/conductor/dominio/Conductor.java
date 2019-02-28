package cl.domito.conductor.dominio;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Conductor {

    private static Conductor instance;

    private String id;
    private String nombre;
    private String nick;
    private String password;
    private String celular;
    private String direccion;
    private String servicioActual;
    private String pasajeroActual;
    private int estado;
    private boolean activo;
    private boolean recordarSession;
    private boolean conectado;
    private List<LatLng> latLngs;
    private JSONArray servicios;
    private JSONArray serviciosEspeciales;
    private boolean ocupado;
    private Location location;
    private Location locationDestino;
    private boolean servicioAceptado;
    private boolean servicioCancelado;
    private JSONArray servicio;
    private int indiceViaje = 0;
    private boolean navegando;
    private int cantidadPasajeros;
    private GoogleApiClient googleApiClient;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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
    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public List<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    public JSONArray getServicios() {
        return servicios;
    }

    public JSONArray getServiciosEspeciales() {
        return serviciosEspeciales;
    }

    public void setServicios(JSONArray servicios) {
        this.servicios = servicios;
    }

    public void setServiciosEspeciales(JSONArray serviciosEspeciales) {
        this.serviciosEspeciales = serviciosEspeciales;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public boolean getOcupado() {
        return ocupado;
    }

    public Location getLocationDestino() {
        return locationDestino;
    }

    public void setLocationDestino(Location locationDestino) {
        this.locationDestino = locationDestino;
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

    public boolean isOcupado() {
        return ocupado;
    }

    public boolean isServicioAceptado() {
        return servicioAceptado;
    }

    public void setServicioAceptado(boolean servicioAceptado) {
        this.servicioAceptado = servicioAceptado;
    }

    public boolean isServicioCancelado() {
        return servicioCancelado;
    }

    public void setServicioCancelado(boolean servicioCancelado) {
        this.servicioCancelado = servicioCancelado;
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

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }
}

