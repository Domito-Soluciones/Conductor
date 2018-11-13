package cl.domito.conductor.listener;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.LoginActivity;
import cl.domito.conductor.activity.ServicioActivity;
import cl.domito.conductor.http.Utilidades;

/**
 * Created by elsan on 01-05-2018.
 */

public class MyNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

    Activity activity;

    public MyNavigationItemSelectedListener(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.servicio) {
            Intent mainIntent = new Intent(this.activity,ServicioActivity.class);
            this.activity.startActivity(mainIntent);
        }
        if (id == R.id.salir) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String url = Utilidades.URL_BASE_CONDUCTOR + "CambiarEstadoConductor.php";
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("usuario", Utilidades.USER));
                    params.add(new BasicNameValuePair("estado", "0"));
                    Utilidades.enviarPost(url,params);
                }
            });
            thread.start();
            Utilidades.CONDUCTOR_ACTIVO = false;
            Intent mainIntent = new Intent(this.activity, LoginActivity.class);
            this.activity.startActivity(mainIntent);
            this.activity.finish();
        }

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
