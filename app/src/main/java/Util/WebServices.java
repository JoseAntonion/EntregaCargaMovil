package Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import To.ArchivoOdtPorPatenteTO;

public class WebServices {

    private String RUTA_WEB_SERVICE = "RUTA DEL WEBSERVICE";

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


        del = new HttpGet(RUTA_WEB_SERVICE + "/DatosTxtDocContable/docContable=");
        del.setHeader("content-type", "application/json");
        resp = httpClient.execute(del);
        respStr = EntityUtils.toString(resp.getEntity());
        responseObject = new JSONObject(respStr);
        responseObject1 = new JSONObject(respStr);
        responseObject1 = responseObject.getJSONObject("ValidacionTO");
        String filas = responseObject1.getString("Filas");


        responseObject = responseObject.getJSONObject("ArregloDeElementos");
        String cantidadElementos = responseObject.getString("CantidadDeElementos");

        for(int i = 0;i<Integer.parseInt(cantidadElementos);i++) {
            archivoOdtPorPatenteTO.setCodBarra(responseObject.getString("codigodebarra"));
            archivoOdtPorPatenteTO.setEstadoODT(responseObject.getString("estadoodt"));
            archivoOdtPorPatenteTO.setFormaPago(responseObject.getString("formapago"));
            archivoOdtPorPatenteTO.setNumeroODT(responseObject.getString("numeroodt"));
            archivoOdtPorPatenteTO.setNumeroPiezas(responseObject.getInt("numeropiezas"));
            archivoOdtPorPatenteTO.setPlanilla(responseObject.getString("planilla"));
            archivoOdtPorPatenteTOs.add(archivoOdtPorPatenteTO);

        }

        /*JSONArray respJSON = responseObject.getJSONArray("ArchivoDocElectronicoTO");

        for (int i = 0; i < respJSON.length(); i++) {
            archivoDocElectronicoTO =  new ArchivoDocElectronicoTO();
            JSONObject obj = respJSON.getJSONObject(i);
                archivoDocElectronicoTO.setTipoDoc(obj.getString("TipoDoc"));
                archivoDocElectronicoTOs.add(archivoDocElectronicoTO);
        }*/

        return archivoOdtPorPatenteTOs;
    }

}
