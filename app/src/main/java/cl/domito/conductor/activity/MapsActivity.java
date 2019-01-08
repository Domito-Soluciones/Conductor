package cl.domito.conductor.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;
import cl.domito.conductor.thread.DatosConductorOperation;
import cl.domito.conductor.thread.DesAsignarServicioOperation;
import cl.domito.conductor.thread.RealizarServicioOperation;


public class MapsActivity extends FragmentActivity  implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,LocationListener
{

    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private MapsActivity mapsActivity;
    private SupportMapFragment mapFragment;
    private View servicioLayout;
    private TextView textViewNServicio;
    private TextView textViewOrigen;
    private TextView textViewDestino;
    private TextView textViewTipo;
    private TextView textViewNombre;
    private TextView textViewDireccion;
    private TextView textViewCelular;
    private Button button2;
    private Button button4;
    private Button button5;
    private ImageView imageButton;
    private DrawerLayout drawerLayout;
    private NavigationView  navigationView;
    private LocationManager locationManager;

    @Override
    protected void onResume() {
        super.onResume();
        if(mMap != null) {
            mMap.clear();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        servicioLayout = findViewById(R.id.relativeLayout5);
        textViewNServicio = findViewById(R.id.textView4);
        textViewOrigen = findViewById(R.id.textView7);
        textViewDestino = findViewById(R.id.textView10);
        textViewTipo = findViewById(R.id.textView12);
        textViewNombre = findViewById(R.id.textView14);
        textViewDireccion = findViewById(R.id.textView16);
        textViewCelular = findViewById(R.id.textView18);
        button2 = findViewById(R.id.button2);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        imageButton = findViewById(R.id.imageView2);
        navigationView = findViewById(R.id.nav_view);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        DatosConductorOperation datosConductorOperation = new DatosConductorOperation(this);
        datosConductorOperation.execute();

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarServicio();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desasignarServicio();
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
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

        ActivityUtils.updateUI(this,mMap,lastLocation);
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            ActivityUtils.eliminarSharedPreferences(getSharedPreferences("preferencias",Context.MODE_PRIVATE),"idUsuario");
            Conductor.getInstance().setActivo(false);
            Intent mainIntent = new Intent(this,LoginActivity.class);
            this.startActivity(mainIntent);
            this.finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void llamar()
    {
        ActivityUtils.llamar(this,textViewCelular.getText().toString());
    }

}
