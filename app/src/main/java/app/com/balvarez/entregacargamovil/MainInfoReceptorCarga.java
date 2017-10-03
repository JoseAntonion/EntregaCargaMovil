package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import To.DatosReceptorTO;
import Util.Globales;

public class MainInfoReceptorCarga extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

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
    //private ArrayList<EntregaOdtMasivoTO> listaOdt = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_info_receptor_carga);
        recibir = getIntent();
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
                if(validarRut(txt_RutReceptor.getText().toString())) {
                    datosReceptor = new DatosReceptorTO();
                    datosReceptor.setNombre(txtNombreReceptor.getText().toString());
                    datosReceptor.setApPaterno(txtApPaternoReceptor.getText().toString());
                    datosReceptor.setApMaterno(txtApMaternoReceptor.getText().toString());
                    datosReceptor.setTelefono(txtFonoReceptor.getText().toString());
                    datosReceptor.setRut(txt_RutReceptor.getText().toString());
                    Globales.datosReceptor = datosReceptor;
                    if(Globales.odtMasiva == null){
                        Intent intento = new Intent(MainInfoReceptorCarga.this, MainFirma.class);
                        intento.putExtra("odt", ODT);
                        intento.putExtra("tipoDoc", tipoDoc);
                        intento.putExtra("rut", txt_RutReceptor.getText().toString());
                        startActivity(intento);
                    }else{
                        Intent intento = new Intent(MainInfoReceptorCarga.this, MainFirma.class);
                        //intento.putExtra("odtses", listaOdt);
                        intento.putExtra("rut", txt_RutReceptor.getText().toString());
                        intento.putExtra("tipoDoc", tipoDoc);
                        startActivity(intento);
                    }

                    break;
                }else{
                    Toast.makeText(activity.getApplicationContext(),
                            "Debe ingresa un RUT VALIDO !!", Toast.LENGTH_LONG).show();
                }
            }
            default:
                break;
        }
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            // CODIGO A EJECUTAR CUANDO EL EDITTEXT PIERDA EL FOCO
            if(!validarRut(txt_RutReceptor.getText().toString())){
            //if(!validarRut(v.toString())){
                Toast.makeText(activity.getApplicationContext(),
                        "Debe ingresa un RUT VALIDO !!", Toast.LENGTH_LONG).show();
                //txt_RutReceptor.requestFocusFromTouch();
            }
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


}
