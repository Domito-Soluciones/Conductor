package cl.domito.conductor.http;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RequestConductor {

    public static boolean loginConductor(String reqUrl)
    {
        try {
            JSONObject login = Utilidades.obtenerJsonObject(reqUrl);
            if(!login.getString("id").equals("0"))
            {
                return true;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    public static String datosConductor(String reqUrl) throws JSONException {
        JSONObject nombre = null;
        try {
            nombre = Utilidades.obtenerJsonObject(reqUrl);
            return nombre.getString("nombre");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return nombre.getString("nombre");
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
            params.add(new BasicNameValuePair("conductor",Utilidades.USER));
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
}
