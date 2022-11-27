package com.example.terraproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TarikActivity extends AppCompatActivity {
    EditText etNorek, etSaldo;
    Button btnTarikSaldo;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarik);

        //set navigation icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //binding variabel
        etNorek = findViewById(R.id.editTextNoRekening);
        etSaldo = findViewById(R.id.editTextInputSaldo);
        btnTarikSaldo = findViewById(R.id.buttonKirimSaldo);

        btnTarikSaldo.setOnClickListener(view -> {
            String norek = etNorek.getText().toString();
            String saldo = etSaldo.getText().toString();

            if (norek.isEmpty() || saldo.isEmpty()){
                showSnackbar(view , "No Rekening dan Saldo harus diisi");
            } else {
                sendTarik(norek, saldo);
            }
        });
    }

    // ketika navigation sudah diset, kita buat function nya
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //fungsi snackbar dan terdapat parameter
    public void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor("#F57C00"))
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .setAction("Oke", view1 -> {
                }).setActionTextColor(Color.parseColor("#FFFFFFFF"));
        snackbar.show();
    }
    //munculin progressDialog / loading
    private void dialog(){
        progressDialog = new ProgressDialog(TarikActivity.this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //    fungsi library volley
    private void sendTarik(String norek, String saldo){
        dialog();
        //Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(TarikActivity.this);

        // Request a string response from the provided URL.
        String url = "http://192.168.1.9:8080/client/nasabah/tariktunai/2";
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
//                    try {
//                        JSONObject jsonObject = new JSONObject(response);

                        Log.d("cek status", "isi response  : " + response);

                        if(progressDialog.isShowing()) progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Tarik Tunai Berhasil", Toast.LENGTH_SHORT).show();

//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }, error -> Log.d("STATUS_SETOR", "tidak dapat response")
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("norekStr", norek);
                params.put("jumlahtarikStr", saldo);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}