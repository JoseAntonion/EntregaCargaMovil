package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import java.util.Random;

import To.EntregaOdtMasivoTO;
import To.OdtMasivoTO;
import Util.Globales;
import Util.Utilidades;

public class MainEscanerBulto extends AppCompatActivity implements View.OnClickListener {

    private Utilidades util = new Utilidades();
    private EditText txtBultoManual;
    private EditText txt_OdtAEntregar;
    private TextView lblBultosFaltantes;
    private TextView lblBultosLeidos;
    private Button btn_entrega_carga;
    private Button btn_NO_entrega_carga;
    private ImageButton btn_bulto_manual;
    private Activity activity;
    private String ODT;
    private String OLD2;
    private int aux;
    private int faltantes = 0;
    private int leidos = 0;
    ScanManager mScanMgr;
    Context mContext;
    Intent recibir;
    EntregaOdtMasivoTO odtM;
    OdtMasivoTO odtSingle;
    private int totalEntrega;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_escaner_bulto);
        txt_OdtAEntregar = (EditText) findViewById(R.id.txtOdtEscaneaBulto);
        txtBultoManual = (EditText) findViewById(R.id.txtBultoManual);
        lblBultosFaltantes = (TextView) findViewById(R.id.txtBultosFaltantes);
        lblBultosLeidos = (TextView) findViewById(R.id.txtBultosLeidos);
        lblBultosFaltantes = (TextView) findViewById(R.id.txtBultosFaltantes);
        btn_bulto_manual = (ImageButton) findViewById(R.id.btnBultoManual);
        btn_bulto_manual.setOnClickListener(this);
        btn_entrega_carga = (Button) findViewById(R.id.btnEntregaCarga2);
        btn_entrega_carga.setEnabled(false);
        btn_entrega_carga.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(Globales.cantidadBultosOriginal == leidos){
                    new ValidaEntrega().execute();
                }else{
                    Toast.makeText(activity.getApplicationContext(),
                            "Aun hay bultos faltantes !!!", Toast.LENGTH_LONG).show();
                }

            }
        });
        //btn_entrega_carga.setOnClickListener(this);
        btn_NO_entrega_carga = (Button) findViewById(R.id.btnNoEntregaCarga2);
        btn_NO_entrega_carga.setOnClickListener(this);
        /*btn_NO_entrega_carga.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intento = new Intent(MainEscanerBulto.this, MainMotivoNoEntrega.class);
                startActivity(intento);
                System.gc();
                finish();
            }
        });*/

        recibir = getIntent();
        Bundle extras = recibir.getExtras();
        OLD2 = (extras.get("old2") == null)?"":extras.get("old2").toString();
        ODT = (extras.get("odt") == null)?"":extras.get("odt").toString();
        aux = (extras.get("aux") == null)?0: (int) extras.get("aux");
        txt_OdtAEntregar.setText(ODT);
        txt_OdtAEntregar.setEnabled(false);
        mScanMgr = ScanManager.getInstance();
        mContext = getApplicationContext();

        if(aux == 0){
            try {
                Globales.bultosMultiples = util.leeBultosMultiples(ODT);
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
        }else{
            faltantes = recibir.getExtras().getInt("faltantes",0);
            leidos = recibir.getExtras().getInt("leidos",0);
            lblBultosFaltantes.setText(String.valueOf(faltantes));
            lblBultosLeidos.setText(String.valueOf(leidos));
            if(faltantes == 0){
                btn_entrega_carga.setEnabled(true);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnNoEntregaCarga2: {
                Intent intento = new Intent(MainEscanerBulto.this, MainMotivoNoEntrega.class);
                intento.putExtra("odt",ODT);
                startActivity(intento);
                break;
            }
            case R.id.btnBultoManual: {
                try {
                    if(!txtBultoManual.getText().toString().equals("")){
                        if(util.validaBulto(txtBultoManual.getText().toString())){
                            if(validaNumeroCodigoBarraRepetido(txtBultoManual.getText().toString())){
                                if(Globales.cantidadBultosOriginal > leidos){
                                    faltantes--;
                                    leidos++;
                                    Intent intento = new Intent(MainEscanerBulto.this, MainEscanerBulto.class);
                                    intento.putExtra("odt",ODT);
                                    intento.putExtra("faltantes",faltantes);
                                    intento.putExtra("leidos",leidos);
                                    intento.putExtra("aux",1);
                                    startActivity(intento);
                                    finish();
                                }else{
                                    Toast.makeText(activity.getApplicationContext(),
                                            "Ya se leyeron todos los bultos asociados !!!", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(activity.getApplicationContext(),
                                        "Ya fue leido el bulto !!!", Toast.LENGTH_LONG).show();
                                txtBultoManual.setText("");
                                txtBultoManual.requestFocus();
                            }
                        }else{
                            Toast.makeText(activity.getApplicationContext(),
                                    "Bulto invalido", Toast.LENGTH_LONG).show();
                            txtBultoManual.setText("");
                            txtBultoManual.requestFocus();
                        }
                    } else {
                        Toast.makeText(activity.getApplicationContext(),
                                "Bulto Manual VACÍO !!!", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(activity.getApplicationContext(),
                            e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (JSONException e) {
                    Toast.makeText(activity.getApplicationContext(),
                            e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;
            }
            /*case R.id.btnEntregaCarga2: {
                new ValidaEntrega().execute();
                break;
            }*/

            default:
                break;
        }
    }

    //Procesos de lectura de codigo barra
    private void registerReceiver(){
        IntentFilter intFilter=new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
        registerReceiver(this.mResultReceiver, intFilter);
        //mScanMgr.startScan();
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
                            if(validaNumeroCodigoBarraRepetido(svalue1)){
                                if(Globales.cantidadBultosOriginal > leidos){
                                    faltantes--;
                                    leidos++;
                                    Intent intento = new Intent(MainEscanerBulto.this, MainEscanerBulto.class);
                                    intento.putExtra("odt",ODT);
                                    intento.putExtra("faltantes",faltantes);
                                    intento.putExtra("leidos",leidos);
                                    intento.putExtra("aux",1);
                                    startActivity(intento);
                                    finish();
                                }else{
                                    Toast.makeText(activity.getApplicationContext(),
                                            "Ya se leyeron todos los bultos asociados !!!", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(activity.getApplicationContext(),
                                        "El bulto ya fue escaneado", Toast.LENGTH_LONG).show();
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

    public class ValidaEntrega extends AsyncTask<Void,Void,String> {

        ProgressDialog MensajeProgreso;
        Boolean bandera = false;
        int bultosLeidos = Integer.parseInt((String) lblBultosLeidos.getText());
        int bultosTotales = 0;

       /* public ValidaEntrega() throws IOException, JSONException {
        }*/


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setMessage("Validando Entrega...");
            MensajeProgreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String respStr = null;
            //bultosLeidos = 1;
            try {
                bultosTotales = util.leeCantidadBultosODT(ODT);
                if (bultosLeidos == bultosTotales) {
                    bandera = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return respStr;
        }


        @Override
        protected void onPostExecute(String result) {
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();
            if (bandera) {
                MensajeEntregaMasivoEfePed();
            }else{
                Toast.makeText(getApplicationContext(), "Faltan Bultos por Escanear",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private AlertDialog MensajeEntregaMasivoEfePed() {
        final String DEFAULT_TITLE = "Entrega Carga Movil";
        final String DEFAULT_MESSAGE = "Desea cargas mas ODTs ?";
        final String DEFAULT_YES = "Si";
        final String DEFAULT_NO = "No";

        odtM = new EntregaOdtMasivoTO();
        odtM.setOdt(ODT);
        odtM.setCantidad(Integer.parseInt(lblBultosLeidos.getText().toString()));
        Globales.odtMasiva.add(odtM);
        try {
            if(util.buscaFormaPagoOdt(ODT).equals("PED")){
                Globales.totalValoresODT += util.buscaValorOdt(ODT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OdtMasivoTO Odt = new OdtMasivoTO();
        Odt.setOdt(ODT);
        try {
            Odt.setValor(util.buscaValorOdt(ODT));
            Odt.setPlanilla(util.buscaPlanillaOdt(ODT));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Globales.registroOdtMultiples.add(Odt);

        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(DEFAULT_TITLE);
        downloadDialog.setMessage(DEFAULT_MESSAGE);
        downloadDialog.setPositiveButton(DEFAULT_YES,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*OdtMasivoTO Odt = new OdtMasivoTO();
                        Odt.setOdt(ODT);
                        try {
                            Odt.setValor(util.buscaValorOdt(ODT));
                            Odt.setPlanilla(util.buscaPlanillaOdt(ODT));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Globales.registroOdtMultiples.add(Odt);*/
                        Intent intent = new Intent(MainEscanerBulto.this, MainODT.class);
                        //intent.putExtra("masivo", 1);
                        intent.putExtra("old2", "1");
                        startActivity(intent);
                    }
                });
        downloadDialog.setNegativeButton(DEFAULT_NO,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        odtSingle = new OdtMasivoTO();
                        try {
                            odtSingle.setPlanilla(util.buscaPlanillaOdt(ODT));
                            odtSingle.setOdt(ODT);
                            Globales.odtSingle = odtSingle;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(OLD2.equals("")){
                            try {
                                if(util.buscaFormaPagoOdt(ODT).equals("EFE")){
                                    Intent intento = new Intent(MainEscanerBulto.this, MainInfoReceptorCarga.class);
                                    //Intent intento = new Intent(MainEscanerBulto.this, MainCancelacionOdt.class);
                                    intento.putExtra("odt", ODT);
                                    startActivity(intento);
                                }else{
                                    //Intent intento = new Intent(MainEscanerBulto.this, MainInfoReceptorCarga.class);
                                    Intent intento = new Intent(MainEscanerBulto.this, MainCancelacionOdt.class);
                                    intento.putExtra("odt", ODT);
                                    startActivity(intento);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                if(util.buscaFormaPagoOdt(ODT).equals("EFE")){
                                    Intent intento = new Intent(MainEscanerBulto.this, MainInfoReceptorCarga.class);
                                    //Intent intento = new Intent(MainEscanerBulto.this, MainCancelacionOdt.class);
                                    //intento.putExtra("odtses", odtMasiva);
                                    startActivity(intento);
                                }else{
                                    //Intent intento = new Intent(MainEscanerBulto.this, MainInfoReceptorCarga.class);
                                    Intent intento = new Intent(MainEscanerBulto.this, MainCancelacionOdt.class);
                                    //intento.putExtra("odtses", odtMasiva);
                                    startActivity(intento);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        return downloadDialog.show();
    }

    public boolean validaNumeroCodigoBarraRepetido(String bulto){
        boolean encontro = false;
        for(int i=0;i<Globales.bultosMultiples.size();i++){
            if(Globales.bultosMultiples.get(i).toString().equals(bulto)){
                Globales.bultosMultiples.remove(i);
                encontro = true;
            }
        }
        return encontro;
    }
}
