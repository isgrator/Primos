package com.example.primos;

public interface TaskListener {
    void onPreExecute();
    void onProgressUpdate(double progreso);
    void onPostExecute(boolean resultado);
    void onCancelled();
}
