package Util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import com.epson.eposprint.Print;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.pdf417.PDF417Writer;

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
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import To.ArchivoDocElectronicoTO;
import To.ArchivoOdtPorPatenteTO;
import To.BoletaTO;
import To.CiudadTO;
import To.ComunaTO;
import To.EstadosOdtTO;
import To.FacturaTO;

public class Utilidades {

    private String[] recibeSplit;
    private String[] recibeSplitAux;

    static Print printer = null;

    public static Print getPrinter() {
        return printer;
    }
    public static void setPrinter(Print printer) {
        Utilidades.printer = printer;
    }

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

    public void creaDirectorioArchivos() {

        File nomArchivo = null;
        File nomArchivo2 = null;
        File nomArchivo3 = null;
        File nomArchivo4 = null;
        FileWriter lfilewriter = null;
        FileWriter lfilewriter2 = null;
        FileWriter lfilewriter3 = null;
        FileWriter lfilewriter4 = null;
        BufferedWriter lout = null;
        File lroot = Environment.getExternalStorageDirectory();
        Globales.rutaArchivos = lroot.getAbsolutePath();
        Globales.rutaArchivosFinal = Globales.rutaArchivos+"/EntregaCarga/Data/";
        // CREA DIRECTORIO Y ARCHIVOS LOCALES
        try {
            if (lroot.canWrite()) {
                File dir = new File(Globales.rutaArchivosFinal);
                if (!dir.exists()) {
                    dir.mkdirs();
                    nomArchivo = new File(dir, "ArchivoDatosEntrega.txt");
                    nomArchivo2 = new File(dir, "comunas.txt");
                    nomArchivo3 = new File(dir, "ciudades.txt");
                    nomArchivo4 = new File(dir, "docElectronico.txt");
                    lfilewriter = new FileWriter(nomArchivo);
                    lfilewriter2 = new FileWriter(nomArchivo2);
                    lfilewriter3 = new FileWriter(nomArchivo3);
                    lfilewriter4 = new FileWriter(nomArchivo4);
                }
            }
            Globales.odtsXpatente = Globales.rutaArchivosFinal+"ArchivoDatosEntrega.txt";
            Globales.Comunas = Globales.rutaArchivosFinal+"comunas.txt";
            Globales.Ciudades = Globales.rutaArchivosFinal+"ciudades.txt";
            Globales.docElectronico = Globales.rutaArchivosFinal+"docElectronico.txt";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void escribirEnArchivo(Object lista,String tipo) {
        File lfile = null;
        FileWriter lfilewriter = null;

        try {
            switch (tipo){
                case "ODTS":{

                    ArrayList<ArchivoOdtPorPatenteTO> listODT = (ArrayList<ArchivoOdtPorPatenteTO>)lista;
                    lfile = new File(Globales.odtsXpatente);
                    lfilewriter = new FileWriter(lfile, false);
                    BufferedWriter ex = new BufferedWriter(lfilewriter);
                    for (int i = 0; i < listODT.size(); i++) {
                        ex.write("\n" + listODT.get(i).getPlanilla() + "~" + listODT.get(i).getNumeroODT() + "~" + listODT.get(i).getEstadoODT()
                                + "~" + listODT.get(i).getFormaPago() + "~" + String.valueOf(listODT.get(i).getNumeroPiezas())
                                + "~" + listODT.get(i).getCodBarra()+"~"+listODT.get(i).getValorOdt());
                    }
                    ex.close();
                    break;
                }
                case "COMUNAS":{
                    ArrayList<ComunaTO> listComunas = (ArrayList<ComunaTO>)lista;
                    lfile = new File(Globales.Comunas);
                    lfilewriter = new FileWriter(lfile, false);
                    BufferedWriter ex = new BufferedWriter(lfilewriter);
                    for (int i = 0; i < listComunas.size(); i++) {
                        ex.write("\n" + listComunas.get(i).getCodCiudad() + "~" + listComunas.get(i).getCodComuna() + "~" + listComunas.get(i).getNombreComuna());
                    }
                    ex.close();
                    break;
                }
                case "CIUDADES":{
                    ArrayList<CiudadTO> listCiudades = (ArrayList<CiudadTO>)lista;
                    lfile = new File(Globales.Ciudades);
                    lfilewriter = new FileWriter(lfile, false);
                    BufferedWriter ex = new BufferedWriter(lfilewriter);
                    for (int i = 0; i < listCiudades.size(); i++) {
                        ex.write("\n" + listCiudades.get(i).getCodCiudad()
                                + "~" + listCiudades.get(i).getNombreCiudad());
                    }
                    ex.close();
                    break;
                }
                case "FACTURA":{
                    ArrayList<FacturaTO> listFactura = (ArrayList<FacturaTO>)lista;
                    lfile = new File(Globales.docElectronico);
                    lfilewriter = new FileWriter(lfile, false);
                    BufferedWriter ex = new BufferedWriter(lfilewriter);
                    for (int i = 0; i < listFactura.size(); i++) {
                        ex.write("\n" + listFactura.get(i).getRutFactura()
                                + "~" + listFactura.get(i).getRazonFactura()
                                + "~" + listFactura.get(i).getDireccionFactura()
                                + "~" + listFactura.get(i).getFonoFactura()
                                + "~" + listFactura.get(i).getGiroFactura()
                                + "~" + listFactura.get(i).getComunaFactura()
                                + "~" + listFactura.get(i).getCiudadFactura()
                                + "~" + listFactura.get(i).getTotalFactura());
                    }
                    ex.close();
                    break;

                }
                case "BOLETA":{
                    ArrayList<BoletaTO> listBoleta = (ArrayList<BoletaTO>)lista;
                    lfile = new File(Globales.docElectronico);
                    lfilewriter = new FileWriter(lfile, false);
                    BufferedWriter ex = new BufferedWriter(lfilewriter);
                    for (int i = 0; i < listBoleta.size(); i++) {
                        ex.write("\n" + listBoleta.get(i).getTotalBoleta());
                    }
                    ex.close();
                    break;

                }
                default:
                    break;
            }
        } catch (Exception var17) {
            Log.e("error", "Error al escribir fichero -"+tipo+"- a memoria interna");
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

    public ArrayList<ComunaTO> cargaDesdeArchivoComuna(String codCiudad) throws IOException, ClientProtocolException, JSONException {

        FileReader fr = null;
        ComunaTO comuna;
        ArrayList<ComunaTO> listaComunas = new ArrayList<>();
        String aux = "";

        try {
            fr = new FileReader(Globales.Comunas);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            if (s != null) {
                do {
                    if (!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("XXXXXXXXXXXXXXXXXX")) {
                        this.recibeSplit = s.split("~");
                        if(recibeSplit[0].toString().equals(codCiudad)){
                            comuna = new ComunaTO();
                            //comuna.setCodCiudad(recibeSplit[0]);
                            comuna.setCodComuna(recibeSplit[1]);
                            comuna.setNombreComuna(recibeSplit[2]);
                            listaComunas.add(comuna);
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
        return listaComunas;
    }

    public ArrayList<CiudadTO> cargaDesdeArchivoCiudad() throws IOException, ClientProtocolException, JSONException {

        FileReader fr = null;
        CiudadTO ciudad;
        ArrayList<CiudadTO> listaCiudades = new ArrayList<>();
        String aux = "";

        try {
            fr = new FileReader(Globales.Ciudades);
            BufferedReader br = new BufferedReader(fr);
            String s = br.readLine();
            if (s != null) {
                do {
                    if (!s.equalsIgnoreCase("") && !s.equalsIgnoreCase("XXXXXXXXXXXXXXXXXX")) {
                        this.recibeSplit = s.split("~");
                        ciudad = new CiudadTO();
                        ciudad.setCodCiudad(recibeSplit[0]);
                        ciudad.setNombreCiudad(recibeSplit[1]);
                        listaCiudades.add(ciudad);
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
        return listaCiudades;
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

    public int buscaValorOdt(String odt) throws IOException, ClientProtocolException, JSONException {
        int valor = 0;
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
                            valor = Integer.parseInt(recibeSplit[6].toString());
                            break;
                        }
                    }
                    s = br.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valor;
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

    public ArrayList<String> leeBultosMultiples(String odt) throws IOException, ClientProtocolException, JSONException {
        ArrayList<String> bultos = new ArrayList<>();
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
                            bultos.add(recibeSplit[5]);
                        }
                    }
                    s = br.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bultos;
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
                                if(!recibeSplit[2].toString().equals("57"))//EVITA QUE SE CONTABILICEN ODTS EN REINGRESO
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

    public void cambiaEstadoOdtArchivoENTREGADO(String odt) throws  IOException, ClientProtocolException,JSONException{

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

    public void cambiaEstadoOdtArchivoREINGRESO(String odt) throws  IOException, ClientProtocolException,JSONException{

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
                            sAux = recibeSplitAux[0]+"~"+recibeSplitAux[1]+"~57~"+recibeSplitAux[3]+"~"
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

    public String TraePlanillaDeODT(String odt) throws  IOException,ClientProtocolException,JSONException{
        String planilla = "";
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
                            planilla = recibeSplit[0].toString();
                            break;
                        }
                    }
                    s = br.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return planilla;
    }

    public Boolean ConectarEpsonPrueba(Context ctx) throws EposException {

        Boolean retorna = false;
        int deviceType = Print.DEVTYPE_BLUETOOTH;

        Print printer1 = new Print(ctx.getApplicationContext());
        int interval = 1000;

        try {

            if (printer != null) {
                try {
                    printer.closePrinter();
                    printer = null;
                } catch (Exception e) {
                    printer = null;
                }
            }

            if (printer == null) {
                printer1.openPrinter(deviceType, Globales.Impresora.trim(),
                        Print.TRUE, interval);
                setPrinter(printer1);
            }
            retorna = true;

        } catch (Exception e) {
            printer1 = null;
            retorna = false;
            throw e;
        }

        return retorna;
    }

    public void BoletaPrueba(Activity activity) throws UnsupportedEncodingException {
        final int SEND_TIMEOUT = 10 * 1000;

        int[] status = new int[1];
        int[] battery = new int[1];
        //String ted = archivoDocElectronicoTO.get(0).getTed().replace("�", "ú");
        /*String tedPrueba = "<TED version=\"1.0\"><DD><RE>89622400-K</RE><TD>39</TD><F>67</F><FE>2015-04-07</FE>" +
                "<RR>66666666-6</RR><RSR></RSR><MNT>4750</MNT><IT1>Segun venta</IT1><CAF version=\"1.0\"><DA><RE>89622400-K</RE>" +
                "<RS>PULLMAN CARGO S A</RS><TD>39</TD><RNG><D>1</D><H>100</H></RNG><FA>2014-10-06</FA><RSAPK>" +
                "<M>oANFuryTpJhE5tQAs5f5zZKACixwtpPHdn/Us6xVzDqCGOuy/MZ6qpTlwnh/4x8zzY6mXjhRrj+uQE1wwTf9xQ==</M>" +
                "<E>Aw==</E></RSAPK><IDK>100</IDK></DA><FRMA algoritmo=\"SHA1withRSA\">" +
                "UeGgP6fW8qu+3CXBLNJEqSi47bW661BEudr83dPQ3vs4Em3gluW1EccaCx+EVQsHk00+bEWPL0qeK6mItXKzKA==</FRMA></CAF>" +
                "<TSTED>2017-06-23T14:53:37</TSTED></DD><FRMT algoritmo=\"SHA1withRSA\">" +
                "PqMbqJV71RnhomkhgvBF4HhRydUu4J6LhHo6ugfr9nxRjvVNJQu/lny+Z2k2ajvPYUWKL9J+Wej6/VFvWqB45g==</FRMT></TED>";*/

        String tedPrueba = "<TED version=\"1.0\"><DD><RE>89622400-K</RE><TD>33</TD><F>10</F><FE>2017-09-08</FE><RR>17176015-1</RR><RSR>PRUEBA</RSR><MNT>4800</MNT><IT1>Según detalle ODT número 50000002694</IT1><CAF version=\"1.0\"><DA><RE>89622400-K</RE><RS>PULLMAN CARGO S A</RS><TD>33</TD><RNG><D>1</D><H>1000</H></RNG><FA>2014-07-15</FA><RSAPK><M>vjVjJLz3P/ctah5a6BUsPqZoiwasfp9v6lC/Y12jinrHlNvyCBgvinwnS1pWRrdjWO4XnXRfM1ZgYwnGvMG/Rw==</M><E>Aw==</E></RSAPK><IDK>100</IDK></DA><FRMA algoritmo=\"SHA1withRSA\">gwv/6+tNMDHczOVC545zbqJAH6QZNWts6kohJsSD4JLnjs/9yUkXuVZsmBmj7BlqPXy+IsSgklYYJccWtLG+Tw==</FRMA></CAF><TSTED>2017-09-08T15:39:02</TSTED></DD><FRMT algoritmo=\"SHA1withRSA\">LYrW2oDhLj+WNtzpc+Pm+DJUyhhu7CrlfcmAuU57HIzuRVxLFdFnd+Yy2rTX0ijXUTAt553ocYS/giR/ke6YWQ==</FRMT></TED>";

        Boolean retorna = false;
        Intent intent = new Intent();
        intent.putExtra("printername", "TM-P80");
        intent.putExtra("language", Builder.MODEL_ANK);
        Builder builder = null;
        intent = activity.getIntent();

        try {
            Print printer = Utilidades.getPrinter();
            builder = new Builder("TM-P80", Builder.MODEL_ANK);

            builder.addTextLineSpace(20);
            builder.addTextFont(builder.FONT_B);
            builder.addTextSize(1, 1);
            builder.addText(" prueba \n");
            builder.addText("GIRO: prueba \n");
            builder.addTextLineSpace(30);

            builder.addText("RUT :prueba \n\n");
            printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_RIGHT);
            builder.addTextLineSpace(20);
            builder.addText("SANTIAGO 21 DE OCTUBRE DE 2016\n");
            printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_CENTER);
            builder.addText("................................................................\n");
            printer = Utilidades.getPrinter();
            builder.addTextFont(builder.FONT_A);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("BOLETA ELECTRONICA N° prueba \n");
            printer = Utilidades.getPrinter();
            builder.addTextFont(builder.FONT_B);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            builder.addText("................................................................\n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            builder.addText("prueba  \n");
            builder.addText("................................................................\n");
            printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_CENTER);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("DETALLES DE PRODUCTOS\n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            builder.addText("................................................................\n");
            printer = Utilidades.getPrinter();

            //for (int i = 0; i < archivoDocElectronicoTO.size(); i++) {

            builder.addTextAlign(builder.ALIGN_LEFT);
            builder.addTextFont(builder.FONT_A);
            builder.addText(" prueba \n"); printer = Utilidades.getPrinter();
            builder.addTextFont(builder.FONT_B); builder.addText("SEGUN ODT prueba \n");
            printer = Utilidades.getPrinter();
            builder.addTextPosition(80);
            builder.addText(" X prueba                     TOTAL prueba \n");
            printer = Utilidades.getPrinter();

            //}


            builder.addTextLineSpace(20);
            builder.addTextStyle(Builder.FALSE,Builder.FALSE,Builder.TRUE,Builder.COLOR_1);
            builder.addTextPosition(300);
            builder.addText("Neto $ : ");
            builder.addText(" prueba  \n");
            printer = Utilidades.getPrinter();
            builder.addTextPosition(200);
            builder.addText("Monto I.V.A 19% $ : ");
            builder.addText(" prueba \n");
            printer = Utilidades.getPrinter();
            builder.addTextPosition(290);
            builder.addText("TOTAL $ : ");
            builder.addText("prueba  \n"); printer = Utilidades.getPrinter();


            // CODIGO TED PDF417

            try {

                //builder.addSymbol(Prueba, builder.SYMBOL_PDF417_STANDARD, builder.LEVEL_DEFAULT, 3, 3, 0);
                //com.google.zxing.Writer writer = new com.google.zxing.pdf417.encoder.PDF417Writer();

                // Declara variables necesarias
                Writer writer = new PDF417Writer();
                com.google.zxing.common.BitMatrix bitMatrix;

                //Formatea TED a PDF417 y asigna a variable BitMatrix
                bitMatrix = writer.encode(tedPrueba,com.google.zxing.BarcodeFormat.PDF_417,480,160);

                //Transforma BitMatrix en Bitmap
                Bitmap imagen = net.glxn.qrgen.android.MatrixToImageWriter.toBitmap(bitMatrix);

                // Alinea al centro de la hoja y agrega imagen del TED como Bitmap
                builder.addTextAlign(builder.ALIGN_CENTER);
                //builder.addImage(imagen, 0, 0, 500, 100, builder.COLOR_1);
                builder.addImage(imagen, 0, 0, 480, 160, builder.COLOR_1);

            } catch (WriterException e) {
                e.printStackTrace();
            }

            builder.addFeedLine(1);
            builder.addTextAlign(builder.ALIGN_CENTER);
            builder.addText("Timbre Electronico S.I.I. Res. 0 del 2013 \n");
            builder.addText("Verifique documento en: www.portaldte.cl");

            builder.addFeedLine(3);

            builder.addCut(Builder.CUT_FEED);
            printer = Utilidades.getPrinter();

            printer.sendData(builder, SEND_TIMEOUT, status, battery);

            retorna = true;
        } catch (EposException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            retorna = false;
        }
    }

    public Boolean Boleta(Activity activity,ArrayList<ArchivoDocElectronicoTO> docElec) throws UnsupportedEncodingException {
        final int SEND_TIMEOUT = 10 * 1000;

        int[] status = new int[1];
        int[] battery = new int[1];

        String ted = docElec.get(0).getTed().replace("�", "ú");
        String empresa;
        Boolean retorna = false;
        Intent intent = new Intent();

        intent.putExtra("printername", "TM-P80");
        intent.putExtra("language", Builder.MODEL_ANK);

        // INVERTIR FECHA a DD-MM-AA
        String fechaInvertida = convierteFecha(docElec.get(0).getfCreacion(),"yyyy-MM-dd","dd-MM-yyyy");

        Builder builder = null;
        intent = activity.getIntent();
        try {
            Print printer = Utilidades.getPrinter();
            builder = new Builder("TM-P80", Builder.MODEL_ANK);

            builder.addTextLineSpace(20);
            builder.addTextFont(builder.FONT_B); builder.addTextSize(1, 1);
            builder.addText(docElec.get(0).getRazonSocial() +
                    "\n"); builder.addText("GIRO: " +
                    docElec.get(0).getGiro() + "\n");
            builder.addTextLineSpace(30);
            builder.addText(docElec.get(0).getcMatriz() +
                    "\n"); builder.addText("RUT :" +
                    docElec.get(0).getRut() + "\n\n");
            printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_RIGHT);
            builder.addTextLineSpace(20);
            builder.addText(docElec.get(0).getFechaActual()+"\n");
            //builder.addText("SANTIAGO 11 DE OCTUBRE DE 2017\n");
            printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_CENTER); builder.addText(
                    "................................................................\n"
            ); printer = Utilidades.getPrinter();
            builder.addTextFont(builder.FONT_A);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,
                    Builder.COLOR_1); builder.addText("BOLETA ELECTRONICA N° " +
                    docElec.get(0).getFolio() + "\n"); printer =
                    Utilidades.getPrinter(); builder.addTextFont(builder.FONT_B);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,
                    Builder.COLOR_1); builder.addText(
                    "................................................................\n"
            ); printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,
                    Builder.COLOR_1);
            builder.addText(fechaInvertida +" \n");
            builder.addText("................................................................\n"
            ); printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_CENTER);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,
                    Builder.COLOR_1); builder.addText("DETALLES DE PRODUCTOS\n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,
                    Builder.COLOR_1); builder.addText(
                    "................................................................\n"
            ); printer = Utilidades.getPrinter();

            for (int i = 0; i < docElec.size(); i++) {

                builder.addTextAlign(builder.ALIGN_LEFT);
                builder.addTextFont(builder.FONT_A);
                builder.addText(docElec.get(i).getDescAdi() +
                        "\n"); printer = Utilidades.getPrinter();
                builder.addTextFont(builder.FONT_B); builder.addText("SEGUN ODT "
                        + docElec.get(i).getoDT() + "\n"); printer =
                        Utilidades.getPrinter(); builder.addTextPosition(80);
                builder.addText(docElec.get(i).getCantItem() +
                        " X " + docElec.get(i).getPrecioUnit() +
                        "                    TOTAL " +
                        docElec.get(i).getMontoItem() + "\n"); printer =
                        Utilidades.getPrinter();

            }

			  /*
			  builder.addTextFont(builder.FONT_A);
			  builder.addText("EVALUACION PSICOMETRICO RIGUROSO \n");
			  printer = Utilidades.getPrinter();
			  builder.addTextFont(builder.FONT_B);
			  builder.addText("SEGUN ODT 729\n");
			  printer = Utilidades.getPrinter();
			  builder.addTextPosition(80);
			  builder.addTextLineSpace(30);
			  builder.addText("10 X 200                    TOTAL 2000\n");
			  printer = Utilidades.getPrinter();
			  */
            builder.addTextLineSpace(20);
            builder.addTextStyle(Builder.FALSE,Builder.FALSE,Builder.TRUE,Builder.COLOR_1);
            builder.addTextPosition(300);
            builder.addText("Neto $ : ");
            builder.addText(docElec.get(0).getNeto() +" \n");
            printer = Utilidades.getPrinter();
            builder.addTextPosition(200);
            builder.addText("Monto I.V.A 19% $ : ");
            builder.addText(docElec.get(0).getIva() + " \n");
            printer = Utilidades.getPrinter();
            builder.addTextPosition(290);
            builder.addText("TOTAL $ : ");
            builder.addText(docElec.get(0).getTotal() +" \n"); printer = Utilidades.getPrinter();


            // CODIGO TED PDF417

            try {

                //builder.addSymbol(Prueba, builder.SYMBOL_PDF417_STANDARD, builder.LEVEL_DEFAULT, 3, 3, 0);
                //com.google.zxing.Writer writer = new com.google.zxing.pdf417.encoder.PDF417Writer();

                // Declara variables necesarias
                Writer writer = new PDF417Writer();
                com.google.zxing.common.BitMatrix bitMatrix;

                //Formatea TED a PDF417 y asigna a variable BitMatrix
                bitMatrix = writer.encode(ted,com.google.zxing.BarcodeFormat.PDF_417,480,160);

                //Transforma BitMatrix en Bitmap
                Bitmap imagen = net.glxn.qrgen.android.MatrixToImageWriter.toBitmap(bitMatrix);

                // Alinea al centro de la hoja y agrega imagen del TED como Bitmap
                builder.addTextAlign(builder.ALIGN_CENTER);
                //builder.addImage(imagen, 0, 0, 500, 100, builder.COLOR_1);
                builder.addImage(imagen, 0, 0, 480, 160, builder.COLOR_1);

            } catch (WriterException e) {
                e.printStackTrace();
            }

            builder.addFeedLine(1);
            builder.addTextAlign(builder.ALIGN_CENTER);
            builder.addText("Timbre Electronico S.I.I. Res. 0 del 2013 \n");
            builder.addText("Verifique documento en: www.portaldte.cl");

            builder.addFeedLine(3);

            builder.addCut(Builder.CUT_FEED);
            printer = Utilidades.getPrinter();

            printer.sendData(builder, SEND_TIMEOUT, status, battery);

            retorna = true;
        } catch (EposException e) {
            // TODO Auto-generated catch block
            retorna = false;
            e.printStackTrace();
        }

        return retorna;
    }

    public void FacturaPrueba(Activity activity) throws UnsupportedEncodingException, WriterException {

        final int SEND_TIMEOUT = 10 * 1000;

        int[] status = new int[1];
        int[] battery = new int[1];

        // String empresa;
        Boolean retorna = false;
        Intent intent = new Intent();

        intent.putExtra("printername", "TM-P80");
        intent.putExtra("language", Builder.MODEL_ANK);

        String tedPrueba = "<TED version=\"1.0\"><DD><RE>89622400-K</RE><TD>33</TD><F>10</F><FE>2017-09-08</FE>" +
                "<RR>17176015-1</RR><RSR>PRUEBA</RSR><MNT>4800</MNT><IT1>Segun detalle ODT numero 50000002694</IT1>" +
                "<CAF version=\"1.0\"><DA><RE>89622400-K</RE><RS>PULLMAN CARGO S A</RS><TD>33</TD><RNG><D>1</D><H>1000</H>" +
                "</RNG><FA>2014-07-15</FA><RSAPK>" +
                "<M>vjVjJLz3P/ctah5a6BUsPqZoiwasfp9v6lC/Y12jinrHlNvyCBgvinwnS1pWRrdjWO4XnXRfM1ZgYwnGvMG/Rw==</M><E>Aw==</E>" +
                "</RSAPK><IDK>100</IDK></DA><FRMA algoritmo=\"SHA1withRSA\">gwv/6+tNMDHczOVC545zbqJAH6QZNWts6kohJsSD4JLnjs/" +
                "9yUkXuVZsmBmj7BlqPXy+IsSgklYYJccWtLG+Tw==</FRMA></CAF><TSTED>2017-09-08T15:39:02</TSTED></DD>" +
                "<FRMT algoritmo=\"SHA1withRSA\">LYrW2oDhLj+WNtzpc+Pm+DJUyhhu7CrlfcmAuU57HIzuRVxLFdFnd+Yy2rTX0ijXUTAt5" +
                "53ocYS/giR/ke6YWQ==</FRMT></TED>";

        Builder builder = null;
        intent = activity.getIntent();
        try {

            builder = new Builder("TM-P80", Builder.MODEL_ANK);

            builder.addTextLineSpace(20);
            builder.addTextFont(builder.FONT_B);
            builder.addTextSize(1, 1);
            //builder.addText(archivoDocElectronicoTO.get(0).getRazonSocial()+ "\n");
            builder.addText("Razon Social: PRUEBA\n");
            //builder.addText("GIRO: " + archivoDocElectronicoTO.get(0).getGiro()+ "\n");
            builder.addText("GIRO: Prueba\n");
            builder.addTextLineSpace(30);
            //builder.addText(archivoDocElectronicoTO.get(0).getcMatriz() + "\n");
            builder.addText("cMatriz\n");
            //builder.addText("RUT :" + archivoDocElectronicoTO.get(0).getRut()+ "\n\n");
            builder.addText("RUT :11.111.111-1\n\n");
            Print printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_RIGHT);
            builder.addTextLineSpace(20);
            builder.addText("SANTIAGO 21 DE OCTUBRE DE 2016\n");
            printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_CENTER);
            builder.addText("................................................................\n");
            printer = Utilidades.getPrinter();
            builder.addTextFont(builder.FONT_A);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            //builder.addText("FACTURA ELECTRONICA N° "+ archivoDocElectronicoTO.get(0).getFolio() + "\n");
            builder.addText("FACTURA ELECTRONICA N° 123456\n");
            printer = Utilidades.getPrinter();
            builder.addTextFont(builder.FONT_B);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            builder.addText("................................................................\n");
            printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_LEFT);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("SEÑOR(ES)         :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            //builder.addText(archivoDocElectronicoTO.get(0).getcRazonSocial()+ " \n");
            builder.addText("Razon Social: Prueba \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("R.U.T             :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            //builder.addText(archivoDocElectronicoTO.get(0).getcRut() + " \n");
            builder.addText("11.111.111-1 \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("GIRO              :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            //builder.addText(archivoDocElectronicoTO.get(0).getcGiro() + " \n");
            builder.addText("Giro: Prueba \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("DIRECCION         :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            //builder.addText(archivoDocElectronicoTO.get(0).getcDireccion()+ " \n");
            builder.addText("Direccion Prueba \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("FECHA             :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            //builder.addText(archivoDocElectronicoTO.get(0).getfCreacion()+ " \n");
            builder.addText("01/01/9999 \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("COMUNA/CIUDAD     :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            //builder.addText(archivoDocElectronicoTO.get(0).getcComuna() + " \n");
            builder.addText("Prueba \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("TELEFONO          :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            //builder.addText(archivoDocElectronicoTO.get(0).getcContacto()+ " \n");
            builder.addText("Fono Prueba \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("FORMA PAGO        :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            //builder.addText(archivoDocElectronicoTO.get(0).getFormaPago()+ " \n");
            builder.addText("Forma Pago Prueba \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("F. VENCIMIENTO    :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            //builder.addText(archivoDocElectronicoTO.get(0).getfCreacion()+ " \n");
            //builder.addText("01/01/9999 \n");
            builder.addText("................................................................\n");
            printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_CENTER);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("DETALLES DE PRODUCTOS\n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            builder.addText("................................................................\n");
            printer = Utilidades.getPrinter();

            builder.addTextLineSpace(20);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addTextPosition(300);
            builder.addText("Neto $ : ");
            //builder.addText(archivoDocElectronicoTO.get(0).getNeto() + " \n");
            builder.addText("NETO \n");
            printer = Utilidades.getPrinter();
            builder.addTextPosition(200);
            builder.addText("Monto I.V.A 19% $ : ");
            //builder.addText(archivoDocElectronicoTO.get(0).getIva() + " \n");
            builder.addText("IVA \n");
            printer = Utilidades.getPrinter();
            builder.addTextPosition(290);
            builder.addText("TOTAL $ : ");
            //builder.addText(archivoDocElectronicoTO.get(0).getTotal() + " \n");
            builder.addText("Precio total \n");

            // CODIGO TED PDF417

            try {

                // Declara variables necesarias
                Writer writer = new PDF417Writer();
                com.google.zxing.common.BitMatrix bitMatrix;

                //Formatea TED a PDF417 y asigna a bariable BitMatrix
                bitMatrix = writer.encode(tedPrueba,com.google.zxing.BarcodeFormat.PDF_417,500,180);

                //Transforma BitMatrix en Bitmap
                Bitmap imagen = net.glxn.qrgen.android.MatrixToImageWriter.toBitmap(bitMatrix);

                // Alinea al centro de la hoja y agrega imagen del TED como Bitmap
                builder.addTextAlign(builder.ALIGN_CENTER);
                builder.addImage(imagen, 0, 0, 500, 180, builder.COLOR_1);

            } catch (WriterException e) {
                e.printStackTrace();
            }

            builder.addFeedLine(1);
            builder.addTextAlign(builder.ALIGN_CENTER);
            builder.addText("Timbre Electronico S.I.I. Res. 0 del 2013 \n");
            builder.addText("Verifique documento en: www.portaldte.cl");

            builder.addFeedLine(3);

            builder.addCut(Builder.CUT_FEED);
            printer = Utilidades.getPrinter();

            printer.sendData(builder, SEND_TIMEOUT, status, battery);

            retorna = true;
        } catch (EposException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            retorna = false;
        }
    }

    public String buscaPlanillaOdt(String odt) throws IOException, ClientProtocolException, JSONException {
        String planilla = "";
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
                            planilla = recibeSplit[0].toString();
                            break;
                        }
                        //s = br.readLine();
                    }
                    s = br.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return planilla;
    }

    public Boolean Factura(Activity activity,ArrayList<ArchivoDocElectronicoTO> archivoDocElectronicoTO) throws UnsupportedEncodingException, WriterException {
        final int SEND_TIMEOUT = 10 * 1000;

        String ted = archivoDocElectronicoTO.get(0).getTed().replace("�", "ú");
        int[] status = new int[1];
        int[] battery = new int[1];

        // String empresa;
        Boolean retorna = false;
        Intent intent = new Intent();

        intent.putExtra("printername", "TM-P80");
        intent.putExtra("language", Builder.MODEL_ANK);

        // INVERTIR FECHA a DD-MM-AA
        String fechaInvertida = convierteFecha(archivoDocElectronicoTO.get(0).getfCreacion(),"yyyy-MM-dd","dd-MM-yyyy");

        //Descripcion de forma de pago
        String formaPago = (archivoDocElectronicoTO.get(0).getFormaPago().equals("1"))?"CONTADO":"CRÉDITO";

        Builder builder = null;
        intent = activity.getIntent();
        try {

            builder = new Builder("TM-P80", Builder.MODEL_ANK);

            builder.addTextLineSpace(20);
            builder.addTextFont(builder.FONT_B);
            builder.addTextSize(1, 1);
            builder.addText("Razon Social: "+archivoDocElectronicoTO.get(0).getRazonSocial()+ "\n");
            builder.addText("GIRO: " + archivoDocElectronicoTO.get(0).getGiro()+ "\n");
            builder.addTextLineSpace(30);
            builder.addText(archivoDocElectronicoTO.get(0).getcMatriz() + "\n");
            builder.addText("RUT :" + archivoDocElectronicoTO.get(0).getRut()+ "\n\n");
            Print printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_RIGHT);
            builder.addTextLineSpace(20);
            builder.addText(archivoDocElectronicoTO.get(0).getFechaActual()+"\n");
            //builder.addText("SANTIAGO 21 DE OCTUBRE DE 2016\n");
            printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_CENTER);
            builder.addText("................................................................\n");
            printer = Utilidades.getPrinter();
            builder.addTextFont(builder.FONT_A);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("FACTURA ELECTRONICA N° "+ archivoDocElectronicoTO.get(0).getFolio() + "\n");
            printer = Utilidades.getPrinter();
            builder.addTextFont(builder.FONT_B);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            builder.addText("................................................................\n");
            printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_LEFT);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("SEÑOR(ES)         :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            builder.addText(archivoDocElectronicoTO.get(0).getcRazonSocial()+ " \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("R.U.T             :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            builder.addText(archivoDocElectronicoTO.get(0).getcRut() + " \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("GIRO              :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            builder.addText(archivoDocElectronicoTO.get(0).getcGiro() + " \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("DIRECCION         :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            builder.addText(archivoDocElectronicoTO.get(0).getcDireccion()+ " \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("FECHA             :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            //builder.addText(archivoDocElectronicoTO.get(0).getfCreacion()+ " \n");
            builder.addText(fechaInvertida+ " \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("COMUNA/CIUDAD     :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            builder.addText(archivoDocElectronicoTO.get(0).getcComuna() + " \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("TELEFONO          :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            builder.addText(archivoDocElectronicoTO.get(0).getcContacto()+ " \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("FORMA PAGO        :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            //builder.addText(archivoDocElectronicoTO.get(0).getFormaPago()+ " \n");
            builder.addText(formaPago+ " \n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("F. VENCIMIENTO    :");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            //builder.addText(archivoDocElectronicoTO.get(0).getfCreacion()+ " \n");
            builder.addText(fechaInvertida+ " \n");
            builder.addText("................................................................\n");
            printer = Utilidades.getPrinter();
            builder.addTextAlign(builder.ALIGN_CENTER);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addText("DETALLES DE PRODUCTOS\n");
            printer = Utilidades.getPrinter();
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.FALSE,Builder.COLOR_1);
            builder.addText("................................................................\n");
            printer = Utilidades.getPrinter();

            for (int i = 0; i < archivoDocElectronicoTO.size(); i++) {

                builder.addTextAlign(builder.ALIGN_LEFT);
                builder.addTextFont(builder.FONT_A);
                builder.addText(archivoDocElectronicoTO.get(i).getDescAdi()
                        + "\n");
                printer = Utilidades.getPrinter();
                builder.addTextFont(builder.FONT_B);
                builder.addText("SEGUN ODT "
                        + archivoDocElectronicoTO.get(i).getoDT() + "\n");
                printer = Utilidades.getPrinter();
                builder.addTextPosition(80);
                builder.addText(archivoDocElectronicoTO.get(i).getCantItem()
                        + " X "
                        + archivoDocElectronicoTO.get(i).getPrecioUnit()
                        + "                    TOTAL "
                        + archivoDocElectronicoTO.get(i).getMontoItem() + "\n");
                printer = Utilidades.getPrinter();

            }
            builder.addTextAlign(builder.ALIGN_LEFT);
            builder.addTextFont(builder.FONT_A);
           /* builder.addText("Descripcion prueba\n");
            printer = Utilidades.getPrinter();
            builder.addTextFont(builder.FONT_B);
            builder.addText("SEGUN ODT 000000000000\n");
            printer = Utilidades.getPrinter();
            builder.addTextPosition(80);
            builder.addText("1 X precio                    TOTAL \n");*/
            printer = Utilidades.getPrinter();
            builder.addTextLineSpace(20);
            builder.addTextStyle(Builder.FALSE, Builder.FALSE, Builder.TRUE,Builder.COLOR_1);
            builder.addTextPosition(300);
            builder.addText("Neto $ : ");
            builder.addText(archivoDocElectronicoTO.get(0).getNeto() + " \n");
            printer = Utilidades.getPrinter();
            builder.addTextPosition(200);
            builder.addText("Monto I.V.A 19% $ : ");
            builder.addText(archivoDocElectronicoTO.get(0).getIva() + " \n");
            printer = Utilidades.getPrinter();
            builder.addTextPosition(290);
            builder.addText("TOTAL $ : ");
            builder.addText(archivoDocElectronicoTO.get(0).getTotal() + " \n");

            // CODIGO TED PDF417

            try {

                // Declara variables necesarias
                Writer writer = new PDF417Writer();
                com.google.zxing.common.BitMatrix bitMatrix;

                //Formatea TED a PDF417 y asigna a bariable BitMatrix
                bitMatrix = writer.encode(ted,com.google.zxing.BarcodeFormat.PDF_417,480,160);

                //Transforma BitMatrix en Bitmap
                Bitmap imagen = net.glxn.qrgen.android.MatrixToImageWriter.toBitmap(bitMatrix);

                // Alinea al centro de la hoja y agrega imagen del TED como Bitmap
                builder.addTextAlign(builder.ALIGN_CENTER);
                builder.addImage(imagen, 0, 0, 480, 160, builder.COLOR_1);

            } catch (WriterException e) {
                e.printStackTrace();
            }

            builder.addFeedLine(1);
            builder.addTextAlign(builder.ALIGN_CENTER);
            builder.addText("Timbre Electronico S.I.I. Res. 0 del 2013 \n");
            builder.addText("Verifique documento en: www.portaldte.cl");

            builder.addFeedLine(3);

            builder.addCut(Builder.CUT_FEED);
            printer = Utilidades.getPrinter();

            printer.sendData(builder, SEND_TIMEOUT, status, battery);

            retorna = true;
        } catch (EposException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            retorna = false;
        }

        return retorna;
    }

    private static String convierteFecha(String stringFechaEntrada, String formatoEntrada, String formatoSalida){
        Log.i("TAG", "stringFechaEntrada :" +  stringFechaEntrada);
        //Definimos formato del string que ingresamos.
        SimpleDateFormat sdf = new SimpleDateFormat(formatoEntrada);
        try {
            Date date = sdf.parse(stringFechaEntrada);
            //Definimos formato del string que deseamos obtener.
            sdf = new SimpleDateFormat(formatoSalida);
            String stringFechaSalida = sdf.format(date);
            Log.i("TAG", "stringFechaSalida :" +  stringFechaSalida);
            Date dateSalida = sdf.parse(stringFechaSalida);
            //Log.i("TAG", "dateSalida :" +  dateSalida);
            return stringFechaSalida;
        }catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
