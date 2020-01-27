package cl.domito.dmttransfer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.dominio.Conductor;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 3000;
    public static String ANDROID_ID;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.cambiarColorBarra(this);
        this.getSupportActionBar().hide();
        View v = View.inflate(getApplicationContext(), R.layout.activity_splash, null);
        Conductor conductor = Conductor.getInstance();
        conductor.context = SplashScreenActivity.this;
        ANDROID_ID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences pref = getApplicationContext().getSharedPreferences
                ("preferencias", Context.MODE_PRIVATE);
        String idConductor = pref.getString("idUsuario", "");
        String clave = pref.getString("claveUsuario","");
        if(!idConductor.equals("")){
           conductor.id = idConductor;
        }
        setContentView(R.layout.activity_splash);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = null;
                if(!idConductor.equals("") && !clave.equals("")){
                    mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                }
                else{
                    mainIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                }
                SplashScreenActivity.this.startActivity(mainIntent);
                SplashScreenActivity.this.finish();
                handler.removeCallbacksAndMessages(null);
            }
        }, 2000);

    }

    @Override
    protected void onResume() {
        System.out.println();
        super.onResume();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }



}
