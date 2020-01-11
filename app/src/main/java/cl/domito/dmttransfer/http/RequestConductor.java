package cl.domito.dmttransfer.http;

import android.location.Location;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cl.domito.dmttransfer.activity.SplashScreenActivity;
import cl.domito.dmttransfer.activity.utils.StringBuilderServiceUtil;
import cl.domito.dmttransfer.activity.utils.StringBuilderUtil;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.thread.EnviarLogOperation;

public class RequestConductor {

    private static JSONObject RESPUESTA;
    private static double latAnterior;
    private static double lngAnterior;
    private static Conductor conductor = Conductor.getInstance();

    public static JSONObject loginConductor(String usuario, String password)
    {
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_CONDUCTOR).append("Login.php");
        String url =  builder.toString();
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("usuario",usuario));
        try {
            byte[] data = password.getBytes("UTF-8");
            String base64 = android.util.Base64.encodeToString(data, android.util.Base64.NO_WRAP);
            params.add(new BasicNameValuePair("password",base64));
            RESPUESTA = Utilidades.enviarPost(url,params);
            if(RESPUESTA != null) {
                return RESPUESTA;
            }
        } catch (Exception e) {
            e.printStackTrace();
        EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
        enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        try {
        }
        catch(Exception e)
        {
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        return null;
    }

    public static JSONObject datosConductor(String reqUrl, List<NameValuePair> params) throws JSONException {
        try {
                RESPUESTA = Utilidades.enviarPost(reqUrl,params);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        return RESPUESTA;
    }

    public static JSONObject obtenerServicioAsignado(String reqUrl, List<NameValuePair> params) throws IOException {
        JSONObject jsonObject = null;
        try {
            jsonObject = Utilidades.enviarPost(reqUrl, params);
        }
        catch (Exception e){
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
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
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
    }

    public static JSONArray getRoute(String idServicio)
    {
        JSONArray jsonArray = null;
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_SERVICIO).append("GetDetalleServicio.php");
        String url = builder.toString();
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("id", idServicio));
        try
        {
            jsonArray = Utilidades.enviarPostArray(url,params);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        return jsonArray;
    }

    public static void actualizarUbicacion(String reqUrl,Location lastLocation) {
        try {
            if(lastLocation != null) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("lat", Double.toString(lastLocation.getLatitude() )));
                params.add(new BasicNameValuePair("lon", Double.toString(lastLocation.getLongitude() )));
                params.add(new BasicNameValuePair("conductor", conductor.id));
                Utilidades.enviarPost(reqUrl, params);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
    }

    public static void actualizarComentarioAdicional(String idServicio,String comentario) {
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            StringBuilder builder = StringBuilderUtil.getInstance();
            builder.append(Utilidades.URL_BASE_SERVICIO).append("AddObservacionServicio.php");
            String url =  builder.toString();
            params.add(new BasicNameValuePair("observacion", comentario));
            params.add(new BasicNameValuePair("idServicio",idServicio ));
            Utilidades.enviarPost(url, params);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
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
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        return object;
    }

    public static JSONObject cambiarEstadoMovil(String estado) {
        JSONObject jsonObject = null;
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_MOVIL).append("ModEstadoMovil.php");
        String url = builder.toString();
        List<NameValuePair> params2 = new ArrayList();
        params2.add(new BasicNameValuePair("conductor",conductor.id));
        params2.add(new BasicNameValuePair("estado",estado));
        params2.add(new BasicNameValuePair("equipo", SplashScreenActivity.ANDROID_ID));
        try {
            jsonObject = Utilidades.enviarPost(url,params2);
        } catch (IOException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        return jsonObject;
    }

    public static JSONObject cambiarServicioMovil(String servicio) {
        JSONObject jsonObject = null;
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_MOVIL ).append( "ModServicioMovil.php");
        String url = builder.toString();
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("conductor",conductor.id));
        params.add(new BasicNameValuePair("servicio",servicio));
        try {
            jsonObject = Utilidades.enviarPost(url,params);
        } catch (IOException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        return jsonObject;
    }

    public static JSONObject cambiarEstadoServicio(String idServicio,String estado,String obs) {
        JSONObject jsonObject = null;
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_SERVICIO ).append( "ModEstadoServicio.php");
        String url2 =builder.toString();
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("id",idServicio));
        params.add(new BasicNameValuePair("estado",estado));
        params.add(new BasicNameValuePair("observacion",obs));
        try {
            jsonObject = Utilidades.enviarPost(url2,params);
        } catch (IOException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        return jsonObject;
    }


    public static void cambiarEstadoPasajeros(String estado) {
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_SERVICIO ).append( "ModEstadoServicioPasajeros.php");
        String url =builder.toString();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("idServicio",conductor.servicioActual));
        params.add(new BasicNameValuePair("estado",estado));
        try {
            Utilidades.enviarPost(url,params);
        } catch (IOException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
    }

    public static void cambiarEstadoPasajero(String estado,String observacion) {
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_SERVICIO ).append( "ModEstadoServicioPasajero.php");
        String url =builder.toString();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("idServicio",conductor.servicioActual));
        try {
            params.add(new BasicNameValuePair("idPasajero",new String(conductor.pasajeroActual.getBytes(), "ISO-8859-1")) );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        params.add(new BasicNameValuePair("observacion",observacion));
        params.add(new BasicNameValuePair("tipo",conductor.servicioActualRuta));
        params.add(new BasicNameValuePair("estado",estado));
        params.add(new BasicNameValuePair("lat",Double.toString(conductor.location.getLatitude())));
        params.add(new BasicNameValuePair("lon",Double.toString(conductor.location.getLongitude())));
        try {
            Utilidades.enviarPost(url,params);
        } catch (IOException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
    }

    public static void obtenerServicioProgramados(String idServicio) {
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_SERVICIO ).append( "GetServicioProgramado.php");
        String url = builder.toString();
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("id",idServicio));
        params.add(new BasicNameValuePair("conductor",conductor.id));
        try {
            conductor.servicio = Utilidades.enviarPostArray(url, params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
    }

    public static JSONArray obtenerNotificaciones() {
        JSONArray jsonArray = null;
        StringBuilder builder = StringBuilderServiceUtil.getInstance();
        builder.append(Utilidades.URL_BASE_NOTIFICACION ).append( "GetNotificaciones.php");
        String url = builder.toString();
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("llave", conductor.id));
        try {
            jsonArray = Utilidades.enviarPostArray(url, params);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        return jsonArray;
    }

    public static void logOut() {
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_MOVIL).append("ModEstadoMovil.php");
        String url =  builder.toString();
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("conductor",conductor.id));
        params.add(new BasicNameValuePair("estado","0"));
        try {
            Utilidades.enviarPost(url,params);
        } catch (IOException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
    }

    public static void cambiarEstadoServicioEspecial(String idServicio,String estado) {
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_SERVICIO).append("ModEstadoServicioEspecial.php");
        String url = builder.toString();
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("id",idServicio));
        params.add(new BasicNameValuePair("estado",estado));
        try {
            Utilidades.enviarPost(url,params);
        } catch (IOException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
    }

    public static void insertarNavegacion() {
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_SERVICIO).append("AddServicioDetalleReal.php");
        String url = builder.toString();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("servicio",conductor.servicioActual));
        params.add(new BasicNameValuePair("lat",Double.toString(conductor.location.getLatitude())));
        params.add(new BasicNameValuePair("lon",Double.toString(conductor.location.getLongitude())));
        try {
            if(conductor.location.getLatitude() != latAnterior &&
                conductor.location.getLongitude() != lngAnterior) {
                Utilidades.enviarPost(url, params);
                latAnterior = conductor.location.getLatitude();
                lngAnterior = conductor.location.getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
    }

    public static void cambiarEstadoNotificacion(String id) {
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_NOTIFICACION).append("ModEstadoNotificacion.php");
        String url = builder.toString();
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("servicio", id));
        try {
            Utilidades.enviarPost(url, params);
        } catch (IOException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
    }

    public static void actualizarLugarDestinoPasajero(String destino)
    {
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_SERVICIO).append("ModDestinoPasajero.php");
        String url =  builder.toString() ;
        try {
            List<NameValuePair> params = new ArrayList();
            params.add(new BasicNameValuePair("servicio",conductor.servicioActual));
            params.add(new BasicNameValuePair("pasajero",conductor.pasajeroActual));
            params.add(new BasicNameValuePair("destino", new String(destino.getBytes(), "ISO-8859-1")));
            if(!destino.equals("")) {
                params.add(new BasicNameValuePair("lat", Double.toString(conductor.location.getLatitude() )));
                params.add(new BasicNameValuePair("lon", Double.toString(conductor.location.getLongitude() )));
            }
            Utilidades.enviarPost(url, params);
        } catch (Exception e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
    }

    public static void enviarLogError(String conductor,String error,String clase, String linea)
    {
        StringBuilder builder = StringBuilderUtil.getInstance();
        builder.append(Utilidades.URL_BASE_LOG).append("LogApp.php");
        String url =  builder.toString();
        try {
            List<NameValuePair> params = new ArrayList();
            params.add(new BasicNameValuePair("id",conductor));
            params.add(new BasicNameValuePair("error",error));
            params.add(new BasicNameValuePair("clase", clase));
            params.add(new BasicNameValuePair("linea", linea));
            Utilidades.enviarPost(url, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
