package app.com.balvarez.entregacargamovil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import To.ArchivoOdtPorPatenteTO;
import Util.Utilidades;
import Util.WebServices;

public class MainEntregaCargaMovil extends AppCompatActivity {

    private Button btn_siguiente;
    private Button btn_limpiar;
    private TextView txt_patente;
    private String patente;
    private Activity activity;
    private Utilidades util;

    // VARIABLES PARA VALIDACION DE CAMPOS
    private static final int NO_PATENTE = 0;
    private static final int NO_CLAVE = 1;
    private static final int USUARIO_VALIDA = 2;
    private static final int NO_CARGA_DE_DATOS = 3;
    private static final int NO_INTERNET = 4;
    private static final int NO_VERSION = 5;
    private String mensaje = "";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entrega_carga_movil);
        activity = this;
        btn_siguiente = (Button) findViewById(R.id.btnSiguiente);
        btn_siguiente.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                /*if(util.verificaConexion(getApplicationContext())){*/
                    txt_patente = (TextView) findViewById(R.id.txtPatente);
                    patente = txt_patente.getText().toString();
                    new TraeOdtPorPatente().execute();
                    /*Intent intent = new Intent(MainEntregaCargaMovil.this, MainResumenPlanilla.class);
                    startActivity(intent);
                    System.gc();
                    finish();*/
                /*}else{
                    Toast.makeText(MainEntregaCargaMovil.this,"Sin conexion a Internet. No se cargaran datos", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainEntregaCargaMovil.this, MainEntregaCargaMovil.class);
                    startActivity(intent);
                    System.gc();
                    finish();
                }*/
            }
        });
        btn_limpiar = (Button) findViewById(R.id.btnLimpiar);
        btn_limpiar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                txt_patente.setText("");
            }
        });
        txt_patente = (TextView) findViewById(R.id.txtPatente);
        patente = txt_patente.getText().toString();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //populateAutoComplete();
            }
        }
    }

/*    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
    }*/

    private boolean mayRequestContacts() {
        return true;
    }

    public class TraeOdtPorPatente extends AsyncTask<Void,Void,String>{

        //String respStr = "";
        //long tiempo = 0;
        ProgressDialog MensajeProgreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setMessage("Consultando Patente...");
            MensajeProgreso.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            String respStr = null;
            ArrayList<ArchivoOdtPorPatenteTO> listaOdts = new ArrayList<>();
            Utilidades util = new Utilidades();

            try {
                WebServices WS = new WebServices();
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                //String imei = telephonyManager.getDeviceId();
                listaOdts = WS.OdtsXPatente(patente);
                //listaOdts = new ArrayList<>();
                util.creaDirectorio();
                util.escribirEnArchivo(listaOdts);
            }
            catch (Exception e){
                e.printStackTrace();
            }


        return respStr;
        }



        @Override
        protected void onPostExecute(String result) {
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();

            Intent intent = new Intent(MainEntregaCargaMovil.this, MainResumenPlanilla.class);
            startActivity(intent);
            System.gc();
            finish();

        }
    }

    // MANEJO DE ERRORES
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == NO_PATENTE) {
                Toast.makeText(getApplicationContext(), "Favor Ingrese Patente",
                        Toast.LENGTH_LONG).show();
            }

            if (msg.what == NO_CLAVE) {
                Toast.makeText(activity.getApplicationContext(),
                        "Favor Ingrese una Clave", Toast.LENGTH_LONG).show();
            }
            if (msg.what == USUARIO_VALIDA) {
                Toast.makeText(activity.getApplicationContext(), mensaje,
                        Toast.LENGTH_LONG).show();
            }
            if (msg.what == NO_INTERNET) {
                Toast.makeText(getApplicationContext(),
                        "Favor revisar conexion a internet", Toast.LENGTH_LONG)
                        .show();
            }
            if (msg.what == NO_VERSION) {
                Toast.makeText(getApplicationContext(),
                        "Debe actualizar la version de la aplicacion", Toast.LENGTH_LONG)
                        .show();
            }

        }
    };

}
