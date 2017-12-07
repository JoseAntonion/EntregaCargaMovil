package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nlscan.android.scan.ScanManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import To.EstadosOdtTO;
import Util.Globales;
import Util.Utilidades;

public class MainODT extends AppCompatActivity implements View.OnClickListener {
    private Button btn_finreparto;
    private Button btn_Volver;
    private Button btn_scan_odt;
    private ImageButton btn_odt_manual;
    private int faltantes;
    private int entregados;
    private TextView lblFaltantes;
    private TextView lblEntregadas;
    private EditText txtOdtManual;
    private Utilidades util = new Utilidades();
    private Activity activity;
    ScanManager mScanMgr;
    Context mContext;
    private int aux;
    Intent recibir;
    private Bundle datos;
    private EstadosOdtTO estado;
    private String formaPago = "";
    private String OLD = "";
    private String OLD2 = "";
    //private String odtAnterior = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_odt);
        recibir = getIntent();
        btn_finreparto = (Button) findViewById(R.id.btnFinReparto);
        btn_finreparto.setOnClickListener(this);
        txtOdtManual = (EditText) findViewById(R.id.txtIngresoOdtManual);
        btn_Volver = (Button) findViewById(R.id.btnVolverODT);
        btn_Volver.setOnClickListener(this);
        btn_odt_manual = (ImageButton) findViewById(R.id.btnOdtManual);
        btn_odt_manual.setOnClickListener(this);
        lblFaltantes = (TextView) findViewById(R.id.lblFaltantes);
        lblEntregadas = (TextView) findViewById(R.id.lblEntregados);
        mScanMgr = ScanManager.getInstance();
        mContext = getApplicationContext();
        estado = new EstadosOdtTO();

        // Captura variables por parametro de otros Activitis
        Bundle extras = recibir.getExtras();
        if(extras != null){
            OLD = (extras.get("old") == null)?"":extras.get("old").toString();
            OLD2 = (extras.get("old2") == null)?"":extras.get("old2").toString();
            //odtAnterior = (extras.get("odtAnterior") == null)?"":extras.get("odtAnterior").toString();
        }
        if(!OLD.equals("") || !OLD2.equals("")){
            btn_finreparto.setEnabled(false);
            btn_Volver.setEnabled(false);
        }

        try {
            estado = util.buscaLeidosFaltantes();
            lblFaltantes.setText(estado.getFaltantes());
            lblEntregadas.setText(estado.getEntregadas());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnFinReparto: {
                Intent intento = new Intent(MainODT.this, MainResumenEntrega.class);
                startActivity(intento);
                finish();
                break;
            }
            case R.id.btnVolverODT: {
                Intent intento = new Intent(MainODT.this, MainResumenPlanilla.class);
                startActivity(intento);
                break;
            }
            case R.id.btnOdtManual: {
                if(!txtOdtManual.getText().toString().equals("")){
                    validaNumeroODTtipoPago(txtOdtManual.getText().toString());
                }else{
                    Toast.makeText(activity.getApplicationContext(), "Debe ingresar numero de ODT", Toast.LENGTH_LONG).show();
                }
                break;
            }
            default:
                break;
        }

    }


    //Procesos de lectura de codigo barra
    private void registerReceiver()
    {
        //mScanMgr = ScanManager.getInstance();
        IntentFilter intFilter=new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
        registerReceiver(this.mResultReceiver, intFilter);
        //mScanMgr.startScan();
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
            //mContext = getApplicationContext();

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
                        validaNumeroODTtipoPago(svalue1.replace("*",""));
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                //Random random = new Random();

            }
        }
    };

    public void validaNumeroODTtipoPago(String odt){
        boolean odtRepetida = false;
        String formaPago;
        try {
            if(!Globales.primera){
                Globales.totalValoresODT = 0;
                Globales.banderaTipoPago = (util.buscaFormaPagoOdt(odt).equals("CTA"))?"1":"0";
                Globales.primera = true;
                Globales.registroOdtMultiples = new ArrayList<>();
            }

            for(int i=0;i<Globales.registroOdtMultiples.size();i++){
                if(odt.equals(Globales.registroOdtMultiples.get(i).getOdt().toString())){
                    odtRepetida = true;
                    break;
                }
            }

            if(odtRepetida){
                Toast.makeText(activity.getApplicationContext(), "La ODT ya se ingreso", Toast.LENGTH_LONG).show();
            }else {
                formaPago = (util.buscaFormaPagoOdt(odt).equals("CTA"))?"1":"0";
                if(formaPago.equals(Globales.banderaTipoPago)){
                    if(util.BuscaODT(odt)){
                        if (!util.validaEstadoOdt(odt).equals("99") || !util.validaEstadoOdt(odt).equals("57")){
                            if (Globales.banderaTipoPago.equals("1")) {
                                Intent intento = new Intent(MainODT.this, MainEntregaCarga.class);
                                intento.putExtra("odt", odt);
                                intento.putExtra("aux", 1);
                                intento.putExtra("old", OLD);
                                startActivity(intento);
                            }else{
                                Intent intento = new Intent(MainODT.this, MainEscanerBulto.class);
                                intento.putExtra("odt", odt);
                                intento.putExtra("old2", OLD2);
                                startActivity(intento);
                            }
                        }else{
                            Toast.makeText(activity.getApplicationContext(), "La ODT se fue procesada", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(activity.getApplicationContext(), "ODT no encontrada", Toast.LENGTH_LONG).show();
                        Intent intento = new Intent(MainODT.this, MainODT.class);
                        startActivity(intento);
                    }
                }else{
                    Toast.makeText(activity.getApplicationContext(), "Debe ingresar ODT con el mismo tipo de pago", Toast.LENGTH_LONG).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}

