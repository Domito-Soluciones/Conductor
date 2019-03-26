package cl.domito.conductor.activity.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

    public static void enviarNotificacion(Context activity,String titulo,String contenido,int smallIcon,Class clase)
    {
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyMgr = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(activity, clase);
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
        mNotifyMgr.notify(1, mBuilder.build());
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
}
