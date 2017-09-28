package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import To.BoletaTO;
import To.CiudadTO;
import To.ComunaTO;
import To.FacturaTO;
import Util.Globales;
import Util.Utilidades;

/**
 * Created by jmunozv on 15-09-2017.
 */

public class MainCancelacionOdt extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

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
    private TextView lblTotalOdt;
    private String codigoCiudad = "";
    private String tipoDoc = "";
    private ArrayList<FacturaTO> listaFacturas = new ArrayList<>();
    ArrayList<BoletaTO> listaBoletas = new ArrayList<>();

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
        spn_comuna.setVisibility(View.INVISIBLE);
        spn_ciudad = (Spinner) findViewById(R.id.sprCiudadFactura);
        spn_ciudad.setVisibility(View.INVISIBLE);
        spn_ciudad.setOnItemSelectedListener(this);
        radioBoleta = (RadioButton) findViewById(R.id.rbBoleta);
        radioBoleta.setOnClickListener(this);
        radioFactura = (RadioButton) findViewById(R.id.rbFactura);
        radioFactura.setOnClickListener(this);
        btnAceptarCancelacion = (Button) findViewById(R.id.btnAceptarCancelacionOdt);
        btnAceptarCancelacion.setOnClickListener(this);
        btnLimpiarCancelacion = (Button) findViewById(R.id.btnLimpiarCancelacionOdt);
        btnLimpiarCancelacion.setOnClickListener(this);
        lblTotalOdt = (TextView) findViewById(R.id.lblValorTotFacturaBoleta);
        lblTotalOdt.setText(String.valueOf(Globales.totalValoresODT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnAceptarCancelacionOdt: {
                tipoDoc = (radioBoleta.isChecked())?"boleta":"factura";
                if(tipoDoc.equals("factura")) {
                    FacturaTO factura = new FacturaTO(txtRutFactura.getText().toString(), txtRazonFactura.getText().toString(), txtDireccionFactura.getText().toString(),
                            txtTelefonoFactura.getText().toString(), txtGiroFactura.getText().toString(), Integer.parseInt(spn_comuna.getSelectedItem().toString()),
                            Integer.parseInt(spn_ciudad.getSelectedItem().toString()), Integer.parseInt(lblTotalOdt.getText().toString()));
                    listaFacturas = new ArrayList<>();
                    listaFacturas.add(factura);
                    ValidaInfoPago();
                }else{
                    BoletaTO boleta = new BoletaTO();
                    boleta.setTotalBoleta(Integer.parseInt(lblTotalOdt.getText().toString()));
                    listaBoletas = new ArrayList<>();
                    listaBoletas.add(boleta);
                    new GuardaPago().execute();
                }
                break;
            }
            case R.id.btnLimpiarCancelacionOdt: {
                limpiar();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if ((position != 0) && (id != 0)) {
            Object item = parent.getItemAtPosition(position);
            codigoCiudad = ((CiudadTO) item).getCodCiudad();
            spn_comuna.setAdapter(creaSpinnerComunas(codigoCiudad));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void ValidaInfoPago() {
        txtRutFactura.setError(null);
        txtRazonFactura.setError(null);
        txtDireccionFactura.setError(null);
        txtTelefonoFactura.setError(null);
        txtGiroFactura.setError(null);

        if(!validarRut(txtRutFactura.getText().toString())){
            txtRutFactura.setError(getString(R.string.error_campo_rut));
            txtRutFactura.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(txtRutFactura.getText().toString())){
            txtRutFactura.setError(getString(R.string.error_campo_obligatorio));
            txtRutFactura.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(txtRazonFactura.getText().toString())){
            txtRazonFactura.setError(getString(R.string.error_campo_obligatorio));
            txtRazonFactura.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(txtDireccionFactura.getText().toString())){
            txtDireccionFactura.setError(getString(R.string.error_campo_obligatorio));
            txtDireccionFactura.requestFocus();
            return;
        }
        if(spn_ciudad.getSelectedItemPosition() == 0){
            Toast.makeText(activity.getApplicationContext(),
                    "Debe Seleccionar CIUDAD", Toast.LENGTH_LONG).show();
            return;
        }
        if(spn_comuna.getSelectedItemPosition() == 0){
            Toast.makeText(activity.getApplicationContext(),
                    "Debe Seleccionar COMUNA", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(txtTelefonoFactura.getText().toString())){
            txtTelefonoFactura.setError(getString(R.string.error_campo_obligatorio));
            txtTelefonoFactura.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(txtGiroFactura.getText().toString())){
            txtGiroFactura.setError(getString(R.string.error_campo_obligatorio));
            txtGiroFactura.requestFocus();
            return;
        }

        new GuardaPago().execute();
    }

    public void mostrar(){
        txtRutFactura.setVisibility(View.VISIBLE);
        txtRazonFactura.setVisibility(View.VISIBLE);
        txtDireccionFactura.setVisibility(View.VISIBLE);
        txtTelefonoFactura.setVisibility(View.VISIBLE);
        txtGiroFactura.setVisibility(View.VISIBLE);
        spn_ciudad.setAdapter(creaSpinnerCiudades());
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

    public void limpiar(){
        txtRutFactura.setText("");
        txtRazonFactura.setText("");
        txtDireccionFactura.setText("");
        txtTelefonoFactura.setText("");
        txtGiroFactura.setText("");
        spn_ciudad.setAdapter(creaSpinnerCiudades());
        spn_comuna.setAdapter(null);
    }

    public static boolean validarRut(String rut) {

        boolean validacion = false;
        try {
            rut =  rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validacion = true;
            }

        } catch (java.lang.NumberFormatException e) {
        } catch (Exception e) {
        }
        return validacion;
    }

    public ArrayAdapter<CiudadTO> creaSpinnerCiudades(){

        //Creamos la lista
        ArrayList<CiudadTO> ciudad = new ArrayList<>();
        ArrayList<CiudadTO> ciudadAUX = new ArrayList<>();
        //La poblamos
        try {
            ciudadAUX = util.cargaDesdeArchivoCiudad();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ciudad.add(new CiudadTO("0","Seleccione Ciudad"));
        for(int i=0 ; i < ciudadAUX.size() ; i++){
            ciudad.add(new CiudadTO(ciudadAUX.get(i).getCodCiudad(),ciudadAUX.get(i).getNombreCiudad()));
        }
        //Creamos el adaptador
        ArrayAdapter<CiudadTO> adapter = new ArrayAdapter<CiudadTO>(this,R.layout.support_simple_spinner_dropdown_item,ciudad);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        return adapter;
    }

    public ArrayAdapter<ComunaTO> creaSpinnerComunas(String codCiudad){

        //Creamos la lista
        ArrayList<ComunaTO> comuna = new ArrayList<>();
        ArrayList<ComunaTO> comunaAUX = new ArrayList<>();
        //La poblamos
        try {
            comunaAUX = util.cargaDesdeArchivoComuna(codCiudad);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        comuna.add(new ComunaTO("0","Seleccione Comuna"));
        for(int i=0 ; i < comunaAUX.size() ; i++){
            comuna.add(new ComunaTO(comunaAUX.get(i).getCodComuna(),comunaAUX.get(i).getNombreComuna()));
        }
        //Creamos el adaptador
        ArrayAdapter<ComunaTO> adapter = new ArrayAdapter<ComunaTO>(this,R.layout.support_simple_spinner_dropdown_item,comuna);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        return adapter;
    }

    public class GuardaPago extends AsyncTask<Void,Void,String> {

        ProgressDialog MensajeProgreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setMessage("Generando Documento...");
            MensajeProgreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
                if(tipoDoc.equals("factura")){
                    util.escribirEnArchivo(listaFacturas,"FACTURA");
                }else{
                    util.escribirEnArchivo(listaBoletas,"BOLETA");
                }
                return null;

        }

        @Override
        protected void onPostExecute(String result) {
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();
            Intent intent = new Intent(MainCancelacionOdt.this, MainInfoReceptorCarga.class);
            intent.putExtra("tipoDoc",tipoDoc);
            startActivity(intent);
        }
    }
}
