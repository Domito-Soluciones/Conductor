package cl.domito.dmttransfer.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import cl.domito.dmttransfer.R;
import cl.domito.dmttransfer.activity.utils.ActivityUtils;
import cl.domito.dmttransfer.dominio.Conductor;

public class ConfiguracionActivity extends AppCompatActivity {

    private ImageView imageViewAtras;
    private RadioButton radioButtonGMaps;
    private RadioButton radioButtonWaze;
    private Conductor conductor;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.cambiarColorBarra(this);
        setContentView(R.layout.activity_configuracion);
        imageViewAtras = findViewById(R.id.imageViewAtras);
        radioButtonGMaps = findViewById(R.id.radioButton2);
        radioButtonWaze = findViewById(R.id.radioButton);

        pref = getApplicationContext().getSharedPreferences
                ("preferencias", Context.MODE_PRIVATE);
        String tipoNav = pref.getString("nav", "");
        if(tipoNav.equals("google")){
            radioButtonGMaps.setChecked(true);
        }
        else if(tipoNav.equals("") || tipoNav.equals("waze")){
            radioButtonWaze.setChecked(true);
        }

        conductor = Conductor.getInstance();

        conductor.context = ConfiguracionActivity.this;

        imageViewAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volver();
            }
        });

        radioButtonGMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarGMaps();
            }
        });




        radioButtonWaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarWaze();
            }
        });

    }

    @Override
    public void onBackPressed() {
        conductor.volver = true;
        super.onBackPressed();
    }

    private void seleccionarGMaps() {
        ActivityUtils.guardarSharedPreferences(pref,"nav","google");
        Toast.makeText(this,"Navegación Google Maps seleccionada",Toast.LENGTH_LONG).show();
    }

    private void seleccionarWaze() {
        ActivityUtils.guardarSharedPreferences(pref,"nav","waze");
        Toast.makeText(this,"Navegación Waze seleccionada",Toast.LENGTH_LONG).show();
}

    private void volver()
    {
        conductor.volver = true;
        this.finish();
    }


}
