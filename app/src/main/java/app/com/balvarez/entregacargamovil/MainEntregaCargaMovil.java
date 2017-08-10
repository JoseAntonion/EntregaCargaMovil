package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainEntregaCargaMovil extends AppCompatActivity implements View.OnClickListener {

    private Button btn_siguiente;
    private Button btn_limpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entrega_carga_movil);
        btn_siguiente = (Button) findViewById(R.id.btnSiguiente);
        btn_siguiente.setOnClickListener(this);
        btn_limpiar = (Button) findViewById(R.id.btnLimpiar);
        btn_limpiar.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnSiguiente: {
                Intent intent = new Intent(MainEntregaCargaMovil.this, MainResumenPlanilla.class);
                startActivity(intent);
                System.gc();
                finish();
            }
            case R.id.btnLimpiar: {
                Intent intent = new Intent(MainEntregaCargaMovil.this, MainEntregaCargaMovil.class);
                startActivity(intent);
                System.gc();
                finish();
            }
            default:
                break;
        }
    }

}
