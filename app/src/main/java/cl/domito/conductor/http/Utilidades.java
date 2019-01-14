package cl.domito.conductor.http;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            StringBuilder result = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            jsonObject = new JSONObject(result.toString());
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
        return  jsonObject;
    }



}
