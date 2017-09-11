package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainInfoReceptorCarga extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private Activity activity;
    private EditText txt_RutReceptor;
    private EditText txtNombreReceptor;
    private EditText txtApellidoReceptor;
    private Button btn_siguiente;
    private Button btn_limpiar;
    private Intent recibir;
    private String ODT = "";
    private String rutReceptor = "";
    private String nombreReceptor = "";
    private String RUT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_info_receptor_carga);
        recibir = getIntent();
        txt_RutReceptor = (EditText) findViewById(R.id.txtRutReceptor);
        //RUT = txtRutReceptor.getText().toString();
        txtNombreReceptor = (EditText) findViewById(R.id.txtNombreReceptor);
        txtApellidoReceptor = (EditText) findViewById(R.id.txtApellidoReceptor);
        btn_limpiar = (Button) findViewById(R.id.btnLimpiarInfoReceptor);
        btn_limpiar.setOnClickListener(this);
        btn_siguiente = (Button) findViewById(R.id.btnSiguienteInfoReceptor);
        btn_siguiente.setOnClickListener(this);
        ODT = recibir.getStringExtra("odt");
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
                    Intent intento = new Intent(MainInfoReceptorCarga.this, MainFirma.class);
                    intento.putExtra("odt", ODT);
                    intento.putExtra("rut", txt_RutReceptor.getText().toString());
                    startActivity(intento);
                    break;
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
        txtApellidoReceptor.setText("");
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
