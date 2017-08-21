package app.com.balvarez.entregacargamovil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainEntregaCarga extends AppCompatActivity implements View.OnClickListener {

    private Button btn_entrega_carga;
    private Button btn_no_entrega_carga;
    private EditText odtAentregar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entrega_carga);
        btn_entrega_carga = (Button) findViewById(R.id.btnEntregaCarga);
        btn_entrega_carga.setOnClickListener(this);
        btn_no_entrega_carga = (Button) findViewById(R.id.btnNoEntregaCarga);
        btn_no_entrega_carga.setOnClickListener(this);
        odtAentregar = (EditText) findViewById(R.id.txtODT);
        odtAentregar.setFilters((InputFilter[]) getIntent().getExtras().get("odt"));
        /*InputFilter[] filtros = new InputFilter[1];
        filtros[0] = new InputFilter.AllCaps();
        odtAentregar.setFilters(filtros);*/
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnEntregaCarga: {

                Intent intento = new Intent(MainEntregaCarga.this, MainFirma.class);
                startActivity(intento);
                finish();
                System.gc();
                finish();
                break;
            }
            case R.id.btnNoEntregaCarga: {

                Intent intento = new Intent(MainEntregaCarga.this, MainMotivoNoEntrega.class);
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
