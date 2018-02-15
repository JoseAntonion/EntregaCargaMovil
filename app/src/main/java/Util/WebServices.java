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

import To.ArchivoDocElectronicoTO;
import To.ArchivoOdtPorPatenteTO;
import To.CiudadTO;
import To.ComunaTO;
import To.UsuarioReceptorTO;
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
                archivoOdtPorPatenteTO.setCodBarra(obj.getString("CcodigoBarra"));
                archivoOdtPorPatenteTO.setEstadoODT(obj.getString("Ueoestado"));
                archivoOdtPorPatenteTO.setFormaPago(obj.getString("Doformapago"));
                archivoOdtPorPatenteTO.setNumeroODT(obj.getString("Ueonumeroot"));
                archivoOdtPorPatenteTO.setNumeroPiezas(obj.getInt("Donumeropiezas"));
                archivoOdtPorPatenteTO.setPlanilla(obj.getString("Dinumerodocumento"));
                archivoOdtPorPatenteTO.setValorOdt(obj.getInt("Dovalorflete"));
                archivoOdtPorPatenteTOs.add(archivoOdtPorPatenteTO);
            }
        } else {
            archivoOdtPorPatenteTO =  new ArchivoOdtPorPatenteTO();
            responseObject1 = responseObject.getJSONObject("DocumentoInterno");
            archivoOdtPorPatenteTO.setCodBarra(responseObject1.getString("CcodigoBarra"));
            archivoOdtPorPatenteTO.setEstadoODT(responseObject1.getString("Ueoestado"));
            archivoOdtPorPatenteTO.setFormaPago(responseObject1.getString("Doformapago"));
            archivoOdtPorPatenteTO.setNumeroODT(responseObject1.getString("Ueonumeroot"));
            archivoOdtPorPatenteTO.setNumeroPiezas(responseObject1.getInt("Donumeropiezas"));
            archivoOdtPorPatenteTO.setPlanilla(responseObject1.getString("Dinumerodocumento"));
            archivoOdtPorPatenteTO.setValorOdt(responseObject1.getInt("Dovalorflete"));
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

    public ArrayList<CiudadTO> TraeCiudades() throws IOException,ClientProtocolException, JSONException {

        String respStr;
        HttpClient httpClient;
        HttpGet del;
        HttpResponse resp;
        JSONObject responseObject;
        httpClient = new DefaultHttpClient();
        CiudadTO ciudad = null;
        ArrayList<CiudadTO> ciudades =  new ArrayList<CiudadTO>();

        del = new HttpGet(RUTA_WEB_SERVICE + "/TraeCiudades");
        del.setHeader("content-type", "application/json");
        resp = httpClient.execute(del);
        respStr = EntityUtils.toString(resp.getEntity());
        responseObject = new JSONObject(respStr);

        JSONArray respJSON = responseObject.getJSONArray("ciudadTO");

        for (int i = 0; i < respJSON.length(); i++) {
            ciudad =  new CiudadTO();
            JSONObject obj = respJSON.getJSONObject(i);
            ciudad.setCodCiudad(obj.getString("Codigo_ciudad"));
            ciudad.setNombreCiudad(obj.getString("Nombre_ciudad"));
            ciudades.add(ciudad);
        }
        return ciudades;
    }

    public ArrayList<ComunaTO> TraeComunas() throws IOException,ClientProtocolException, JSONException {

        String respStr;
        HttpClient httpClient;
        HttpGet del;
        HttpResponse resp;
        JSONObject responseObject;
        httpClient = new DefaultHttpClient();
        ComunaTO comuna = null;
        ArrayList<ComunaTO> comunas =  new ArrayList<ComunaTO>();

        del = new HttpGet(RUTA_WEB_SERVICE + "/TraeComunas");
        del.setHeader("content-type", "application/json");
        resp = httpClient.execute(del);
        respStr = EntityUtils.toString(resp.getEntity());
        responseObject = new JSONObject(respStr);

        JSONArray respJSON = responseObject.getJSONArray("comunaTO");

        for (int i = 0; i < respJSON.length(); i++) {
            comuna =  new ComunaTO();
            JSONObject obj = respJSON.getJSONObject(i);
            comuna.setCodCiudad(obj.getString("Codigo_ciudad"));
            comuna.setCodComuna(obj.getString("Codigo_comuna"));
            comuna.setNombreComuna(obj.getString("Nombre_comuna"));
            comunas.add(comuna);
        }
        return comunas;
    }

    public void retornaImpresoraPrueba(String imei) throws IOException, ClientProtocolException, JSONException {

        String respStr;
        HttpClient httpClient;
        HttpGet del;
        HttpResponse resp;
        JSONObject responseObject;
        httpClient = new DefaultHttpClient();
        ValidaTO validaTO = null;

        del = new HttpGet("http://webservices.pullman.cl/TRA-P-00000002-4/rest/entrega/impresora/imei=" + imei);

        del.setHeader("content-type", "application/json");

        resp = httpClient.execute(del);
        respStr = EntityUtils.toString(resp.getEntity());

        responseObject = new JSONObject(respStr);
        validaTO = new ValidaTO();
        validaTO.setValida(responseObject.getString("Valida"));

        if (!validaTO.getValida().equals("0")) {
            Globales.Impresora = validaTO.getValida();
        }

        //return validaTO;
    }

    public ValidaTO GuardarEntrega(String NroPlanilla, String NumeroOdt,
                                       String RecepcionRut, String RecepcionNombre,
                                       String UsuarioTelefono, String Imei, String Aplicacion,
                                       String Version,
                                       String ReceptorNombres, String ReceptorAPaterno,
                                       String ReceptorAMaterno, String concepto, String motivoReingreso,
                                       String observacionReingreso) throws IOException, ClientProtocolException, JSONException {
        String respStr;
        HttpClient httpClient;
        HttpGet del;
        HttpResponse resp;
        JSONObject responseObject;
        httpClient = new DefaultHttpClient();
        ValidaTO validaTO = null;
        String Latitud = String.valueOf(Globales.latitud);
        String Longitud = String.valueOf(Globales.longitud);


        if (Longitud.equals("")) {
            Longitud = "%20";
        }
        if (Latitud.equals("")) {
            Latitud = "%20";
        }

        del = new HttpGet("http://webservices.pullman.cl/TRA-P-00000002-4/rest/entrega/GrabaEntregaOdt/NroPlanilla="
                + NroPlanilla.trim() + "/NumeroOdt=" + NumeroOdt.trim()
                + "/RecepcionRut=" + RecepcionRut.trim() + "/RecepcionNombre="
                + RecepcionNombre.trim() + "/UsuarioTelefono="
                + UsuarioTelefono.trim() + "/Imei=" + Imei.trim()
                + "/Aplicacion=" + Aplicacion.trim() + "/Version="
                + Version.trim() + "/Longitud=" + Longitud.trim() + "/Latitud="
                + Latitud.trim() + "/ReceptorNombres=" + ReceptorNombres.trim()
                + "/ReceptorAPaterno=" + ReceptorAPaterno.trim()
                + "/ReceptorAMaterno=" + ReceptorAMaterno.trim() + "/concepto="
                + concepto.trim() + "/motivoReingreso="
                + motivoReingreso.trim() + "/observacionReingreso="
                + observacionReingreso.trim());
        del.setHeader("content-type", "application/json");

        resp = httpClient.execute(del);
        respStr = EntityUtils.toString(resp.getEntity());

        responseObject = new JSONObject(respStr);
        validaTO = new ValidaTO();
        validaTO.setValida(responseObject.getString("Valida"));

        return validaTO;
    }

    public ArrayList<ArchivoDocElectronicoTO> RetornaDocContable(String tipoDocumento, String empresa, String fecha, String rut, String razonSocial,
                                                                 String direccion, String comuna, String tipoPago, String excento, String neto, String iva,
                                                                 String total, String agencia, String punto, String arqueo, String estado, String estadoDoc,
                                                                 String usuario, String mac, String ip, String sistema, String formulario, String version,
                                                                 String url, String impresion, String solAnulacion, String giro, String tipoVia, String numeracion,
                                                                 String fono, String extra, String odt) throws IOException,ClientProtocolException, JSONException {

        String respStr;
        HttpClient httpClient;
        HttpGet del;
        HttpResponse resp;
        JSONObject responseObject;
        JSONObject responseObject1;
        httpClient = new DefaultHttpClient();
        ValidaTO validaTO = null;
        ArchivoDocElectronicoTO archivoDocElectronicoTO = new ArchivoDocElectronicoTO();
        ArrayList<ArchivoDocElectronicoTO>  archivoDocElectronicoTOs = new ArrayList<ArchivoDocElectronicoTO>();


        if(tipoDocumento.equals("")){tipoDocumento = "%20";}else{tipoDocumento=tipoDocumento.replace(" ", "%20");}
        if(empresa.equals("")){empresa = "%20";}else{empresa=empresa.replace(" ", "%20");}
        if(fecha.equals("")){fecha = "%20";}else{fecha=fecha.replace(" ", "%20");}
        if(rut.equals("")){rut = "%20";}else{rut=rut.replace(" ", "%20");}
        if(razonSocial.equals("")){razonSocial = "%20";}else{razonSocial=razonSocial.replace(" ", "%20");}
        if(direccion.equals("")){direccion = "%20";}else{direccion=direccion.replace(" ", "%20");}
        if(comuna.equals("")){comuna = "SANTIAGO";}else{comuna=comuna.replace(" ", "%20");}
        if(tipoPago.equals("")){tipoPago = "%20";}else{tipoPago=tipoPago.replace(" ", "%20");}
        if(excento.equals("")){excento = "%20";}else{excento=excento.replace(" ", "%20");}
        if(neto.equals("")){neto = "%20";}else{neto=neto.replace(" ", "%20");}
        if(iva.equals("")){iva = "%20";}else{iva=iva.replace(" ", "%20");}
        if(total.equals("")){total = "%20";}else{total=total.replace(" ", "%20");}
        if(agencia.equals("")){agencia = "001253";}else{agencia=agencia.replace(" ", "%20");}// Se deja en duro agencia LONQUEN, hasta implementacion en todas las agencias.
        if(punto.equals("")){punto = "%20";}else{punto=punto.replace(" ", "%20");}
        if(arqueo.equals("")){arqueo = "%20";}else{arqueo=arqueo.replace(" ", "%20");}
        if(estado.equals("")){estado = "%20";}else{estado=estado.replace(" ", "%20");}
        if(estadoDoc.equals("")){estadoDoc = "%20";}else{estadoDoc=estadoDoc.replace(" ", "%20");}
        if(usuario.equals("")){usuario = "%20";}else{usuario=usuario.replace(" ", "%20");}
        if(mac.equals("")){mac = "%20";}else{mac=mac.replace(" ", "%20");}
        if(ip.equals("")){ip = "%20";}else{ip=ip.replace(" ", "%20");}
        if(sistema.equals("")){sistema = "%20";}else{sistema=sistema.replace(" ", "%20");}
        if(formulario.equals("")){formulario = "%20";}else{formulario=formulario.replace(" ", "%20");}
        if(version.equals("")){version = "%20";}else{version=version.replace(" ", "%20");}
        if(url.equals("")){url = "%20";}else{url=url.replace(" ", "%20");}
        if(impresion.equals("")){impresion = "%20";}else{impresion=impresion.replace(" ", "%20");}
        if(solAnulacion.equals("")){solAnulacion = "%20";}else{solAnulacion=solAnulacion.replace(" ", "%20");}
        if(giro.equals("")){giro = "%20";}else{giro=giro.replace(" ", "%20");}
        if(tipoVia.equals("")){tipoVia = "%20";}else{tipoVia=tipoVia.replace(" ", "%20");}
        if(numeracion.equals("")){numeracion = "%20";}else{numeracion=numeracion.replace(" ", "%20");}
        if(fono.equals("")){fono = "%20";}else{fono=fono.replace(" ", "%20");}
        if(extra.equals("")){extra = "%20";}else{extra=extra.replace(" ", "%20");}
        if(odt.equals("")){odt = "%20";}else{odt=odt.replace(" ", "%20");}



        del = new HttpGet("http://webservices.pullman.cl/TRA-P-00000002-4/rest/entrega/GrabaDocElectronico/tipoDocumento="+tipoDocumento+"/empresa="+empresa+"/fecha="+fecha+"/Rut="+rut+
                "/razonSocial="+razonSocial+"/direccion="+direccion+"/comuna="+comuna+"/tipoPago="+tipoPago+
                "/excento="+excento+"/neto="+neto+"/iva="+iva+"/total="+total+"/agencia="+agencia+"/punto="+punto+
                "/arqueo="+arqueo+"/estado="+estado+"/estadoDoc="+estadoDoc+"/usuario="+usuario+"/mac="+mac+"/ip="+ip+
                "/sistema="+sistema+"/formulario="+formulario+"/version="+version+"/urlDoc="+url+"/impresion="+impresion+
                "/solAnulacion="+solAnulacion+"/giro="+giro+"/tipoVia="+tipoVia+"/numeracion="+numeracion+"/fono="+fono+
                "/extra="+extra+"/odt="+odt);
        del.setHeader("content-type", "application/json");

        resp = httpClient.execute(del);
        respStr = EntityUtils.toString(resp.getEntity());

        responseObject = new JSONObject(respStr);
        responseObject1 = new JSONObject(respStr);

        responseObject1 = responseObject.getJSONObject("ValidacionTO");
        String fechaActuals = responseObject.getString("fecha");
        String filas = responseObject1.getString("Filas");

        if(Integer.parseInt(filas) == 1){

            responseObject = responseObject.getJSONObject("ArchivoDocElectronicoTO");
            String tipoDoc = responseObject.getString("TipoDoc");
            if(!tipoDoc.equals("0")){

                archivoDocElectronicoTO =  new ArchivoDocElectronicoTO();
                //////////////////////////////////////////////////
                archivoDocElectronicoTO.setFechaActual(fechaActuals);
                //////////////////////////////////////////////////
                archivoDocElectronicoTO.setTipoDoc(responseObject.getString("TipoDoc"));
                archivoDocElectronicoTO.setFolio(responseObject.getString("Folio"));
                archivoDocElectronicoTO.setfCreacion(responseObject.getString("FCreacion"));
                archivoDocElectronicoTO.setIndNoRebaja(responseObject.getString("IndNoRebaja"));
                archivoDocElectronicoTO.setTipoDespacho(responseObject.getString("TipoDespacho"));
                archivoDocElectronicoTO.setIndTraslado(responseObject.getString("IndTraslado"));
                archivoDocElectronicoTO.setIndServicio(responseObject.getString("IndServicio"));
                archivoDocElectronicoTO.setFormaPago(responseObject.getString("FormaPago"));
                archivoDocElectronicoTO.setRut(responseObject.getString("Rut"));
                archivoDocElectronicoTO.setRazonSocial(responseObject.getString("RazonSocial"));
                archivoDocElectronicoTO.setGiro(responseObject.getString("Giro"));
                archivoDocElectronicoTO.setcActividad(responseObject.getString("CActividad"));
                archivoDocElectronicoTO.setAgencia(responseObject.getString("Agencia"));
                archivoDocElectronicoTO.setcAgencia(responseObject.getString("CAgencia"));
                archivoDocElectronicoTO.setcEmiTraExep(responseObject.getString("CEmiTraExep"));
                archivoDocElectronicoTO.setDireccion(responseObject.getString("Direccion"));
                archivoDocElectronicoTO.setComuna(responseObject.getString("Comuna"));
                archivoDocElectronicoTO.setCiudad(responseObject.getString("Ciudad"));
                archivoDocElectronicoTO.setcVendedor(responseObject.getString("CVendedor"));
                archivoDocElectronicoTO.setcRut(responseObject.getString("CRut"));
                archivoDocElectronicoTO.setcIntRecep(responseObject.getString("CIntRecep"));
                archivoDocElectronicoTO.setcRazonSocial(responseObject.getString("CRazonSocial"));
                archivoDocElectronicoTO.setcGiro(responseObject.getString("CGiro"));
                archivoDocElectronicoTO.setcContacto(responseObject.getString("CContacto"));
                archivoDocElectronicoTO.setcCorreo(responseObject.getString("CCorreo"));
                archivoDocElectronicoTO.setcDireccion(responseObject.getString("CDireccion"));
                archivoDocElectronicoTO.setcComuna(responseObject.getString("CComuna"));
                archivoDocElectronicoTO.setcCiudad(responseObject.getString("CCiudad"));
                archivoDocElectronicoTO.setPatente(responseObject.getString("Patente"));
                archivoDocElectronicoTO.settRut(responseObject.getString("TRut"));
                archivoDocElectronicoTO.setDirDest(responseObject.getString("DirDest"));
                archivoDocElectronicoTO.setComDest(responseObject.getString("ComDest"));
                archivoDocElectronicoTO.setCiuDest(responseObject.getString("CiuDest"));
                archivoDocElectronicoTO.setNeto(responseObject.getString("Neto"));
                archivoDocElectronicoTO.setMontoExento(responseObject.getString("MontoExento"));
                archivoDocElectronicoTO.settIva(responseObject.getString("TIva"));
                archivoDocElectronicoTO.setIva(responseObject.getString("Iva"));
                archivoDocElectronicoTO.setIvaProp(responseObject.getString("IvaProp"));
                archivoDocElectronicoTO.setIvaTer(responseObject.getString("IvaTer"));
                archivoDocElectronicoTO.setTotal(responseObject.getString("Total"));
                archivoDocElectronicoTO.setTimStmp(responseObject.getString("TimStmp"));
                archivoDocElectronicoTO.setcTImp(responseObject.getString("CTImp"));
                archivoDocElectronicoTO.settImpAd(responseObject.getString("TImpAd"));
                archivoDocElectronicoTO.setMontImpAd(responseObject.getString("MontImpAd"));
                archivoDocElectronicoTO.setIvaNoRet(responseObject.getString("IvaNoRet"));
                archivoDocElectronicoTO.setMontNoFact(responseObject.getString("MontNoFact"));
                archivoDocElectronicoTO.setTipoCodigo(responseObject.getString("TipoCodigo"));
                archivoDocElectronicoTO.setcItem(responseObject.getString("CItem"));
                archivoDocElectronicoTO.setIndExep(responseObject.getString("IndExep"));
                archivoDocElectronicoTO.setoDT(responseObject.getString("ODT"));
                archivoDocElectronicoTO.setDescAdi(responseObject.getString("DescAdi"));
                archivoDocElectronicoTO.setCantItem(responseObject.getString("CantItem"));
                archivoDocElectronicoTO.setUniMed(responseObject.getString("UniMed"));
                archivoDocElectronicoTO.setPrecioUnit(responseObject.getString("PrecioUnit"));
                archivoDocElectronicoTO.setPorCentDescto(responseObject.getString("PorCentDescto"));
                archivoDocElectronicoTO.setMontDesct(responseObject.getString("MontDesct"));
                archivoDocElectronicoTO.setPorcentRecar(responseObject.getString("PorcentRecar"));
                archivoDocElectronicoTO.setMontRecar(responseObject.getString("MontRecar"));
                archivoDocElectronicoTO.setMontoItem(responseObject.getString("MontoItem"));
                archivoDocElectronicoTO.setValCodInt(responseObject.getString("ValCodInt"));
                archivoDocElectronicoTO.setCodImpAd(responseObject.getString("CodImpAd"));
                archivoDocElectronicoTO.setTipoMov(responseObject.getString("TipoMov"));
                archivoDocElectronicoTO.setGlosa(responseObject.getString("Glosa"));
                archivoDocElectronicoTO.setTipoVal(responseObject.getString("TipoVal"));
                archivoDocElectronicoTO.setValor(responseObject.getString("Valor"));
                archivoDocElectronicoTO.setTipoDoc2(responseObject.getString("TipoDoc2"));
                archivoDocElectronicoTO.setIndGloRef(responseObject.getString("IndGloRef"));
                archivoDocElectronicoTO.setFolioDocRef(responseObject.getString("FolioDocRef"));
                archivoDocElectronicoTO.setfEmision(responseObject.getString("FEmision"));
                archivoDocElectronicoTO.setCodRef(responseObject.getString("CodRef"));
                archivoDocElectronicoTO.setRazonRef(responseObject.getString("RazonRef"));
                archivoDocElectronicoTO.setFormPago(responseObject.getString("FormPago"));
                archivoDocElectronicoTO.setMontEscrito(responseObject.getString("MontEscrito"));
                archivoDocElectronicoTO.setObs(responseObject.getString("Obs"));
                archivoDocElectronicoTO.setSubTotal(responseObject.getString("SubTotal"));
                archivoDocElectronicoTO.setNumInt(responseObject.getString("NumInt"));
                archivoDocElectronicoTO.setAgencias(responseObject.getString("Agencias"));
                archivoDocElectronicoTO.setcMatriz(responseObject.getString("CMatriz"));
                archivoDocElectronicoTO.setSucursal(responseObject.getString("Sucursal"));
                archivoDocElectronicoTO.setImpresora(responseObject.getString("Impresora"));
                if(!responseObject.getString("ted").isEmpty()){
                    archivoDocElectronicoTO.setTed(responseObject.getString("ted"));
                }
                archivoDocElectronicoTOs.add(archivoDocElectronicoTO);
            }
        }else if(Integer.parseInt(filas) > 1){

            JSONArray respJSON = responseObject.getJSONArray("ArchivoDocElectronicoTO");

            for (int i = 0; i < respJSON.length(); i++) {

                archivoDocElectronicoTO =  new ArchivoDocElectronicoTO();
                JSONObject obj = respJSON.getJSONObject(i);
                archivoDocElectronicoTO.setFechaActual(fechaActuals);
                archivoDocElectronicoTO.setTipoDoc(obj.getString("TipoDoc"));
                archivoDocElectronicoTO.setFolio(obj.getString("Folio"));
                archivoDocElectronicoTO.setfCreacion(obj.getString("FCreacion"));
                archivoDocElectronicoTO.setIndNoRebaja(obj.getString("IndNoRebaja"));
                archivoDocElectronicoTO.setTipoDespacho(obj.getString("TipoDespacho"));
                archivoDocElectronicoTO.setIndTraslado(obj.getString("IndTraslado"));
                archivoDocElectronicoTO.setIndServicio(obj.getString("IndServicio"));
                archivoDocElectronicoTO.setFormaPago(obj.getString("FormaPago"));
                archivoDocElectronicoTO.setRut(obj.getString("Rut"));
                archivoDocElectronicoTO.setRazonSocial(obj.getString("RazonSocial"));
                archivoDocElectronicoTO.setGiro(obj.getString("Giro"));
                archivoDocElectronicoTO.setcActividad(obj.getString("CActividad"));
                archivoDocElectronicoTO.setAgencia(obj.getString("Agencia"));
                archivoDocElectronicoTO.setcAgencia(obj.getString("CAgencia"));
                archivoDocElectronicoTO.setcEmiTraExep(obj.getString("CEmiTraExep"));
                archivoDocElectronicoTO.setDireccion(obj.getString("Direccion"));
                archivoDocElectronicoTO.setComuna(obj.getString("Comuna"));
                archivoDocElectronicoTO.setCiudad(obj.getString("Ciudad"));
                archivoDocElectronicoTO.setcVendedor(obj.getString("CVendedor"));
                archivoDocElectronicoTO.setcRut(obj.getString("CRut"));
                archivoDocElectronicoTO.setcIntRecep(obj.getString("CIntRecep"));
                archivoDocElectronicoTO.setcRazonSocial(obj.getString("CRazonSocial"));
                archivoDocElectronicoTO.setcGiro(obj.getString("CGiro"));
                archivoDocElectronicoTO.setcContacto(obj.getString("CContacto"));
                archivoDocElectronicoTO.setcCorreo(obj.getString("CCorreo"));
                archivoDocElectronicoTO.setcDireccion(obj.getString("CDireccion"));
                archivoDocElectronicoTO.setcComuna(obj.getString("CComuna"));
                archivoDocElectronicoTO.setcCiudad(obj.getString("CCiudad"));
                archivoDocElectronicoTO.setPatente(obj.getString("Patente"));
                archivoDocElectronicoTO.settRut(obj.getString("TRut"));
                archivoDocElectronicoTO.setDirDest(obj.getString("DirDest"));
                archivoDocElectronicoTO.setComDest(obj.getString("ComDest"));
                archivoDocElectronicoTO.setCiuDest(obj.getString("CiuDest"));
                archivoDocElectronicoTO.setNeto(obj.getString("Neto"));
                archivoDocElectronicoTO.setMontoExento(obj.getString("MontoExento"));
                archivoDocElectronicoTO.settIva(obj.getString("TIva"));
                archivoDocElectronicoTO.setIva(obj.getString("Iva"));
                archivoDocElectronicoTO.setIvaProp(obj.getString("IvaProp"));
                archivoDocElectronicoTO.setIvaTer(obj.getString("IvaTer"));
                archivoDocElectronicoTO.setTotal(obj.getString("Total"));
                archivoDocElectronicoTO.setTimStmp(obj.getString("TimStmp"));
                archivoDocElectronicoTO.setcTImp(obj.getString("CTImp"));
                archivoDocElectronicoTO.settImpAd(obj.getString("TImpAd"));
                archivoDocElectronicoTO.setMontImpAd(obj.getString("MontImpAd"));
                archivoDocElectronicoTO.setIvaNoRet(obj.getString("IvaNoRet"));
                archivoDocElectronicoTO.setMontNoFact(obj.getString("MontNoFact"));
                archivoDocElectronicoTO.setTipoCodigo(obj.getString("TipoCodigo"));
                archivoDocElectronicoTO.setcItem(obj.getString("CItem"));
                archivoDocElectronicoTO.setIndExep(obj.getString("IndExep"));
                archivoDocElectronicoTO.setoDT(obj.getString("ODT"));
                archivoDocElectronicoTO.setDescAdi(obj.getString("DescAdi"));
                archivoDocElectronicoTO.setCantItem(obj.getString("CantItem"));
                archivoDocElectronicoTO.setUniMed(obj.getString("UniMed"));
                archivoDocElectronicoTO.setPrecioUnit(obj.getString("PrecioUnit"));
                archivoDocElectronicoTO.setPorCentDescto(obj.getString("PorCentDescto"));
                archivoDocElectronicoTO.setMontDesct(obj.getString("MontDesct"));
                archivoDocElectronicoTO.setPorcentRecar(obj.getString("PorcentRecar"));
                archivoDocElectronicoTO.setMontRecar(obj.getString("MontRecar"));
                archivoDocElectronicoTO.setMontoItem(obj.getString("MontoItem"));
                archivoDocElectronicoTO.setValCodInt(obj.getString("ValCodInt"));
                archivoDocElectronicoTO.setCodImpAd(obj.getString("CodImpAd"));
                archivoDocElectronicoTO.setTipoMov(obj.getString("TipoMov"));
                archivoDocElectronicoTO.setGlosa(obj.getString("Glosa"));
                archivoDocElectronicoTO.setTipoVal(obj.getString("TipoVal"));
                archivoDocElectronicoTO.setValor(obj.getString("Valor"));
                archivoDocElectronicoTO.setTipoDoc2(obj.getString("TipoDoc2"));
                archivoDocElectronicoTO.setIndGloRef(obj.getString("IndGloRef"));
                archivoDocElectronicoTO.setFolioDocRef(obj.getString("FolioDocRef"));
                archivoDocElectronicoTO.setfEmision(obj.getString("FEmision"));
                archivoDocElectronicoTO.setCodRef(obj.getString("CodRef"));
                archivoDocElectronicoTO.setRazonRef(obj.getString("RazonRef"));
                archivoDocElectronicoTO.setFormPago(obj.getString("FormPago"));
                archivoDocElectronicoTO.setMontEscrito(obj.getString("MontEscrito"));
                archivoDocElectronicoTO.setObs(obj.getString("Obs"));
                archivoDocElectronicoTO.setSubTotal(obj.getString("SubTotal"));
                archivoDocElectronicoTO.setNumInt(obj.getString("NumInt"));
                archivoDocElectronicoTO.setAgencias(obj.getString("Agencias"));
                archivoDocElectronicoTO.setcMatriz(obj.getString("CMatriz"));
                archivoDocElectronicoTO.setSucursal(obj.getString("Sucursal"));
                archivoDocElectronicoTO.setImpresora(obj.getString("Impresora"));
                if(i == 0){
                    if(!obj.getString("ted").isEmpty()){
                        archivoDocElectronicoTO.setTed(obj.getString("ted"));
                    }
                }
                archivoDocElectronicoTOs.add(archivoDocElectronicoTO);

            }
        }
        return archivoDocElectronicoTOs;
    }


    public String buscaDatosCtaCte(String cta) throws IOException, ClientProtocolException, JSONException {

        String respStr;
        String respuesta = "";
        HttpClient httpClient;
        HttpGet del;
        HttpResponse resp;
        JSONObject responseObject;
        JSONObject responseObject1;
        httpClient = new DefaultHttpClient();
        ValidaTO validaTO = null;

        del = new HttpGet(RUTA_WEB_SERVICE + "/TraeDatosCuenta/ctacte="+cta);
        del.setHeader("content-type", "application/json");

        try {
            resp = httpClient.execute(del);
            respStr = EntityUtils.toString(resp.getEntity());

            responseObject = new JSONObject(respStr);
            responseObject1 = responseObject.getJSONObject("datosCuentaTO");
            respuesta = responseObject1.getString("Cta_descripcion");
        }catch (Exception e){
            //e.printStackTrace();
            respuesta = "Error: "+e.getMessage();
            return respuesta;
        }


        return respuesta;
    }

    public String RealizaTraspaso(String cta,String odt) throws IOException, ClientProtocolException, JSONException {
        ValidaTO validaTO = new ValidaTO();
        String respuesta = "";

        try {

            HttpClient httpclient = new DefaultHttpClient();
            JSONObject responseObject;
            HttpPost httppost = new HttpPost(RUTA_WEB_SERVICE+ "/TraspasoCtaCte");
            httppost.setHeader("Content-Type", "application/json");

            // forma el JSON y tipo de contenido
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("odt", odt);
            jsonObject.put("cta", cta);
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
            respuesta = validaTO.getMensaje();

        } catch (Exception e) {

            respuesta = e.getMessage();
            return respuesta;
        }
        return respuesta;
    }

    public UsuarioReceptorTO TraeDatosUsuarioReceptor(String rut) throws IOException, JSONException {
        String respStr;
        UsuarioReceptorTO usuario = new UsuarioReceptorTO();
        HttpClient httpClient;
        HttpGet del;
        HttpResponse resp;
        JSONObject responseObject;
        JSONObject responseObject1;
        httpClient = new DefaultHttpClient();
        ValidaTO validaTO = null;

        del = new HttpGet(RUTA_WEB_SERVICE + "/TraeDatosClienteReceptor/rut="+rut);
        del.setHeader("content-type", "application/json");

        try {
            resp = httpClient.execute(del);
            respStr = EntityUtils.toString(resp.getEntity());

            responseObject = new JSONObject(respStr);
            responseObject1 = responseObject.getJSONObject("requestClienteReceptorTO");
            usuario.setRut(String.valueOf(responseObject1.getString("rut").equals("")?"":responseObject1.getString("rut")));
            usuario.setNombre(String.valueOf(responseObject1.getString("nombre").equals("")?"":responseObject1.getString("nombre")));
            usuario.setApPaterno(String.valueOf(responseObject1.getString("apPaterno").equals("")?"":responseObject1.getString("apPaterno")));
            usuario.setApMaterno(String.valueOf(responseObject1.getString("apMaterno").equals("")?"":responseObject1.getString("apMaterno")));
            usuario.setTelefono(String.valueOf(responseObject1.getString("fono").equals("")?"":responseObject1.getString("fono")));
        }catch (Exception e){
            throw e;
        }

        return usuario;
    }

    public String GraModUsuarioReceptor(String rut,String nombre, String appaterno, String apmaterno, String fono) throws IOException, ClientProtocolException, JSONException {
        ValidaTO validaTO = new ValidaTO();
        String respuesta = "";

        try {

            HttpClient httpclient = new DefaultHttpClient();
            JSONObject responseObject;
            HttpPost httppost = new HttpPost(RUTA_WEB_SERVICE+ "/AgregaClienteReceptor");
            httppost.setHeader("Content-Type", "application/json");


            // forma el JSON y tipo de contenido
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("rut", rut);
            jsonObject.put("nombre", nombre);
            jsonObject.put("apPaterno", appaterno);
            jsonObject.put("apMaterno", apmaterno);
            jsonObject.put("fono", fono);
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
            respuesta = validaTO.getMensaje();

        } catch (Exception e) {

            respuesta = e.getMessage();
            return respuesta;
        }
        return respuesta;
    }

}
