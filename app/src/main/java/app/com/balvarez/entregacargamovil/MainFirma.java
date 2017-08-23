package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.coatedmoose.customviews.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import To.ValidaTO;
import Util.Globales;
import Util.Utilidades;
import Util.WebServices;

public class MainFirma extends AppCompatActivity implements View.OnClickListener {

    private SignatureView signature;
    private Button btn_finalizar;
    private Button btn_limpiar;
    private String encodedImage2;
    private Activity activity;
    Intent recibir;
    private String ODT;
    private Utilidades util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        recibir = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_firma);
        btn_finalizar = (Button) findViewById(R.id.btnFinalizar);
        btn_finalizar.setOnClickListener(this);
        btn_limpiar = (Button) findViewById(R.id.btnLimpiar);
        btn_limpiar.setOnClickListener(this);
        signature = (SignatureView) this.findViewById(R.id.signatureView4);
        ODT = recibir.getStringExtra("odt");
        util = new Utilidades();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnFinalizar: {
                new CapturaImagen().execute();
            }
            case R.id.btnLimpiar: {
                signature.clearSignature();
            }
            default:
                break;
        }
    }

    public class CapturaImagen extends AsyncTask<Void,Void,String> {

        Bitmap imagen = signature.getImage();
        File sd = Environment.getExternalStorageDirectory();
        File fichero = new File(sd, "Firma.jpg");
        WebServices ws = new WebServices();
        ValidaTO resp = new ValidaTO();
        ValidaTO resp2 = new ValidaTO();
        ProgressDialog MensajeProgreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setMessage("Ingresando Firma...");
            MensajeProgreso.show();

        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                if (sd.canWrite()) {
                    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                    imagen.compress(Bitmap.CompressFormat.PNG, 90, arrayOutputStream);
                    Globales.Imagen = arrayOutputStream.toByteArray();
                    encodedImage2 = Base64.encodeToString(Globales.Imagen,Base64.DEFAULT);
                    fichero.createNewFile();
                    OutputStream os = new FileOutputStream(fichero);
                    imagen.compress(Bitmap.CompressFormat.JPEG, 90, os);
                    os.close();

                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String imei = telephonyManager.getDeviceId();
                    resp = ws.GrabaImagen(imei,encodedImage2,"ACT",imei,imei);
                    resp2 = ws.CambiaEstadoODT(ODT,"99",imei,"MainFirma", Globales.version,"EntregaCargaMovil",imei);
                    util.cambiaEstadoOdtArchivo(ODT);

                    if(resp.getValida().equals("1")){
                        Toast.makeText(activity.getApplicationContext(),
                                resp.getMensaje(), Toast.LENGTH_LONG).show();
                    }else if(resp.getValida().equals("0")){
                        Toast.makeText(activity.getApplicationContext(),
                                "Problemas al guardar Imagen: "+resp.getMensaje(), Toast.LENGTH_LONG).show();
                    }



                    //Obtener Geo-Referencia
                /*Utilidades utilidades = new Utilidades();
                if (utilidades.verificaConexion(getApplicationContext())) {
                    if(VerificarGPS()){
                        //comenzarLocalizacion();
                        GPSTracker gps = new GPSTracker(activity);
                        if(gps.canGetLocation())
                        {
                            latitud += gps.getLatitude();
                            longitud += gps.getLongitude();
                        }
                        new Valida().execute();
                    }

                }else{
                    mHandler.sendEmptyMessage(NO_INTERNET);
                }*/
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();
            if(resp.getValida().equals("0")){
                Toast.makeText(activity.getApplicationContext(),
                        "Error al guardar - ws.GrabaImagen.", Toast.LENGTH_LONG).show();
                MensajeFinRepartoINCORRECTO();
            }else if(resp.getValida().equals("1")){
                Toast.makeText(activity.getApplicationContext(),
                        "Imagen Guardada", Toast.LENGTH_LONG).show();
                if(resp2.getValida().equals("1")){
                    Toast.makeText(activity.getApplicationContext(),
                            resp2.getMensaje(), Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(activity.getApplicationContext(),
                            resp2.getMensaje(), Toast.LENGTH_LONG).show();
                MensajeFinRepartoCORRECTO();
            }
        }
    }

    private AlertDialog MensajeFinRepartoCORRECTO() {
        final String DEFAULT_TITLE = "Entrega Carga Movil";
        final String DEFAULT_MESSAGE = "ODT Entregada Correctamente";
        final String DEFAULT_YES = "Aceptar";


        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(DEFAULT_TITLE);
        downloadDialog.setMessage(DEFAULT_MESSAGE);
        downloadDialog.setPositiveButton(DEFAULT_YES,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainFirma.this, MainODT.class);
                        startActivity(intent);
                        finish();
                        System.gc();
                        finish();
                    }
                });

        return downloadDialog.show();
    }

    private AlertDialog MensajeFinRepartoINCORRECTO() {
        final String DEFAULT_TITLE = "Entrega Carga Movil";
        final String DEFAULT_MESSAGE = "Problemas en el proceso - Captura de Firma";
        final String DEFAULT_YES = "Aceptar";


        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(DEFAULT_TITLE);
        downloadDialog.setMessage(DEFAULT_MESSAGE);
        downloadDialog.setPositiveButton(DEFAULT_YES,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainFirma.this, MainODT.class);
                        startActivity(intent);
                        finish();
                        System.gc();
                        finish();
                    }
                });

        return downloadDialog.show();
    }

    }
