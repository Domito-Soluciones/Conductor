package cl.domito.dmttransfer.activity.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
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

import org.json.JSONObject;

import java.util.ArrayList;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.FinServicioActivity;
import cl.domito.dmttransfer.activity.adapter.ReciclerViewPasajeroAdapter;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.thread.CambiarEstadoServicioOperation;
import cl.domito.dmttransfer.thread.FinalizarRutaPasajerosOperation;
import cl.domito.dmttransfer.thread.ObtenerServicioOperation;

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
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            NotificationManager mNotifyMgr = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                CharSequence channelName = "canal-" + id;
                NotificationChannel notificationChannel = null;
                notificationChannel = new NotificationChannel(id + "", channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationChannel.setSound(soundUri,null);
                mNotifyMgr.createNotificationChannel(notificationChannel);
            }
            Intent intent = new Intent(activity, clase);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//diferenciar
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);
            mBuilder = new NotificationCompat.Builder(activity, id + "")
                    .setContentIntent(pendingIntent)
                    .setContentTitle(titulo)
                    .setSmallIcon(smallIcon)
                    .setContentText(contenido)
                    .setVibrate(new long[]{100, 250, 100, 500})
                    .setAutoCancel(true)
                    .setSound(soundUri);
            mNotifyMgr.notify(id, mBuilder.build());
        }
        catch(Exception e)
        {

        }
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
        if(conductor.servicio != null && conductor.servicio.length() > 0) {
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
        if(conductor.servicio != null && conductor.servicio.length() > 0) {
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
        if(conductor.servicio != null && conductor.servicio.length() > 0) {
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
        /*else if(conductor.servicioActualRuta.contains("XX"))
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
        }*/
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
