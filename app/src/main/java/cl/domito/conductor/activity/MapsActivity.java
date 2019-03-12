package cl.domito.conductor.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.adapter.ReciclerViewPasajeroAdapter;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.http.Utilidades;
import cl.domito.conductor.service.AsignacionServicioService;
import cl.domito.conductor.thread.CambiarEstadoOperation;
import cl.domito.conductor.thread.CambiarUbicacionOperation;
import cl.domito.conductor.thread.DatosConductorOperation;
import cl.domito.conductor.thread.DesAsignarServicioOperation;
import cl.domito.conductor.thread.IniciarServicioOperation;
import cl.domito.conductor.thread.InsertarNavegacionOperation;
import cl.domito.conductor.thread.LogoutOperation;
import cl.domito.conductor.thread.NotificationOperation;
import cl.domito.conductor.thread.RealizarServicioOperation;


public class MapsActivity extends FragmentActivity  implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,LocationListener
{

    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private SupportMapFragment mapFragment;
    private ImageButton buttonNavegar;
    private ImageView imageButton;
    private DrawerLayout drawerLayout;
    private NavigationView  navigationView;
    private LocationManager locationManager;
    private ConstraintLayout constraintLayoutPasajero;
    private ConstraintLayout constraintLayoutEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imageButton = findViewById(R.id.imageViewMenu);
        navigationView = findViewById(R.id.nav_view);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        constraintLayoutEstado = findViewById(R.id.constrainLayoutEstado);
        constraintLayoutPasajero = findViewById(R.id.constraitLayoutPasajero);

        Conductor.getInstance().setContext(getApplicationContext());

        if(savedInstanceState!=null)
        {
            ArrayList<String> lista = new ArrayList();
            Conductor conductor = Conductor.getInstance();
            int estadoVisible = Integer.parseInt(savedInstanceState.getString("clEstado"));
            int pasajeroVisible = Integer.parseInt(savedInstanceState.getString("clEstado"));
            constraintLayoutEstado.setVisibility(estadoVisible);
            constraintLayoutPasajero.setVisibility(pasajeroVisible);
        }

        DatosConductorOperation datosConductorOperation = new DatosConductorOperation(this);
        datosConductorOperation.execute();

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
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver, new IntentFilter("custom-event-name"));
        super.onResume();
        Conductor conductor = Conductor.getInstance();
        if(conductor.isServicioAceptado())
        {
            IniciarServicioOperation realizarServicioOperation = new IniciarServicioOperation(this);
            realizarServicioOperation.execute(mMap,conductor.getServicioActual());
            //conductor.setServicioAceptado(false);
        }
        //if(mMap != null) {
        //mMap.clear();
        //}
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int clEstado = constraintLayoutEstado.getVisibility();
        int clPasajero = constraintLayoutPasajero.getVisibility();
        outState.putString("clEstadoVisible", clEstado+"");
        outState.putString("clPasajeroVisible", clPasajero+"");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println("");
    }



    @Override
    protected void onPause() {
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        bManager.unregisterReceiver(broadcastReceiver);
        super.onPause();
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
        Log.e("ERROR", "Se ha interrumpido la conexión con Google Play Services");
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    @Override
    public void onProviderEnabled(String provider) {
        //iniciarUbicacion(true);
    }

    @Override
    public void onProviderDisabled(String provider) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Activar Ubicación");
        dialog.setMessage("El acceso a la ubicacion le permite tener una mejor experiencia,¿Desea activar la ubicación?");
        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                abrirVentanaUbicacion();
            }
        });
        dialog.setNegativeButton(android.R.string.no, null);
        AlertDialog alertDialog = dialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this.getResources().getColor(R.color.colorPrimary));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this.getResources().getColor(R.color.colorPrimary));
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

        Conductor.getInstance().setGoogleApiClient(apiClient);
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

    public void iniciarUbicacion(boolean updateUI)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},100);
            return;
        }
        else
        {

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
        if (id == R.id.historico) {
            Intent mainIntent = new Intent(this,HistoricoActivity.class);
            this.startActivity(mainIntent);
        }
        if (id == R.id.configuracion) {
            Intent mainIntent = new Intent(this,ConfiguracionActivity.class);
            this.startActivity(mainIntent);
        }
        if (id == R.id.produccion) {
            Intent mainIntent = new Intent(this,ProduccionActivity.class);
            this.startActivity(mainIntent);
        }
        if (id == R.id.descuento) {
            Intent mainIntent = new Intent(this,DescuentoActivity.class);
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
        String numero  = "";
        if(numero.equals(""))
        {
            Toast.makeText(this,"No se suministro un número de telefono",Toast.LENGTH_LONG);
        }
        else {
            ActivityUtils.llamar(this, numero);
        }
    }

    private void notificar(String titulo,String valor)
    {
        NotificationOperation notificationOperation = new NotificationOperation(this);
        notificationOperation.execute();
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
                            //servicioLayout.setVisibility(View.GONE);
                        }
                    });
                break;
                case AsignacionServicioService.MOSTRAR_LAYOUT_SERVICIO:
                    MapsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                           // servicioLayout.setVisibility(View.VISIBLE);
                        }
                    });
                break;
                case AsignacionServicioService.MOSTRAR_NOTIFICACION_SERVICIO:
                    notificar("Nuevo Servicio",null);
                break;
                case AsignacionServicioService.LLENAR_LAYOUT_SERVICIO:
                break;
                case AsignacionServicioService.CAMBIAR_UBICACION:
                    iniciarUbicacion(false);
                break;
                case AsignacionServicioService.CALCULAR_DISTACIA:

                    break;
            }
        }
    };

    private void abrirVentanaUbicacion() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
                case 100: {

                    if (grantResults.length == 0
                            || grantResults[0] !=
                            PackageManager.PERMISSION_GRANTED) {

                        // aqui no
                    } else {
                        iniciarUbicacion(true);
                    }
                    return;
                }
                case 101:{
                    if (grantResults.length == 0
                            || grantResults[0] !=
                            PackageManager.PERMISSION_GRANTED) {

                        // aqui no
                    } else {
                        llamar();
                    }
                    return;
                }
        }
    }

}
