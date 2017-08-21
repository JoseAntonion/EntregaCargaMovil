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

    private Button btn_ingresar;
    private Button btn_Volver;
    private ListView ls_lista_odts;
    private Utilidades util = new Utilidades();
    private ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_resumen_planilla);
        btn_ingresar = (Button) findViewById(R.id.btnSiguiente);
        btn_ingresar.setOnClickListener(this);
        /*btn_ingresar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intento = new Intent(MainResumenPlanilla.this, MainODT.class);
                startActivity(intento);
                System.gc();
                finish();
            }
        });*/
        btn_Volver = (Button) findViewById(R.id.btnVolver);
        btn_Volver.setOnClickListener(this);
        ls_lista_odts = (ListView) findViewById(R.id.lstResumenPlanilla);
        try {
            ArrayList<ArchivoOdtPorPatenteTO> contenidoArchivo = new ArrayList<>();
            ArrayList<String> arregloStringAdaptador = new ArrayList<>();
            contenidoArchivo = util.cargaDesdeArchivo();
            for (int i=0 ; i < contenidoArchivo.size() ; i++){
                arregloStringAdaptador.add(contenidoArchivo.get(i).getNumeroODT()+"                                "+contenidoArchivo.get(i).getNumeroPiezas());
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
    }

    @Override
    public void onClick(View v) {

            switch (v.getId()) {

                case R.id.btnSiguiente: {
                    Intent intento = new Intent(MainResumenPlanilla.this, MainODT.class);
                        startActivity(intento);
                        //System.gc();
                       finish();
                       break;

                }
                case R.id.btnVolver: {

                    Intent intento = new Intent(MainResumenPlanilla.this, MainEntregaCargaMovil.class);
                    startActivity(intento);
                    //System.gc();
                    finish();
                    break;
                }

                default:
                    break;
            }


    }


   /* public class LlenaListaOdts extends AsyncTask<Void,Void,String>{

        @Override
        protected ArrayAdapter<ArchivoOdtPorPatenteTO> doInBackground(Void... params) {
            try {
                ArrayList<ArchivoOdtPorPatenteTO> contenidoArchivo = new ArrayList<>();
                ArrayList<String> contenidoArchivoPrueba = new ArrayList<>();
                contenidoArchivo = util.leeArchivo();
                //contenidoArchivoPrueba = util.cargaDesdeArchivoPrueba();
                ArrayAdapter<ArchivoOdtPorPatenteTO> adaptador = new ArrayAdapter<ArchivoOdtPorPatenteTO>(this,android.R.layout.simple_list_item_1,contenidoArchivo);
                //ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contenidoArchivoPrueba);
                ls_lista_odts.setAdapter(adaptador);

        *//*} catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();*//*
            }catch (Exception e) {
                e.printStackTrace();
            }
            return adaptador;
        }
    }*/

}
