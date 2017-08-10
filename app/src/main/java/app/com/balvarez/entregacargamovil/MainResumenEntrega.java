package app.com.balvarez.entregacargamovil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainResumenEntrega extends AppCompatActivity implements View.OnClickListener {

    private Button btn_finalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_resumen_entrega);
        btn_finalizar = (Button) findViewById(R.id.btnFinalizar);
        btn_finalizar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnFinalizar: {

                Intent intento = new Intent(MainResumenEntrega.this, MainEntregaCargaMovil.class);
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
