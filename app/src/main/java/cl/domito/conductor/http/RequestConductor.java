package cl.domito.conductor.http;

import android.location.Location;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.dominio.Conductor;

public class RequestConductor {

    private static JSONObject RESPUESTA;
    private static List<NameValuePair> PARAMS = new ArrayList<NameValuePair>();

    public static boolean loginConductor(String reqUrl)
    {
        try {
            if(Conductor.getInstance().isConectado()) {
                RESPUESTA = Utilidades.obtenerJsonObject(reqUrl);
                if (!RESPUESTA.getString("id").equals("0")) {
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

    public static JSONObject datosConductor(String reqUrl) throws JSONException {
        try {
            if(Conductor.getInstance().isConectado()) {
                RESPUESTA = Utilidades.obtenerJsonObject(reqUrl);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return RESPUESTA;
        }

    public static JSONObject obtenerServicioAsignado(String reqUrl) {
        JSONObject servicio = null;
        try {
            servicio = Utilidades.obtenerJsonObject(reqUrl);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return servicio;
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

    public static JSONObject getRoute(String url)
    {
        return Utilidades.obtenerJsonObject(url);
    }

    public static void actualizarUbicacion(String reqUrl,Location lastLocation) {
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("lat", lastLocation.getLatitude()+""));
            params.add(new BasicNameValuePair("lon", lastLocation.getLongitude()+""));
            params.add(new BasicNameValuePair("conductor",Conductor.getInstance().getNick()));
            Utilidades.enviarPost(reqUrl,params);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
