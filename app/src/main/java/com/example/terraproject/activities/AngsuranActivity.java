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

public class AngsuranActivity extends AppCompatActivity {
    private static final Object TAG = "test";
    private ProgressDialog progressDialog;
    EditText etNoPembiayaan, etAngsuran;
    Button btnAngsur;
    RequestQueue queue;
    String nikKaryawan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angsuran);
        getSupportActionBar().setTitle("Angsuran Nasabah");

        //set navigation icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //deklarasi variabel baru untuk menampung data yang berhasil di get
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        nikKaryawan = prefs.getString("nikKaryawan", "");// is the default value.

        Log.d("cek nik " ,"karyawan " + nikKaryawan);

        //binding variabel
        etNoPembiayaan = findViewById(R.id.editTextNoPembiayaan);
        etAngsuran = findViewById(R.id.editTextInputAngsuran);
        btnAngsur = findViewById(R.id.buttonKirimAngsuran);
        queue = Volley.newRequestQueue(this);

        btnAngsur.setOnClickListener(view -> {
            String noPembiayaan = etNoPembiayaan.getText().toString();
            String angsuran = etAngsuran.getText().toString();

            if(noPembiayaan.isEmpty() || angsuran.isEmpty()){
                showSnackbar(view , "No Pembiayaan dan Saldo harus diisi");
            } else {
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
        progressDialog = new ProgressDialog(AngsuranActivity.this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void makeJsonObjReq() {
        dialog();

        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("nikkaryawan" , nikKaryawan);
        postParam.put("noPembiayaan", etNoPembiayaan.getText().toString());
        postParam.put("bayarangsuran", etAngsuran.getText().toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                "http://192.168.1.9:8081/client/angsuran", new JSONObject(postParam),
                response -> {
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    etNoPembiayaan.setText("");
                    etAngsuran.setText("");
                    Toast.makeText(getApplicationContext(), "Bayar Angsuran Berhasil", Toast.LENGTH_SHORT).show();

                }, error -> {
                    VolleyLog.d("Cek Error", "Error: " + error.getMessage());
                    if(progressDialog.isShowing()) progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Bayar Angsuran Gagal", Toast.LENGTH_SHORT).show();
                    Log.d("STATUS_ANGSURAN", "tidak dapat response");
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