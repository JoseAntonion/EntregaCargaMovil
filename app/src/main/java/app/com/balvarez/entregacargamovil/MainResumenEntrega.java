package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import To.ArchivoOdtPorPatenteTO;
import Util.Globales;
import Util.Utilidades;

public class MainResumenEntrega extends AppCompatActivity implements View.OnClickListener {

    private Button btn_finalizar;
    private Utilidades util;
    private ArrayAdapter<String> adaptador;
    private ListView lst_resumen_entrega;
    private String estado;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        util = new Utilidades();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_resumen_entrega);
        btn_finalizar = (Button) findViewById(R.id.btnLimpiar);
        btn_finalizar.setOnClickListener(this);
        lst_resumen_entrega = (ListView) findViewById(R.id.lstResumenEntrega);
        try {
            ArrayList<ArchivoOdtPorPatenteTO> contenidoArchivo = new ArrayList<>();
            ArrayList<String> arregloStringAdaptador = new ArrayList<>();
            contenidoArchivo = util.cargaDesdeArchivo();
            for (int i=0 ; i < contenidoArchivo.size() ; i++){
                if(contenidoArchivo.get(i).getEstadoODT().equals("99"))
                    estado = "Entregado";
                if(contenidoArchivo.get(i).getEstadoODT().equals("55"))
                    estado = "No Entregado";
                if(contenidoArchivo.get(i).getEstadoODT().equals("57"))
                    estado = "Reingreso";
                arregloStringAdaptador.add(contenidoArchivo.get(i).getNumeroODT()+"          "+estado);
            }
            adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arregloStringAdaptador);
            //ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contenidoArchivoPrueba);
            lst_resumen_entrega.setAdapter(adaptador);
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

            case R.id.btnLimpiar: {
                MensajeFinReparto();
                break;
            }
            default:
                break;
        }
    }

    private AlertDialog MensajeFinReparto() {
        final String DEFAULT_TITLE = "Entrega Carga Movil";
        final String DEFAULT_MESSAGE = "Desea entregar otra ODT ?";
        final String DEFAULT_YES = "Si";
        final String DEFAULT_NO = "No";


        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(DEFAULT_TITLE);
        downloadDialog.setMessage(DEFAULT_MESSAGE);
        downloadDialog.setPositiveButton(DEFAULT_YES,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainResumenEntrega.this, MainODT.class);
                        startActivity(intent);
                        finish();
                        System.gc();
                        finish();
                    }
                });
        downloadDialog.setNegativeButton(DEFAULT_NO,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainResumenEntrega.this, MainEntregaCargaMovil.class);
                        startActivity(intent);
                        finish();
                        System.gc();
                        finish();
                    }
                });

        return downloadDialog.show();
    }
}
