package com.example.terraproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.terraproject.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TarikActivity extends AppCompatActivity {
    private static final Object TAG = "test";
    EditText etNoKTP, etSaldo;
    Button btnTarikSaldo;
    private ProgressDialog progressDialog;
    RequestQueue queue;
    String nikKaryawan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarik);
        getSupportActionBar().setTitle("Menu Tarik Tunai");

        //set navigation icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //deklarasi variabel baru untuk menampung data yang berhasil di get
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        nikKaryawan = prefs.getString("nikKaryawan", "");// is the default value.

        Log.d("cek Data", "nik karyawan " + nikKaryawan);

        //binding variabel
        etNoKTP = findViewById(R.id.editTextNoKTP);
        etSaldo = findViewById(R.id.editTextInputSaldo);
        btnTarikSaldo = findViewById(R.id.buttonKirimSaldo);
        queue = Volley.newRequestQueue(this);

        btnTarikSaldo.setOnClickListener(view -> {
            String etNoKTP = this.etNoKTP.getText().toString();
            String saldo = etSaldo.getText().toString();

            if (etNoKTP.isEmpty() || saldo.isEmpty()){
                showSnackbar(view , "No Rekening dan Saldo harus diisi");
            } else {
//                sendTarik(etNoKTP, saldo);
                makeJsonObjReq();
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

    private void makeJsonObjReq() {
        dialog();

        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("nikKaryawan" , nikKaryawan);
        postParam.put("nikKtp", etNoKTP.getText().toString());
        postParam.put("nominal", etSaldo.getText().toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                "http://192.168.1.9:8081/client/nasabah/tariktunai/"+etNoKTP.getText().toString(), new JSONObject(postParam),
                response -> {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    etNoKTP.setText("");
                    etSaldo.setText("");
                    Toast.makeText(getApplicationContext(), "Tarik Tunai Berhasil", Toast.LENGTH_SHORT).show();

                }, error -> {
                    VolleyLog.d("Cek Error", "Error: " + error.getMessage());
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Tarik Tunai Gagal", Toast.LENGTH_SHORT).show();
                    Log.d("STATUS_SETOR", "tidak dapat response");
                }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        jsonObjReq.setTag(TAG);
        // Adding request to request queue
        queue.add(jsonObjReq);
    }
}