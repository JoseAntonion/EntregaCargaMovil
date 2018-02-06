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
import android.location.Criteria;
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
import com.google.zxing.WriterException;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import To.ArchivoCapturaImagen;
import To.ArchivoDocElectronicoTO;
import To.ValidaTO;
import Util.Globales;
import Util.Utilidades;
import Util.WebServices;

public class MainFirma extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private SignatureView signature;
    private Button btn_finalizar;
    private Button btn_limpiar;
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
    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;
    private Bitmap firma;
    private String firmaB64;

    String voice2text; //added

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
        firmaB64 = "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_Finalizar: {

                // GEO-REFERENCIA
                if (VerificarGPS()) {
                    getLocationFinalNOFAKEUNZELDA();
                }
                // Captura firma del control signature y la codifica a un string base64
                PreparaFirma();
                //VERIFICA CONEXION A INTERNET
                if(util.verificaConexion(getApplicationContext())){
                    new CapturaImagen().execute();
                }else{
                    new CapturaImagenOffLine().execute();
                }
            }
            case R.id.btnLimpiar: {
                signature.clearSignature();
            }
            default:
                break;
        }
    }

    public class CapturaImagenOffLine extends  AsyncTask<Void, Void, String>{

        ProgressDialog MensajeProgreso;
        int neto,iva;
        ArrayList<String> odetes = new ArrayList<>();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setTitle("Proceso Off-Line");
            MensajeProgreso.setMessage("Realizando Entrega sin RED...");
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
                        datoOff.setRUT(Globales.datosReceptor.getRut());
                        datoOff.setNOMBRE(Globales.datosReceptor.getNombre());
                        datoOff.setTELEFONO(Globales.datosReceptor.getTelefono());
                        datoOff.setIMEI(imei);
                        datoOff.setAPPATERNO(Globales.datosReceptor.getApPaterno());
                        datoOff.setAPMATERNO(Globales.datosReceptor.getApMaterno());
                        // Valida si la ODT es para traspaso a CtaCte
                        if(!Globales.ctaTraspaso.equals("")){
                            datoOff.setTIPOPAGO("CTA");
                        }else{
                            datoOff.setTIPOPAGO(util.buscaFormaPagoOdt(Globales.registroOdtMultiples.get(i).getOdt()));
                        }
                        //////////////////////////////////////////////
                        datoOff.setTOTAL(util.buscaValorOdt(Globales.registroOdtMultiples.get(i).getOdt()));
                        if (datoOff.getTOTAL() > 0) {
                            try {
                                neto = Redondeo(datoOff.getTOTAL() / 1.19);
                                iva = Redondeo(neto * 0.19);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        datoOff.setNETO(neto);
                        datoOff.setIVA(iva);
                        datoOff.setFIRMA(firmaB64);
                        if(tipoDoc.equals("factura")){
                            datoOff.setTIPODOCUMENTO("33");
                            datoOff.setRUTFACTURA(Globales.factura.getRutFactura());
                            datoOff.setRAZONFACTURA(Globales.factura.getRazonFactura());
                            datoOff.setDIRECCIONFACTURA(Globales.factura.getDireccionFactura());
                            datoOff.setCOMUNA(Globales.factura.getDescComuna());
                            datoOff.setGIROFACTURA(Globales.factura.getGiroFactura());
                            datoOff.setFONOFACTURA(Globales.factura.getFonoFactura());
                        }else if(tipoDoc.equals("boleta")){
                            datoOff.setTIPODOCUMENTO("39");
                            datoOff.setRUTFACTURA("#");
                            datoOff.setRAZONFACTURA("#");
                            datoOff.setDIRECCIONFACTURA("#");
                            datoOff.setCOMUNA("#");
                            datoOff.setGIROFACTURA("#");
                            datoOff.setFONOFACTURA("#");
                        }else{
                            datoOff.setTIPODOCUMENTO("#");
                            datoOff.setRUTFACTURA("#");
                            datoOff.setRAZONFACTURA("#");
                            datoOff.setDIRECCIONFACTURA("#");
                            datoOff.setCOMUNA("#");
                            datoOff.setGIROFACTURA("#");
                            datoOff.setFONOFACTURA("#");
                        }
                        if(Globales.ctaTraspaso.equals("")){
                            datoOff.setCtaCte("#");
                        }else{
                            datoOff.setCtaCte(Globales.ctaTraspaso);
                        }
                        datoOff.setLatitudEntrega(Globales.latitud);
                        datoOff.setLongitudEntrega(Globales.longitud);
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
                    datoOff.setPLANILLA(Globales.registroOdtMultiples.get(i).getPlanilla());
                    datoOff.setODT(Globales.registroOdtMultiples.get(i).getOdt());
                    // Se agrega odt al arreglo para poder enviarlas al metodo de impresion
                    odetes.add(Globales.registroOdtMultiples.get(i).getOdt());
                    ////////////////////////////////////////////////
                    datoOff.setRUT(Globales.datosReceptor.getRut());
                    datoOff.setNOMBRE(Globales.datosReceptor.getNombre());
                    datoOff.setTELEFONO(Globales.datosReceptor.getTelefono());
                    datoOff.setIMEI(imei);
                    datoOff.setAPPATERNO(Globales.datosReceptor.getApPaterno());
                    datoOff.setAPMATERNO(Globales.datosReceptor.getApMaterno());
                    // Valida si la ODT es para traspaso a CtaCte
                    if(!Globales.ctaTraspaso.equals("")){
                        datoOff.setTIPOPAGO("CTA");
                    }else{
                        datoOff.setTIPOPAGO(util.buscaFormaPagoOdt(Globales.registroOdtMultiples.get(i).getOdt()));
                    }
                    //////////////////////////////////////////////
                    datoOff.setTOTAL(util.buscaValorOdt(Globales.registroOdtMultiples.get(i).getOdt()));
                    if (datoOff.getTOTAL() > 0) {
                        try {
                            neto = Redondeo(datoOff.getTOTAL() / 1.19);
                            iva = Redondeo(neto * 0.19);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    datoOff.setNETO(neto);
                    datoOff.setIVA(iva);
                    datoOff.setFIRMA(firmaB64);
                    if(tipoDoc.equals("factura")){
                        datoOff.setTIPODOCUMENTO("33");
                        datoOff.setRUTFACTURA(Globales.factura.getRutFactura());
                        datoOff.setRAZONFACTURA(Globales.factura.getRazonFactura());
                        datoOff.setDIRECCIONFACTURA(Globales.factura.getDireccionFactura());
                        datoOff.setCOMUNA(Globales.factura.getDescComuna());
                        datoOff.setGIROFACTURA(Globales.factura.getGiroFactura());
                        datoOff.setFONOFACTURA(Globales.factura.getFonoFactura());
                    }else if(tipoDoc.equals("boleta")){
                        datoOff.setTIPODOCUMENTO("39");
                        datoOff.setRUTFACTURA("#");
                        datoOff.setRAZONFACTURA("#");
                        datoOff.setDIRECCIONFACTURA("#");
                        datoOff.setCOMUNA("#");
                        datoOff.setGIROFACTURA("#");
                        datoOff.setFONOFACTURA("#");
                    }else{
                        datoOff.setTIPODOCUMENTO("#");
                        datoOff.setRUTFACTURA("#");
                        datoOff.setRAZONFACTURA("#");
                        datoOff.setDIRECCIONFACTURA("#");
                        datoOff.setCOMUNA("#");
                        datoOff.setGIROFACTURA("#");
                        datoOff.setFONOFACTURA("#");
                    }
                    if(Globales.ctaTraspaso.equals("")){
                        datoOff.setCtaCte("#");
                    }else{
                        datoOff.setCtaCte(Globales.ctaTraspaso);
                    }
                    datoOff.setLatitudEntrega(Globales.latitud);
                    datoOff.setLongitudEntrega(Globales.longitud);
                    listaDatosOffline.add(datoOff);
                    util.cambiaEstadoOdtArchivoENTREGADO(Globales.registroOdtMultiples.get(i).getOdt());
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            // ESCRIBE EN ARCHIVO LOCAL LOS DATOS NECESARIOS PARA SU POSTERIOR ENVÍO
            util.escribirEnArchivo(listaDatosOffline,"OFFLINE");
            try {
                if(util.buscaFormaPagoOdt(Globales.registroOdtMultiples.get(i).getOdt()).equals("PED")){
                    if(Globales.ctaTraspaso.equals("")){
                        if (util.ConectarEpsonPrueba(activity.getApplicationContext())) {
                            // IMPRESION OFFLINE
                            util.ImprimeOffLine(activity, odetes);
                        }
                    }
                }
            } catch (EposException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();
            MensajeFinRepartoOFFLINE();
        }

    }

    public class CapturaImagen extends AsyncTask<Void, Void, String> {

        //Bitmap imagen = signature.getImage();

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
            MensajeProgreso.setMessage("Realizando Entrega...");
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

            resp = new ValidaTO();
            resp2 = new ValidaTO();

            try {
                if (sd.canWrite()) {

                    if (Globales.odtMasiva != null) {
                        for (int o = 0; o < Globales.odtMasiva.size(); o++) {
                            //resp = ws.GrabaImagen(RUT, encodedFirma, "ACT", RUT, imei, Globales.odtMasiva.get(o).getOdt(), "ENTREGA");
                            resp = ws.GrabaImagen(RUT, firmaB64, "ACT", RUT, imei, Globales.odtMasiva.get(o).getOdt(), "ENTREGA");
                            resp2 = ws.CambiaEstadoODT(Globales.odtMasiva.get(o).getOdt(), "99", RUT, "MainFirma", Globales.version, "EntregaCargaMovil", imei);
                            util.cambiaEstadoOdtArchivoENTREGADO(Globales.odtMasiva.get(o).getOdt());
                        }
                    } else {
                        //resp = ws.GrabaImagen(RUT, encodedFirma, "ACT", RUT, imei, ODT, "ENTREGA");
                        resp = ws.GrabaImagen(RUT, firmaB64, "ACT", RUT, imei, ODT, "ENTREGA");
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

    private AlertDialog MensajeFinRepartoOFFLINE() {

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
                        finish();
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

    private int Redondeo(double valor) {
        String val = valor + "";
        BigDecimal big = new BigDecimal(val);
        big = big.setScale(0, RoundingMode.HALF_UP);
        int redondeado = Integer.parseInt(String.valueOf(big));
        return redondeado;
    }


    /////////////////////////////////// GEO-LOCALIZACION ///////////////////////////////////////////
    public static boolean isLocationEnabled(Context context) {
        //...............
        return true;
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
            if (location != null) {
                //Log.e("TAG", "GPS is on");
                Globales.latitud = location.getLatitude();
                Globales.longitud = location.getLongitude();
/*                Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
                searchNearestPlace(voice2text);*/
            }
            else{
                //This is what you need:
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            }
        }
        else
        {
            //prompt user to enable location....
            //.................
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);

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

    public void searchNearestPlace(String v2txt) {
        //.....
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    public void PreparaFirma(){
        firma = signature.getImage();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        firma.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        firmaB64 = android.util.Base64.encodeToString(byteArray, Base64.NO_WRAP);
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

    }
