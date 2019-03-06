package cl.domito.conductor.http;

import android.location.Location;

import com.google.gson.JsonObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.dominio.Conductor;

public class RequestConductor {

    private static JSONObject RESPUESTA;

    public static boolean loginConductor(String reqUrl, List<NameValuePair> params)
    {
        try {
            if(Conductor.getInstance().isConectado()) {
                RESPUESTA = Utilidades.enviarPost(reqUrl,params);
                if (!RESPUESTA.getString("conductor_id").equals("0")) {
                    Conductor.getInstance().setId(RESPUESTA.getString("conductor_id"));
                    return true;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static JSONObject datosConductor(String reqUrl, List<NameValuePair> params) throws JSONException {
        try {
            if(Conductor.getInstance().isConectado()) {
                RESPUESTA = Utilidades.enviarPost(reqUrl,params);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return RESPUESTA;
    }

    public static JSONObject obtenerServicioAsignado(String reqUrl, List<NameValuePair> params) throws IOException {
        JSONObject jsonObject = null;
        if(Conductor.getInstance().isConectado()) {
            jsonObject = Utilidades.enviarPost(reqUrl,params);
        }
        return jsonObject;
    }

    public static void desAsignarServicio(String reqUrl,String idServicio) {
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", idServicio));
            params.add(new BasicNameValuePair("conductor",Conductor.getInstance().getNick()));
            Utilidades.enviarPost(reqUrl,params);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static JSONArray getRoute(String url, List<NameValuePair> params)
    {
        return Utilidades.enviarPostArray(url,params);
    }

    public static void actualizarUbicacion(String reqUrl,Location lastLocation) {
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("lat", lastLocation.getLatitude()+""));
            params.add(new BasicNameValuePair("lon", lastLocation.getLongitude()+""));
            params.add(new BasicNameValuePair("conductor",Conductor.getInstance().getId()));
            Utilidades.enviarPost(reqUrl,params);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static JSONArray getServicios(String url, List<NameValuePair> params) {
        JSONArray object = null;
        try {
            object = Utilidades.enviarPostArray(url,params);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return object;
    }
}
