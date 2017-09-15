package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import Util.Utilidades;

/**
 * Created by jmunozv on 15-09-2017.
 */

public class MainCancelacionOdt extends AppCompatActivity implements View.OnClickListener {

    private Activity activity;
    private Utilidades util;
    private Intent recibir;
    private EditText txtRutFactura;
    private EditText txtRazonFactura;
    private EditText txtDireccionFactura;
    private EditText txtTelefonoFactura;
    private EditText txtGiroFactura;
    private Spinner spn_comuna;
    private Spinner spn_ciudad;
    private RadioButton radioBoleta;
    private RadioButton radioFactura;
    private Button btnLimpiarCancelacion;
    private Button btnAceptarCancelacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        util = new Utilidades();
        recibir = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cancelacion_odt);
        txtRutFactura = (EditText) findViewById(R.id.txtRutFactura);
        txtRutFactura.setVisibility(View.INVISIBLE);
        txtRazonFactura = (EditText) findViewById(R.id.txtRazonFactura);
        txtRazonFactura.setVisibility(View.INVISIBLE);
        txtDireccionFactura = (EditText) findViewById(R.id.txtDireccionFactura);
        txtDireccionFactura.setVisibility(View.INVISIBLE);
        txtTelefonoFactura = (EditText) findViewById(R.id.txtTelefonoFactura);
        txtTelefonoFactura.setVisibility(View.INVISIBLE);
        txtGiroFactura = (EditText) findViewById(R.id.txtGiroFactura);
        txtGiroFactura.setVisibility(View.INVISIBLE);
        spn_comuna = (Spinner) findViewById(R.id.sprComunaFactura);
        spn_comuna.setVisibility(View.GONE);
        spn_ciudad = (Spinner) findViewById(R.id.sprCiudadFactura);
        spn_ciudad.setVisibility(View.GONE);
        radioBoleta = (RadioButton) findViewById(R.id.rbBoleta);
        radioBoleta.setOnClickListener(this);
        radioFactura = (RadioButton) findViewById(R.id.rbFactura);
        radioFactura.setOnClickListener(this);
        btnAceptarCancelacion = (Button) findViewById(R.id.btnAceptarCancelacionOdt);
        btnAceptarCancelacion.setOnClickListener(this);
        btnLimpiarCancelacion = (Button) findViewById(R.id.btnLimpiarCancelacionOdt);
        btnLimpiarCancelacion.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnAceptarCancelacionOdt: {

                break;
            }
            case R.id.btnLimpiarCancelacionOdt: {

                break;
            }
            case R.id.rbBoleta: {
                esconder();
                break;
            }
            case R.id.rbFactura: {
                mostrar();
                break;
            }
            default:
                break;
        }
    }

    public void mostrar(){
        txtRutFactura.setVisibility(View.VISIBLE);
        txtRazonFactura.setVisibility(View.VISIBLE);
        txtDireccionFactura.setVisibility(View.VISIBLE);
        txtTelefonoFactura.setVisibility(View.VISIBLE);
        txtGiroFactura.setVisibility(View.VISIBLE);
        spn_comuna.setVisibility(View.VISIBLE);
        spn_ciudad.setVisibility(View.VISIBLE);
    }

    public void esconder(){
        txtRutFactura.setVisibility(View.INVISIBLE);
        txtRazonFactura.setVisibility(View.INVISIBLE);
        txtDireccionFactura.setVisibility(View.INVISIBLE);
        txtTelefonoFactura.setVisibility(View.INVISIBLE);
        txtGiroFactura.setVisibility(View.INVISIBLE);
        spn_comuna.setVisibility(View.INVISIBLE);
        spn_ciudad.setVisibility(View.INVISIBLE);
    }
}
