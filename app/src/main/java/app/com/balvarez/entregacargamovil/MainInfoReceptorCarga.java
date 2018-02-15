package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import To.DatosReceptorTO;
import To.UsuarioReceptorTO;
import Util.Globales;
import Util.Utilidades;
import Util.WebServices;

public class MainInfoReceptorCarga extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {//, View.OnFocusChangeListener {

    private Activity activity;
    private EditText txt_RutReceptor;
    private EditText txtNombreReceptor;
    private EditText txtApPaternoReceptor;
    private EditText txtApMaternoReceptor;
    private Button btn_siguiente;
    private Button btn_limpiar;
    private Intent recibir;
    private String ODT = "";
    private String tipoDoc = "";
    private EditText txtFonoReceptor;
    private String RUT = "";
    private DatosReceptorTO datosReceptor;
    Utilidades util;
    private String rutReceptor = "";
    WebServices WS;
    //private ArrayList<EntregaOdtMasivoTO> listaOdt = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_info_receptor_carga);
        recibir = getIntent();
        util = new Utilidades();
        WS = new WebServices();
        txt_RutReceptor = (EditText) findViewById(R.id.txtRutReceptor);
        //RUT = txtRutReceptor.getText().toString();
        txtNombreReceptor = (EditText) findViewById(R.id.txtNombreReceptor);
        txtApPaternoReceptor = (EditText) findViewById(R.id.txtApellidoPaterno);
        txtApMaternoReceptor = (EditText) findViewById(R.id.txtApellidoMaterno);
        txtFonoReceptor = (EditText) findViewById(R.id.txtFonoReceptor);
        btn_limpiar = (Button) findViewById(R.id.btnLimpiarInfoReceptor);
        btn_limpiar.setOnClickListener(this);
        btn_siguiente = (Button) findViewById(R.id.btnSiguienteInfoReceptor);
        btn_siguiente.setOnClickListener(this);
        Bundle extras = recibir.getExtras();
        //listaOdt = (ArrayList<EntregaOdtMasivoTO>) ((extras == null)?new ArrayList<>():extras.get("odtses"));
        if(extras != null){
            ODT = (extras.get("odt") == null)?"":extras.get("odt").toString();
            tipoDoc = (extras.get("tipoDoc") == null)?"":extras.get("tipoDoc").toString();
        }

        txt_RutReceptor.setOnFocusChangeListener(this);
        txt_RutReceptor.requestFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnLimpiarInfoReceptor: {
                limpiar();
                break;
            }
            case R.id.btnSiguienteInfoReceptor: {
                try{
                    if(!txt_RutReceptor.getText().toString().equals("")){
                        if(ValidaInfoReceptor()) {
                            datosReceptor = new DatosReceptorTO();
                            datosReceptor.setNombre(txtNombreReceptor.getText().toString());
                            datosReceptor.setApPaterno(txtApPaternoReceptor.getText().toString());
                            datosReceptor.setApMaterno(txtApMaternoReceptor.getText().toString());
                            datosReceptor.setTelefono(txtFonoReceptor.getText().toString());
                            datosReceptor.setRut(txt_RutReceptor.getText().toString());
                            Globales.datosReceptor = datosReceptor;
                            if(util.verificaConexion(activity.getApplicationContext())){
                                new GraModDatosReceptor(datosReceptor).execute();
                            }
                            if(Globales.odtMasiva == null){
                                Intent intento = new Intent(MainInfoReceptorCarga.this, MainFirma.class);
                                intento.putExtra("odt", ODT);
                                intento.putExtra("tipoDoc", tipoDoc);
                                intento.putExtra("rut", txt_RutReceptor.getText().toString());
                                startActivity(intento);
                            }else{
                                Intent intento = new Intent(MainInfoReceptorCarga.this, MainFirma.class);
                                intento.putExtra("rut", txt_RutReceptor.getText().toString());
                                intento.putExtra("tipoDoc", tipoDoc);
                                startActivity(intento);
                            }
                        }
                    }else{
                        Toast.makeText(activity.getApplicationContext(),
                                "Rut vacio", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(activity.getApplicationContext(),
                            "Error en MainInfoReceptorCarga: "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            }
            default:
                break;
        }
    }

    public void limpiar(){
        txt_RutReceptor.setText("");
        txtNombreReceptor.setText("");
        txtApMaternoReceptor.setText("");
        txtApPaternoReceptor.setText("");
        txtFonoReceptor.setText("");
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

    public boolean ValidaInfoReceptor() {

        txt_RutReceptor.setError(null);
        txtNombreReceptor.setError(null);
        txtApPaternoReceptor.setError(null);
        txtApMaternoReceptor.setError(null);
        txtFonoReceptor.setError(null);

        if(!validarRut(txt_RutReceptor.getText().toString())){
            txt_RutReceptor.setError(getString(R.string.error_campo_rut));
            txt_RutReceptor.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(txtNombreReceptor.getText().toString())){
            txtNombreReceptor.setError(getString(R.string.error_campo_obligatorio));
            txtNombreReceptor.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(txtApPaternoReceptor.getText().toString())){
            txtApPaternoReceptor.setError(getString(R.string.error_campo_obligatorio));
            txtApPaternoReceptor.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(txtApMaternoReceptor.getText().toString())){
            txtApMaternoReceptor.setError(getString(R.string.error_campo_obligatorio));
            txtApMaternoReceptor.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(txtFonoReceptor.getText().toString())){
            txtFonoReceptor.setError(getString(R.string.error_campo_obligatorio));
            txtFonoReceptor.requestFocus();
            return false;
        }


        return true;
        //new GuardaPago().execute();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        UsuarioReceptorTO usuario = new UsuarioReceptorTO();

        if(!hasFocus){
            if(util.verificaConexion(activity.getApplicationContext())){
                try {
                    new BuscaDatosReceptor(String.valueOf(txt_RutReceptor.getText())).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class BuscaDatosReceptor extends AsyncTask<Void, Void, String> {

        ProgressDialog MensajeProgreso;
        WebServices WS = new WebServices();
        UsuarioReceptorTO usuario = new UsuarioReceptorTO();
        String rut = "";
        boolean trajoDatos = false;

        public BuscaDatosReceptor(String s) {
            rut = s;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setTitle("Consulta Usuario");
            MensajeProgreso.setMessage("Consultando RUT en sistema...");
            MensajeProgreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                usuario = WS.TraeDatosUsuarioReceptor(rut);
                if(usuario != null) {
                    trajoDatos = true;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();
            if(trajoDatos) {
                txtNombreReceptor.setText(usuario.getNombre());
                txtApPaternoReceptor.setText(usuario.getApPaterno());
                txtApMaternoReceptor.setText(usuario.getApMaterno());
                txtFonoReceptor.setText(usuario.getTelefono());
            }else {
                Toast.makeText(activity.getApplicationContext(),
                        "No se encontraron registros", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class GraModDatosReceptor extends AsyncTask<Void, Void, String> {

        ProgressDialog MensajeProgreso;
        WebServices WS = new WebServices();
        String respuesta = "";
        DatosReceptorTO receptor = new DatosReceptorTO();

        public GraModDatosReceptor(DatosReceptorTO datosReceptor) {
            receptor = datosReceptor;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setTitle("Registro Usuario");
            MensajeProgreso.setMessage("Modificando información...");
            MensajeProgreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                respuesta = WS.GraModUsuarioReceptor(receptor.getRut(),receptor.getNombre(),receptor.getApPaterno(),receptor.getApMaterno(),receptor.getTelefono());
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();
            if(respuesta.equals("Realizado correctamente")) {
                Toast.makeText(activity.getApplicationContext(),
                        respuesta, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(activity.getApplicationContext(),
                        "Error en MainInfoReceptorCarga: "+respuesta, Toast.LENGTH_LONG).show();
            }
        }
    }
}
