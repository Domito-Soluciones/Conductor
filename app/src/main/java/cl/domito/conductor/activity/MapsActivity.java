package cl.domito.conductor.activity;

import android.Manifest;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.service.AsignacionServicioService;
import cl.domito.conductor.thread.CambiarEstadoOperation;
import cl.domito.conductor.thread.DatosConductorOperation;
import cl.domito.conductor.thread.DesAsignarServicioOperation;
import cl.domito.conductor.thread.LogoutOperation;
import cl.domito.conductor.thread.RealizarServicioOperation;


public class MapsActivity extends FragmentActivity  implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,LocationListener
{

    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private MapsActivity mapsActivity;
    private SupportMapFragment mapFragment;
    private View servicioLayout;
    private TextView textViewIdServicioValor;
    private TextView textViewOrigenValor;
    private TextView textViewDestinoValor;
    private TextView textViewTipoValor;
    private TextView textViewNombreValor;
    private TextView textViewDireccionValor;
    private TextView textViewCelularValor;
    private Button buttonConfirmar;
    private Button buttonCancelar;
    private Button buttonEstado;
    private ImageButton buttonLlamar;
    private ImageView imageButton;
    private DrawerLayout drawerLayout;
    private NavigationView  navigationView;
    private LocationManager locationManager;

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver, new IntentFilter("custom-event-name"));
        super.onResume();
        if(mMap != null) {
            mMap.clear();
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        bManager.unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        AsignacionServicioService.IS_INICIADO = false;
        Intent i = new Intent(this, AsignacionServicioService.class);
        stopService(i);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        servicioLayout = findViewById(R.id.constrainLayoutServicio);
        textViewIdServicioValor = findViewById(R.id.textViewIdServicioValor);
        textViewOrigenValor = findViewById(R.id.textViewOrigenValor);
        textViewDestinoValor = findViewById(R.id.textViewDestinoValor);
        textViewTipoValor = findViewById(R.id.textViewTipoValor);
        textViewNombreValor = findViewById(R.id.textViewNombreValor);
        textViewDireccionValor = findViewById(R.id.textViewDireccionValor);
        textViewCelularValor = findViewById(R.id.textViewCelularValor);
        buttonEstado = findViewById(R.id.buttonEstado);
        buttonConfirmar = findViewById(R.id.buttonConfirmar);
        buttonCancelar = findViewById(R.id.buttonCancelar);
        buttonLlamar = findViewById(R.id.buttonLlamar);
        imageButton = findViewById(R.id.imageViewMenu);
        navigationView = findViewById(R.id.nav_view);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        DatosConductorOperation datosConductorOperation = new DatosConductorOperation(this);
        datosConductorOperation.execute();

        buttonEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstadoConductor();
            }
        });

        buttonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarServicio();
            }
        });

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desasignarServicio();
            }
        });

        buttonLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamar();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirMenuContextual();
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return getMenuContextual(menuItem);
            }
        });

        mapFragment.getMapAsync(this);
        navigationView.setItemIconTintList(null);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        iniciarUbicacion(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("","");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("ERROR", "Se ha interrumpido la conexi�n con Google Play Services");
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.map_style));
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

            }
        });

        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

            }
        });

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage((FragmentActivity) this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }


    private void realizarServicio() {
        RealizarServicioOperation realizarServicioOperation = new RealizarServicioOperation(this);
        realizarServicioOperation.execute(mMap);
    }

    private void desasignarServicio() {
        DesAsignarServicioOperation desAsignarServicioOperation = new DesAsignarServicioOperation(this);
        desAsignarServicioOperation.execute();
    }

    private void cambiarEstadoConductor() {
        Conductor conductor = Conductor.getInstance();
        CambiarEstadoOperation cambiarEstadoOperation = new CambiarEstadoOperation(this);
        cambiarEstadoOperation.execute();
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

    private void iniciarUbicacion(boolean updateUI)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},0);
            return;
        }
        mMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) getApplicationContext().getSystemService(this.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1000, this);
        Location lastLocation =
                LocationServices.FusedLocationApi.getLastLocation(apiClient);
        Conductor.getInstance().setLocation(lastLocation);
        if(updateUI) {
            ActivityUtils.updateUI(this, mMap, lastLocation);
        }
    }

    private void abrirMenuContextual() {
        drawerLayout = this.findViewById(R.id.drawer_layout);
        navigationView = this.findViewById(R.id.nav_view);
        drawerLayout.openDrawer(Gravity.LEFT);
        navigationView.bringToFront();
        drawerLayout.requestLayout();
    }

    private boolean getMenuContextual(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.servicio) {
            Intent mainIntent = new Intent(this,ServicioActivity.class);
            this.startActivity(mainIntent);
        }
        if (id == R.id.salir) {
            LogoutOperation logoutOperation = new LogoutOperation(this);
            logoutOperation.execute();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void llamar()
    {
        String numero = textViewCelularValor.getText().toString();
        if(numero.equals(""))
        {
            Toast.makeText(this,"No se suministro un n�mero de telefono",Toast.LENGTH_LONG);
        }
        else {
            ActivityUtils.llamar(this, numero);
        }
    }

    private void notificar(String titulo,String valor)
    {
        ActivityUtils.enviarNotificacion(this,titulo,valor, R.drawable.furgoneta);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String value = intent.getStringExtra("value");
            switch (message)
            {
                case AsignacionServicioService.OCULTAR_LAYOUT_SERVICIO:
                    MapsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            servicioLayout.setVisibility(View.GONE);
                        }
                    });
                break;
                case AsignacionServicioService.MOSTRAR_LAYOUT_SERVICIO:
                    MapsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            servicioLayout.setVisibility(View.VISIBLE);
                        }
                    });
                break;
                case AsignacionServicioService.MOSTRAR_NOTIFICACION_SERVICIO:
                    notificar("Nuevo Servicio",value);
                break;
                case AsignacionServicioService.LLENAR_LAYOUT_SERVICIO:
                    try {
                        String[] datos = value.split(AsignacionServicioService.separador);
                        String partida = new String(datos[0].getBytes("ISO-8859-1"), "UTF-8");
                        String destino = new String(datos[1].getBytes("ISO-8859-1"), "UTF-8");
                        textViewIdServicioValor.setText(datos[2]);
                        textViewOrigenValor.setText(URLDecoder.decode(partida,"ISO-8859-1"));
                        textViewDestinoValor.setText(URLDecoder.decode(destino,"ISO-8859-1"));
                        textViewTipoValor.setText(datos[3]);
                        textViewNombreValor.setText(datos[4]);
                        textViewDireccionValor.setText(datos[5]);
                        textViewCelularValor.setText(datos[6]);
                    }
                    catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                break;
                case AsignacionServicioService.CAMBIAR_UBICACION:
                    iniciarUbicacion(false);
                break;
            }
        }
    };

}
