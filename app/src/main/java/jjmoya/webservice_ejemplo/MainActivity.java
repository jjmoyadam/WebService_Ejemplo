package jjmoya.webservice_ejemplo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    //atributos para la inserccion

    private String inserNombre="";
    private String inserDni="";
    private String inserEmail="";
    private String inserTelefono="";

    public  EditText edemail;
    private EditText eddni;
    private EditText ednombre;
    private EditText edtelefono;

    private TextView tvemail;
    private TextView tvdni;
    private TextView tvnombre;
    private TextView tvtelefono;

    private EditText edbuscardni;

    private Button btmostrar;
    private Button btguardar;
    private Button btbusquedadni;
    private ImageButton btatras;
    private ImageButton btadenalte;

    //atributos para Consulta_datos datos
    private int posicion = 0;
    private List listaPersonas;
    private ProgressDialog pDialog;


    //objeto para manejar los datos de la base de datos
    private BBDD baseDatos;
    private Funciones funciones;

    //obtener el contexto de la app
    public Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //objeto para el manejo con la base de datos

        baseDatos= new BBDD();
        funciones=new Funciones();

        //asociamos las variables a los elementos del layout
        tvnombre = (TextView) findViewById(R.id.tv_nombre);
        tvtelefono = (TextView) findViewById(R.id.tv_telefono);
        tvemail = (TextView) findViewById(R.id.tv_correo);
        tvdni = (TextView) findViewById(R.id.tv_dni);

        ednombre = (EditText) findViewById(R.id.ed_nombre);
        edtelefono = (EditText) findViewById(R.id.ed_telefono);
        edemail = (EditText) findViewById(R.id.ed_correo);
        eddni = (EditText) findViewById(R.id.ed_dni);

        edbuscardni=(EditText)findViewById(R.id.ed_busquedaDni);

        btmostrar = (Button) findViewById(R.id.bt_mostrar);
        btguardar = (Button) findViewById(R.id.bt_Guardar);
        btbusquedadni= (Button) findViewById(R.id.bt_buscarDni);
        btatras=(ImageButton)findViewById(R.id.IB_atras);
        btadenalte=(ImageButton)findViewById(R.id.IB_adelante);



        //inicializamos el arraylist para recoger los datos
        listaPersonas = new ArrayList();

        //FUNCIONALIDAD DE LOS BOTONES

        /**
         * lanza el hilo de mostrar todos los datos de la base de datos
         */
        btmostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new WebService_Mostrar(MainActivity.this).execute();
            }
        });


        /**
         * Guarda los datos del formulario y resetea formulario
         */
        btguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //recogemos los datos de la IU
                 inserNombre=ednombre.getText().toString().trim();
                 inserDni=eddni.getText().toString().trim();
                 inserEmail=edemail.getText().toString().trim();
                 inserTelefono=edtelefono.getText().toString().trim();

                //comprobamos que todos los campos esten llenos
                if (!inserNombre.equals("")||!inserDni.equals("")|| !inserTelefono.equals("")||!inserEmail.equals(""))

                    new Insertar(MainActivity.this).execute();

                else

                    Toast.makeText(MainActivity.this, "Informacion Por Rellenar", Toast.LENGTH_LONG).show();
            }
        });

        /**
         * Servicio que recupera los datos de una persona segun el dni
         */
        btbusquedadni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new WebService_MostrarDNI(MainActivity.this).execute();
            }
        });

        btadenalte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //si el arraylist de mostrar personas no esta vacio
                if(!listaPersonas.isEmpty()){
                    //comprobamos que posicion no tenga un valor superior al de nuestro array
                    if(posicion>=listaPersonas.size()-1){
                        //si es superior o igual, iguala el valor de posicion al tamaño de nuestro array
                        posicion=listaPersonas.size()-1;
                        //mostramos el valor de la persona
                        mostrarPersona(posicion);
                    }else{
                        posicion++;
                        mostrarPersona(posicion);

                    }

                }

            }
        });
        btatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //si el arraylist de mostrar personas no esta vacio
                if(!listaPersonas.isEmpty()){
                    //comprobamos que posicion no tenga un valor negativo
                    if(posicion<=0){
                        //si es superior o igual, iguala el valor de posicion al tamaño de nuestro array
                        posicion=0;
                        //mostramos el valor de la persona
                        mostrarPersona(posicion);
                    }else{
                        posicion--;
                        mostrarPersona(posicion);

                    }

                }

            }
        });

    }

    /**
     * Metodo para manejar el objeto json que recibimos de la Bd para
     * para cargarlo en un objeto de tipo persona y a su vez lo guardamos un array de objetos persona
     *
     * @return
     */
    private boolean filtrarDatos() {

        //boolean para el resultado
        boolean resul = false;
        //limpiamos la lista de personas para rellenar
        listaPersonas.clear();
        //creacion del objeto persona para manejar los datos
        Personas personas = new Personas();

        //Recogemos los datos de la ejecucion del metodo Consulta_datos que recibe un objeto json
        String respuesta = baseDatos.Consulta_datos();


        //compara respuesta ignorando mayusculas y minusculas con la cadena ""
        if (!respuesta.equalsIgnoreCase("")) {

            //creacion del objeto Json
            JSONObject json;

            //error de json
            boolean error_json = false;

            //devuelve un array json si existe el indice dni e indexamos los datos para Consulta_datos
            try {

                //json respuesta del objeto
                json = new JSONObject(respuesta);

                //creamos un array de tipo json para recoger los datos si existe el indice datos
                JSONArray jsonArray = json.optJSONArray("datos");

                //for para recorrer los datos
                for (int i = 0; i < jsonArray.length(); i++) {

                    //creacion del objeto
                    Personas persona = new Personas();

                    //obtenemos el objeto json de la primera posicion 0
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                    //guardamos los datos en el objeto persona
                    persona.setDni(jsonArrayChild.optString("dni"));
                    persona.setNombre(jsonArrayChild.optString("nombre"));
                    persona.setTelefono(jsonArrayChild.optString("telefono"));
                    persona.setEmail(jsonArrayChild.optString("email"));

                    //y lo añadimos a listapersona que es un array de objeto persona
                    listaPersonas.add(persona);

                }

            } catch (JSONException e) {
                // Error al convertir a JSON
                // => esto sucede porque no hay alumnos en la consulta y el arrya json está vacío
                // o por cualquie otro motivo
                e.printStackTrace();
                error_json = true;

            }

            //devolvemos el objeto json si no hay error en la creacion del objeto json

            if (error_json)
                resul = false;
            else
                resul = true;
        } else
            resul = false;

        return resul;

    }

    /**
     *
     * @param posicion
     */
    private void mostrarPersona(final int posicion){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Personas personas2= (Personas) listaPersonas.get(posicion);
                ednombre.setText(personas2.getNombre());
                eddni.setText(personas2.getDni());
                edtelefono.setText(personas2.getTelefono());
                edemail.setText(personas2.getEmail());

            }
        });


    }

    /**
     * Metodo para cargar datos de la BD segun el dato de dni
     *
     * @return
     */
    private boolean filtrarDatosdni() {

        //Recoger datos de dni
        String dato_dni=edbuscardni.getText().toString();

        //boolean para el resultado
        boolean resul = false;
        //limpiamos la lista de personas para rellenar
        listaPersonas.clear();
        //creacion del objeto persona para manejar los datos
        Personas personas = new Personas();


        String respuesta = baseDatos.ConsultaPorDni(dato_dni);


        //compara respuesta ignorando mayusculas y minusculas con la cadena "" y miramos que la respuesta no este vacia, si lo esta es que
        //no existe
        if (!respuesta.equalsIgnoreCase("")) {

            //creacion del objeto Json
            JSONObject json;

            //error de json
            boolean error_json = false;

            //devuelve un array json si existe el indice dni e indexamos los datos para Consulta_datos
            try {

                //json respuesta del objeto
                json = new JSONObject(respuesta);

                //creamos un array de tipo json para recoger los datos si existe el indice datos
                JSONArray jsonArray = json.optJSONArray("datos");

                //for para recorrer los datos
                for (int i = 0; i < jsonArray.length(); i++) {

                    //creacion del objeto
                    Personas persona = new Personas();

                    //obtenemos el objeto json de la primera posicion 0
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                    //guardamos los datos en el objeto persona
                    persona.setDni(jsonArrayChild.optString("dni"));
                    persona.setNombre(jsonArrayChild.optString("nombre"));
                    persona.setTelefono(jsonArrayChild.optString("telefono"));
                    persona.setEmail(jsonArrayChild.optString("email"));

                    //y lo añadimos a listapersona que es un array de objeto persona
                    listaPersonas.add(persona);

                }

            } catch (JSONException e) {
                // Error al convertir a JSON
                // => esto sucede porque no hay alumnos en la consulta y el arrya json está vacío
                // o por cualquie otro motivo
                e.printStackTrace();
                error_json = true;

            }

            //devolvemos el objeto json si no hay error en la creacion del objeto json

            if (error_json)
                resul = false;
            else
                resul = true;

        } else
            resul = false;

        return resul;
        //fin de mostrar dni

    }


    /**
     * Metodo para mostrar mensajes
     * @param mensaje
     */
    public void tostada(String mensaje) {
        Toast toast1 = Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT);
        toast1.show();
    }

    /**
     * Metodo para resetear Datos
     *
     * @
     */
    private void resetDatos() {

        eddni.setText("");
        edemail.setText("");
        edtelefono.setText("");
        ednombre.setText("");


    }

    /**** ASYNTASK*****************/

    //MOSTRAR................ASYNTASK.............
    class WebService_Mostrar extends AsyncTask<String, String, String> {

        //Activity
        private Activity context;


        WebService_Mostrar(Activity context) {

            this.context = context;

        }

        // ANTES DE EJECUTAR mostramos una barra de progreso para cargar los datos si es necesario
        @Override
        protected void onPreExecute() {

            // Crea la barra de progreso si es necesario
            if (pDialog == null)
                pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Conectando a la Base de Datos....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        // EJECUTA Tarea a realizar en segundo plano (con otro hilo que no está en el Interfaz de Usuario)
        // por lo tanto esta tarea no puede interaccionar con el usuario
        @Override
        protected String doInBackground(String... strings) {

            String resultado = "ERROR";

            //filtramos los datos para mostrarlos
            if (filtrarDatos()) {

                resultado = "OK";
            } else
                resultado = "ERROR";

            return resultado;

        }

        /**
         * mostramos los datos en la UI si el metodo filtrar datos nos devuelve OK
         * Mediante el metodo Consulta_datos persona
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {

            //borramos la barra de dialogo
            pDialog.dismiss();

            // si el resultado del if anterior es ok mostramos los datos
            if (result.equals("OK")) {
                int posicion = 0;
                // se puede Consulta_datos el alumno
                mostrarPersona(posicion);
            } else {

                tostada("NO SE PUEDE MOSTRAR EL MENSAJE");

            }

        }
    }

    //MOSTRARDNI................ASYNTASK.............
    class WebService_MostrarDNI extends AsyncTask<String, String, String> {

        //Activity
        private Activity context;


        WebService_MostrarDNI(Activity context) {

            this.context = context;

        }

        // ANTES DE EJECUTAR mostramos una barra de progreso para cargar los datos si es necesario
        @Override
        protected void onPreExecute() {

            // Crea la barra de progreso si es necesario
            if (pDialog == null)
                pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Conectando a la Base de Datos....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        // EJECUTA Tarea a realizar en segundo plano (con otro hilo que no está en el Interfaz de Usuario)
        // por lo tanto esta tarea no puede interaccionar con el usuario
        @Override
        protected String doInBackground(String... strings) {

            String resultado = "ERROR";

            //filtramos los datos para mostrarlos
            if (filtrarDatosdni()) {

                resultado = "OK";
            } else
                resultado = "ERROR";

            return resultado;

        }

        /**
         * mostramos los datos en la UI si el metodo filtrar datos nos devuelve OK
         * Mediante el metodo Consulta_datos persona
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {

            //borramos la barra de dialogo
            pDialog.dismiss();

            // si el resultado del if anterior es ok mostramos los datos
            if (result.equals("OK")) {
                int posicion = 0;
                // se puede Consulta_datos el alumno
                mostrarPersona(posicion);
            } else {

                tostada("No Existe Coincidendcia De DNI");

            }

        }
    }


    //INSERTAR................ASYNTASK.............
    class Insertar extends AsyncTask<String, String, String> {

        private Activity context;

        Insertar(Activity context) {


            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {

            if (baseDatos.insertar(inserDni,inserNombre,inserTelefono,inserEmail))
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(context, "Persona INSERTADA con exito", Toast.LENGTH_LONG).show();
                        //Reseteamos los campos
                        ednombre.setText("");
                        eddni.setText("");
                        edemail.setText("");
                        edtelefono.setText("");
                    }
                });
            else
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Persona NO INSERTADA con exito", Toast.LENGTH_LONG).show();
                    }
                });
            return null;
        }
    }




}

