package app.com.balvarez.entregacargamovil;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import To.ArchivoOdtPorPatenteTO;
import Util.Globales;
import Util.Utilidades;
import Util.WebServices;

public class MainEntregaCargaMovil extends AppCompatActivity {

    private Button btn_siguiente;
    private Button btn_limpiar;
    private TextView txt_patente;
    private String patente;
    private Activity activity;
    private TextView vers;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String version = "v"+Build.VERSION.INCREMENTAL;

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // PERMISO PARA GUARDAR EN MEMORIA INTERNA
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
        // PERMISO PARA USAR CAMARA
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

            }
        }
        // PERMISO PARA USAR CAMARA
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entrega_carga_movil);
        activity = this;
        vers= (TextView) findViewById(R.id.txtversion);
        vers.setText(Globales.version);
        btn_siguiente = (Button) findViewById(R.id.btnSiguienteInicio);
        btn_siguiente.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                    patente = txt_patente.getText().toString();
                    if(!patente.equals("")){
                        new TraeOdtPorPatente().execute();
                    }else{
                        Toast.makeText(getApplicationContext(), "Debe ingresar una PATENTE !!!",
                                Toast.LENGTH_LONG).show();
                    }

            }
        });
        btn_limpiar = (Button) findViewById(R.id.btnLimpiarInicio);
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
        Boolean bandera = false;
        boolean net = false;

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


            if(util.verificaConexion(getApplicationContext())){
                try {
                    net = true;
                    WebServices WS = new WebServices();
                    listaOdts = WS.OdtsXPatente(patente);
                    if(!listaOdts.isEmpty()){
                        util.creaDirectorio();
                        util.escribirEnArchivo(listaOdts);
                        bandera = true;
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return respStr;
        }

        @Override
        protected void onPostExecute(String result) {
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();
            if(!net){
                Toast.makeText(activity.getApplicationContext(), "Debe tener conexión a INTERNET para cargar datos de la Patente !!!",
                        Toast.LENGTH_LONG).show();
            }else {
                if (bandera) {
                    Intent intent = new Intent(MainEntregaCargaMovil.this, MainResumenPlanilla.class);
                    startActivity(intent);
                    System.gc();
                    finish();
                } else {
                    Toast.makeText(activity.getApplicationContext(), "No se encontraron ODT asociadas. Verifique Patente !",
                            Toast.LENGTH_LONG).show();
                }
            }


        }
    }
}
