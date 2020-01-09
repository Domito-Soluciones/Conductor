package cl.domito.dmttransfer.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.adapter.ReciclerViewProgramadoAdapter;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.activity.utils.StringBuilderUtil;
import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.Utilidades;
import cl.domito.dmttransfer.service.BurbujaService;
import cl.domito.dmttransfer.thread.CambiarEstadoOperation;
import cl.domito.dmttransfer.thread.DatosConductorOperation;
import cl.domito.dmttransfer.thread.EnviarLogOperation;
import cl.domito.dmttransfer.thread.LogoutOperation;
import cl.domito.dmttransfer.thread.NotificationOperation;
import cl.domito.dmttransfer.thread.ObtenerServiciosOperation;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,LocationListener
{

    private GoogleApiClient apiClient;
    private ImageView imageButton;
    private DrawerLayout drawerLayout;
    private NavigationView  navigationView;
    private LocationManager locationManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Conductor conductor;
    private AlertDialog dialog;


    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        conductor = Conductor.getInstance();
        if(conductor.id == null){
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
            finish();
            return;
        }
        imageButton = findViewById(R.id.imageViewMenu);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);

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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        navigationView.setItemIconTintList(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startActivity(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {

            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        //obtenerServicos();  no s donde se llan pero esta wea dejaba la caga
        DatosConductorOperation datosConductorOperation = new DatosConductorOperation(this);
        datosConductorOperation.execute();
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage((FragmentActivity) this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        conductor.googleApiClient = apiClient;
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        conductor.context = MainActivity.this;
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver, new IntentFilter("custom-event-name"));
        if(conductor.volver) {
            abrirMenuContextual();
            conductor.volver = false;
        }
        //
        else {
            refreshContent();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        //LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        //bManager.unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        iniciarUbicacion();
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
        //iniciarUbicacion(true);
    }

    @Override
    public void onProviderDisabled(String provider) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Activar Ubicaci�n");
        dialog.setMessage("El acceso a la ubicacion le permite tener una mejor experiencia,�Desea activar la ubicaci�n?");
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

    private void cambiarEstadoConductor() {
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

    public void iniciarUbicacion()
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
        locationManager = (LocationManager) getApplicationContext().getSystemService(this.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1000, this);

        Location lastLocation =
                LocationServices.FusedLocationApi.getLastLocation(apiClient);
        conductor.location = lastLocation;
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
        if (id == R.id.historico) {
            Intent mainIntent = new Intent(this, HistoricoActivity.class);
            this.startActivity(mainIntent);
        }
        if (id == R.id.configuracion) {
            Intent mainIntent = new Intent(this, ConfiguracionActivity.class);
            this.startActivity(mainIntent);
        }
        if (id == R.id.produccion) {
            Intent mainIntent = new Intent(this, ProduccionActivity.class);
            this.startActivity(mainIntent);
        }
        if (id == R.id.salir) {
            LogoutOperation logoutOperation = new LogoutOperation(this);
            logoutOperation.execute();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                   //showAlertDialog("test","test");
                }
            });
            }
    };


  /* private void showAlertDialog(String message, String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Widget_AppCompat_ActionBar_Solid);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        if (!alert.isShowing()) {
            alert.show();
        }
    }*/
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
                        iniciarUbicacion();
                    }
                    return;
                }
        }
    }

    private void obtenerServicos()
    {
        conductor.context = MainActivity.this;
        ObtenerServiciosOperation obtenerServiciosOperation = new ObtenerServiciosOperation();
        JSONArray jsonArray = null;
        try {
            jsonArray = obtenerServiciosOperation.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        } catch (InterruptedException e) {
            e.printStackTrace();
            EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
            enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
        }
        ArrayList<String> lista = null;
        if(jsonArray != null) {
            lista = new ArrayList();
            String ant = "";
            for (int i = 0; i < jsonArray.length(); i++) {
                try
                {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String servicioId = jsonObject.getString("servicio_id");
                    String servicioFecha = jsonObject.getString("servicio_fecha");
                    Date date = Utilidades.FORMAT.parse(servicioFecha);
                    String servicioHora = jsonObject.getString("servicio_hora");
                    String servicioCliente = jsonObject.getString("servicio_cliente");
                    String servicioEstado = jsonObject.getString("servicio_estado");
                    StringBuilder builder = StringBuilderUtil.getInstance();
                    if (!servicioId.equals(ant)) {
                        builder.append(servicioId).append("%").append(Utilidades.FORMAT.format(date))
                                .append("%").append(servicioHora).append("%").append(servicioCliente)
                                .append("%").append(servicioEstado);
                        lista.add(builder.toString());
                    }
                    ant = servicioId;
                } catch (Exception e) {
                    e.printStackTrace();
                    EnviarLogOperation enviarLogOperation = new EnviarLogOperation();
                    enviarLogOperation.execute(conductor.id,e.getMessage(),e.getStackTrace()[0].getClassName(),Integer.toString(e.getStackTrace()[0].getLineNumber()));
                }
            }
        }
        if(lista != null && lista.size() > 0 ) {
            String[] array = new String[lista.size()];
            array = lista.toArray(array);
            mAdapter = new ReciclerViewProgramadoAdapter(this, array);

            recyclerView.setAdapter(mAdapter);
        }
        else
        {
            mAdapter = new ReciclerViewProgramadoAdapter(this, new String[0]);
            Toast.makeText(this,"No hay servicios programados",Toast.LENGTH_LONG).show();
            recyclerView.setAdapter(mAdapter);
        }
    }

    private void refreshContent() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                obtenerServicos();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        NotificationManager notificationManager = ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.cancelAll();
        super.onDestroy();
    }
}
