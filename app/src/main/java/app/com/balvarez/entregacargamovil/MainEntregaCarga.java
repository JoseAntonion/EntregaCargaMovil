package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import To.EntregaOdtMasivoTO;
import To.OdtMasivoTO;
import Util.Globales;
import Util.Utilidades;

public class MainEntregaCarga extends AppCompatActivity implements View.OnClickListener {

    private Button btn_entrega_carga;
    private Button btn_no_entrega_carga;
    private EditText odtAentregar;
    private String ODT;
    private String OLD;
    private Intent recibir;
    private Utilidades util;
    private EditText txtCantidadBultos;
    private Activity activity;
    //private ArrayList<EntregaOdtMasivoTO> odtMasiva;
    EntregaOdtMasivoTO odtM;
    private int totalEntrega;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        util = new Utilidades();
        recibir = getIntent();
        MainODT main = new MainODT();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entrega_carga);
        btn_entrega_carga = (Button) findViewById(R.id.btnEntregaCarga);
        btn_entrega_carga.setOnClickListener(this);
        btn_no_entrega_carga = (Button) findViewById(R.id.btnNoEntregaCarga);
        btn_no_entrega_carga.setOnClickListener(this);
        txtCantidadBultos = (EditText) findViewById(R.id.txtCantBultos);
        Bundle extras = recibir.getExtras();
        ODT = (extras.get("odt") == null)?"":extras.get("odt").toString();
        OLD = (extras.get("old") == null)?"":extras.get("old").toString();
        odtAentregar = (EditText) findViewById(R.id.txtODT);
        odtAentregar.setText(ODT);
        odtAentregar.setEnabled(false);
        //odtMasiva = new ArrayList<EntregaOdtMasivoTO>();
        txtCantidadBultos.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


    @Override
    public void onClick(View v) {
        int cantidadBultosMaximo = 0;
        int cantidadBultosIngresado = 0;

        if(txtCantidadBultos.getText().length()>0){
            cantidadBultosIngresado = Integer.parseInt(txtCantidadBultos.getText().toString());
        }

        try {
            cantidadBultosMaximo = util.leeCantidadBultosODT(ODT);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (v.getId()) {

            case R.id.btnEntregaCarga: {
                //Envio de ODT pendientes
                if(util.verificaConexion(getApplicationContext())){
                    new EnvioODTPendiente().execute();
                }
                /////////////////////////////////
                if(cantidadBultosIngresado == cantidadBultosMaximo){
                    MensajeEntregaMasivo(cantidadBultosMaximo,cantidadBultosIngresado);
                }else{
                    Toast.makeText(activity.getApplicationContext(),
                            "Debe ingresa cantidad Exacta de Bultos", Toast.LENGTH_LONG).show();
                }

                break;
            }
            case R.id.btnNoEntregaCarga: {
                //Envio de ODT pendientes
                if(util.verificaConexion(getApplicationContext())){
                    new EnvioODTPendiente().execute();
                }
                /////////////////////////////////
                Intent intento = new Intent(MainEntregaCarga.this, MainMotivoNoEntrega.class);
                intento.putExtra("odt", ODT);
                startActivity(intento);
                finish();
                break;
            }
            default:
                break;
        }
    }

    private AlertDialog MensajeEntregaMasivo(final int cantidadBultosMaximo,final int cantidadBultosIngresado) {
        final String DEFAULT_TITLE = "Entrega Carga Movil";
        final String DEFAULT_MESSAGE = "Desea cargas mas ODTs ?";
        final String DEFAULT_YES = "Si";
        final String DEFAULT_NO = "No";

        odtM = new EntregaOdtMasivoTO();
        odtM.setOdt(ODT);
        odtM.setCantidad(Integer.parseInt(txtCantidadBultos.getText().toString()));
        Globales.odtMasiva.add(odtM);
        try {
            Globales.totalValoresODT += util.buscaValorOdt(ODT);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OdtMasivoTO odt = new OdtMasivoTO();
        odt.setOdt(ODT);
        try {
            odt.setPlanilla(util.buscaPlanillaOdt(ODT));
            odt.setValor(util.buscaValorOdt(ODT));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Globales.registroOdtMultiples.add(odt);

        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(DEFAULT_TITLE);
        downloadDialog.setMessage(DEFAULT_MESSAGE);
        downloadDialog.setPositiveButton(DEFAULT_YES,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /*odtM = new EntregaOdtMasivoTO();
                        odtM.setOdt(ODT);
                        odtM.setCantidad(Integer.parseInt(txtCantidadBultos.getText().toString()));
                        Globales.odtMasiva.add(odtM);*/
                        Intent intent = new Intent(MainEntregaCarga.this, MainODT.class);
                        //intent.putExtra("masivo", 1);
                        intent.putExtra("old", "1");
                        intent.putExtra("odtAnterior", ODT);
                        startActivity(intent);
                    }
                });
        downloadDialog.setNegativeButton(DEFAULT_NO,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Globales.esCTACTE = "si";
                        if(OLD.equals("")){
                            Intent intento = new Intent(MainEntregaCarga.this, MainInfoReceptorCarga.class);
                            intento.putExtra("odt", ODT);
                            startActivity(intento);
                        }else{
                            Intent intento = new Intent(MainEntregaCarga.this, MainInfoReceptorCarga.class);
                            startActivity(intento);
                        }
                    }
                });

        return downloadDialog.show();
    }

    public class EnvioODTPendiente extends AsyncTask<Void, Void, String> {

        ProgressDialog MensajeProgreso;
        String subioOffnile = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setTitle("Proceso Off-Line");
            MensajeProgreso.setMessage("Realizando Entrega ODT pendiente...");
            MensajeProgreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                if(util.EnviaOdtPendiente()){
                    //Toast.makeText(mContext, "Se enviaron datos de ODT PENDIENTES",Toast.LENGTH_LONG).show();
                    subioOffnile = "Se enviaron datos de ODT PENDIENTES";
                }
            }catch (Exception e){
                //Toast.makeText(mContext, e.getMessage(),Toast.LENGTH_LONG).show();
                subioOffnile = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();
            Toast.makeText(activity.getApplicationContext(), subioOffnile,Toast.LENGTH_LONG).show();
        }
    }

}
