package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainEntregaCargaMovil extends AppCompatActivity {
    //prueba
    private Button btn_siguiente;
    private Button btn_limpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entrega_carga_movil);
        btn_siguiente = (Button) findViewById(R.id.btnSiguiente);
        btn_siguiente.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainEntregaCargaMovil.this, MainResumenPlanilla.class);
                startActivity(intent);
                System.gc();
                finish();
            }
        });
        btn_limpiar = (Button) findViewById(R.id.btnLimpiar);
        btn_limpiar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainEntregaCargaMovil.this, MainEntregaCargaMovil.class);
                startActivity(intent);
                System.gc();
                finish();
            }
        });


    }




}
