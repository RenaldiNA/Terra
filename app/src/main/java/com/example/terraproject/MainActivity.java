package com.example.terraproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        //deklarasi variabel baru untuk menampung data yang berhasil di get
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String perfNama = prefs.getString("nama", "");// is the default value.

        Log.d("cek Data", "data nama " + perfNama);

        Button btnSetor = findViewById(R.id.buttonSetorTunai);
        Button btnTarik = findViewById(R.id.buttonTarikTunai);
//        Button btnCicilan = findViewById(R.id.buttonCicilanBaru);
        Button btnBayar = findViewById(R.id.buttonBayarAngsuran);
        Button btnPinjaman = findViewById(R.id.buttonPinjamanNasabah);
        Button btnTabungan = findViewById(R.id.buttonTabunganNasabah);
        Button btnLogout = findViewById(R.id.btnLogout);
        TextView textViewUser = findViewById(R.id.textViewUser);

        textViewUser.setText("Hai CO" + ", " + perfNama);

        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Hallo Community Officer, " + perfNama)
                .setContentText("Selamat Datang di Terra")
                .show();

        btnSetor.setOnClickListener(view -> {
            // fungsi menuju ke setor tunai
            Intent setor = new Intent(getApplicationContext(), SetorActivity.class);
            startActivity(setor); // jalankan activity ketika sudah di inialisasi
        });

        btnTarik.setOnClickListener(view -> {
            // fungsi menuju ke tarik tunai
            Intent tarik = new Intent(getApplicationContext(), TarikActivity.class);
            startActivity(tarik); // jalankan activity ketika sudah di inialisasi
        });

        btnPinjaman.setOnClickListener(view -> {
            // fungsi menuju ke pinjaman nasabah
            Intent pinjaman = new Intent(getApplicationContext(), PinjamanActivity.class);
            startActivity(pinjaman); // jalankan activity ketika sudah di inialisasi
        });

        btnTabungan.setOnClickListener(view -> {
            // fungsi menuju ke tabungan nasabah
            Intent tabungan = new Intent(getApplicationContext(), TabunganActivity.class);
            startActivity(tabungan); // jalankan activity ketika sudah di inialisasi
        });

        btnLogout.setOnClickListener(view -> {
            logOut();
        });

    }

    //fungsi klik ketika logout
    private void logOut() {
        SharedPreferences rm = getSharedPreferences("session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = rm.edit();
        String perfNama= rm.getString("nama", "No Nama Defined");

        //jika email tidak kosong
        if(!perfNama.isEmpty()){
            editor.clear();
            editor.apply();
            sendToLogin();
        }
    }

    // fungsi menuju ke login
    private void sendToLogin() {
        //handel ketika klik tombol back akan langsung keluar ke home
        startActivity(new Intent(getApplicationContext(), LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }
}