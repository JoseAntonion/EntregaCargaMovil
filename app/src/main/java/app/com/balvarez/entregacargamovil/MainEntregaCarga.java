package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import Util.Utilidades;

public class MainEntregaCarga extends AppCompatActivity implements View.OnClickListener {

    private Button btn_entrega_carga;
    private Button btn_no_entrega_carga;
    private EditText odtAentregar;
    private String ODT = "";
    private Intent recibir;
    private Utilidades util;
    private TextView txtCantidadBultos;
    private Activity activity;

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
        txtCantidadBultos = (TextView) findViewById(R.id.txtCantBultos);
        ODT = recibir.getStringExtra("odt");
        odtAentregar = (EditText) findViewById(R.id.txtODT);
        odtAentregar.setText(ODT);
        odtAentregar.setEnabled(false);
    }


    @Override
    public void onClick(View v) {
        int cantidadBultosMaximo = 0;
        int cantidadBultosIngresado = Integer.parseInt(txtCantidadBultos.getText().toString());
        try {
            cantidadBultosMaximo = util.leeCantidadBultosODT(ODT);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (v.getId()) {

            case R.id.btnEntregaCarga: {
                if(cantidadBultosIngresado > cantidadBultosMaximo){
                    Toast.makeText(activity.getApplicationContext(),
                            "Excedido en la cantidad de bultos correspondientes.", Toast.LENGTH_LONG).show();
                }else {
                    Intent intento = new Intent(MainEntregaCarga.this, MainFirma.class);
                    intento.putExtra("odt", ODT);
                    startActivity(intento);
                    finish();
                    System.gc();
                    finish();
                }
                break;
            }
            case R.id.btnNoEntregaCarga: {
                Intent intento = new Intent(MainEntregaCarga.this, MainMotivoNoEntrega.class);
                intento.putExtra("odt", ODT);
                startActivity(intento);
                finish();
                System.gc();
                finish();
                break;
            }
            default:
                break;
        }
    }
}
