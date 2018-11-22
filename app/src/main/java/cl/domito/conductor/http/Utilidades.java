package cl.domito.conductor.http;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class Utilidades {

    //public static String URL_BASE = "http://192.168.43.136/GpsVan/source/httprequest/";
    public static String URL_BASE = "https://www.domito.cl/GpsVan/source/httprequest/";
    public static String URL_BASE_CLIENTE = URL_BASE +  "cliente/";
    public static String URL_BASE_CONDUCTOR = URL_BASE + "conductor/";
    public static String URL_BASE_ESTADISTICA = URL_BASE + "estaditica/";
    public static String URL_BASE_MOVIL = URL_BASE + "movil/";
    public static String URL_BASE_SERVICIO = URL_BASE + "servicio/";
    public static String URL_BASE_TRANSPORTISTA = URL_BASE + "transportista/";
    public static String URL_BASE_USUARIO = URL_BASE + "usuario/";

    public static boolean CONDUCTOR_ACTIVO = false;
    public static String USER;
    public static String NOMBRE = "";
    public static String CANTIDAD_VIAJES = "0";
    public static int TIEMPO_ESPERA = 30;

    public static Context CONTEXT = null;
    public static boolean GONE  =  true;

    public static JSONObject SERVICIO;


    public static JSONObject obtenerJsonObject(String urlDest)
    {
        JSONObject json= null;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlDest);
        request.addHeader("Referer", "app-conductor");
        HttpResponse response = null;
        try {
            response = client.execute(request);
            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(
                            response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = new JSONObject(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    public static void enviarPost(String urlDest,List<NameValuePair> params) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(urlDest);

        post.setHeader("User-Agent", "");
        post.addHeader("Referer", "app-conductor");
        try {
            post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(post);
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
