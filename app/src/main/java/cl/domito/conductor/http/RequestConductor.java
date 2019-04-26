package cl.domito.conductor.http;

import android.location.Address;
import android.location.Geocoder;
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
    private static double latAnterior;
    private static double lngAnterior;
    private static Conductor conductor = Conductor.getInstance();

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
            if(RESPUESTA != null) {
                if (!RESPUESTA.getString("conductor_id").equals("0")) {
                    conductor.id = RESPUESTA.getString("conductor_id");
                    return true;
                }
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
        try {
            jsonObject = Utilidades.enviarPost(reqUrl, params);
        }
        catch (Exception e){e.printStackTrace();}
        return jsonObject;
    }

    public static void desAsignarServicio(String reqUrl,String idServicio) {
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", idServicio));
            params.add(new BasicNameValuePair("conductor",conductor.id));
            Utilidades.enviarPost(reqUrl,params);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static JSONArray getRoute(String idServicio)
    {
        JSONArray jsonArray = null;
        String url = Utilidades.URL_BASE_SERVICIO + "GetDetalleServicio.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("id", idServicio));
        try
        {
            jsonArray = Utilidades.enviarPostArray(url,params);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static void actualizarUbicacion(String reqUrl,Location lastLocation) {
        try {
            if(lastLocation != null) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("lat", lastLocation.getLatitude() + ""));
                params.add(new BasicNameValuePair("lon", lastLocation.getLongitude() + ""));
                params.add(new BasicNameValuePair("conductor", conductor.id));
                Utilidades.enviarPost(reqUrl, params);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void actualizarComentarioAdicional(String idServicio,String comentario) {
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String url = Utilidades.URL_BASE_SERVICIO + "AddObservacionServicio.php";
            params.add(new BasicNameValuePair("observacion", comentario));
            params.add(new BasicNameValuePair("idServicio",idServicio ));
            Utilidades.enviarPost(url, params);
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
        params2.add(new BasicNameValuePair("conductor",conductor.id));
        params2.add(new BasicNameValuePair("estado",estado));
        try {
            jsonObject = Utilidades.enviarPost(url2,params2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject cambiarServicioMovil(String servicio) {
        JSONObject jsonObject = null;
        String url = Utilidades.URL_BASE_MOVIL + "ModServicioMovil.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("conductor",conductor.id));
        params.add(new BasicNameValuePair("servicio",servicio));
        try {
            jsonObject = Utilidades.enviarPost(url,params);
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


    public static void cambiarEstadoPasajeros(String estado) {
        String url = Utilidades.URL_BASE_SERVICIO + "ModEstadoServicioPasajeros.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("idServicio",conductor.servicioActual));
        params.add(new BasicNameValuePair("estado",estado));
        try {
            Utilidades.enviarPost(url,params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cambiarEstadoPasajero(String estado,String observacion) {
        String url = Utilidades.URL_BASE_SERVICIO + "ModEstadoServicioPasajero.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("idServicio",conductor.servicioActual));
        params.add(new BasicNameValuePair("idPasajero",conductor.pasajeroActual));
        params.add(new BasicNameValuePair("observacion",observacion));
        params.add(new BasicNameValuePair("estado",estado));
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
        params.add(new BasicNameValuePair("conductor",conductor.id));
        try {
            conductor.servicio = Utilidades.enviarPostArray(url, params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static JSONArray obtenerNotificaciones() {
        JSONArray jsonArray = null;
        String url = Utilidades.URL_BASE_NOTIFICACION + "GetNotificaciones.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("llave", conductor.id));
        try {
            jsonArray = Utilidades.enviarPostArray(url, params);
        }
        catch(Exception e)
        {e.printStackTrace();}
        return jsonArray;
    }

    public static void logOut() {
        String url = Utilidades.URL_BASE_MOVIL + "ModEstadoMovil.php";
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("conductor",conductor.id));
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
        String url = Utilidades.URL_BASE_SERVICIO + "AddServicioDetalleReal.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("servicio",conductor.servicioActual));
        params.add(new BasicNameValuePair("lat",conductor.location.getLatitude()+""));
        params.add(new BasicNameValuePair("lon",conductor.location.getLongitude()+""));
        try {
            if(conductor.location.getLatitude() != latAnterior &&
                conductor.location.getLongitude() != lngAnterior) {
                Utilidades.enviarPost(url, params);
                latAnterior = conductor.location.getLatitude();
                lngAnterior = conductor.location.getLongitude();
            }
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

    public static void actualizarLugarDestinoPasajero(String destino)
    {
        String url = Utilidades.URL_BASE_SERVICIO + "ModDestinoPasajero.php";
        try {
            List<NameValuePair> params = new ArrayList();
            params.add(new BasicNameValuePair("servicio",conductor.servicioActual));
            params.add(new BasicNameValuePair("pasajero",conductor.pasajeroActual));
            params.add(new BasicNameValuePair("destino",new String(destino.getBytes(), "ISO-8859-1")));
            Utilidades.enviarPost(url, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
