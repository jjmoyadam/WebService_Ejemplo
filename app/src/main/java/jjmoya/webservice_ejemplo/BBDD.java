package jjmoya.webservice_ejemplo;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by curso on 24/09/2017.
 */




public class BBDD {

    private final String servidor="http://192.168.1.135";

    private final String rutaPhpInsertar="/00ejemplos/agenda/insertagenda.php";
    private final String rutaPhpConsultaTotal="/00ejemplos/agenda/seleccion_agenda.php";
    private final String rutaPhpConsultaPorDni="/00ejemplos/agenda/seleccion_por_dni.php";

    /**
     * Metodo para insertar los datos en la base de datos remota
     *
     * @return resquet  devuelve ok si lo inserta
     */
    public boolean insertar(String dni,String nombre,String telefono,String email) {

        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpPost httpPost;
        httpclient = new DefaultHttpClient();

        //direccion para la inserccion de los datos en la base de datos
        httpPost = new HttpPost(servidor+rutaPhpInsertar);

        //mediante un array de par de valores con tamaño de cuatro cargamos los datos en nuestra base de datos
        nameValuePairs = new ArrayList<NameValuePair>(4);

        nameValuePairs.add(new BasicNameValuePair("dni", dni));
        nameValuePairs.add(new BasicNameValuePair("nombre", nombre));
        nameValuePairs.add(new BasicNameValuePair("telefono", telefono));
        nameValuePairs.add(new BasicNameValuePair("email", email));

        //ahora insertamos los datos le pasamos por parametro el array par valor de elementos
        try {

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpclient.execute(httpPost);

            //si lo inserta devolvera true
            return true;

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        } catch (ClientProtocolException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return false;
    }


    /**
     * Consulta los datos de todo los datos de la base
     * @return devuelve todos los datos de la agenda
     */
    public String Consulta_datos() {
        String resquest = "";
        HttpClient httpclient;
        HttpPost httpPost;
        httpclient = new DefaultHttpClient();

        //direccion para la inserccion de los datos en la base de datos
        httpPost = new HttpPost(servidor+rutaPhpConsultaTotal);


        //ahora recogemos los datos de la bd para mostrarlo por pantalla
        try {
            //creacion del manejador para la respuesa.
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            //ejecutamos la consulta
            resquest = httpclient.execute(httpPost, responseHandler);


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        } catch (ClientProtocolException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return resquest;
    }


    /**
     * Consulta por DNI
     * @param dni
     * @return devuelve el dato
     */
    public String ConsultaPorDni(String dni) {

        String resquest = "";
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpPost httpPost;
        httpclient = new DefaultHttpClient();

        //direccion para la inserccion de los datos en la base de datos
        httpPost = new HttpPost(servidor+rutaPhpConsultaPorDni);

        //mediante un array de par de valores con tamaño de cuatro cargamos los datos en nuestra base de datos
        nameValuePairs = new ArrayList<NameValuePair>(1);

        //recogemos el dato para hacer la consulta del edittext
        nameValuePairs.add(new BasicNameValuePair("edbuscardni",dni));



        //ahora recogemos los datos de la bd para mostrarlo por pantalla
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //creacion del manejador para la respuesa.
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            //recogemos la respuesta
            resquest=httpclient.execute(httpPost,responseHandler);


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        } catch (ClientProtocolException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return resquest;
    }


}
