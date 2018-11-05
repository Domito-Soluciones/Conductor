package cl.domito.conductor.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONException;

import cl.domito.conductor.R;
import cl.domito.conductor.http.RequestConductor;
import cl.domito.conductor.http.Utilidades;
import cl.domito.conductor.listener.BotonServicioListener;
import cl.domito.conductor.listener.MyImageButtonClickListener;
import cl.domito.conductor.listener.MyMapReadyCallBack;
import cl.domito.conductor.listener.MyNavigationItemSelectedListener;


public class MapsActivity extends FragmentActivity   {

    public static GoogleMap mMap;
    public static MapsActivity mapsActivity;
    public static View servicioLayout;
    public static TextView textViewNServicio;
    public static TextView textViewOrigen;
    public static TextView textViewDestino;
    public static TextView textViewTipo;
    public static TextView textViewNombre;
    public static TextView textViewDireccion;
    public static TextView textViewCelular;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Utilidades.CONTEXT = getApplicationContext();
        mapsActivity = this;
        servicioLayout = findViewById(R.id.relativeLayout5);
        textViewNServicio = findViewById(R.id.textView4);
        textViewOrigen = findViewById(R.id.textView7);
        textViewDestino = findViewById(R.id.textView10);
        textViewTipo = findViewById(R.id.textView12);
        textViewNombre = findViewById(R.id.textView14);
        textViewDireccion = findViewById(R.id.textView16);
        textViewCelular = findViewById(R.id.textView18);

        BotonServicioListener botonServicioListener = new BotonServicioListener();
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(botonServicioListener);
        Button button4 = findViewById(R.id.button4);
        button4.setOnClickListener(botonServicioListener);
        Button button5 = findViewById(R.id.button5);
        button5.setOnClickListener(botonServicioListener);

        ImageView imageButton = findViewById(R.id.imageView2);
        MyImageButtonClickListener myImageButtonClickListener = new MyImageButtonClickListener(this);
        imageButton.setOnClickListener(myImageButtonClickListener);

        NavigationView  navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new MyNavigationItemSelectedListener(this));
        navigationView.setItemIconTintList(null);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new MyMapReadyCallBack(this));

        TextView textViewId = findViewById(R.id.textViewUsuario);
        textViewId.setText(Utilidades.USER);
        TextView textViewNombre = findViewById(R.id.textViewNombre);
        final String url = Utilidades.URL_BASE_CONDUCTOR + "NombreConductor.php?rut="+Utilidades.USER;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Utilidades.NOMBRE = RequestConductor.datosConductor(url);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        textViewNombre.setText(Utilidades.NOMBRE);
        TextView textViewViajes = findViewById(R.id.textViewViajes);
        textViewViajes.setText(Utilidades.CANTIDAD_VIAJES+"");



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
