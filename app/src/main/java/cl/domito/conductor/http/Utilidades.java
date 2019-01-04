package cl.domito.conductor.http;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.List;

import cl.domito.conductor.dominio.Conductor;

public class Utilidades {

    //public static String URL_BASE = "https://www.domito.cl/GpsVan/source/httprequest/";
    public static String URL_BASE = "http://192.168.43.136/GpsVan/source/httprequest/";
    public static String URL_BASE_CLIENTE = URL_BASE +  "cliente/";
    public static String URL_BASE_CONDUCTOR = URL_BASE + "conductor/";
    public static String URL_BASE_ESTADISTICA = URL_BASE + "estaditica/";
    public static String URL_BASE_MOVIL = URL_BASE + "movil/";
    public static String URL_BASE_SERVICIO = URL_BASE + "servicio/";
    public static String URL_BASE_TRANSPORTISTA = URL_BASE + "transportista/";
    public static String URL_BASE_USUARIO = URL_BASE + "pasajero/";

    public static JSONObject obtenerJsonObject(String urlDest)
    {
        InputStream is = null;
        String result = "";
        JSONObject jObject = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlDest);
            httppost.addHeader("Referer", "app-cliente");
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            jObject = new JSONObject(result);
            Conductor.getInstance().setConectado(true);
        }
        catch (UnknownHostException ue)
        {
            Conductor.getInstance().setConectado(false);
        }
        catch (Exception e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jObject;
    }
    public static String enviarPost(String urlDest,List<NameValuePair> params) {
        StringBuffer result = new StringBuffer();
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(urlDest);
        post.setHeader("User-Agent", "");
        post.addHeader("Referer", "app-cliente");
        try {
            post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(post);
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
            Conductor.getInstance().setConectado(true);
        }
        catch (UnknownHostException e)
        {
            Conductor.getInstance().setConectado(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return  result.toString();
    }


}
