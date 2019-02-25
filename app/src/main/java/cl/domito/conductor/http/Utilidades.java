package cl.domito.conductor.http;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.dominio.Conductor;

public class Utilidades {

    public static String URL_BASE = "https://www.domito.cl/GpsVan/source/httprequest/";
    //public static String URL_BASE = "http://192.168.43.136/GpsVan/source/httprequest/";
    public static String URL_BASE_CLIENTE = URL_BASE +  "cliente/";
    public static String URL_BASE_CONDUCTOR = URL_BASE + "conductor/";
    public static String URL_BASE_ESTADISTICA = URL_BASE + "estaditica/";
    public static String URL_BASE_MOVIL = URL_BASE + "movil/";
    public static String URL_BASE_NOTIFICACION = URL_BASE + "notificacion/";
    public static String URL_BASE_SERVICIO = URL_BASE + "servicio/";
    public static String URL_BASE_TRANSPORTISTA = URL_BASE + "transportista/";
    public static String URL_BASE_USUARIO = URL_BASE + "pasajero/";


    public static int CREADO = 0;
    public static int EN_PROCCESO_DE_ASIGNACION = 1;
    public static int ASIGNADO = 2;
    public static int ACEPTADO = 3;
    public static int EN_PROGRESO = 4;
    public static int FINALIZADO = 5;
    public static int CANCELADO = 6;


    public static JSONObject enviarPost(String urlDest,List<NameValuePair> params) {
        JSONObject jsonObject = null;
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(urlDest);
        post.setHeader("User-Agent", "");
        post.addHeader("Referer", "app-cliente");
        try {
            if(params != null) {
                params.add(new BasicNameValuePair("app","app"));
                post.setEntity(new UrlEncodedFormEntity(params));
            }
            else
            {
                params = new ArrayList();
                params.add(new BasicNameValuePair("app","app"));
            }
            HttpResponse response = client.execute(post);
            Log.i("I","Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            StringBuilder result = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                result.append(line);
                System.out.println(line);
            }
            jsonObject = new JSONObject(result.toString());
            Log.i("I",result.toString());
            Conductor.getInstance().setConectado(true);
        }
        catch (UnknownHostException e)
        {
            Conductor.getInstance().setConectado(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return  jsonObject;
    }

    public static JSONArray enviarPostArray(String urlDest, List<NameValuePair> params) {
        JSONArray jsonArray = null;
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(urlDest);
        post.setHeader("User-Agent", "");
                post.addHeader("Referer", "app-cliente");
                try {
                    if(params != null) {
                        params.add(new BasicNameValuePair("app","app"));
                        post.setEntity(new UrlEncodedFormEntity(params));
                    }
                    else
                    {
                params = new ArrayList();
                params.add(new BasicNameValuePair("app","app"));
            }
            HttpResponse response = client.execute(post);
            Log.i("I","Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            StringBuilder result = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                result.append(line);
                System.out.println(line);
            }
            jsonArray = new JSONArray(result.toString());
            Log.i("I",result.toString());
            Conductor.getInstance().setConectado(true);
        }
        catch (UnknownHostException e)
        {
            Conductor.getInstance().setConectado(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return  jsonArray;
    }



}
