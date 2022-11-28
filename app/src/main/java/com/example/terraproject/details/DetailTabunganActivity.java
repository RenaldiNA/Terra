package com.example.terraproject.details;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.terraproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailTabunganActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    ListView listView;
    String perfNoRekening;
    ArrayList<HashMap<String, String>> arrayList;
    SimpleAdapter adapter;
    String saldo, nik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tabungan);
        getSupportActionBar().setTitle("Detail Tabungan Nasabah");

        //set navigation icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = findViewById(R.id.listViewLayoutDetail);

        //deklarasi variabel baru untuk menampung data yang berhasil di get
        perfNoRekening = getIntent().getExtras().getString("dataNasabah");

        String[] data = perfNoRekening.split(",");
        String nama = data[0];
        String nikKtp = data[1];
        String dataNoRek = data[2];
        String dataSaldo = data[3];

        Log.d("Cek split" , "hasil " + nikKtp);
        Log.d("Cek split" , "hasil " + dataSaldo);

        nik = nikKtp.split("=")[1];

        Log.d("cek isi", "data key" + nik);

        arrayList = new ArrayList<>();
        getDetailNasabah();

    }

    // ketika navigation sudah diset, kita buat function nya
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void dialog(){
        progressDialog = new ProgressDialog(DetailTabunganActivity.this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //fungsi volley
    private void getDetailNasabah(){
        dialog();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://192.168.1.9:8081/client/nasabah/summary/tabungan/"+nik;

        Log.d("cek isi url", "url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url ,
                response -> {
                    try {
                        //get detailNasabah json
                        JSONArray jsonArray = new JSONArray(response);

                        Log.d("cek json array", "array " + jsonArray);

                        //looping detailNasabah
                        for (int i = 0; i < jsonArray.length(); i ++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            String nominal = object.getString("nominal");
                            String nikkaryawan = object.getString("nikkaryawan");
                            String norek = object.getString("norek");
                            String tanggal = object.getString("tanggal");

                            String[] data = tanggal.split("T");
                            String tgl = data[0];
                            String dataZero = data[1];

                            // tmp hash map for single allData
                            HashMap<String, String> allData = new HashMap<>();

                            // adding each child node to HashMap key => value
                            allData.put("nominal" , nominal);
                            allData.put("nikkaryawan" , nikkaryawan);
                            allData.put("norek", norek);
                            allData.put("tanggal", tgl);

                            // adding contact to array list
                            arrayList.add(allData);

                            //progress dialog
                            if(progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        if(jsonArray.length() == 0){
                            progressDialog.dismiss();

                            new SweetAlertDialog( DetailTabunganActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Belum ada transaksi")
                                    .show();
                        }

                        adapter = new SimpleAdapter(this,
                                arrayList,
                                R.layout.grid_item,
                                new String[] {"nominal" , "nikkaryawan" , "norek" , "tanggal"} ,
                                new int[]{R.id.textViewDetailMutasi , R.id.textViewDetailKeterangan , R.id.textViewDetailNoRek, R.id.textViewDetailTanggal});
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}