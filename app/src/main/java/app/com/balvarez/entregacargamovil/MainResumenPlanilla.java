package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainResumenPlanilla extends AppCompatActivity implements View.OnClickListener {

    private Button btn_ingresar;
    private Button btn_Volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_resumen_planilla);
        btn_ingresar = (Button) findViewById(R.id.btnSiguiente);
        btn_ingresar.setOnClickListener(this);
        btn_Volver = (Button) findViewById(R.id.btnVolver);
        btn_Volver.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnSiguiente: {

                Intent intento = new Intent(MainResumenPlanilla.this, MainODT.class);
                startActivity(intento);
                finish();
                System.gc();
                finish();
                break;
            }
            case R.id.btnVolver: {

                Intent intento = new Intent(MainResumenPlanilla.this, MainEntregaCargaMovil.class);
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
