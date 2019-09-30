package cl.domito.dmttransfer.thread;

import android.os.AsyncTask;

import cl.domito.dmttransfer.dominio.Conductor;
import cl.domito.dmttransfer.http.RequestConductor;

public class EnviarLogOperation  extends AsyncTask<String,Void,Void>{


        @Override
        protected Void doInBackground(String... strings) {
            Conductor conductor = Conductor.getInstance();
            RequestConductor.enviarLogError(strings[0],strings[1],strings[2],strings[3]);
            return null;
        }
    }


