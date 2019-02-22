package com.example.primos;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private EditText inputField, resultField;
    private Button primecheckbutton;
    private MyAsyncTask mAsyncTask;
    //Cancelar la comprobación de si un número es primo
    private boolean isRunning;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputField = (EditText) findViewById(R.id.inputField);
        resultField = (EditText) findViewById(R.id.resultField);
        primecheckbutton = (Button) findViewById(R.id.primecheckbutton);
    }

    //Determinar si un número es primo mediante AsyncTask******************

    public void triggerPrimecheck(View v){
        Log.v(TAG, "Thread " + Thread.currentThread().getId() +
                ": Comienza triggerPrimecheck()");
        long parameter = Long.parseLong(inputField.getText().toString());
        mAsyncTask = new MyAsyncTask();
        mAsyncTask.execute(parameter);
        Log.v(TAG, "Thread " + Thread.currentThread().getId() +
                ": Finaliza triggerPrimecheck()");
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
            for (long factor = 3; factor < limite; factor += 2) {
                if (numComprobar % factor == 0)
                    return false;
                if (factor > limite * progreso / 100) {
                    publishProgress(progreso / 100);
                    progreso += 5;
                }
            }
            Log.v(TAG, "Thread " + Thread.currentThread().getId() +
                    ": Finaliza doInBackground()");
            return true;
        }

        @Override protected void onPreExecute() {
            Log.v(TAG, "Thread " + Thread.currentThread().getId() +
                    ": onPreExecute()");
            resultField.setText("");
            primecheckbutton.setEnabled(false);
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
            primecheckbutton.setEnabled(true);
        }
    }
    //*************************
    //*********************************************************************
}
