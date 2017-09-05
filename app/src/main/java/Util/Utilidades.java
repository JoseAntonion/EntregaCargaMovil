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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import To.ArchivoOdtPorPatenteTO;
import To.EstadosOdtTO;

public class Utilidades {

    private String[] recibeSplit;
    private String[] recibeSplitAux;

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

    public void creaDirectorio() {

        File nomArchivo = null;
        FileWriter lfilewriter = null;
        BufferedWriter lout = null;
        File lroot = Environment.getExternalStorageDirectory();
        Globales.rutaArchivos = lroot.getAbsolutePath();
        Globales.rutaArchivos2 = Globales.rutaArchivos+"/EntregaCarga/Data/";
        // CREA DIRECTORIO Y ARCHIVO LOCAL
        try {
            if (lroot.canWrite()) {
                File dir = new File(Globales.rutaArchivos
                        + "/EntregaCarga/Data/");
                if (!dir.exists()) {
                    dir.mkdirs();
                    nomArchivo = new File(dir, "ArchivoDatosEntrega.txt");
                    lfilewriter = new FileWriter(nomArchivo);
                }
            }
            Globales.odtsXpatente = Globales.rutaArchivos
                    + "/EntregaCarga/Data/ArchivoDatosEntrega.txt";
        } catch (IOException e) {
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
                ex.write("\n" + lista.get(i).getPlanilla() + "~" + lista.get(i).getNumeroODT() + "~" + lista.get(i).getEstadoODT() + "~" + lista.get(i).getFormaPago() + "~" + String.valueOf(lista.get(i).getNumeroPiezas()) + "~" + lista.get(i).getCodBarra());
            }
            ex.close();
        } catch (Exception var17) {
            Log.e("error", "Error al escribir fichero a memoria interna");
        } finally {
            try {
                if (lfilewriter != null) {
                    lfilewriter.close();
                }
            } catch (IOException var16) {
                var16.printStackTrace();
            }

        }
    }

    public int leeCantidadOdtsArchivo() throws IOException, ClientProtocolException, JSONException {
        FileReader fr = null;
        ArchivoOdtPorPatenteTO archivo;
        ArrayList<ArchivoOdtPorPatenteTO> listaArchivo = new ArrayList<>();
        String aux = "";

        try {
            fr = new FileReader(Globales.odtsXpatente);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            if (s != null) {
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
                } while (s != null);
            }

        } catch (Exception ex) {
            Log.e("error", "Error al leer fichero desde memoria interna");
            ex.printStackTrace();
        } finally {
            try {
                if (fr != null) {
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
            if (s != null) {
                do {
                    if (!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("XXXXXXXXXXXXXXXXXX")) {
                        this.recibeSplit = s.split("~");
                        if (!aux.equals(recibeSplit[1])) {
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
                } while (s != null);
            }

        } catch (Exception ex) {
            Log.e("error", "Error al leer fichero desde memoria interna");
            ex.printStackTrace();
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return listaArchivo;

    }

    public String buscaFormaPagoOdt(String odt) throws IOException, ClientProtocolException, JSONException {
        String formaPago = "";
        FileReader fr = null;
        //Boolean encontrado = false;

        try {
            fr = new FileReader(Globales.odtsXpatente);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            if (s != null) {
                while (s != null) {
                    if (!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("XXXXXXXXXXXXXXXXXX")) {
                        this.recibeSplit = s.split("~");
                        if (odt.equals(recibeSplit[1].toString())) {
                            formaPago = recibeSplit[3].toString();
                            break;
                        }
                        //s = br.readLine();
                    }
                    s = br.readLine();
                }
                if (formaPago.equals("")) {
                    formaPago = "NA";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formaPago;
    }

    public int leeCantidadBultosODT(String odt) throws IOException, ClientProtocolException, JSONException {
        int cantidadBultos = 0;
        FileReader fr = null;

        try {
            fr = new FileReader(Globales.odtsXpatente);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            if (s != null) {
                while (s != null) {
                    if (!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("XXXXXXXXXXXXXXXXXX")) {
                        this.recibeSplit = s.split("~");
                        if (odt.equals(recibeSplit[1].toString())) {
                            cantidadBultos = Integer.parseInt(recibeSplit[4]);
                            break;
                        }
                    }
                    s = br.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cantidadBultos;
    }

    public Boolean validaBulto(String codBarra) throws IOException, ClientProtocolException, JSONException {
        Boolean valido = false;
        FileReader fr = null;

        try {
            fr = new FileReader(Globales.odtsXpatente);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            if (s != null) {
                while (s != null) {
                    if (!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("XXXXXXXXXXXXXXXXXX")) {
                        this.recibeSplit = s.split("~");
                        if (codBarra.equals(recibeSplit[5].toString())) {
                            valido = true;
                            break;
                        }
                    }
                    s = br.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return valido;
    }

    public EstadosOdtTO buscaLeidosFaltantes() throws  IOException, ClientProtocolException, JSONException{
        EstadosOdtTO leidosFaltantes = new EstadosOdtTO();
        int leidos = 0;
        int faltantes = 0;
        FileReader fr = null;
        String odtL = "";
        String odtF = "";

        try {
            fr = new FileReader(Globales.odtsXpatente);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            if (s != null) {
                while (s != null) {
                    if (!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("XXXXXXXXXXXXXXXXXX")) {
                        this.recibeSplit = s.split("~");
                        if (recibeSplit[2].toString().equals("99")) {
                            if(!odtL.equals(recibeSplit[1])){
                                leidos++;
                                odtL = recibeSplit[1];
                            }
                        }else{
                            if(!odtF.equals(recibeSplit[1])){
                                faltantes++;
                                odtF = recibeSplit[1];
                            }
                        }

                    }
                    s = br.readLine();
                }
                leidosFaltantes.setEntregadas(String.valueOf(leidos));
                leidosFaltantes.setFaltantes(String.valueOf(faltantes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leidosFaltantes;
    }

    public void cambiaEstadoOdtArchivo(String odt) throws  IOException, ClientProtocolException,JSONException{

        FileReader fr = null;

        try {
            fr = new FileReader(Globales.odtsXpatente);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            if (s != null) {
                while (s != null) {
                    if (!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("XXXXXXXXXXXXXXXXXX")) {
                        this.recibeSplit = s.split("~");
                        if (odt.equals(recibeSplit[1])) {
                            String sAux = s;
                            EliminaFilaArchivo(Globales.odtsXpatente,s);
                            recibeSplitAux = sAux.split("~");
                            sAux = recibeSplitAux[0]+"~"+recibeSplitAux[1]+"~99~"+recibeSplitAux[3]+"~"
                                    +recibeSplitAux[4]+"~"+recibeSplitAux[5];
                            EscribeLineaArchivo(sAux);
                        }

                    }
                    s = br.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void EliminaFilaArchivo(String file, String lineToRemove) {

        try {

            File inFile = new File(file);

            if (!inFile.isFile()) {
                System.out.println("Parameter is not an existing file");
                return;
            }

            //Construct the new file that will later be renamed to the original filename.
            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(file));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            //Read from the original file and write to the new
            //unless content matches data to be removed.
            while ((line = br.readLine()) != null) {

                if (!line.trim().equals(lineToRemove)) {

                    pw.println(line);
                    pw.flush();
                }
            }
            pw.close();
            br.close();

            //Delete the original file
            if (!inFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            //Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(inFile))
                System.out.println("Could not rename file");

        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void EscribeLineaArchivo(String linea) {
        //aki
        File lfile = null;
        FileWriter lfilewriter = null;
        try {
            lfile = new File(Globales.odtsXpatente);
            lfilewriter = new FileWriter(lfile, true);
            BufferedWriter ex = new BufferedWriter(lfilewriter);
            ex.write("\n" +linea);
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

    public String validaEstadoOdt(String odt) throws  IOException,ClientProtocolException,JSONException{
        String estado = "";
        FileReader fr = null;

        try {
            fr = new FileReader(Globales.odtsXpatente);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            if (s != null) {
                while (s != null) {
                    if (!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("XXXXXXXXXXXXXXXXXX")) {
                        this.recibeSplit = s.split("~");
                        if (odt.equals(recibeSplit[1].toString())) {
                            estado = recibeSplit[2];
                            break;
                        }
                    }
                    s = br.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return estado;
    }

    public Boolean BuscaODT(String odt) throws  IOException,ClientProtocolException,JSONException{
        Boolean encontrado = false;
        FileReader fr = null;

        try {
            fr = new FileReader(Globales.odtsXpatente);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            if (s != null) {
                while (s != null) {
                    if (!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("XXXXXXXXXXXXXXXXXX")) {
                        this.recibeSplit = s.split("~");
                        if (odt.equals(recibeSplit[1].toString())) {
                            encontrado = true;
                            break;
                        }
                    }
                    s = br.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encontrado;
    }
}
