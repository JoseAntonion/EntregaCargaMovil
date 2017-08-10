package app.com.balvarez.entregacargamovil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainFirma extends AppCompatActivity implements View.OnClickListener {

    private Button btn_finalizar;
    private Button btn_limpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_firma);
        btn_finalizar = (Button) findViewById(R.id.btnFinalizar);
        btn_finalizar.setOnClickListener(this);
        btn_limpiar = (Button) findViewById(R.id.btnLimpiar);
        btn_limpiar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnFinalizar: {
                Intent intent = new Intent(MainFirma.this, MainODT.class);
                startActivity(intent);
                finish();
                System.gc();
                finish();
            }
            case R.id.btnLimpiar: {
                Intent intent = new Intent(MainFirma.this, MainFirma.class);
                startActivity(intent);
                finish();
                System.gc();
                finish();
            }
            default:
                break;
        }
    }
}
