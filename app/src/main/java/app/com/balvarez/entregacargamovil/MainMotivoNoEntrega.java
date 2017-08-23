package app.com.balvarez.entregacargamovil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import Util.Globales;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                Intent intento = new Intent(MainMotivoNoEntrega.this, MainODT.class);
                startActivity(intento);
                finish();
                System.gc();
                finish();
                break;
            }
            case R.id.btnTomarFoto: {
                //tomarFoto();

                //*****************************************************************

                /*String file = Globales.rutaArchivos + getCode() + ".jpg";
                File mi_foto = new File( file );
                try {
                    mi_foto.createNewFile();
                } catch (IOException ex) {
                    Log.e("ERROR ", "Error:" + ex);
                }
                //
                Uri uri = Uri.fromFile( mi_foto );
                //Abre la camara para tomar la foto
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Guarda imagen
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                //Retorna a la actividad
                startActivityForResult(cameraIntent, 0);*/

                //****************************************************************************

                //Creamos el Intent para llamar a la Camara
                Intent cameraIntent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //Creamos una carpeta en la memeria del terminal
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
            }
            case R.id.btnLimpiar: {
                spn_motivo.setAdapter(null);
            }
            default:
                break;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getCode()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoCode = "pic_" + date;
        return photoCode;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void tomarFoto() {
        Intent foto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(foto.resolveActivity(getPackageManager()) != null )
        {
            startActivityForResult(foto,REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Comprovamos que la foto se a realizado
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //Creamos un bitmap con la imagen recientemente
            //almacenada en la memoria
            Bitmap bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/Fotos/" + NombreFoto);
            //Añadimos el bitmap al imageView para
            //mostrarlo por pantalla
            //img.setImageBitmap(bMap);
            txtNombreFoto.setText(NombreFoto);
            txtNombreFoto.setVisibility(View.VISIBLE);
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap)extras.get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            //MAS DE UNA FOTO
            *//*if(listaFotos==null)
                listaFotos = new ArrayList<FotoTO>();

            listaFotos.add(new FotoTO("1",byteArray));*//*

        }

    }*/
}
