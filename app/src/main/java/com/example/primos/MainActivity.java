package com.example.primos;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

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
            mAsyncTask = new MyAsyncTask(); //Tras hacer nueva instancia, el estado es PENDING
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

    //Clase interna************
    private class MyAsyncTask extends AsyncTask<Long, Double, Boolean> {
        @Override protected Boolean doInBackground(Long... n) {
            Log.v(TAG, "Thread " + Thread.currentThread().getId() +
                    ": Comienza doInBackground()");
            long numComprobar = n[0];
            if (numComprobar < 2 || numComprobar % 2 == 0)
                return false;
            double limite = Math.sqrt(numComprobar) + 0.0001;
            double progreso = 0;
            for (long factor = 3; factor < limite && !isCancelled(); factor += 2) { //detendremos la comprobación lo antes posible una vez nos lo indique el usuario
                if (numComprobar % factor == 0)
                    return false;
                if (factor > limite * progreso / 100) {
                    publishProgress(progreso / 100);
                    progreso += 5;
                }
            }
            Log.v(TAG, "Thread " + Thread.currentThread().getId() +
                    ": Finaliza doInBackground()");
            //Log.i("Thread","Estado al final de doInBackground: "+mAsyncTask.getStatus());
            return true;
        }

        @Override protected void onPreExecute() {
            Log.v(TAG, "Thread " + Thread.currentThread().getId() +
                    ": onPreExecute()");
            resultField.setText("");
            //primecheckbutton.setEnabled(false);
            primecheckbutton.setText("CANCELAR");
            //Log.i("Thread","Estado al final de onPreExecute: "+mAsyncTask.getStatus());
        }

        @Override protected void onProgressUpdate(Double... progress) {
            Log.v(TAG, "Thread " + Thread.currentThread().getId() +
                    ": onProgressUpdate()");
            resultField.setText(String.format("%.1f%% completed",
                    progress[0] * 100));
        }

        @Override protected void onPostExecute(Boolean isPrime) {
            Log.v(TAG, "Thread " + Thread.currentThread().getId() +
                    ": onPostExecute()");
            resultField.setText(isPrime + "");
            //primecheckbutton.setEnabled(true);
            primecheckbutton.setText("¿ES PRIMO?");
            //isRunning=false;
            //Log.i("Thread","Estado al final de onPostExecute: "+mAsyncTask.getStatus());
        }

        @Override protected void onCancelled() {
            Log.v(TAG, "Thread " + Thread.currentThread().getId() +
                    ": onCancelled");
            super.onCancelled();
            resultField.setText("Proceso cancelado");
            primecheckbutton.setText("¿ES PRIMO?");
            //Log.i("Thread","Estado al final de onCancelled: "+mAsyncTask.getStatus());
        }
    }//FIN CLASE INTERNA ***********
    //*********************************************************************
}
