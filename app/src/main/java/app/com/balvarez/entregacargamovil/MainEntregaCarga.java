package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import To.EntregaOdtMasivoTO;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        util = new Utilidades();
        recibir = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entrega_carga);
        btn_entrega_carga = (Button) findViewById(R.id.btnEntregaCarga);
        btn_entrega_carga.setOnClickListener(this);
        btn_no_entrega_carga = (Button) findViewById(R.id.btnNoEntregaCarga);
        btn_no_entrega_carga.setOnClickListener(this);
        txtCantidadBultos = (EditText) findViewById(R.id.txtCantBultos);
        Bundle extras = recibir.getExtras();
        ODT = (extras.isEmpty())?"":extras.get("odt").toString();
        OLD = (extras.get("old") == null)?"":extras.get("old").toString();
        odtAentregar = (EditText) findViewById(R.id.txtODT);
        odtAentregar.setText(ODT);
        odtAentregar.setEnabled(false);
        //odtMasiva = new ArrayList<EntregaOdtMasivoTO>();
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
                if(cantidadBultosIngresado == cantidadBultosMaximo){
                    MensajeEntregaMasivo(cantidadBultosMaximo,cantidadBultosIngresado);
                }else{
                    Toast.makeText(activity.getApplicationContext(),
                            "Debe ingresa cantidad Exacta de Bultos", Toast.LENGTH_LONG).show();
                }

                break;
            }
            case R.id.btnNoEntregaCarga: {
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
                        startActivity(intent);
                    }
                });
        downloadDialog.setNegativeButton(DEFAULT_NO,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(OLD.equals("")){
                            Intent intento = new Intent(MainEntregaCarga.this, MainInfoReceptorCarga.class);
                            intento.putExtra("odt", ODT);
                            startActivity(intento);
                        }else{
                            Intent intento = new Intent(MainEntregaCarga.this, MainInfoReceptorCarga.class);
                            //intento.putExtra("odtses", odtMasiva);
                            startActivity(intento);
                        }
                    }
                });

        return downloadDialog.show();
    }
}
