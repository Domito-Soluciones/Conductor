package cl.domito.conductor.activity.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.activity.MapsActivity;
import cl.domito.conductor.http.Utilidades;

public class ActivityUtils {
    public static void enviarNotificacion()
    {
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyMgr = (NotificationManager) Utilidades.CONTEXT.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(Utilidades.CONTEXT, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(Utilidades.CONTEXT, 0, intent, 0);
        mBuilder = new NotificationCompat.Builder(Utilidades.CONTEXT)
                .setContentIntent(pendingIntent)
                .setContentTitle("Titulo")
                .setSmallIcon(android.support.v4.R.drawable.notification_icon_background)
                .setContentText("Hola que tal?")
                .setVibrate(new long[]{100, 250, 100, 500})
                .setAutoCancel(true);
        mNotifyMgr.notify(1, mBuilder.build());
    }

    public static void dibujarRuta(String origen,String destino) {

        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList();

        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyDzk5KMW7MRXU2HUqXUSyCgGlCwib1nTu8").build();
        DirectionsApiRequest req = DirectionsApi.getDirections(context, origen, destino);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e("TAG", ex.getLocalizedMessage());
        }

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLACK).width(12);
            MapsActivity.mMap.addPolyline(opts);
        }


        //MapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 6));
    }
}
