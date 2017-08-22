package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nlscan.android.scan.ScanManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.Random;

import Util.Globales;
import Util.Utilidades;

public class MainEscanerBulto extends AppCompatActivity {

    private Utilidades util = new Utilidades();
    private EditText txt_OdtAEntregar;
    private TextView lblBultosFaltantes;
    private TextView lblBultosLeidos;
    private Activity activity;
    private String ODT;
    private int faltantes = 0;
    private int leidos = 0;
    ScanManager mScanMgr;
    Context mContext;
    Intent recibir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_escaner_bulto);
        txt_OdtAEntregar = (EditText) findViewById(R.id.txtODT);
        lblBultosFaltantes = (TextView) findViewById(R.id.txtBultosFaltantes);
        lblBultosLeidos = (TextView) findViewById(R.id.txtBultosLeidos);
        recibir = getIntent();
        ODT = recibir.getStringExtra("odt");
        txt_OdtAEntregar.setText(ODT);
        txt_OdtAEntregar.setEnabled(false);
        mScanMgr = ScanManager.getInstance();
        mContext = getApplicationContext();

        try {
            Globales.cantidadBultosOriginal = util.leeCantidadBultosODT(ODT);
            faltantes = Globales.cantidadBultosOriginal;
            lblBultosFaltantes.setText(String.valueOf(faltantes));
            leidos = Globales.cantidadBultosOriginal-faltantes;
            lblBultosLeidos.setText(String.valueOf(leidos));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //new TraeOdtPorPatente().execute();
    }

    /*public class TraeOdtPorPatente extends AsyncTask<Void,Void,String> {

        String Odt = txt_OdtAEntregar.toString();

        @Override
        protected String doInBackground(Void... params) {
            try {
                Globales.cantidadBultosOriginal = util.leeCantidadBultosODT(Odt);
                faltantes = Globales.cantidadBultosOriginal;
                leidos = Globales.cantidadBultosOriginal-faltantes;
                lblBultosFaltantes.setText(String.valueOf(faltantes));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }*/

    //Procesos de lectura de codigo barra
    private void registerReceiver(){
        IntentFilter intFilter=new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
        registerReceiver(this.mResultReceiver, intFilter);
        mScanMgr.startScan();
    }

    private void unRegisterReceiver(){
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
                        if(util.validaBulto(svalue1)){
                            if(Globales.cantidadBultosOriginal > leidos){
                                faltantes--;
                                leidos++;
                                Intent intento = new Intent(MainEscanerBulto.this, MainEscanerBulto.class);
                                intento.putExtra("odt",ODT);
                                intento.putExtra("faltantes",faltantes);
                                intento.putExtra("leidos",leidos);
                                intento.putExtra("aux",1);
                                startActivity(intento);
                            }else{
                                Toast.makeText(activity.getApplicationContext(),
                                        "Ya se leyeron todos los bultos asociados !!!", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(activity.getApplicationContext(),
                                    "No se encontro bulto leido !!!", Toast.LENGTH_LONG).show();
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


}
