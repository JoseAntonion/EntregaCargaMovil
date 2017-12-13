package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import Util.Globales;
import Util.WebServices;

/**
 * Created by jmunozv on 07-12-2017.
 */

public class MainSeleccionaCtaCte extends AppCompatActivity implements View.OnClickListener {

    private Activity activity;
    Intent recibir;
    private EditText txtCtaCte;
    private TextView descripcionCtaCte;
    private String ODT;
    private ImageButton btnBuscaCuentaCte;
    private Button btnVolverCtaCte;
    private Button btnSiguienteCtaCte;
    private TextView lblDescripcionCuenta;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        recibir = getIntent();
        setContentView(R.layout.activity_main_selecciona_ctacte);
        txtCtaCte = (EditText) findViewById(R.id.txtNumeroCtaCte);
        lblDescripcionCuenta = (TextView) findViewById(R.id.lblDescripcionCuenta);
        btnBuscaCuentaCte = (ImageButton) findViewById(R.id.btnBuscaCtaCte);
        btnBuscaCuentaCte.setOnClickListener(this);
        btnVolverCtaCte = (Button) findViewById(R.id.btnVolverCtaCte);
        btnVolverCtaCte.setOnClickListener(this);
        btnSiguienteCtaCte = (Button) findViewById(R.id.btnSiguienteCtaCte);
        btnSiguienteCtaCte.setOnClickListener(this);
        descripcionCtaCte = (TextView) findViewById(R.id.lblDescripcionCuenta);
        Bundle extras = recibir.getExtras();
        if(extras != null){
            ODT = (extras.get("odt") == null)?"":extras.get("odt").toString();
        }
    }


    @Override
    public void onClick(View v) {
        String cuentaDesc = "";
        switch (v.getId()) {
            case R.id.btnVolverCtaCte: {
                Intent intento = new Intent(MainSeleccionaCtaCte.this, MainCancelacionOdt.class);
                intento.putExtra("ODT",Globales.odtPrincipal);
                startActivity(intento);
                finish();
                break;
            }
            case R.id.btnBuscaCtaCte: {
                if(!txtCtaCte.getText().equals("")){
                    new BuscaCuenta(txtCtaCte.getText()).execute();
                }
                break;
            }
            case R.id.btnSiguienteCtaCte: {
                if(lblDescripcionCuenta.getText().equals("No se encontraron registros !!!")){
                    Toast.makeText(activity.getApplicationContext(), "debe ingresar una cuenta valida !!!",
                            Toast.LENGTH_LONG).show();
                }else{
                    new RealizaTraspaso(txtCtaCte.getText()).execute();
                }
                break;
            }
            default:
                break;
        }
    }

    public class RealizaTraspaso extends AsyncTask<Void,Void,String> {

        ProgressDialog MensajeProgreso;
        String CtaCte = "";
        String mensajeOk = "";

        public RealizaTraspaso(Editable text) {
            CtaCte = String.valueOf(text);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setMessage("Realizando traspaso...");
            MensajeProgreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            WebServices ws = new WebServices();
            try {
                mensajeOk = ws.RealizaTraspaso(CtaCte,Globales.odtPrincipal);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();
            if(!mensajeOk.equals("")){
                if(mensajeOk.equals("Realizado correctamente")){
                    Toast.makeText(activity.getApplicationContext(), "Traspaso Realizado correctamente !!!",
                            Toast.LENGTH_LONG).show();
                    Intent intento = new Intent(MainSeleccionaCtaCte.this, MainInfoReceptorCarga.class);
                    intento.putExtra("ODT",Globales.odtPrincipal);
                    intento.putExtra("tipoDoc","CTA");
                    startActivity(intento);
                }else{
                    Toast.makeText(activity.getApplicationContext(), mensajeOk,
                            Toast.LENGTH_LONG).show();
                    Intent intento = new Intent(MainSeleccionaCtaCte.this, MainSeleccionaCtaCte.class);
                    intento.putExtra("ODT",Globales.odtPrincipal);
                    startActivity(intento);
                    finish();
                }
            }

        }
    }


    public class BuscaCuenta extends AsyncTask<Void,Void,String> {

        String CtaCte = "";
        ProgressDialog MensajeProgreso;

        public BuscaCuenta(Editable text) {
            CtaCte = String.valueOf(text);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setMessage("Buscando cuenta...");
            MensajeProgreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            WebServices ws = new WebServices();
            try {
                Globales.cuentaDesc = ws.buscaDatosCtaCte(CtaCte);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();
            if(!Globales.cuentaDesc.equals("")){
                lblDescripcionCuenta.setText(Globales.cuentaDesc);
            }else{
                lblDescripcionCuenta.setText("No se encontraron registros !!!");
            }


        }
    }

    public String buscaCuenta() throws IOException, JSONException {
        WebServices ws = new WebServices();
        return ws.buscaDatosCtaCte(String.valueOf(txtCtaCte.getText()));
    }


}
