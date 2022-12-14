package com.example.terraproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.terraproject.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    EditText etEmail, etPassword;
    private ProgressDialog progressDialog;
    private static final Object TAG = "test";
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        btnLogin    = findViewById(R.id.buttonLogin);
        etEmail     = findViewById(R.id.editTextEmail);
        etPassword  = findViewById(R.id.editTextPassword);
        queue = Volley.newRequestQueue(this);

        // cek sudah dapat data dari sharedpreferences atau belum
        isLogin();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email    = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()){
                    showSnackbar(view , "Email/Password harus diisi");
                } else {
                    sendLogin(email, password);
                }
            }
        });
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

    public void isLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        String dataNama = sharedPreferences.getString("nama", "");//is default value
        Log.d("cek data login nama " , "isi data " + dataNama);

//         jika data email dapat akses ke main dipersilahkan
        if(!dataNama.isEmpty()) {
            Intent goMain = new Intent(this , MainActivity.class);
            startActivity(goMain);
        }

    }

    //munculin progressDialog / loading
    private void dialog(){
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

//    fungsi library volley
    private void sendLogin(String email, String password){
        dialog();
        //Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
//        081283241341
        // Request a string response from the provided URL.
        String url = "http://192.168.1.9:8081/client/login";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String nama = jsonObject.getString("nama");
                        String nikKtp = jsonObject.getString("nikKtp");
                        String nikKaryawan = jsonObject.getString("nikKaryawan");

                        Log.d("cek status", "isi response  : " + response);
                        Log.d("cek status", "isi data  : " + jsonObject);
                        Log.d("cek status", "isi nama  : " + nama);
                        Log.d("cek status", "isi nikKtp  : " + nikKtp);
                        Log.d("cek status", "isi nikKaryawan  : " + nikKaryawan);

                        if(progressDialog.isShowing()) progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = getSharedPreferences("session", MODE_PRIVATE).edit();
                            editor.putString("nama", nama);
                            editor.putString("nikKtp" , nikKtp);
                            editor.putString("nikKaryawan" , nikKaryawan);
                            editor.commit();

                            //jika sukses ke halaman main
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                            if(progressDialog.isShowing()) progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Login Gagal", Toast.LENGTH_SHORT).show();
                            Log.d("STATUS_LOGIN", "tidak dapat response");
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}