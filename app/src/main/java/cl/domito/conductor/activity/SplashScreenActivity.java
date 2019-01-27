package cl.domito.conductor.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;

import cl.domito.conductor.R;
import cl.domito.conductor.dominio.Conductor;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 3000;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getSupportActionBar().hide();
        View v = View.inflate(getApplicationContext(), R.layout.activity_splash, null);
        setContentView(R.layout.activity_splash);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getApplicationContext().getSharedPreferences
                        ("preferencias", Context.MODE_PRIVATE);
                String idConductor = pref.getString("idUsuario",
                        "");
                if(!idConductor.equals(""))
                {
                    Conductor.getInstance().setNick(idConductor);
                    Conductor.getInstance().setConectado(true);
                    Intent mainIntent = new Intent(SplashScreenActivity.this,MapsActivity.class);
                    SplashScreenActivity.this.startActivity(mainIntent);
                    SplashScreenActivity.this.finish();
                    handler.removeCallbacksAndMessages(null);
                }
                else {
                    Intent mainIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    SplashScreenActivity.this.startActivity(mainIntent);
                    SplashScreenActivity.this.finish();
                    handler.removeCallbacksAndMessages(null);
                }
            }
        }, 2000);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }



}
