package app.com.balvarez.entregacargamovil;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import To.ArchivoOdtPorPatenteTO;
import To.CiudadTO;
import To.ComunaTO;
import Util.Globales;
import Util.Utilidades;
import Util.WebServices;

public class MainEntregaCargaMovil extends AppCompatActivity {

    private static final int PETICION_PERMISO_LOCALIZACION = 1;
    private Button btn_siguiente;
    private Button btn_limpiar;
    private TextView txt_patente;
    private String patente;
    private Activity activity;
    private TextView vers;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MY_ACCESS_LOCATION_EXTRA_COMMANDS = 1;
    private Handler mHandler;
    private static final int ACTIVAR_GPS = 1;
    private AlertDialog alert;
    LocationManager mLocationManager;

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
        //LATI LONG
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS},
                        MY_ACCESS_LOCATION_EXTRA_COMMANDS);

            }
        }
        // PERMISO PARA USAR CONTACTOS
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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            }
        }
        int permissionCheck3 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

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
                // PRUEBAS IMPRESION ----------------------------------------------------------------
                /*Utilidades util = new Utilidades();
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String imei = telephonyManager.getDeviceId();

                //WebServices ws = new WebServices();
                Globales.Impresora = "00:01:90:C2:C4:C6";
                try {
                    //ws.retornaImpresoraPrueba(imei);
                    if(util.ConectarEpsonPrueba(this.getApplicationContext())){
                        //util.BoletaPrueba(this);
                        util.FacturaPrueba(this);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (EposException e) {
                    e.printStackTrace();
                } catch (WriterException e) {
                    e.printStackTrace();
                }*/
                // ----------------------------------------------------------------------------------
                // PRUEBAS -----------------------------------------------
                /*Intent intent = new Intent(MainEntregaCargaMovil.this, MainInfoReceptorCarga.class);
                //Intent intent = new Intent(MainEntregaCargaMovil.this, MainCancelacionOdt.class);
                startActivity(intent);
                System.gc();
                finish();*/
                //--------------------------------------------------------
            }
        });
        txt_patente = (TextView) findViewById(R.id.txtPatente);
        txt_patente = (TextView) findViewById(R.id.txtPatente);
        patente = txt_patente.getText().toString();
        Globales.totalValoresODT = 0;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //populateAutoComplete();
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //populateAutoComplete();
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //populateAutoComplete();
            }
        }
        if (requestCode == MY_ACCESS_LOCATION_EXTRA_COMMANDS) {
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
        Boolean isComuna = false;
        Boolean isCiudad = false;
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
            ArrayList<CiudadTO> listaCiudades = new ArrayList<>();
            ArrayList<ComunaTO> listaComunas = new ArrayList<>();
            Utilidades util = new Utilidades();

            //PARA IMRESION INICIAL DE PRUEBA
            WebServices webservices = new WebServices();
            Utilidades utilidades = new Utilidades();
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            try {
                webservices.retornaImpresoraPrueba(imei);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            ////////////////////////////////////

            if(util.verificaConexion(getApplicationContext())){
                try {
                    net = true;
                    WebServices WS = new WebServices();
                    listaOdts = WS.OdtsXPatente(patente);
                    listaCiudades = WS.TraeCiudades();
                    listaComunas = WS.TraeComunas();
                    util.creaDirectorioArchivos();

                    if(!listaOdts.isEmpty()){
                        util.escribirEnArchivo(listaOdts,"ODTS");
                        bandera = true;
                        if(!listaCiudades.isEmpty()){
                            util.escribirEnArchivo(listaCiudades,"CIUDADES");
                            isCiudad = true;
                            if(!listaComunas.isEmpty()){
                                util.escribirEnArchivo(listaComunas,"COMUNAS");
                                isComuna = true;
                            }
                        }
                    }


                        if (util.ConectarEpsonPrueba(activity.getApplicationContext())) {
                            try {
                                util.impresionPrueba(activity);
                                //util.ImprimeOffLine(activity,"50000005774");
                            } catch (Exception e) {
                                Toast.makeText(activity.getApplicationContext(),
                                        "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
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
                /*if(!isComuna)
                    Toast.makeText(activity.getApplicationContext(), "No se cargaron COMUNAS !",
                            Toast.LENGTH_SHORT).show();
                if(!isCiudad)
                    Toast.makeText(activity.getApplicationContext(), "No se cargaron CIUDADES !",
                            Toast.LENGTH_SHORT).show();*/
                if(bandera) {
                    Intent intent = new Intent(MainEntregaCargaMovil.this, MainResumenPlanilla.class);
                    //Intent intent = new Intent(MainEntregaCargaMovil.this, MainCancelacionOdt.class);
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

    private boolean VerificarGPS(){
        boolean retorna = false;
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                    .setCancelable(false)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            mHandler.sendEmptyMessage(ACTIVAR_GPS);
                            dialog.cancel();
                        }
                    });
            alert = builder.create();
            alert.show();
        }else{
            retorna = true;
        }
        return retorna;
    }

    private Location comenzarLocalizacion2() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null && l.getLatitude() != 0.0) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
