package app.com.balvarez.entregacargamovil;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.coatedmoose.customviews.SignatureView;
import com.epson.eposprint.EposException;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import To.ArchivoDocElectronicoTO;
import To.ValidaTO;
import Util.Globales;
import Util.Utilidades;
import Util.WebServices;

public class MainFirma extends AppCompatActivity implements View.OnClickListener {

    private SignatureView signature;
    private Button btn_finalizar;
    private Button btn_limpiar;
    private String encodedFirma;
    private Activity activity;
    Intent recibir;
    private String ODT;
    private String RUT;
    private String tipoDoc;
    private Utilidades util;
    private BluetoothAdapter bAdapter;
    private Handler mHandler;
    private static final int ACTIVAR_GPS = 1;
    private AlertDialog alert;
    boolean impresionValida = false;
    private LocationManager locManager;
    private LocationListener locListener;

    //private ArrayList<EntregaOdtMasivoTO> listaOdt = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        recibir = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_firma);
        btn_finalizar = (Button) findViewById(R.id.btn_Finalizar);
        btn_finalizar.setOnClickListener(this);
        btn_limpiar = (Button) findViewById(R.id.btnLimpiar);
        btn_limpiar.setOnClickListener(this);
        signature = (SignatureView) this.findViewById(R.id.signatureView4);
        Bundle extras = recibir.getExtras();
        //listaOdt = (ArrayList<EntregaOdtMasivoTO>) ((extras == null)?new ArrayList<>():extras.get("odtses"));
        if (extras != null) {
            ODT = (extras.get("odt") == null) ? "" : extras.getString("odt");
            RUT = (extras.get("rut") == null) ? "" : extras.getString("rut");
            tipoDoc = (extras.get("tipoDoc") == null) ? "" : extras.getString("tipoDoc");
        }
        util = new Utilidades();
        bAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bAdapter != null) {
            if (!bAdapter.isEnabled()) {
                bAdapter.enable();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_Finalizar: {
                // GEO-REFERENCIA
                if (VerificarGPS()) {
                    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                    Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Globales.latitud = loc.getLatitude();
                    Globales.longitud = loc.getLongitude();
                    //comenzarLocalizacion();
                    /*GPSTraker gps = new GPSTraker(activity.getApplicationContext());
                    Location l = gps.getLocation();
                    if (l != null) {
                        Globales.latitud = l.getLatitude();
                        Globales.longitud = l.getLongitude();
                    }*/
                }
                new CapturaImagen().execute();
            }
            case R.id.btnLimpiar: {
                signature.clearSignature();
            }
            default:
                break;
        }
    }

    public class CapturaImagen extends AsyncTask<Void, Void, String> {

        Bitmap imagen = signature.getImage();
        File sd = Environment.getExternalStorageDirectory();
        File fichero = new File(sd, "firma.jpg");
        WebServices ws = new WebServices();
        ValidaTO resp;
        ValidaTO resp2;
        ProgressDialog MensajeProgreso;
        boolean guardoImagen = false;
        boolean cambioEstado = false;

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
            //////////////////////////////////////////////////////////////////////////////////////
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
            //ArrayList<DatosRepartoTO> datosRepartoTOs = new ArrayList<DatosRepartoTO>();
            //datosRepartoTOs = new ArrayList<DatosRepartoTO>();
            //datosRepartoTOs = utilidades.TraeOdtEntregadasNOconfirmadas();
            String tipoPago = "";
            String odt = "";
            int total = 0;
            int neto = 0;
            int iva = 0;
            int i = 0;
            if (Globales.registroOdtMultiples.size() > 1) {
                for (i = 0; i < Globales.registroOdtMultiples.size(); i++) {

                    try {
                        webservices.GuardarEntrega(Globales.registroOdtMultiples.get(i).getPlanilla(), Globales.registroOdtMultiples.get(i).getOdt(), Globales.datosReceptor.getRut(), Globales.datosReceptor.getNombre(),
                                Globales.datosReceptor.getTelefono(), imei, "EntregaCargaMovil", Globales.version, Globales.datosReceptor.getNombre(),
                                Globales.datosReceptor.getApPaterno(), Globales.datosReceptor.getApMaterno(), "%20", "%20", "%20");
                        tipoPago = tipoPago + util.buscaFormaPagoOdt(Globales.registroOdtMultiples.get(i).getOdt()) + "~";
                        odt = odt + Globales.registroOdtMultiples.get(i).getOdt() + "~";
                        total = total + util.buscaValorOdt(Globales.registroOdtMultiples.get(i).getOdt());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                try {
                    webservices.GuardarEntrega(Globales.registroOdtMultiples.get(i).getPlanilla(), Globales.registroOdtMultiples.get(i).getOdt(), Globales.datosReceptor.getRut(), Globales.datosReceptor.getNombre(),
                            Globales.datosReceptor.getTelefono(), imei, "EntregaCargaMovil", Globales.version, Globales.datosReceptor.getNombre(),
                            Globales.datosReceptor.getApPaterno(), Globales.datosReceptor.getApMaterno(), "%20", "%20", "%20");
                    tipoPago = tipoPago + util.buscaFormaPagoOdt(Globales.registroOdtMultiples.get(i).getOdt()) + "~";
                    odt = odt + Globales.registroOdtMultiples.get(i).getOdt() + "~";
                    total = total + util.buscaValorOdt(Globales.registroOdtMultiples.get(i).getOdt());
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }


            if (total > 0) {
                try {
                    neto = Redondeo(total / 1.19);
                    iva = Redondeo(neto * 0.19);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /*if(ResumenEntrega.listaFotos != null){
                for (int i = 0; i < ResumenEntrega.listaFotos.size(); i++) {
                    webservices.GuardaImagenODT(ResumenEntrega.listaFotos.get(i).getId(), ResumenEntrega.listaFotos.get(i).getImagen(), Globales.usuario, imei, "", "ENTREGA MOVIL", "CANCELACION FACTURA", Globales.VERSION_APLICACION, "800");
                }
            }*/

            /*utilidades.ModificaTipoPago("EFE");// "EFE" = PAGO CON EFECTIVO
            utilidades.ModificaEstadoXUnaEntrega("1");
            utilidades.ModificaEstadoConfirmado();*/

            //DocumentoElectronico documentoElectronico = new DocumentoElectronico();
            ArrayList<ArchivoDocElectronicoTO> archivoDocElectronicoTOs = new ArrayList<ArchivoDocElectronicoTO>();


            if (tipoDoc.equals("boleta")) {
                try {
                    archivoDocElectronicoTOs = webservices.RetornaDocContable("39", "02", "", "", "",
                            "", "", tipoPago, "0", Integer.toString((int) neto), Integer.toString((int) iva), Integer.toString((int) total), "", "", "000000000000000",
                            "03", "61", imei, imei, imei, "EntregaCargaMovil", "CancelacionBoleta", Globales.version, "", "1", "-1", "", "", "",
                            "", "", odt);
                    while (!impresionValida) {
                        if (util.ConectarEpsonPrueba(activity.getApplicationContext())) {
                            try {
                                //Llama a la creacion e impresion de la BOLETA
                                // Desarrollo
                                //util.BoletaPrueba(activity);
                                // Prueba real 1
                                if (util.Boleta(activity, archivoDocElectronicoTOs)) {
                                    impresionValida = true;
                                }
                            } catch (Exception e) {
                                Toast.makeText(activity.getApplicationContext(),
                                        "Error: " + e.getMessage() + ". Se reintentera la Impresion.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (EposException e1) {
                    e1.printStackTrace();
                }
            } else if (tipoDoc.equals("factura")) {

                try {
                    archivoDocElectronicoTOs = webservices.RetornaDocContable("33", "02", "", Globales.factura.getRutFactura(), Globales.factura.getRazonFactura(), Globales.factura.getDireccionFactura(),
                            Globales.factura.getDescComuna(), tipoPago, "0", Integer.toString((int) neto), Integer.toString((int) iva), Integer.toString((int) total), "", "", "000000000000000",
                            "03", "61", imei, imei, imei, "EntregaCargaMovil", "CancelacionFactura", Globales.version, "", "1", "-1", Globales.factura.getGiroFactura(), "", "",
                            Globales.factura.getFonoFactura(), "", odt);
                    while (!impresionValida) {
                        if (utilidades.ConectarEpsonPrueba(activity.getApplicationContext())) {
                            try {
                                //Llama a la creacion e imprecion de la FACTURA
                                //Desarrollo
                                //util.FacturaPrueba(activity);
                                // Prueba real 1
                                if (util.Factura(activity, archivoDocElectronicoTOs)) {
                                    impresionValida = true;
                                }
                            } catch (Exception e) {
                                Toast.makeText(activity.getApplicationContext(),
                                        "Error: " + e.getMessage() + ". Se reintentera la Impresion.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (EposException e1) {
                    e1.printStackTrace();
                }
            }

            /*Intent intento = new Intent(MainFirma.this, MenuUsuario.class);
            startActivity(intento);
            finish();
            System.gc();
            respStr = "1";

			return respStr;*/
            //////////////////////////////////////////////////////////////////////////////////////
            resp = new ValidaTO();
            resp2 = new ValidaTO();

            try {
                if (sd.canWrite()) {
                    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                    imagen.compress(Bitmap.CompressFormat.PNG, 90, arrayOutputStream);
                    Globales.Imagen = arrayOutputStream.toByteArray();
                    encodedFirma = Base64.encodeToString(Globales.Imagen, Base64.DEFAULT);
                    fichero.createNewFile();
                    OutputStream os = new FileOutputStream(fichero);
                    imagen.compress(Bitmap.CompressFormat.JPEG, 90, os);
                    os.close();

                    if (Globales.odtMasiva != null) {
                        for (int o = 0; o < Globales.odtMasiva.size(); o++) {
                            resp = ws.GrabaImagen(RUT, encodedFirma, "ACT", RUT, imei, Globales.odtMasiva.get(o).getOdt(), "ENTREGA");
                            resp2 = ws.CambiaEstadoODT(Globales.odtMasiva.get(o).getOdt(), "99", RUT, "MainFirma", Globales.version, "EntregaCargaMovil", imei);
                            util.cambiaEstadoOdtArchivoENTREGADO(Globales.odtMasiva.get(o).getOdt());
                        }
                    } else {
                        resp = ws.GrabaImagen(RUT, encodedFirma, "ACT", RUT, imei, ODT, "ENTREGA");
                        resp2 = ws.CambiaEstadoODT(ODT, "99", imei, "MainFirma", Globales.version, "EntregaCargaMovil", imei);
                        util.cambiaEstadoOdtArchivoENTREGADO(ODT);
                    }

                    if (resp != null) {
                        if (resp.getValida().equals("1")) {
                            guardoImagen = true;
                        }
                    }
                    if (resp2 != null) {
                        if (resp2.getValida().equals("1")) {
                            cambioEstado = true;
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(activity.getApplicationContext(),
                        e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(activity.getApplicationContext(),
                        e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(activity.getApplicationContext(),
                        e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            // PRUEBAS IMPRESION ----------------------------------------------------------------
            /*TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();*/

            //WebServices ws = new WebServices();
            /*Globales.Impresora = "00:01:90:C2:C4:C6";
            try {
                //ws.retornaImpresoraPrueba(imei);
                if(!Globales.esCTACTE.equals("si")){
                    if(util.ConectarEpsonPrueba(activity.getApplicationContext())) {
                        if (tipoDoc.equals("factura"))
                            util.FacturaPrueba(activity);
                        else
                            util.BoletaPrueba(activity);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EposException e) {
                e.printStackTrace();
            } catch (WriterException e) {
                e.printStackTrace();
            }*/
            // ----------------------------------------------------------------------------------
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();

            if (guardoImagen && cambioEstado) {
                Toast.makeText(activity.getApplicationContext(),
                        "Firma Guardada", Toast.LENGTH_LONG).show();
                Toast.makeText(activity.getApplicationContext(),
                        "ODT Entregada", Toast.LENGTH_LONG).show();
                MensajeFinRepartoCORRECTO();
            } else if (!guardoImagen && cambioEstado) {
                Toast.makeText(activity.getApplicationContext(),
                        "La firma no fue guardada: " + resp.getMensaje(), Toast.LENGTH_LONG).show();
                Toast.makeText(activity.getApplicationContext(),
                        "ODT Entregada", Toast.LENGTH_LONG).show();
                MensajeFinRepartoCORRECTO();
            } else if (guardoImagen && !cambioEstado) {
                Toast.makeText(activity.getApplicationContext(),
                        "Firma Guardada", Toast.LENGTH_LONG).show();
                Toast.makeText(activity.getApplicationContext(),
                        "ODT no ingreso al sistema: " + resp2.getMensaje(), Toast.LENGTH_LONG).show();
                MensajeFinRepartoINCORRECTO();
            } else if (!guardoImagen && !cambioEstado) {
                Toast.makeText(activity.getApplicationContext(),
                        "La firma no fue guardada: " + resp.getMensaje(), Toast.LENGTH_LONG).show();
                Toast.makeText(activity.getApplicationContext(),
                        "ODT no ingreso al sistema: " + resp2.getMensaje(), Toast.LENGTH_LONG).show();
                MensajeFinRepartoINCORRECTO();
            }
        }
    }

    private AlertDialog MensajeFinRepartoCORRECTO() {

        final String DEFAULT_TITLE = "Entrega Carga Movil";
        final String DEFAULT_MESSAGE = "ODT Entregada Correctamente";
        final String DEFAULT_YES = "Aceptar";

        Globales.totalValoresODT = 0;
        Globales.banderaTipoPago = "";
        Globales.registroOdtMultiples = null;
        Globales.primera = false;
        Globales.esCTACTE = "";

        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(DEFAULT_TITLE);
        downloadDialog.setMessage(DEFAULT_MESSAGE);
        downloadDialog.setPositiveButton(DEFAULT_YES,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainFirma.this, MainODT.class);
                        startActivity(intent);
                    }
                });

        return downloadDialog.show();
    }

    private AlertDialog MensajeFinRepartoINCORRECTO() {
        final String DEFAULT_TITLE = "Entrega Carga Movil";
        final String DEFAULT_MESSAGE = "Problemas en el proceso - MainFirma";
        final String DEFAULT_YES = "Aceptar";

        Globales.totalValoresODT = 0;
        Globales.banderaTipoPago = "";
        Globales.registroOdtMultiples = null;
        Globales.primera = false;
        Globales.esCTACTE = "";

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

    private boolean VerificarGPS() {
        boolean retorna = false;
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
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
        } else {
            retorna = true;
        }
        return retorna;
    }

    private int Redondeo(double valor) {
        String val = valor + "";
        BigDecimal big = new BigDecimal(val);
        big = big.setScale(0, RoundingMode.HALF_UP);
        int redondeado = Integer.parseInt(String.valueOf(big));
        return redondeado;
    }

    private void comenzarLocalizacion() {
        // Obtenemos una referencia al LocationManager
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Obtenemos la ultima posicion conocida
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
        Location loc = locManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Mostramos la ultima posicion conocida
        //mostrarPosicion(loc);

        //Asignar latitu y longitud a variales


        // Nos registramos para recibir actualizaciones de la posicion
        locListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //mostrarPosicion(location);
            }

            public void onProviderDisabled(String provider) {
                // lblEstado.setText("Provider OFF");
            }

            public void onProviderEnabled(String provider) {
                // lblEstado.setText("Provider ON ");
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // Log.i("", "Provider Status: " + status);
                // lblEstado.setText("Provider Status: " + status);
            }
        };

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
                0, locListener);

        Globales.latitud = loc.getLatitude();
        Globales.longitud = loc.getLongitude();
    }

    }
