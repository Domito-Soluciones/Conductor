package cl.domito.conductor.activity.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.Utilidades;

public class ActivityUtils {

    public static String URL_GEOCODER =
            "https://maps.googleapis.com/maps/api/geocode/json?";
    public static String URL_PLACES =
            "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    public static String URL_DIRECTIONS =
            "https://maps.googleapis.com/maps/api/directions/json?";

    public static void hideSoftKeyBoard(Activity activity)
    {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*public static String getGeocoder(Activity activity,String latitud,String longitud,String tipo) {
        String addressComponents = "";
        try {
            String url = URL_GEOCODER + "latlng="+latitud+","+longitud+"&sensor=true&key="+activity.getString(R.string.api_key);
            JSONObject json = Utilidades.obtenerJsonObject(url);
            String status = json.getString("status");
            if (status.equalsIgnoreCase("OK")) {
                JSONArray results = json.getJSONArray("results");
                JSONObject zero = results.getJSONObject(0);
                addressComponents = zero.getString("formatted_address");
                String placeId = zero.getString("place_id");
                if(tipo.equals(Conductor.BUSCAR_PARTIDA+""))
                {
                    Conductor.getInstance().setPlaceIdOrigen(placeId);
                    Conductor.getInstance().setPlaceIdOrigenNombre(addressComponents);
                }
                else if(tipo.equals(Usuario.BUSCAR_DESTINO+""))
                {
                    Conductor.getInstance().setPlaceIdDestino(placeId);
                    Conductor.getInstance().setPlaceIdDestinoNombre(addressComponents);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return addressComponents;
    }*/

    public static JSONArray getPlaces(Activity activity, String latitud, String longitud,String input) {
        JSONArray results = null;
        try {
            String url = URL_PLACES + "input="+URLEncoder.encode(input, "utf8")+"&location="+latitud+","+longitud+"&sensor=true&radius=1000&key="+activity.getString(R.string.api_key);
            JSONObject json = Utilidades.obtenerJsonObject(url);
            String status = json.getString("status");
            if (status.equalsIgnoreCase("OK")) {
                results = json.getJSONArray("predictions");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public static List<LatLng> getDirections(Activity activity,GoogleMap mMap,String origen,String[] destinos)
    {
        int largo = destinos.length;
        String destinoFinal = destinos[largo-1];
        String waypoints = "";
        StringBuilder waypointsBuilder = new StringBuilder();
        List<LatLng> polyline = null;
        try {
            if (largo > 1) {
                waypointsBuilder.append("&waypoints=");
                for (int i = 0; i < largo; i++) {
                    if (i == largo) {
                        continue;
                    }
                    waypointsBuilder.append(destinos[i]).append("|");
                }
                waypoints = waypointsBuilder.toString().substring(0, waypointsBuilder.toString().length() - 1);
            }
            String url = URL_DIRECTIONS + "origin=" + URLEncoder.encode(origen, "utf8") + "&destination=" + URLEncoder.encode(destinoFinal, "utf8") + waypoints + "&key=" + activity.getString(R.string.api_key);
            JSONObject json = Utilidades.obtenerJsonObject(url);
            String status = json.getString("status");
            if(status.equals("OK"))
            {
                JSONArray array =json.getJSONArray("routes");
                JSONObject object = (JSONObject) array.get(0);
                String points = object.getJSONObject("overview_polyline").getString("points");
                polyline = decodePolyline(points);
                PolylineOptions polylineOptions = new PolylineOptions().width(10).color(Color.BLACK);
                polylineOptions.addAll(polyline);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Polyline line = mMap.addPolyline(polylineOptions);
                    }
                });
            }
            else if(status.equals("NOT_FOUND"))
            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity.getApplicationContext(),"Ruta no encontrada",Toast.LENGTH_LONG);
                    }
                });
            }
            LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
            latLngBounds.include(polyline.get(0)).include(polyline.get(polyline.size()-1));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 300));
                }
            });

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return polyline;
    }

    public static void updateUI(Activity activity,GoogleMap googleMap,Location loc) {
        if (loc != null) {
            Conductor.getInstance().setLatitud(loc.getLatitude());
            Conductor.getInstance().setLongitud(loc.getLongitude());
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            Toast.makeText(activity, "mal", Toast.LENGTH_LONG);
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
        sharedPreferences.edit().remove(key).commit();
    }

    public static void enviarNotificacion(Activity activity,String titulo,String contenido,int smallIcon)
    {
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyMgr = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(activity, MapsActivity.class);
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

    public static Runnable mensajeError(Activity activity)
    {
        TextView textViewError = null;
        String nombre = activity.getComponentName().getClassName();
        if(nombre.equals("cl.domito.cliente.activity.MapsActivity"))
        {
            textViewError = activity.findViewById(R.id.textViewError);
        }
        else if(nombre.equals("cl.domito.cliente.activity.LoginActivity"))
        {
            textViewError = activity.findViewById(R.id.textViewError2);
        }
        TextView finalTextViewError = textViewError;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(Conductor.getInstance().isConectado())
                {
                    finalTextViewError.setVisibility(View.GONE);
                }
                else if(!Conductor.getInstance().isConectado())
                {
                    finalTextViewError.setVisibility(View.VISIBLE);
                }
            }
        };
        return runnable;
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
        activity.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));

    }

}
