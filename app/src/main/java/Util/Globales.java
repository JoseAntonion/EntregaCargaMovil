package Util;

import android.app.Application;

import java.util.ArrayList;

import To.EntregaOdtMasivoTO;


public class Globales extends Application {

    public static String odtsXpatente;
    public static String Ciudades;
    public static String Comunas;
    public static String Impresora;
    public static String rutaArchivos;
    public static String rutaArchivosFinal;
    public static int cantidadOriginalOdts;
    public static int cantidadBultosOriginal;
    public static String imagenCodificada;
    public static byte[] Imagen;
    public static String version = "v1.0";
    public static ArrayList<EntregaOdtMasivoTO> odtMasiva = new ArrayList<>();
    public static double latitud;
    public static double longitud;
    public static int totalValoresODT;
    public static String docElectronico;
    public static String esCTACTE;
    public static String banderaTipoPago;
    public static ArrayList<String> registroOdtMultiples;
    public static boolean primera;


}
