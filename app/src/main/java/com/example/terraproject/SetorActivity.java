package com.example.terraproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class SetorActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    EditText etNorek, etSaldo;
    Button btnSetorSaldo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setor);

        //set navigation icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etNorek = findViewById(R.id.editTextNoRekSetor);
        etSaldo = findViewById(R.id.editTextInputSetorSaldo);
        btnSetorSaldo = findViewById(R.id.buttonSetorSaldo);

        btnSetorSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String norek = etNorek.getText().toString();
                String saldo = etSaldo.getText().toString();

                if (norek.isEmpty() || saldo.isEmpty()){
                    showSnackbar(view , "No Rekening dan Saldo harus diisi");
                } else {
                    sendSetor(norek, saldo);
                }
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
        progressDialog = new ProgressDialog(SetorActivity.this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //    fungsi library volley
    private void sendSetor(String norek, String saldo){
        dialog();
        //Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(SetorActivity.this);

        // Request a string response from the provided URL.
        String url = "http://192.168.1.9:8080/client/nasabah/setortunai/" + etNorek;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        Log.d("cek status", "isi response  : " + response);

                        if(progressDialog.isShowing()) progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Setor Tunai Berhasil", Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.d("STATUS_SETOR", "tidak dapat response")
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                params.put("norek", norek);
                params.put("jumlahtarik", saldo);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}