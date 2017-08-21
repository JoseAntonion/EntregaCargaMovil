package Util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import To.ArchivoOdtPorPatenteTO;

public class Utilidades {

    private String[] recibeSplit;

    public boolean verificaConexion(Context ctx) {
        boolean bConectado = false;
        ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        for (int i = 0; i < 2; i++) {
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                bConectado = true;
            }
        }
        return bConectado;
    }

    public void creaDirectorio(){

        File nomArchivo = null;
        FileWriter lfilewriter = null;
        BufferedWriter lout = null;
        File lroot = Environment.getExternalStorageDirectory();

        // CREA DIRECTORIO Y ARCHIVO LOCAL
        try {
            if (lroot.canWrite()) {
                File dir = new File(lroot.getAbsolutePath()
                        + "/EntregaCarga/Data/");
                if (!dir.exists()) {
                    dir.mkdirs();
                    nomArchivo = new File(dir, "ArchivoDatosEntrega.txt");
                    lfilewriter = new FileWriter(nomArchivo);
                }
            }
            Globales.odtsXpatente = lroot.getAbsolutePath()
                    + "/EntregaCarga/Data/ArchivoDatosEntrega.txt";
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void escribirEnArchivo(ArrayList<ArchivoOdtPorPatenteTO> lista) {
        File lfile = null;
        FileWriter lfilewriter = null;

        try {
            lfile = new File(Globales.odtsXpatente);
            lfilewriter = new FileWriter(lfile, false);
            BufferedWriter ex = new BufferedWriter(lfilewriter);
            for (int i = 0; i < lista.size(); i++) {
                //ex.newLine();
                ex.write("\n"+ lista.get(i).getPlanilla() + "~" +lista.get(i).getNumeroODT() + "~" +lista.get(i).getEstadoODT() + "~" +lista.get(i).getFormaPago() +"~"+ String.valueOf(lista.get(i).getNumeroPiezas()) +"~"+ lista.get(i).getCodBarra());
            }
            ex.close();
        } catch (Exception var17) {
            Log.e("error", "Error al escribir fichero a memoria interna");
        } finally {
            try {
                if(lfilewriter != null) {
                    lfilewriter.close();
                }
            } catch (IOException var16) {
                var16.printStackTrace();
            }

        }
    }

   /* public void escribirGrabarParteInspector() {
        File lfile = null;
        FileWriter lfilewriter = null;

        try {
            Date date = new Date();
            String fecha = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss").format((Calendar.getInstance().getTime()));
            //----------------------------------------------------------------


            StringBuilder sb = new StringBuilder();
            for(int i=0;i<ActivityPuntosAControlar.listaPuntosAControlar.size();i++)
            {
                if(ActivityPuntosAControlar.listaPuntosAControlar.get(i).isInfraccion())
                {
                    if(ActivityPuntosAControlar.listaPuntosAControlar.get(i).getInfractores()!=null && ActivityPuntosAControlar.listaPuntosAControlar.get(i).getInfractores().size()>0 )
                    {
                        for(int j = 0;j<ActivityPuntosAControlar.listaPuntosAControlar.get(i).getInfractores().size();j++)
                        {
                            sb.append(ActivityPuntosAControlar.listaPuntosAControlar.get(i).getId()+",");
                            sb.append(ActivityPuntosAControlar.listaPuntosAControlar.get(i).getTotal()+",");
                            sb.append(ActivityPuntosAControlar.listaPuntosAControlar.get(i).getInfractores().get(j).getRut()+",");
                            sb.append(ActivityPuntosAControlar.listaPuntosAControlar.get(i).getObservacion().replace(" ","%20").replace("\n","%20")+"~" ) ;

                        }
                    }
                }
            }

            String chorizo = sb.toString();
            String rut = Usuario.getRut();
            String caratula = Control.getCaratula();
            //----------------------------------------------------------------
            lfile = new File(Globales.archivoGrabaParteInspector);
            lfilewriter = new FileWriter(lfile, true);
            BufferedWriter ex = new BufferedWriter(lfilewriter);
            ex.write("\n" + rut + "~~" + caratula+"~~"+chorizo);
            ex.close();
        } catch (Exception var17) {
            Log.e("error", "Error al escribir fichero a memoria interna");
        } finally {
            try {
                if(lfilewriter != null) {
                    lfilewriter.close();
                }
            } catch (IOException var16) {
                var16.printStackTrace();
            }

        }
    }*/

    public int leeCantidadOdtsArchivo() throws IOException, ClientProtocolException, JSONException {
        FileReader fr = null;
        ArchivoOdtPorPatenteTO archivo;
        ArrayList<ArchivoOdtPorPatenteTO> listaArchivo = new ArrayList<>();
        String aux = "";

        try {
            fr = new FileReader(Globales.odtsXpatente);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            if(s != null) {
                do {
                    if (!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("XXXXXXXXXXXXXXXXXX")) {
                        this.recibeSplit = s.split("~");
                            archivo = new ArchivoOdtPorPatenteTO();
                            archivo.setPlanilla(recibeSplit[0]);
                            archivo.setNumeroODT(recibeSplit[1]);
                            archivo.setEstadoODT(recibeSplit[2]);
                            archivo.setFormaPago(recibeSplit[3]);
                            archivo.setNumeroPiezas(Integer.parseInt(recibeSplit[4]));
                            archivo.setCodBarra(recibeSplit[5]);
                            listaArchivo.add(archivo);
                            aux = recibeSplit[1];
                    }
                    s = br.readLine();
                }while(s!=null);
            }

        } catch (Exception ex) {
            Log.e("error", "Error al leer fichero desde memoria interna");
            ex.printStackTrace();
        } finally {
            try {
                if(fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return listaArchivo.size();
    }

    public ArrayList<ArchivoOdtPorPatenteTO> cargaDesdeArchivo() throws IOException, ClientProtocolException, JSONException {

        FileReader fr = null;
        ArchivoOdtPorPatenteTO archivo;
        ArrayList<ArchivoOdtPorPatenteTO> listaArchivo = new ArrayList<>();
        String aux = "";

        try {
            fr = new FileReader(Globales.odtsXpatente);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            if(s != null) {
                do {
                    if (!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("XXXXXXXXXXXXXXXXXX")) {
                        this.recibeSplit = s.split("~");
                        if(!aux.equals(recibeSplit[1])){
                            archivo = new ArchivoOdtPorPatenteTO();
                            archivo.setPlanilla(recibeSplit[0]);
                            archivo.setNumeroODT(recibeSplit[1]);
                            archivo.setEstadoODT(recibeSplit[2]);
                            archivo.setFormaPago(recibeSplit[3]);
                            archivo.setNumeroPiezas(Integer.parseInt(recibeSplit[4]));
                            archivo.setCodBarra(recibeSplit[5]);
                            listaArchivo.add(archivo);
                            aux = recibeSplit[1];
                        }
                    }
                    s = br.readLine();
                }while(s!=null);
            }

        } catch (Exception ex) {
            Log.e("error", "Error al leer fichero desde memoria interna");
            ex.printStackTrace();
        } finally {
            try {
                if(fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return listaArchivo;

    }

}
