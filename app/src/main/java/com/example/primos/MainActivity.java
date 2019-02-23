package com.example.primos;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements TaskListener{

    private static final String TAG = MainActivity.class.getName();
    private EditText inputField, resultField;
    private Button primecheckbutton;
    private MyAsyncTask mAsyncTask;
    //Cancelar la comprobación de si un número es primo
    //private boolean isRunning;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputField = (EditText) findViewById(R.id.inputField);
        resultField = (EditText) findViewById(R.id.resultField);
        primecheckbutton = (Button) findViewById(R.id.primecheckbutton);
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        //Al pasar a segundo plano debería detener la ejecución del hilo que comprueba el número
        if(mAsyncTask!=null && mAsyncTask.getStatus() == AsyncTask.Status.RUNNING){  //if(isRunning){
            //isRunning = false;
            Log.v(TAG, "Cancelando test " + Thread.currentThread().getId());
            Toast.makeText(this, "Cancelado test"+ Thread.currentThread().getId(), Toast.LENGTH_LONG).show();
            mAsyncTask.cancel(true);
        }

    }

    //Determinar si un número es primo mediante AsyncTask******************

    public void triggerPrimecheck(View v) {

        /*
            Dependiendo del estado de ejecución de la tarea asíncrona, comenzaremos
            una nueva comprobación o detendremos una comprobación en curso
         */
        if(mAsyncTask == null || mAsyncTask.getStatus() == AsyncTask.Status.FINISHED){
            /*if(mAsyncTask != null) {
                Log.i("Thread", "Estado al principio de triggerPrimeCheck (DEL TOO): " + mAsyncTask.getStatus());
            }*/
            mAsyncTask = new MyAsyncTask(this); //Tras hacer nueva instancia, el estado es PENDING
            //NOTA: Con "this", referencia a la clase que implementa el interface de comunicación, que en nuestro caso será MainActivity
        }

            if (mAsyncTask != null && mAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
                //if(!isRunning) {
                //isRunning = true;
                //Log.i("Thread", "Estado al principio de triggerPrimeCheck 1: " + mAsyncTask.getStatus());
                Log.v(TAG, "Thread " + Thread.currentThread().getId() +
                        ": triggerPrimecheck() comienza");
                long parameter = Long.parseLong(inputField.getText().toString());
                //mAsyncTask = new MyAsyncTask();  //Tras hacer nueva instancia, el estado es PENDING
                mAsyncTask.execute(parameter); //Tras ejecutar, el estado es RUNNING
                Log.v(TAG, "Thread " + Thread.currentThread().getId() +
                        ": triggerPrimecheck() termina");
                //Log.i("Thread", "Estado al final de triggerPrimeCheck 1: " + mAsyncTask.getStatus());
            } else if(mAsyncTask.getStatus() == AsyncTask.Status.RUNNING){
                //isRunning = false;
                Log.v(TAG, "Cancelando test " + Thread.currentThread().getId());
                mAsyncTask.cancel(true);
                //Log.i("Thread", "Estado al final de triggerPrimeCheck 2: " + mAsyncTask.getStatus());
            }
    }
    //*********************************************************************

    //Implementación de los métodos de TaskListener**********************
    @Override public void onPreExecute() {
        resultField.setText("");
        primecheckbutton.setText("CANCELAR");
        lockScreenOrientation();
    }

    @Override public void onProgressUpdate(double progreso) {
        resultField.setText(String.format("%.1f%% completado", progreso*100));
    }

    @Override public void onPostExecute(boolean resultado) {
        resultField.setText(resultado + "");
        primecheckbutton.setText("¿ES PRIMO?");
        unlockScreenOrientation();
    }

    @Override public void onCancelled() {
        resultField.setText("Proceso cancelado");
        primecheckbutton.setText("¿ES PRIMO?");
        unlockScreenOrientation();
    }
    //*******************************************************************

    //Mëtodos para bloquear la orientación de la pantalla cuando esté en ejecución el hilo secundario
    private void lockScreenOrientation() {
        int currentOrientation= getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
    //***********************************************************************************************
}
