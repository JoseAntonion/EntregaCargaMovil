package app.com.balvarez.entregacargamovil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class MainMotivoNoEntrega extends AppCompatActivity implements View.OnClickListener {

    private Button btn_finalizar;
    private Button btn_limpiar;
    private Spinner spn_motivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_motivo_no_entrega);
        btn_finalizar = (Button) findViewById(R.id.btnFinalizar);
        btn_finalizar.setOnClickListener(this);
        btn_limpiar = (Button) findViewById(R.id.btnLimpiar);
        btn_limpiar.setOnClickListener(this);
        spn_motivo=(Spinner) findViewById(R.id.spnMotivo);
        //spinner1.getAdapter().clear()
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnFinalizar: {

                Intent intento = new Intent(MainMotivoNoEntrega.this, MainODT.class);
                startActivity(intento);
                finish();
                System.gc();
                finish();
                break;
            }
            case R.id.btnVolver: {

                Intent intento = new Intent(MainMotivoNoEntrega.this, MainMotivoNoEntrega.class);
                startActivity(intento);
                finish();
                System.gc();
                finish();
                break;
            }
            case R.id.btnLimpiar: {
                spn_motivo.setAdapter(null);

            }
            default:
                break;
        }
    }
}
