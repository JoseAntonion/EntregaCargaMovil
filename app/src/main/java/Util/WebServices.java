package Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import To.ArchivoOdtPorPatenteTO;

public class WebServices {

    private String RUTA_WEB_SERVICE = "http://webservices.pullman.cl/ServiciosCarga/rest/ServiciosAndroid";

    public ArrayList<ArchivoOdtPorPatenteTO> OdtsXPatente(String patente) throws IOException,
            ClientProtocolException, JSONException {

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


        del = new HttpGet(RUTA_WEB_SERVICE + "/TraeDocumentos/patente="+patente);
        del.setHeader("content-type", "application/json");
        resp = httpClient.execute(del);
        respStr = EntityUtils.toString(resp.getEntity());
        responseObject = new JSONObject(respStr);
        //responseObject = responseObject.getJSONObject("diDocInternoTO");
        JSONArray respJSON = responseObject.getJSONArray("diDocInternoTO");

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
        return archivoOdtPorPatenteTOs;
    }

}
