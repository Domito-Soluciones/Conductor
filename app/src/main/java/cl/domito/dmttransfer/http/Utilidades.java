package cl.domito.dmttransfer.http;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.dominio.Conductor;

public class Utilidades {

    public static int tipoError = 0;
    public static boolean DEBUG = true;

    public static String URL_BASE = "https://transfer.domitoapp.cl/source/httprequest/";
    public static String URL_BASE_CONDUCTOR = URL_BASE + "conductor/";
    public static String URL_BASE_MOVIL = URL_BASE + "movil/";
    public static String URL_BASE_NOTIFICACION = URL_BASE + "notificacion/";
    public static String URL_BASE_SERVICIO = URL_BASE + "servicio/";
    public static String URL_BASE_LIQUIDACION = URL_BASE + "liquidacion/";
    public static SimpleDateFormat FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static Conductor conductor = Conductor.getInstance();


    public static JSONObject enviarPost(String urlDest, List<NameValuePair> params) throws IOException {
        JSONObject jsonObject = null;
        Activity activity = (Activity) conductor.context;
        TextView textViewError = activity.findViewById(R.id.textViewError);
        if (!validarConexion()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(textViewError != null) {
                        textViewError.setVisibility(View.VISIBLE);
                    }
                }
            });
            return null;
        }
        else
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(textViewError != null) {
                        textViewError.setVisibility(View.GONE);
                    }
                }
            });
        }
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 10000);
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
                log(urlDest,line);
            }
            jsonObject = new JSONObject(result.toString());
        } catch (UnknownHostException e) {

        } catch (IOException ioe) {
            tipoError = 1;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(ioe.toString().contains("timed out")) {
                        Toast.makeText(activity, "Ocurrio un problema, favor reintentar", Toast.LENGTH_SHORT).show();;
                    }
                }
            });
        } catch (Exception e) {
            tipoError = 1;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(e.toString().contains("timed out")) {
                        Toast.makeText(activity, "Ocurrio un problema, favor reintentar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONArray enviarPostArray(String urlDest, List<NameValuePair> params) {
        JSONArray jsonArray = null;
        Activity activity = (Activity) conductor.context;
        TextView textViewError = activity.findViewById(R.id.textViewError);
        if (!validarConexion()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(textViewError != null) {
                        textViewError.setVisibility(View.VISIBLE);
                    }
                }
            });
            return null;
        }
        else
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(textViewError != null) {
                        textViewError.setVisibility(View.GONE);
                    }
                }
            });
        }
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 10000);
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
                log(urlDest,line);
            }
            jsonArray = new JSONArray(result.toString());

        } catch (UnknownHostException e) {

        } catch (IOException ioe) {
            tipoError = 1;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(ioe.toString().contains("timed out")) {
                        Toast.makeText(activity, "Ocurrio un problema, favor reintentar", Toast.LENGTH_SHORT).show();;
                    }
                }
            });
        } catch (Exception e) {
            tipoError = 1;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(e.toString().contains("timed out")) {
                        Toast.makeText(activity, "Ocurrio un problema, favor reintentar", Toast.LENGTH_SHORT).show();;
                    }
                }
            });
            e.printStackTrace();
        }

        return jsonArray;
    }


    public static boolean validarConexion()
    {
        if(isNetDisponible())
        {
            return true;
        }
        return false;
    }

    private static boolean isNetDisponible() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                conductor.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();
        return (actNetInfo != null && actNetInfo.isConnected());
    }

    public static void log(String tag,String texto)
    {
        if(DEBUG)
        {
            Log.i(tag,texto);
        }
    }

}
