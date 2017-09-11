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

import To.EstadosOdtTO;
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
        datos = this.getIntent().getExtras();

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
                System.gc();
                finish();
                break;
            }
            case R.id.btnVolverODT: {
                Intent intento = new Intent(MainODT.this, MainResumenPlanilla.class);
                //Intent intento = new Intent(MainODT.this, MainEscanerBulto.class);
                //intento.putExtra("odt","50000002670");
                startActivity(intento);
                break;
            }
            case R.id.btnOdtManual: {
                try {
                    if (!util.validaEstadoOdt(txtOdtManual.getText().toString()).equals("99")) {
                        formaPago = util.buscaFormaPagoOdt(txtOdtManual.getText().toString());
                        if (formaPago != "" || formaPago != null) {
                            if (formaPago.equals("CTA")) {
                                Intent intento = new Intent(MainODT.this, MainEntregaCarga.class);
                                intento.putExtra("odt", txtOdtManual.getText().toString());
                                intento.putExtra("aux", 1);
                                startActivity(intento);
                            } else if (formaPago.equals("PED") || formaPago.equals("EFE")) {
                                Intent intento = new Intent(MainODT.this, MainEscanerBulto.class);
                                intento.putExtra("odt", txtOdtManual.getText().toString());
                                startActivity(intento);
                            } else {
                                Toast.makeText(activity.getApplicationContext(),
                                        "No se encontro FORMA DE PAGO para la ODT escaneada", Toast.LENGTH_LONG).show();
                            }
                        } else
                            Toast.makeText(activity.getApplicationContext(), "ERROR!! Forma de Pago VACIA o NULA", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(activity.getApplicationContext(), "La ODT se encuentra ENTREGADA", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(activity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (JSONException e) {
                    Toast.makeText(activity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
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
                        if(util.BuscaODT(svalue1.replace("*",""))) {
                            if (!util.validaEstadoOdt(svalue1.replace("*", "")).equals("99")) {
                                formaPago = util.buscaFormaPagoOdt(svalue1.replace("*", ""));
                                if (formaPago != "" || formaPago != null) {
                                    if (formaPago.equals("CTA")) {
                                        Intent intento = new Intent(MainODT.this, MainEntregaCarga.class);
                                        intento.putExtra("odt", svalue1);
                                        intento.putExtra("aux", 1);
                                        startActivity(intento);
                                        System.gc();
                                    } else if (formaPago.equals("PED") || formaPago.equals("EFE")) {
                                        Intent intento = new Intent(MainODT.this, MainEscanerBulto.class);
                                        intento.putExtra("odt", svalue1);
                                        startActivity(intento);
                                        System.gc();
                                    } else {
                                        Toast.makeText(activity.getApplicationContext(),
                                                "No se encontro FORMA DE PAGO para la ODT escaneada", Toast.LENGTH_LONG).show();
                                    }
                                } else
                                    Toast.makeText(activity.getApplicationContext(), "ERROR Forma de Pago !!!!", Toast.LENGTH_LONG).show();
                            } else
                                Toast.makeText(activity.getApplicationContext(), "La ODT se encuentra ENTREGADA", Toast.LENGTH_LONG).show();
                        }else
                            Toast.makeText(activity.getApplicationContext(), "ODT no encontrada", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                //Random random = new Random();

            }
        }
    };

}

