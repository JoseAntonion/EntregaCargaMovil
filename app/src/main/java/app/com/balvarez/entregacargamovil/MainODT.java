package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainODT extends AppCompatActivity implements View.OnClickListener {
    private Button btn_finreparto;//btnScanODT
    private Button btn_Volver;
    private Button btn_scan_odt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_odt);
        btn_finreparto = (Button) findViewById(R.id.btnFinReparto);
        btn_finreparto.setOnClickListener(this);
        btn_Volver = (Button) findViewById(R.id.btnVolver);
        btn_Volver.setOnClickListener(this);
        btn_scan_odt = (Button) findViewById(R.id.btnScanODT);
        btn_scan_odt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnFinReparto: {

                Intent intento = new Intent(MainODT.this, MainResumenEntrega.class);
                startActivity(intento);
                finish();
                System.gc();
                finish();
                break;
            }
            case R.id.btnVolver: {

                Intent intento = new Intent(MainODT.this, MainResumenPlanilla.class);
                startActivity(intento);
                finish();
                System.gc();
                finish();
                break;
            }
            case R.id.btnScanODT: {

                Intent intento = new Intent(MainODT.this, MainEntregaCarga.class);
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
