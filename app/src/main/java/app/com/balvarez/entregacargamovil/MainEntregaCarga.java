package app.com.balvarez.entregacargamovil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainEntregaCarga extends AppCompatActivity implements View.OnClickListener {

    private Button btn_entrega_carga;
    private Button btn_no_entrega_carga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entrega_carga);
        btn_entrega_carga = (Button) findViewById(R.id.btnEntregaCarga);
        btn_entrega_carga.setOnClickListener(this);
        btn_no_entrega_carga = (Button) findViewById(R.id.btnNoEntregaCarga);
        btn_no_entrega_carga.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnEntregaCarga: {
                Intent intent = new Intent(MainEntregaCarga.this, MainFirma.class);
                startActivity(intent);
                finish();
                System.gc();
                finish();
            }
            case R.id.btnNoEntregaCarga: {
                Intent intent = new Intent(MainEntregaCarga.this, MainMotivoNoEntrega.class);
                startActivity(intent);
                finish();
                System.gc();
                finish();
            }

        }
    }
}
