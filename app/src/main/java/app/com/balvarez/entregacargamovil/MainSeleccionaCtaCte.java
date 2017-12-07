package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jmunozv on 07-12-2017.
 */

public class MainSeleccionaCtaCte extends AppCompatActivity implements View.OnClickListener {

    private Activity activity;
    Intent recibir;
    private EditText txtCtaCte;
    private TextView descripcionCtaCte;
    private String ODT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        recibir = getIntent();
        setContentView(R.layout.activity_main_selecciona_ctacte);
        txtCtaCte = (EditText) findViewById(R.id.txtNumeroCtaCte);
        txtCtaCte.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Toast.makeText(activity.getApplicationContext(),
                        "v: "+v+" - keyCode: "+keyCode+" - event: "+event, Toast.LENGTH_LONG).show();
                return false;
            }
        });
        descripcionCtaCte = (TextView) findViewById(R.id.lblDescripcionCuenta);
        Bundle extras = recibir.getExtras();
        if(extras != null){
            ODT = (extras.get("odt") == null)?"":extras.get("odt").toString();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnVolverCtaCte: {
                Intent intento = new Intent(MainSeleccionaCtaCte.this, MainCancelacionOdt.class);
                intento.putExtra("ODT",ODT);
                startActivity(intento);
                finish();
                break;
            }
            case R.id.btnSiguienteCtaCte: {

                break;
            }
            default:
                break;
        }
    }

    public class RealizaTras extends AsyncTask<Void,Void,String> {

        ProgressDialog MensajeProgreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setMessage("Generando Documento...");
            MensajeProgreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {


            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();
            //Intent intent = new Intent(MainCancelacionOdt.this, MainInfoReceptorCarga.class);
            //intent.putExtra("tipoDoc",tipoDoc);
            //startActivity(intent);
        }
    }

}
