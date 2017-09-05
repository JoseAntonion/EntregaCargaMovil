package app.com.balvarez.entregacargamovil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import To.ValidaTO;
import Util.Utilidades;
import Util.WebServices;

public class MainMotivoNoEntrega extends AppCompatActivity implements View.OnClickListener {

    private Button btn_finalizar;
    private Button btn_limpiar;
    private Button btn_tomarFoto;
    private Spinner spn_motivo;
    private ArrayAdapter spinner_adapter;
    private TextView txtNombreFoto;
    private Intent recibir;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String ODT = "";
    private String NombreFoto = "fotoPrueba.jpg";
    private String encodedImage2;
    private Activity activity;
    private Utilidades util;
    private Bitmap bMap;
    private  int RESULT_LOAD_IMG = 0;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String imgDecodableString;
    private String userChoosenTask;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        util = new Utilidades();
        activity = this;
        recibir = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_motivo_no_entrega);
        ODT = recibir.getStringExtra("odt");
        btn_finalizar = (Button) findViewById(R.id.btnFinalizar);
        btn_finalizar.setOnClickListener(this);
        btn_limpiar = (Button) findViewById(R.id.btnLimpiar);
        btn_limpiar.setOnClickListener(this);
        btn_tomarFoto = (Button) findViewById(R.id.btnTomarFoto);
        btn_tomarFoto.setOnClickListener(this);
        spn_motivo = (Spinner) findViewById(R.id.spnMotivo);
        txtNombreFoto = (TextView) findViewById(R.id.txtNombreFoto);
        txtNombreFoto.setVisibility(View.INVISIBLE);
        spinner_adapter = ArrayAdapter.createFromResource( this, R.array.reingreso , android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_motivo.setAdapter(spinner_adapter);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnFinalizar: {
                new CapturaImagen().execute();
                /*Intent intento = new Intent(MainMotivoNoEntrega.this, MainODT.class);
                startActivity(intento);
                finish();
                System.gc();
                finish();*/
                break;
            }
            case R.id.btnTomarFoto: {
                //tomarFoto();
                Intent foto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(foto.resolveActivity(getPackageManager()) != null )
                {
                    startActivityForResult(foto,REQUEST_IMAGE_CAPTURE);
                }
                /*//Creamos el Intent para llamar a la Camara
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //Creamos una carpeta en la memoria del terminal
                File imagesFolder = new File(Globales.rutaArchivos2, "Fotos");
                imagesFolder.mkdirs();
                //añadimos el nombre de la imagen
                //File image = new File(imagesFolder, ODT+getCode()+".jpg");
                File image = new File(imagesFolder, NombreFoto);
                Uri uriSavedImage = Uri.fromFile(image);
                //Le decimos al Intent que queremos grabar la imagen
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                //Lanzamos la aplicacion de la camara con retorno (forResult)
                startActivityForResult(cameraIntent, 1);
                break;*/
                break;
            }
            case R.id.btnLimpiar: {
                spn_motivo.setAdapter(null);
            }
            default:
                break;
        }
    }

    public boolean tomarFoto()
    {
        try
        {
            Intent foto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(foto.resolveActivity(getPackageManager()) != null )
            {
                startActivityForResult(foto,REQUEST_IMAGE_CAPTURE);
            }

            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /*@SuppressLint("SimpleDateFormat")
    private String getCode()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoCode = "pic_" + date;
        return photoCode;
    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    /*private void tomarFoto() {
        Intent foto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(foto.resolveActivity(getPackageManager()) != null )
        {
            startActivityForResult(foto,REQUEST_IMAGE_CAPTURE);
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Comprovamos que la foto se a realizado
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap)extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            encodedImage2 = android.util.Base64.encodeToString(byteArray, Base64.NO_WRAP);
            //ListaFotosTO.lista.add(byteArray);

            //bMap = BitmapFactory.decodeFile(Globales.rutaArchivos2 + "/Fotos/" + NombreFoto);
            txtNombreFoto.setText(NombreFoto);
            txtNombreFoto.setVisibility(View.VISIBLE);
            spn_motivo.setAdapter(spinner_adapter);
        }

    }

   /* protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Comprovamos que la foto se a realizado
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //Creamos un bitmap con la imagen recientemente
            //almacenada en la memoria
            //Bitmap bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/Fotos/" + NombreFoto);
            bMap = BitmapFactory.decodeFile(Globales.rutaArchivos2 + "/Fotos/" + NombreFoto);
            encodedImage2 = encodeTobase64(bMap);
            //Añadimos el bitmap al imageView para
            //mostrarlo por pantalla
            //img.setImageBitmap(bMap);
            txtNombreFoto.setText(NombreFoto);
            txtNombreFoto.setVisibility(View.VISIBLE);
            spn_motivo.setAdapter(spinner_adapter);
        }
    }*/

    public class CapturaImagen extends AsyncTask<Void,Void,String> {

        /*Bitmap imagen = signature.getImage();
        File sd = Environment.getExternalStorageDirectory();
        File fichero = new File(sd, "Firma.jpg");*/

        File sd = Environment.getExternalStorageDirectory();
        //File fichero = new File(sd+"/Fotos/", NombreFoto);
        //Bitmap imagen = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() +"/Fotos/"+NombreFoto);
        //Bitmap imagen = bMap;
        WebServices ws = new WebServices();
        ValidaTO resp = new ValidaTO();
        ValidaTO resp2 = new ValidaTO();
        ProgressDialog MensajeProgreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MensajeProgreso = new ProgressDialog(activity);
            MensajeProgreso.setCancelable(false);
            MensajeProgreso.setIndeterminate(true);
            MensajeProgreso.setMessage("Ingresando Datos...");
            MensajeProgreso.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                if (sd.canWrite()) {
                    //ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                    //imagen.compress(Bitmap.CompressFormat.PNG, 100, arrayOutputStream);
                    //Globales.Imagen = getBitmapBytes(imagen);
                    //encodedImage2 = Base64.encodeToString(Globales.Imagen,Base64.DEFAULT);
                    //encodedImage2 = encodeTobase64(imagen);
                    //encodedImage2 = encodeTobase64(imagen);
                    /*fichero.createNewFile();
                    OutputStream os = new FileOutputStream(fichero);
                    imagen.compress(Bitmap.CompressFormat.JPEG, 90, os);
                    os.close();*/

                    //FotoTO fotos = ListaFotosTO.lista.pop();

                    //String base = android.util.Base64.encodeToString(ListaFotosTO.lista.pop(), Base64.NO_WRAP);

                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String imei = telephonyManager.getDeviceId();
                    resp = ws.GrabaImagen(imei,encodedImage2,"ACT",imei,imei,ODT);
                    //resp2 = ws.CambiaEstadoODT(ODT,"99",imei,"MainFirma", Globales.version,"EntregaCargaMovil",imei);
                    //util.cambiaEstadoOdtArchivo(ODT);

                    /*if(resp.getValida().equals("1")){
                        Toast.makeText(activity.getApplicationContext(),
                                resp.getMensaje(), Toast.LENGTH_LONG).show();
                    }else if(resp.getValida().equals("0")){
                        Toast.makeText(activity.getApplicationContext(),
                                "Problemas al guardar Imagen: "+resp.getMensaje(), Toast.LENGTH_LONG).show();
                    }*/
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (MensajeProgreso.isShowing())
                MensajeProgreso.dismiss();
            if(resp.getValida().equals("0")){
                Toast.makeText(activity.getApplicationContext(),
                        "Error al guardar - ws.GrabaImagen."+resp.getMensaje(), Toast.LENGTH_LONG).show();
                MensajeFinRepartoINCORRECTO(resp.getMensaje());
            }else if(resp.getValida().equals("1")){
                Toast.makeText(activity.getApplicationContext(),
                        "Imagen Guardada", Toast.LENGTH_LONG).show();
                /*if(resp2.getValida().equals("1")){
                    Toast.makeText(activity.getApplicationContext(),
                            resp2.getMensaje(), Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(activity.getApplicationContext(),
                            resp2.getMensaje(), Toast.LENGTH_LONG).show();*/
                MensajeFinRepartoCORRECTO();
            }
        }
    }

    private AlertDialog MensajeFinRepartoCORRECTO() {
        final String DEFAULT_TITLE = "Entrega Carga Movil";
        final String DEFAULT_MESSAGE = "Datos Guardados";
        final String DEFAULT_YES = "Aceptar";


        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(DEFAULT_TITLE);
        downloadDialog.setMessage(DEFAULT_MESSAGE);
        downloadDialog.setPositiveButton(DEFAULT_YES,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainMotivoNoEntrega.this, MainODT.class);
                        startActivity(intent);
                        finish();
                        System.gc();
                        finish();
                    }
                });

        return downloadDialog.show();
    }

    private AlertDialog MensajeFinRepartoINCORRECTO(String mensaje) {
        final String DEFAULT_TITLE = "Entrega Carga Movil";
        final String DEFAULT_MESSAGE = "Problemas en el proceso - Motivo No Entrega: "+mensaje;
        final String DEFAULT_YES = "Aceptar";


        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(DEFAULT_TITLE);
        downloadDialog.setMessage(DEFAULT_MESSAGE);
        downloadDialog.setPositiveButton(DEFAULT_YES,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainMotivoNoEntrega.this, MainODT.class);
                        startActivity(intent);
                        finish();
                        System.gc();
                        finish();
                    }
                });

        return downloadDialog.show();
    }

}
