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
import cl.domito.dmttransfer.thread.EnviarLogOperation;

public class Utilidades {

    public static int tipoError = 0;
    public static boolean DEBUG = true;

    public static String URL_BASE = "https://transfer.domitoapp.cl/source/httprequest/";
    public static String URL_BASE_CONDUCTOR = URL_BASE + "conductor/";
    public static String URL_BASE_MOVIL = URL_BASE + "movil/";
    public static String URL_BASE_NOTIFICACION = URL_BASE + "notificacion/";
    public static String URL_BASE_SERVICIO = URL_BASE + "servicio/";
    public static String URL_BASE_LIQUIDACION = URL_BASE + "liquidacion/";
    public static String URL_BASE_LOG = URL_BASE + "log/";
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
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");

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
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,ioe.getMessage(),ioe.getStackTrace()[0].getClassName(),ioe.getStackTrace()[0].getLineNumber()+"");
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
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
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
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
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
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,ioe.getMessage(),ioe.getStackTrace()[0].getClassName(),ioe.getStackTrace()[0].getLineNumber()+"");
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
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),e.getStackTrace()[0].getLineNumber()+"");
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

    public static String formatoMoneda(String cantidad){
        if(cantidad.length() < 4){
            return cantidad;
        }
        if(cantidad.length() == 4){
            String mil = cantidad.substring(0,1);
            String resto = cantidad.substring(1,4);
            return mil+"."+resto;
        }
        if(cantidad.length() == 5){
            String mil = cantidad.substring(0,2);
            String resto = cantidad.substring(2,5);
            return mil+"."+resto;
        }
        if(cantidad.length() == 6){
            String mil = cantidad.substring(0,3);
            String resto = cantidad.substring(3,6);
            return mil+"."+resto;
        }
        if(cantidad.length() == 7){
            String millon = cantidad.substring(0,1);
            String mil = cantidad.substring(1,4);
            String resto = cantidad.substring(4,7);
            return millon+"."+mil+"."+resto;
        }
        if(cantidad.length() == 8){
            String millon = cantidad.substring(0,2);
            String mil = cantidad.substring(2,5);
            String resto = cantidad.substring(5,8);
            return millon+"."+mil+"."+resto;
        }
        if(cantidad.length() == 9){
            String millon = cantidad.substring(0,3);
            String mil = cantidad.substring(3,6);
            String resto = cantidad.substring(6,9);
            return millon+"."+mil+"."+resto;
        }
        return cantidad;
    }

}
