package app.com.balvarez.entregacargamovil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import To.ArchivoOdtPorPatenteTO;
import Util.Globales;
import Util.Utilidades;

public class MainResumenPlanilla extends AppCompatActivity implements View.OnClickListener {

    private Button btn_siguente;
    private Button btn_Volver;
    private ListView ls_lista_odts;
    private Utilidades util = new Utilidades();
    private ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_resumen_planilla);
        btn_siguente = (Button) findViewById(R.id.btnSiguienteOdt);
        btn_siguente.setOnClickListener(this);
        btn_Volver = (Button) findViewById(R.id.btnVolverODT);
        btn_Volver.setOnClickListener(this);
        ls_lista_odts = (ListView) findViewById(R.id.lstResumenPlanilla);
        try {
            ArrayList<ArchivoOdtPorPatenteTO> contenidoArchivo = new ArrayList<>();
            ArrayList<String> arregloStringAdaptador = new ArrayList<>();
            contenidoArchivo = util.cargaDesdeArchivo();
            for (int i=0 ; i < contenidoArchivo.size() ; i++){
                if(!contenidoArchivo.get(i).getEstadoODT().equals("99")){
                    arregloStringAdaptador.add(contenidoArchivo.get(i).getNumeroODT()+"                                "+contenidoArchivo.get(i).getNumeroPiezas());
                }
            }
            adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arregloStringAdaptador);
            //ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contenidoArchivoPrueba);
            ls_lista_odts.setAdapter(adaptador);
            Globales.cantidadOriginalOdts = util.leeCantidadOdtsArchivo();
        /*} catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();*/
        }catch (Exception e) {
            e.printStackTrace();
        }
        Globales.totalValoresODT = 0;
        Globales.banderaTipoPago = "";
        Globales.registroOdtMultiples = null;
        Globales.primera = false;
        Globales.esCTACTE = "";
    }


    @Override
    public void onBackPressed()
    {
        Intent intento = new Intent(MainResumenPlanilla.this, MainEntregaCargaMovil.class);
        startActivity(intento);
        finish();
    }

    @Override
    public void onClick(View v) {

            switch (v.getId()) {

                case R.id.btnSiguienteOdt: {
                    // PRUEBAS IMPRESION ----------------------------------------------------------------
                   /* TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String imei = telephonyManager.getDeviceId();

                    //WebServices ws = new WebServices();
                    Globales.Impresora = "00:01:90:C2:C4:C6";
                    try {
                        //ws.retornaImpresoraPrueba(imei);
                        if(util.ConectarEpsonPrueba(this.getApplicationContext())){
                            //util.BoletaPrueba(this);
                            util.FacturaPrueba(this);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (EposException e) {
                        e.printStackTrace();
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }*/
                    // ----------------------------------------------------------------------------------

                    // PRUEBA DE PANTALLAS------------------------------------------------------------------
                    /*Intent intento = new Intent(MainResumenPlanilla.this, MainFirma.class);
                    startActivity(intento);
                    break;*/
                    //--------------------------------------------------------------------------------------


                    Intent intento = new Intent(MainResumenPlanilla.this, MainODT.class);
                    //Intent intento = new Intent(MainResumenPlanilla.this, MainFirma.class);
                    startActivity(intento);
                    break;

                }
                case R.id.btnVolverODT: {

                    Intent intento = new Intent(MainResumenPlanilla.this, MainEntregaCargaMovil.class);
                    startActivity(intento);
                    finish();
                    break;
                }

                default:
                    break;
            }


    }
}
