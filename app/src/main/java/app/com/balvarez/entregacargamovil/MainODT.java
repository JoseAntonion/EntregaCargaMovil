package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nlscan.android.scan.ScanManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import Util.Globales;
import Util.Utilidades;

public class MainODT extends AppCompatActivity implements View.OnClickListener {
    private Button btn_finreparto;
    private Button btn_Volver;
    private Button btn_scan_odt;
    private int faltantes;
    private int entregados;
    private TextView lblFaltantes;
    private TextView lblEntregadas;
    private Utilidades util = new Utilidades();
    private static final String BS_PACKAGE = "com.google.zxing.client.android";
    public static final int REQUEST_CODE = 0x0000c0de;
    private Activity activity;
    ScanManager mScanMgr;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        mScanMgr=ScanManager.getInstance();
        mContext = getApplicationContext();
        //aActivity = this;
        setContentView(R.layout.activity_main_odt);
        btn_finreparto = (Button) findViewById(R.id.btnFinReparto);
        btn_finreparto.setOnClickListener(this);
        btn_Volver = (Button) findViewById(R.id.btnVolver);
        btn_Volver.setOnClickListener(this);
        btn_scan_odt = (Button) findViewById(R.id.btnScanODT);
        btn_scan_odt.setOnClickListener(this);
        lblFaltantes = (TextView) findViewById(R.id.lblFaltantes);
        lblEntregadas = (TextView) findViewById(R.id.lblEntregados);

        try {
            faltantes = util.leeCantidadOdtsArchivo();
            lblFaltantes.setText(String.valueOf(faltantes));
            entregados = Globales.cantidadOriginalOdts-faltantes;
            lblEntregadas.setText(String.valueOf(entregados));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*InputFilter[] filtros = new InputFilter[1];
        filtros[0] = new InputFilter.AllCaps();
        odtEscaneada.setFilters(filtros);*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnFinReparto: {

                Intent intento = new Intent(MainODT.this, MainResumenEntrega.class);
                startActivity(intento);
                finish();
                System.gc();
                finish();
                break;
            }
            case R.id.btnVolver: {

                Intent intento = new Intent(MainODT.this, MainResumenPlanilla.class);
                startActivity(intento);
                finish();
                System.gc();
                finish();
                break;
            }
            case R.id.btnScanODT: {
                //new EscaneaODT().execute();
                /*Intent intentScan = new Intent(BS_PACKAGE + ".SCAN");
                intentScan.putExtra("PROMPT_MESSAGE", "Enfoque entre 9 y 11 cm.");
                String targetAppPackage = findTargetAppPackage(intentScan);
                if (targetAppPackage == null) {
                    showDownloadDialog();
                } else {
                    startActivityForResult(intentScan, REQUEST_CODE);
                }*/
                /*InputFilter[] filtros = new InputFilter[1];
                filtros[0] = new InputFilter.AllCaps();
                Intent intento = new Intent(MainODT.this, MainEntregaCarga.class);
                intento.putExtra("odt", filtros);
                startActivity(intento);
                System.gc();*/
                break;
            }
            default:
                break;
        }

    }

    private AlertDialog showDownloadDialog() {
        final String DEFAULT_TITLE = "Instalar Barcode Scanner?";
        final String DEFAULT_MESSAGE = "Esta aplicacion necesita Barcode Scanner. Quiere instalarla?";
        final String DEFAULT_YES = "Si";
        final String DEFAULT_NO = "No";

        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(DEFAULT_TITLE);
        downloadDialog.setMessage(DEFAULT_MESSAGE);
        downloadDialog.setPositiveButton(DEFAULT_YES,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri = Uri
                                .parse("market://details?id=" + BS_PACKAGE);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            activity.startActivity(intent);
                        } catch (ActivityNotFoundException anfe) {
                            // Hmm, market is not installed
                            Toast.makeText(
                                    MainODT.this,
                                    "Android market no esta instalado,no puedo instalar Barcode Scanner",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

        downloadDialog.setNegativeButton(DEFAULT_NO,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        return downloadDialog.show();
    }

    private String findTargetAppPackage(Intent intent) {

        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> availableApps = pm.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);

        if (availableApps != null) {

            for (ResolveInfo availableApp : availableApps) {
                String packageName = availableApp.activityInfo.packageName;
                if (BS_PACKAGE.contains(packageName)) {
                    return packageName;
                }
            }
        }
        return null;
    }

    public class EscaneaODT extends AsyncTask<Void,Void,String> {

        ProgressDialog MensajeProgreso;

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setMessage("Consultando Patente...");
            MensajeProgreso.show();

        }*/

        @Override
        protected String doInBackground(Void... params) {
            Intent intentScan = new Intent(BS_PACKAGE + ".SCAN");
            intentScan.putExtra("PROMPT_MESSAGE", "Enfoque entre 9 y 11 cm.");
            String targetAppPackage = findTargetAppPackage(intentScan);
            if (targetAppPackage == null) {
                showDownloadDialog();
            } else {
                startActivityForResult(intentScan, REQUEST_CODE);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();

            InputFilter[] filtros = new InputFilter[1];
            filtros[0] = new InputFilter.AllCaps();
            Intent intento = new Intent(MainODT.this, MainEntregaCarga.class);
            intento.putExtra("odt", filtros);
            startActivity(intento);
            System.gc();
        }
    }

    //Procesos de lectura de codigo barra
    private void registerReceiver()
    {
        IntentFilter intFilter=new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
        registerReceiver(this.mResultReceiver, intFilter);
        mScanMgr.startScan();
    }

    private void unRegisterReceiver()
    {
        try {
            unregisterReceiver(mResultReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    private BroadcastReceiver mResultReceiver=new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            String formaPago = "";

            if(ScanManager.ACTION_SEND_SCAN_RESULT.equals(action)){
                byte[] bvalue1=intent.getByteArrayExtra(ScanManager.EXTRA_SCAN_RESULT_ONE_BYTES);
                byte[] bvalue2=intent.getByteArrayExtra(ScanManager.EXTRA_SCAN_RESULT_TWO_BYTES);
                String svalue1=null;
                String svalue2=null;
                try {

                    if(bvalue1!=null)
                        svalue1=new String(bvalue1,"GBK");
                    if(bvalue2!=null)
                        svalue2=new String(bvalue1,"GBK");
                    svalue1=svalue1==null?"":svalue1;
                    svalue2=svalue2==null?"":svalue2;
                    if (svalue1.length()>0) {
                        formaPago = util.buscaFormaPagoOdt(svalue1);
                        if(formaPago.equals("CTA")){
                            Intent intento = new Intent(MainODT.this, MainEntregaCarga.class);
                            intento.putExtra("odt",svalue1);
                            startActivity(intento);
                            System.gc();
                        }else if(formaPago.equals("PED")){
                            Intent intento = new Intent(MainODT.this, MainEscanerBulto.class);
                            intento.putExtra("odt",svalue1);
                            startActivity(intento);
                            System.gc();
                        }else{
                            Toast.makeText(activity.getApplicationContext(),
                                    "No se encontro FORMA DE PAGO para la odt escaneada", Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                Random random = new Random();

            }
        }
    };

    public String formaPagoODT(String odt){
        String tipoPago = "";

        return tipoPago;
    }

}

