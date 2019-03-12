package cl.domito.conductor.http;

import android.location.Location;

import com.google.gson.JsonObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.dominio.Conductor;

public class RequestConductor {

    private static JSONObject RESPUESTA;

    public static boolean loginConductor(String usuario, String password)
    {
        String url = Utilidades.URL_BASE_CONDUCTOR + "Login.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("usuario",usuario));
        try {
            byte[] data = password.getBytes("UTF-8");
            String base64 = android.util.Base64.encodeToString(data, android.util.Base64.NO_WRAP);
            params.add(new BasicNameValuePair("password",base64));
            RESPUESTA = Utilidades.enviarPost(url,params);
            if (!RESPUESTA.getString("conductor_id").equals("0")) {
                Conductor.getInstance().setId(RESPUESTA.getString("conductor_id"));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static JSONObject datosConductor(String reqUrl, List<NameValuePair> params) throws JSONException {
        try {
                RESPUESTA = Utilidades.enviarPost(reqUrl,params);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return RESPUESTA;
    }

    public static JSONObject obtenerServicioAsignado(String reqUrl, List<NameValuePair> params) throws IOException {
        JSONObject jsonObject = null;
        jsonObject = Utilidades.enviarPost(reqUrl,params);
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

    public static JSONArray getRoute(String idServicio)
    {
        String url = Utilidades.URL_BASE_SERVICIO + "GetDetalleServicio.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("id", idServicio));
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

    public static JSONObject cambiarEstadoMovil(String estado) {
        JSONObject jsonObject = null;
        String url2 = Utilidades.URL_BASE_MOVIL + "ModEstadoMovil.php";
        List<NameValuePair> params2 = new ArrayList();
        params2.add(new BasicNameValuePair("conductor",Conductor.getInstance().getId()));
        params2.add(new BasicNameValuePair("estado",estado));
        try {
            jsonObject = Utilidades.enviarPost(url2,params2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject cambiarEstadoServicio(String idServicio,String estado) {
        JSONObject jsonObject = null;
        String url2 = Utilidades.URL_BASE_SERVICIO + "ModEstadoServicio.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("id",idServicio));
        params.add(new BasicNameValuePair("estado",estado));
        try {
            jsonObject = Utilidades.enviarPost(url2,params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public static void finalizarservicio() {
        Conductor conductor = Conductor.getInstance();
        String url = Utilidades.URL_BASE_SERVICIO + "ModEstadoServicioPasajero.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("idServicio",conductor.getServicioActual()));
        params.add(new BasicNameValuePair("idPasajero",conductor.getPasajeroActual()));
        try {
            Utilidades.enviarPost(url,params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void obtenerServicioProgramados(String idServicio) {
        String url = Utilidades.URL_BASE_SERVICIO + "GetServicioProgramado.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("id",idServicio));
        params.add(new BasicNameValuePair("conductor",Conductor.getInstance().getNick()));
        Conductor.getInstance().setServicio(Utilidades.enviarPostArray(url, params));
    }

    public static JSONArray obtenerNotificaciones() {
        String url = Utilidades.URL_BASE_NOTIFICACION + "GetNotificaciones.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("llave", Conductor.getInstance().getNick()));
        JSONArray jsonArray = Utilidades.enviarPostArray(url, params);
        return jsonArray;
    }

    public static void logOut() {
        String url = Utilidades.URL_BASE_MOVIL + "ModEstadoMovil.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("conductor",Conductor.getInstance().getId()));
        params.add(new BasicNameValuePair("estado","0"));
        try {
            Utilidades.enviarPost(url,params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cambiarEstadoServicioEspecial(String idServicio,String estado) {
        String url = Utilidades.URL_BASE_SERVICIO + "ModEstadoServicioEspecial.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("id",idServicio));
        params.add(new BasicNameValuePair("estado",estado));
        try {
            Utilidades.enviarPost(url,params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void insertarNavegacion() {
        Conductor conductor = Conductor.getInstance();
        String url = Utilidades.URL_BASE_SERVICIO + "AddServicioDetalleReal.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("servicio",conductor.getServicioActual()));
        params.add(new BasicNameValuePair("lat",conductor.getLocation().getLatitude()+""));
        params.add(new BasicNameValuePair("lon",conductor.getLocation().getLatitude()+""));
        try {
            Utilidades.enviarPost(url,params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cambiarEstadoNotificacion(String id) {
        String url = Utilidades.URL_BASE_NOTIFICACION + "ModEstadoNotificacion.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("id", id));
        try {
            Utilidades.enviarPost(url, params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
