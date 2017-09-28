package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import To.TipoReingresoTO;
import To.ValidaTO;
import Util.GPSTraker;
import Util.Globales;
import Util.Utilidades;
import Util.WebServices;

public class MainMotivoNoEntrega extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String LOGTAG = "";
    private static final int PETICION_PERMISO_LOCALIZACION = 1;
    private Button btn_finalizar;
    private Button btn_limpiar;
    private Button btn_tomarFoto;
    private Spinner spn_motivo;
    private ArrayAdapter spinner_adapter;
    private TextView txtNombreFoto;
    private Intent recibir;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String ODT = "";
    private String NombreFoto = "fotoPrueba.jpg";
    private String encodedImage2;
    private Activity activity;
    private Utilidades util;
    private Bitmap bMap;
    private int RESULT_LOAD_IMG = 0;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String imgDecodableString;
    private String userChoosenTask;
    private static final int ACTIVAR_GPS = 1;
    private Handler mHandler;
    private AlertDialog alert;
    private String codigoReingreso = "";
    private double latitud;
    private double longitud;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        util = new Utilidades();
        activity = this;
        recibir = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_motivo_no_entrega);
        ODT = recibir.getStringExtra("odt");
        btn_finalizar = (Button) findViewById(R.id.btnFinalizarNoEntrega);
        btn_finalizar.setOnClickListener(this);
        btn_limpiar = (Button) findViewById(R.id.btnLimpiarNoEntrega);
        btn_limpiar.setOnClickListener(this);
        btn_tomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        btn_tomarFoto.setOnClickListener(this);
        spn_motivo = (Spinner) findViewById(R.id.spnMotivo);
        spn_motivo.setAdapter(creaSpinnerReingresos());
        spn_motivo.setOnItemSelectedListener(this);
        txtNombreFoto = (TextView) findViewById(R.id.txtNombreFoto);
        txtNombreFoto.setVisibility(View.INVISIBLE);
        ActivityCompat.requestPermissions(MainMotivoNoEntrega.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},123);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnFinalizarNoEntrega: {
                // GEO-REFERENCIA
                if (VerificarGPS()) {
                    GPSTraker gps = new GPSTraker(activity.getApplicationContext());
                    Location l = gps.getLocation();
                    if (l != null) {
                        Globales.latitud = l.getLatitude();
                        Globales.longitud = l.getLongitude();
                    }
                }
                if (!codigoReingreso.isEmpty() && !codigoReingreso.equals("99")) {
                    if (txtNombreFoto.getVisibility() != View.INVISIBLE) {
                        new CapturaImagen().execute();
                    } else {
                        Toast.makeText(activity.getApplicationContext(),
                                "Debe tomar FOTOGRAFIA !!!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(activity.getApplicationContext(),
                            "Debe ingresar un MOTIVO !!!", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.btnTomarFoto: {
                Intent foto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (foto.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(foto, REQUEST_IMAGE_CAPTURE);
                }
                break;
            }
            case R.id.btnLimpiarNoEntrega: {
                txtNombreFoto.setVisibility(View.INVISIBLE);
                spn_motivo.setAdapter(creaSpinnerReingresos());
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Comprovamos que la foto se a realizado
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            encodedImage2 = android.util.Base64.encodeToString(byteArray, Base64.NO_WRAP);
            //ListaFotosTO.lista.add(byteArray);

            //bMap = BitmapFactory.decodeFile(Globales.rutaArchivos2 + "/Fotos/" + NombreFoto);
            txtNombreFoto.setText(NombreFoto);
            txtNombreFoto.setVisibility(View.VISIBLE);
            //spn_motivo.setAdapter(creaSpinnerReingresos());
        }

    }

    // Asigna el valor del indice seleccionado en el spinner a una variable
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if ((position != 0) && (id != 0)) {
            Object item = parent.getItemAtPosition(position);
            codigoReingreso = ((TipoReingresoTO) item).getIndice();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class CapturaImagen extends AsyncTask<Void, Void, String> {

        File sd = Environment.getExternalStorageDirectory();
        WebServices ws = new WebServices();
        ValidaTO resp = new ValidaTO();
        ValidaTO resp2 = new ValidaTO();
        ProgressDialog MensajeProgreso;
        Utilidades util = new Utilidades();
        boolean guardoImagen = false;
        boolean reingreso = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setMessage("Ingresando Datos...");
            MensajeProgreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                if (sd.canWrite()) {
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String imei = telephonyManager.getDeviceId();
                    resp = ws.GrabaImagen(imei,encodedImage2,"ACT",imei,imei,ODT,"REINGRESO");
                    resp2 = ws.GrabaReingreso(util.TraePlanillaDeODT(ODT),ODT,util.leeCantidadBultosODT(ODT),"T",codigoReingreso);
                    util.cambiaEstadoOdtArchivoREINGRESO(ODT);
                    if(resp != null) {
                        if (resp.getValida().equals("1")) {
                            guardoImagen = true;
                        }
                    }
                    if(resp2 != null){
                        if (resp2.getValida().equals("1")) {
                            reingreso = true;
                        }
                    }
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
            if(guardoImagen && reingreso){
                Toast.makeText(activity.getApplicationContext(),
                        "Imagen Guardada", Toast.LENGTH_LONG).show();
                Toast.makeText(activity.getApplicationContext(),
                        "Observacion insertada en Reingreso", Toast.LENGTH_LONG).show();
                if (MensajeProgreso.isShowing())
                    MensajeProgreso.dismiss();
                MensajeFinRepartoCORRECTO();
            }else if(!guardoImagen && reingreso){
                Toast.makeText(activity.getApplicationContext(),
                        "La firma no fue guardada: "+resp.getMensaje(), Toast.LENGTH_LONG).show();
                Toast.makeText(activity.getApplicationContext(),
                        "Observacion insertada en Reingreso", Toast.LENGTH_LONG).show();
                if (MensajeProgreso.isShowing())
                    MensajeProgreso.dismiss();
                MensajeFinRepartoCORRECTO();
            }else if(guardoImagen && !reingreso) {
                Toast.makeText(activity.getApplicationContext(),
                        "Firma Guardada", Toast.LENGTH_LONG).show();
                Toast.makeText(activity.getApplicationContext(),
                        "No se guarda registro de Reingreso: "+resp2.getMensaje(), Toast.LENGTH_LONG).show();
                if (MensajeProgreso.isShowing())
                    MensajeProgreso.dismiss();
                MensajeFinRepartoINCORRECTO(resp2.getMensaje());
            }else if(!guardoImagen && !reingreso){
                Toast.makeText(activity.getApplicationContext(),
                        "La firma no fue guardada: "+resp.getMensaje(), Toast.LENGTH_LONG).show();
                Toast.makeText(activity.getApplicationContext(),
                        "No se guarda registro de Reingreso: "+resp2.getMensaje(), Toast.LENGTH_LONG).show();
                if (MensajeProgreso.isShowing())
                    MensajeProgreso.dismiss();
                MensajeFinRepartoINCORRECTO(resp.getMensaje()+" - "+resp2.getMensaje());
            }
        }
    }

    private AlertDialog MensajeFinRepartoCORRECTO() {
        final String DEFAULT_TITLE = "Entrega Carga Movil";
        final String DEFAULT_MESSAGE = "Datos Guardados";
        final String DEFAULT_YES = "Aceptar";


        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(DEFAULT_TITLE);
        downloadDialog.setMessage(DEFAULT_MESSAGE);
        downloadDialog.setPositiveButton(DEFAULT_YES,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainMotivoNoEntrega.this, MainODT.class);
                        startActivity(intent);
                        finish();
                        System.gc();
                        finish();
                    }
                });

        return downloadDialog.show();
    }

    private AlertDialog MensajeFinRepartoINCORRECTO(String mensaje) {
        final String DEFAULT_TITLE = "Entrega Carga Movil";
        final String DEFAULT_MESSAGE = "Problemas en el proceso - Motivo No Entrega: "+mensaje;
        final String DEFAULT_YES = "Aceptar";


        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(DEFAULT_TITLE);
        downloadDialog.setMessage(DEFAULT_MESSAGE);
        downloadDialog.setPositiveButton(DEFAULT_YES,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainMotivoNoEntrega.this, MainODT.class);
                        startActivity(intent);
                        finish();
                        System.gc();
                        finish();
                    }
                });

        return downloadDialog.show();
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

    public ArrayAdapter<TipoReingresoTO> creaSpinnerReingresos(){

        //Creamos la lista
        ArrayList<TipoReingresoTO> reingresos = new ArrayList<>();
        //La poblamos
        reingresos.add(new TipoReingresoTO("99", "Seleccione Motivo"));
        reingresos.add(new TipoReingresoTO("0", "SIN MORADORES"));
        reingresos.add(new TipoReingresoTO("10", "CLIENTE SIN DINERO"));
        reingresos.add(new TipoReingresoTO("2", "FUERA DE PLAZO"));
        reingresos.add(new TipoReingresoTO("3", "SE CAMBIO DE DOMICILIO"));
        reingresos.add(new TipoReingresoTO("5", "NO RESPONDEN"));
        reingresos.add(new TipoReingresoTO("6", "NO RECIBE"));
        reingresos.add(new TipoReingresoTO("7", "FALTA ORDEN DE COMPRA"));
        reingresos.add(new TipoReingresoTO("8", "BULTO NO CORRESPONDE"));
        reingresos.add(new TipoReingresoTO("9", "DAÑO MERCADERIA"));

        //Creamos el adaptador
        ArrayAdapter<TipoReingresoTO> adapter = new ArrayAdapter<TipoReingresoTO>(this,R.layout.support_simple_spinner_dropdown_item,reingresos);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        return adapter;
    }

}


