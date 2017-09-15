package Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import To.ArchivoOdtPorPatenteTO;
import To.ValidaTO;

public class WebServices {

    private String RUTA_WEB_SERVICE = "http://webservices.pullman.cl/ServiciosCarga/rest/ServiciosAndroid";

    public ArrayList<ArchivoOdtPorPatenteTO> OdtsXPatente(String patente) throws IOException,ClientProtocolException, JSONException {

        String respStr;
        String archivoContruido = "";
        HttpClient httpClient;
        HttpGet del;
        HttpResponse resp;
        JSONObject responseObject;
        JSONObject responseObject1;
        httpClient = new DefaultHttpClient();
        ArchivoOdtPorPatenteTO archivoOdtPorPatenteTO = null;
        ArrayList<ArchivoOdtPorPatenteTO> archivoOdtPorPatenteTOs =  new ArrayList<ArchivoOdtPorPatenteTO>();
        int filas;

        del = new HttpGet(RUTA_WEB_SERVICE + "/TraeDocumentos/patente="+patente);
        del.setHeader("content-type", "application/json");
        resp = httpClient.execute(del);
        respStr = EntityUtils.toString(resp.getEntity());
        responseObject = new JSONObject(respStr);
        filas = responseObject.getInt("Filas");
        if(filas>1){
            JSONArray respJSON = responseObject.getJSONArray("DocumentoInterno");

            for (int i = 0; i < respJSON.length(); i++) {
                archivoOdtPorPatenteTO =  new ArchivoOdtPorPatenteTO();
                JSONObject obj = respJSON.getJSONObject(i);
                archivoOdtPorPatenteTO.setCodBarra(obj.getString("Ccodigobarra"));
                archivoOdtPorPatenteTO.setEstadoODT(obj.getString("Ueoestado"));
                archivoOdtPorPatenteTO.setFormaPago(obj.getString("Doformapago"));
                archivoOdtPorPatenteTO.setNumeroODT(obj.getString("Ueonumeroot"));
                archivoOdtPorPatenteTO.setNumeroPiezas(obj.getInt("Donumeropiezas"));
                archivoOdtPorPatenteTO.setPlanilla(obj.getString("Dinumerodocumento"));
                archivoOdtPorPatenteTOs.add(archivoOdtPorPatenteTO);
            }
        } else {
            archivoOdtPorPatenteTO =  new ArchivoOdtPorPatenteTO();
            responseObject1 = responseObject.getJSONObject("DocumentoInterno");
            archivoOdtPorPatenteTO.setCodBarra(responseObject1.getString("Ccodigobarra"));
            archivoOdtPorPatenteTO.setEstadoODT(responseObject1.getString("Ueoestado"));
            archivoOdtPorPatenteTO.setFormaPago(responseObject1.getString("Doformapago"));
            archivoOdtPorPatenteTO.setNumeroODT(responseObject1.getString("Ueonumeroot"));
            archivoOdtPorPatenteTO.setNumeroPiezas(responseObject1.getInt("Donumeropiezas"));
            archivoOdtPorPatenteTO.setPlanilla(responseObject1.getString("Dinumerodocumento"));
            archivoOdtPorPatenteTOs.add(archivoOdtPorPatenteTO);
        }

        return archivoOdtPorPatenteTOs;
    }

    public ValidaTO GrabaImagen(String rut,String imagen,String estado, String usuario,String imei, String odt,String motivo) throws IOException,ClientProtocolException,JSONException{

        ValidaTO validaTO = new ValidaTO();

        try {

            HttpClient httpclient = new DefaultHttpClient();
            JSONObject responseObject;
            HttpPost httppost = new HttpPost(RUTA_WEB_SERVICE+ "/GuardaImagen");
            httppost.setHeader("Content-Type", "application/json");
            //String base = android.util.Base64.encodeToString(imagen, Base64.NO_WRAP);

            // forma el JSON y tipo de contenido
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("rut", rut);
            jsonObject.put("imagen", imagen);
            jsonObject.put("estado", estado);
            jsonObject.put("usuario", usuario);
            jsonObject.put("imei", imei);
            jsonObject.put("odt", odt);
            jsonObject.put("motivo", motivo);
            //
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            stringEntity.setContentType("application/json");
            httppost.setEntity(stringEntity);
            // ejecuta
            HttpResponse response = httpclient.execute(httppost);
            String respStr = EntityUtils.toString(response.getEntity());

            responseObject = new JSONObject(respStr);
            validaTO.setValida(responseObject.getString("Valida"));
            validaTO.setError(responseObject.getString("Error"));
            validaTO.setMensaje(responseObject.getString("Mensaje"));
            // return true;

        } catch (Exception e) {

            System.out.print(e.getMessage());
            return new ValidaTO();
        }
        return validaTO;
    }

    public ValidaTO CambiaEstadoODT(String odt, String estado, String usuario, String formulario, String version, String proyecto, String imei) throws IOException{
        ValidaTO validaTO = new ValidaTO();

        try {

            HttpClient httpclient = new DefaultHttpClient();
            JSONObject responseObject;
            HttpPost httppost = new HttpPost(RUTA_WEB_SERVICE+ "/CambiaEstadoODTCargaMovil");
            httppost.setHeader("Content-Type", "application/json");

            // forma el JSON y tipo de contenido
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("odt", odt);
            jsonObject.put("estado", estado);
            jsonObject.put("usuario", usuario);
            jsonObject.put("formulario", formulario);
            jsonObject.put("version", version);
            jsonObject.put("proyecto", proyecto);
            jsonObject.put("imei", imei);
            //
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            stringEntity.setContentType("application/json");
            httppost.setEntity(stringEntity);
            // ejecuta
            HttpResponse response = httpclient.execute(httppost);
            String respStr = EntityUtils.toString(response.getEntity());

            responseObject = new JSONObject(respStr);
            validaTO.setValida(responseObject.getString("Valida"));
            validaTO.setError(responseObject.getString("Error"));
            validaTO.setMensaje(responseObject.getString("Mensaje"));
            // return true;

        } catch (Exception e) {

            System.out.print(e.getMessage());
            return validaTO;
        }
        return validaTO;
    }

    public ValidaTO GrabaReingreso(String planilla,String odt,int piezas, String tipoDevolucion,String codigoReingreso) throws IOException,ClientProtocolException,JSONException{
        ValidaTO validaTO = new ValidaTO();

        try {

            HttpClient httpclient = new DefaultHttpClient();
            JSONObject responseObject;
            HttpPost httppost = new HttpPost(RUTA_WEB_SERVICE+ "/AgregaReingreso");
            httppost.setHeader("Content-Type", "application/json");

            // forma el JSON y tipo de contenido
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("planilla", planilla);
            jsonObject.put("odt", odt);
            jsonObject.put("piezas", piezas);
            jsonObject.put("tipo_devolucion", tipoDevolucion);
            jsonObject.put("codigo_reingreso", codigoReingreso);
            jsonObject.put("latitud", String.valueOf(Globales.latitud));
            jsonObject.put("longitud", String.valueOf(Globales.longitud));
            //
            StringEntity stringEntity = new StringEntity(jsonObject.toString());
            stringEntity.setContentType("application/json");
            httppost.setEntity(stringEntity);
            // ejecuta
            HttpResponse response = httpclient.execute(httppost);
            String respStr = EntityUtils.toString(response.getEntity());

            responseObject = new JSONObject(respStr);
            validaTO.setValida(responseObject.getString("Valida"));
            validaTO.setError(responseObject.getString("Error"));
            validaTO.setMensaje(responseObject.getString("Mensaje"));
            // return true;

        } catch (Exception e) {

            System.out.print(e.getMessage());
            return validaTO;
        }
        return validaTO;
    }
}
