package cl.domito.conductor.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import cl.domito.conductor.R;
import cl.domito.conductor.activity.utils.ActivityUtils;
import cl.domito.conductor.dominio.Conductor;

public class ConfiguracionActivity extends AppCompatActivity {

    private ImageView imageViewAtras;
    private RadioButton radioButtonGMaps;
    private RadioButton radioButtonWaze;
    private Conductor conductor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        imageViewAtras = findViewById(R.id.imageViewAtras);
        radioButtonGMaps = findViewById(R.id.radioButton2);
        radioButtonWaze = findViewById(R.id.radioButton);

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
        SharedPreferences pref = getApplicationContext().getSharedPreferences
                ("preferencias", Context.MODE_PRIVATE);
        ActivityUtils.guardarSharedPreferences(pref,"nav","google");
        Toast.makeText(this,"Navegación Google Maps seleccionada",Toast.LENGTH_LONG).show();
    }

    private void seleccionarWaze() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences
            ("preferencias", Context.MODE_PRIVATE);
        ActivityUtils.guardarSharedPreferences(pref,"nav","waze");
        Toast.makeText(this,"Navegación Waze seleccionada",Toast.LENGTH_LONG).show();
}

    private void volver()
    {
        conductor.volver = true;
        this.finish();
    }


}
