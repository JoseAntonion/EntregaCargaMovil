package app.com.balvarez.entregacargamovil;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import To.ArchivoCapturaImagen;
import To.TipoReingresoTO;
import To.ValidaTO;
import Util.Globales;
import Util.Utilidades;
import Util.WebServices;

import static app.com.balvarez.entregacargamovil.MainFirma.isLocationEnabled;

public class MainMotivoNoEntrega extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, LocationListener {

    private Button btn_finalizar;
    private Button btn_limpiar;
    private Button btn_tomarFoto;
    private Spinner spn_motivo;
    private TextView txtNombreFoto;
    private Intent recibir;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String ODT = "";
    private String NombreFoto = "FotoRespaldo.jpg";
    private String encodedImage2;
    private Activity activity;
    private Utilidades util;
    private static final int ACTIVAR_GPS = 1;
    private AlertDialog alert;
    private String codigoReingreso = "";
    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;
    private LocationListener locListener;
    private Handler mHandler;



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
                    getLocationFinalNOFAKEUNZELDA();
                }
                if (!codigoReingreso.isEmpty() && !codigoReingreso.equals("99")) {
                    if (txtNombreFoto.getVisibility() != View.INVISIBLE) {
                        if(util.verificaConexion(getApplicationContext())){
                            new CapturaImagen().execute();
                        }else{
                            new CapturaImagenOffLine().execute();
                        }
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
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
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
    @Override
    public void onLocationChanged(Location location) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
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

    public class CapturaImagenOffLine extends  AsyncTask<Void, Void, String>{

        ProgressDialog MensajeProgreso;
        ArrayList<String> odetes = new ArrayList<>();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setTitle("Proceso Off-Line");
            MensajeProgreso.setMessage("Registrando reingreso sin RED...");
            MensajeProgreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            ArrayList<ArchivoCapturaImagen> listaDatosOffline = new ArrayList<>();

            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            int i = 0;
            // CONSULTA SI HAY MAS DE UNA ODT INGRESADA
            if (Globales.registroOdtMultiples.size() > 1) {
                for (i = 0; i < Globales.registroOdtMultiples.size(); i++) {
                    try {
                        ArchivoCapturaImagen datoOff = new ArchivoCapturaImagen();
                        datoOff.setPLANILLA(Globales.registroOdtMultiples.get(i).getPlanilla());
                        datoOff.setODT(Globales.registroOdtMultiples.get(i).getOdt());
                        // Se crea un array con las ODTS entregadas, para poder enviarlas al metodo de impresion
                        odetes.add(Globales.registroOdtMultiples.get(i).getOdt());
                        /////////////////////////////////////////////////////////
                        datoOff.setIMEI(imei);
                        datoOff.setFIRMA(encodedImage2);
                        // Vuelve a llamar al metodo de geolocalizacion, por si esta nulo
                        getLocationFinalNOFAKEUNZELDA();
                        datoOff.setLatitudEntrega(Globales.latitud);
                        datoOff.setLongitudEntrega(Globales.longitud);
                        datoOff.setCantidadBultosODT(util.leeCantidadBultosODT(Globales.registroOdtMultiples.get(i).getOdt()));
                        datoOff.setCodigoMotivoReingreso(codigoReingreso);
                        listaDatosOffline.add(datoOff);
                        util.cambiaEstadoOdtArchivoENTREGADO(Globales.odtMasiva.get(i).getOdt());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                try {
                    ArchivoCapturaImagen datoOff = new ArchivoCapturaImagen();
                    datoOff.setPLANILLA(util.buscaPlanillaOdt(ODT));
                    datoOff.setODT(ODT);
                    // Se crea un array con las ODTS entregadas, para poder enviarlas al metodo de impresion
                    odetes.add(ODT);
                    /////////////////////////////////////////////////////////
                    datoOff.setIMEI(imei);
                    datoOff.setFIRMA(encodedImage2);
                    // Vuelve a llamar al metodo de geolocalizacion, por si esta nulo
                    getLocationFinalNOFAKEUNZELDA();
                    datoOff.setLatitudEntrega(Globales.latitud);
                    datoOff.setLongitudEntrega(Globales.longitud);
                    datoOff.setCantidadBultosODT(util.leeCantidadBultosODT(ODT));
                    datoOff.setCodigoMotivoReingreso(codigoReingreso);
                    listaDatosOffline.add(datoOff);
                    util.cambiaEstadoOdtArchivoENTREGADO(ODT);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            // ESCRIBE EN ARCHIVO LOCAL LOS DATOS NECESARIOS PARA SU POSTERIOR ENVÍO
            util.escribirEnArchivo(listaDatosOffline,"OFFLINE-REINGRESO");
            // Se cambia a valor 1 para no generar problemas de entrega multiple (Mensaje "Debe ingresar una ODT del mismo tipo de pago")
            Globales.banderaTipoPago = "1";
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();
            MensajeFinRepartoCORRECTO();
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

    protected void getLocationFinalNOFAKEUNZELDA() {
        if (isLocationEnabled(activity.getApplicationContext())) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            //You can still do this if you like, you might get lucky:
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(bestProvider);
            //do{
                if (location != null) {
                    //Log.e("TAG", "GPS is on");
                    Globales.latitud = location.getLatitude();
                    Globales.longitud = location.getLongitude();
/*                Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
                searchNearestPlace(voice2text);*/
                } else {
                    //This is what you need:
                    locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
                }
            //}while (location == null);
        }
        else
        {
            //prompt user to enable location....
            //.................
        }
    }

}


