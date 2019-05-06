package cl.domito.conductor.activity.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.FinServicioActivity;
import cl.domito.conductor.activity.adapter.ReciclerViewPasajeroAdapter;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.thread.CambiarEstadoServicioOperation;
import cl.domito.conductor.thread.FinalizarRutaPasajerosOperation;
import cl.domito.conductor.thread.ObtenerServicioOperation;

public class ActivityUtils {

    public static String URL_GEOCODER =
            "https://maps.googleapis.com/maps/api/geocode/json?";

    public static void hideSoftKeyBoard(Activity activity)
    {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void guardarSharedPreferences(SharedPreferences sharedPreferences,String llave,String valor)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(llave, valor);
        editor.commit();
    }

    public static void eliminarSharedPreferences(SharedPreferences sharedPreferences,String key)
    {
        sharedPreferences.edit().putString(key, "").commit();
    }

    public static void enviarNotificacion(int id,Context activity,String titulo,String contenido,int smallIcon,Class clase)
    {
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyMgr = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(activity, clase);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//diferenciar
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder = new NotificationCompat.Builder(activity)
                .setContentIntent(pendingIntent)
                .setContentTitle(titulo)
                .setSmallIcon(smallIcon)
                .setContentText(contenido)
                .setVibrate(new long[]{100, 250, 100, 500})
                .setAutoCancel(true)
                .setSound(soundUri);
        mNotifyMgr.notify(id, mBuilder.build());
    }

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context,vectorDrawableResourceId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static List<LatLng> decodePolyline(final String encodedPath) {

        int len = encodedPath.length();

        final List<LatLng> path = new ArrayList<>(len / 2);
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;
    }

    public static void llamar(Activity activity,String numero)
    {
        String dial = numero;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 101);
            return;

        } else {
            activity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }

    }

    public static void dibujarRuta(Activity activity,GoogleMap mMap,JSONArray route) {
        PolylineOptions polylineOptions = new PolylineOptions().width(10).color(Color.BLACK);
        LatLng latLngInicio = null;
        LatLng latLngFin = null;
        for(int i = 0 ; i < route.length(); i++)
        {
            try {
                JSONObject jsonObject = (JSONObject) route.get(i);
                LatLng latLng = new LatLng(jsonObject.getDouble("lat"),jsonObject.getDouble("lng"));
                polylineOptions.add(latLng);
                if(i == 0)
                {
                    latLngInicio = latLng;
                }
                if(i == route.length() - 1)
                {
                    latLngFin = latLng;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLngInicio).include(latLngFin);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mMap != null) {
                    Polyline line = mMap.addPolyline(polylineOptions);
                    line.setColor(activity.getResources().getColor(R.color.colorLinea));
                    line.setWidth(15f);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150), 1000, null);
                }
            }
        });

    }

    public static boolean isRunning(Class<?> serviceClass, Activity activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    public static boolean checkNetworkAvailable(Context context) {
        boolean networkStatus = false;
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                networkStatus = true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    networkStatus = true;
            }
        }catch(Exception e){
            e.printStackTrace();
            networkStatus = false;
        }
        return networkStatus;
    }

    public static void volver(Activity activity)
    {
        activity.finish();
    }


    public static AlertDialog setProgressDialog(Activity activity) {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(activity);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(activity);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(activity);
        tvText.setText("Cargando ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
        //dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
        return dialog;
    }

    public static void recargarPasajeros(Activity activity)
    {
        ArrayList<String> lista = new ArrayList();
        Conductor conductor = Conductor.getInstance();
        String idServicio = conductor.servicioActual;
        try {
            ObtenerServicioOperation obtenerServicioOperation = new ObtenerServicioOperation();
            conductor.servicio = obtenerServicioOperation.execute(conductor.servicioActual).get();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(conductor.servicio != null) {
            try {
                JSONObject primero = conductor.servicio.getJSONObject(0);
                String ruta = primero.getString("servicio_truta").split("-")[0];
                if (primero.getString("servicio_estado").equals("4"))
                {
                    conductor.zarpeIniciado = true;
                }
                if ((ruta.equals("ZP") && !conductor.zarpeIniciado)){
                    String cliente = primero.getString("servicio_cliente");
                    String destino = primero.getString("servicio_cliente_direccion");
                    lista.add(cliente + "%%" + destino + "%0%0");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if(conductor.servicio != null) {
            for (int i = 0; i < conductor.servicio.length(); i++) {
                try {
                    JSONObject servicio = conductor.servicio.getJSONObject(i);
                    if (servicio.getString("servicio_id").equals(idServicio)) {
                        String id = servicio.getString("servicio_pasajero_id");
                        String nombre = servicio.getString("servicio_pasajero_nombre");
                        String celular = servicio.getString("servicio_pasajero_celular");
                        String destino = servicio.getString("servicio_destino");
                        String estado = servicio.getString("servicio_pasajero_estado");
                        if (servicio.getString("servicio_truta").contains("ZP")) {
                            if (!estado.equals("3") && !estado.equals("2")) {
                                lista.add(nombre + "%" + celular + "%" + destino + "%" + estado + "%" + id);
                            }
                        } else if (servicio.getString("servicio_truta").contains("RG")) {
                            if (!estado.equals("3") && !estado.equals("2") && !estado.equals("1")) {
                                lista.add(nombre + "%" + celular + "%" + destino + "%" + estado + "%" + id);
                            }
                        }else if(servicio.getString("servicio_truta").contains("XX"))
                        {
                            if (!estado.equals("3") && !estado.equals("2") && !estado.equals("1")) {
                                lista.add(nombre + "%" + celular + "%" + destino + "%" + estado + "%" + id);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(conductor.servicio != null) {
            try {
                JSONObject ultimo = conductor.servicio.getJSONObject(conductor.servicio.length() - 1);
                String ruta = ultimo.getString("servicio_truta").split("-")[0];
                if (ruta.equals("RG")) {
                    String cliente = ultimo.getString("servicio_cliente");
                    String destino = ultimo.getString("servicio_cliente_direccion");
                    lista.add(cliente + "%%" + destino + "%0%0");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if(lista.size() > 0 ) {
            String[] array = new String[lista.size()];
            array  = lista.toArray(array);
            ReciclerViewPasajeroAdapter mAdapter = new ReciclerViewPasajeroAdapter(activity,array);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RecyclerView recyclerView = activity.findViewById(R.id.recyclerViewPasajero);
                    recyclerView.setAdapter(mAdapter);
                }
            });
        }
        else if(!conductor.servicioActualRuta.contains("XX"))
        {

            AlertDialog.Builder dialogo2 = new AlertDialog.Builder(activity);
            dialogo2.setTitle("Motivo Cancelación");
            dialogo2.setMessage("Ingrese motivo de cancelación");
            dialogo2.setCancelable(false);
            final EditText input = new EditText(activity);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            dialogo2.setView(input);
            dialogo2.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!input.getText().toString().equals("")) {
                        activity.finish();
                        CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
                        cambiarEstadoServicioOperation.execute(conductor.servicioActual,"6",input.getText().toString());
                        conductor.zarpeIniciado = false;
                        Toast.makeText(activity,"Servicio cancelado",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialogo2.show();
        }
    }

    public static void finalizar(Activity activity)
    {
        Conductor conductor = Conductor.getInstance();
        try {
            JSONObject json = conductor.servicio.getJSONObject(0);
            Intent intent = new Intent(conductor.context, FinServicioActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", json.getString("servicio_id"));
            bundle.putString("cliente", json.getString("servicio_cliente"));
            bundle.putString("fecha", json.getString("servicio_fecha"));
            bundle.putString("tarifa", json.getString("servicio_tarifa"));
            intent.putExtras(bundle);
            CambiarEstadoServicioOperation cambiarEstadoServicioOperation = new CambiarEstadoServicioOperation();
            cambiarEstadoServicioOperation.execute(conductor.servicioActual, "5","");
            FinalizarRutaPasajerosOperation finalizarRutaPasajerosOperation = new FinalizarRutaPasajerosOperation(activity);
            finalizarRutaPasajerosOperation.execute("3");
            conductor.zarpeIniciado = false;
            conductor.locationDestino = null;
            activity.finish();
            activity.startActivity(intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
