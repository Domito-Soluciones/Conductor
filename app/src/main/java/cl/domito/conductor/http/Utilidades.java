package cl.domito.conductor.http;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;

public class Utilidades {

    public static int reintentos = 0;

    public static String URL_BASE = "https://www.domito.cl/Test3/source/httprequest/";
    //public static String URL_BASE = "http://192.168.43.136/GpsVan/source/httprequest/";
    public static String URL_BASE_CLIENTE = URL_BASE + "cliente/";
    public static String URL_BASE_CONDUCTOR = URL_BASE + "conductor/";
    public static String URL_BASE_ESTADISTICA = URL_BASE + "estaditica/";
    public static String URL_BASE_MOVIL = URL_BASE + "movil/";
    public static String URL_BASE_NOTIFICACION = URL_BASE + "notificacion/";
    public static String URL_BASE_SERVICIO = URL_BASE + "servicio/";
    public static String URL_BASE_TRANSPORTISTA = URL_BASE + "transportista/";
    public static String URL_BASE_USUARIO = URL_BASE + "pasajero/";
    public static SimpleDateFormat FORMAT = new SimpleDateFormat("dd-MM-yyyy");


    public static JSONObject enviarPost(String urlDest, List<NameValuePair> params) throws IOException {
        JSONObject jsonObject = null;
        Activity activity = (Activity) Conductor.getInstance().getContext();
        TextView textViewError = activity.findViewById(R.id.textViewError);
        if (!validarConexion()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewError.setVisibility(View.VISIBLE);
                }
            });
            return null;
        }
        else
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewError.setVisibility(View.GONE);
                }
            });
        }
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        HttpConnectionParams.setSoTimeout(httpParams, 3000);
        HttpClient client = new DefaultHttpClient(httpParams);
        HttpPost post = new HttpPost(urlDest);
        post.setHeader("User-Agent", "");
        post.addHeader("Referer", "app-cliente");
        try {
            if (params != null) {
                params.add(new BasicNameValuePair("app", "app"));
                post.setEntity(new UrlEncodedFormEntity(params));
            } else {
                params = new ArrayList();
                params.add(new BasicNameValuePair("app", "app"));
            }
            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            StringBuilder result = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                result.append(line);
                System.out.println(line);
            }
            jsonObject = new JSONObject(result.toString());
        } catch (UnknownHostException e) {
        } catch (IOException ioe) {
            reintentos++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONArray enviarPostArray(String urlDest, List<NameValuePair> params) {
        JSONArray jsonArray = null;
        Activity activity = (Activity) Conductor.getInstance().getContext();
        TextView textViewError = activity.findViewById(R.id.textViewError);
        if (!validarConexion()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewError.setVisibility(View.VISIBLE);
                }
            });
            return null;
        }
        else
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewError.setVisibility(View.GONE);
                }
            });
        }
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        HttpConnectionParams.setSoTimeout(httpParams, 3000);
        HttpClient client = new DefaultHttpClient(httpParams);
        HttpPost post = new HttpPost(urlDest);
        post.setHeader("User-Agent", "");
        post.addHeader("Referer", "app-cliente");
        try {
            if (params != null) {
                params.add(new BasicNameValuePair("app", "app"));
                post.setEntity(new UrlEncodedFormEntity(params));
            } else {
                params = new ArrayList();
                params.add(new BasicNameValuePair("app", "app"));
            }
            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            StringBuilder result = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());
            jsonArray = new JSONArray(result.toString());
        }
        catch (UnknownHostException e) {
        }
        catch (Exception e) {
            //e.printStackTrace();
        }

        return jsonArray;
    }


    public static boolean validarConexion()
    {
        if(isNetDisponible())
        {
            if(isOnlineNet())
            {
                return true;
            }
        }
        return false;
    }


    private static boolean isOnlineNet() {
        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");
            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isNetDisponible() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                Conductor.getInstance().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();
        return (actNetInfo != null && actNetInfo.isConnected());
    }
}
