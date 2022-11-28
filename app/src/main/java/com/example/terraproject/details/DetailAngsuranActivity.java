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
import com.example.terraproject.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailAngsuranActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    ListView listView;
    String perfNikKTP , nik;
    ArrayList<HashMap<String, String>> arrayList;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_angsuran);
        getSupportActionBar().setTitle("Detail Angsuran Nasabah");

        //set navigation icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = findViewById(R.id.listViewLayoutDetailAngsuran);

        //deklarasi variabel baru untuk menampung data yang berhasil di get
        perfNikKTP= getIntent().getExtras().getString("dataAngsuran");

        String[] data = perfNikKTP.split(",");
        String tenor = data[0];
        String jumlahPembiayaan = data[1];
        String tanggalPembiayaan = data[2];
        String noPembiayaan = data[3];
        String nikKTP = data[4];

        String getNik = nikKTP.split("=")[1];
        nik = getNik.substring(0 , getNik.length()-1);
        Log.d("cek nik", "data nik " + nik);

        arrayList = new ArrayList<>();
        getDetailAngsuran();
    }

    // ketika navigation sudah diset, kita buat function nya
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void dialog(){
        progressDialog = new ProgressDialog(DetailAngsuranActivity.this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //fungsi volley
    private void getDetailAngsuran(){
        dialog();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://192.168.1.9:8081/client/nasabah/summary/pinjaman/"+nik;

        Log.d("cek isi url", "url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url ,
                response -> {
                    try {
                        //get detailNasabah json
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray detailPinjaman = jsonObject.getJSONArray("pembiayaan");

                        //looping pembiayaan
                        for (int i = 0; i < detailPinjaman.length(); i ++){
                            JSONObject object = detailPinjaman.getJSONObject(i);
                            JSONArray detailAngsuran = object.getJSONArray("angsurans");

                            Log.d("Cek", "getDetailAngsuran: " + detailAngsuran);

                            //looping angsuran
                            for (int j = 0; j < detailAngsuran.length(); j ++){
                                JSONObject objectAngsuran = detailAngsuran.getJSONObject(j);
                                String noPembiayaan = objectAngsuran.getString("noPembiayaan");
                                String bayarangsuran = objectAngsuran.getString("bayarangsuran");
                                String tanggalcicilan = objectAngsuran.getString("tanggalcicilan");
                                String nikkaryawan = objectAngsuran.getString("nikkaryawan");

                                Log.d("cek object angsuran " , "data " + objectAngsuran);

                                String[] data = tanggalcicilan.split("T");
                                String tanggal = data[0];
                                String dataZero = data[1];

                                // tmp hash map for single allData
                                HashMap<String, String> allData = new HashMap<>();

                                // adding each child node to HashMap key => value
                                allData.put("noPembiayaan" , noPembiayaan);
                                allData.put("bayarangsuran" , bayarangsuran);
                                allData.put("tanggalcicilan" , tanggal);
                                allData.put("nikKaryawan" , nikkaryawan );

                                // adding contact to array list
                                arrayList.add(allData);

                                //progress dialog
                                if(progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            }  if(detailAngsuran.length() == 0){
                                progressDialog.dismiss();

                                new SweetAlertDialog(DetailAngsuranActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Belum ada angsuran")
                                        .show();
                            }
                        }

                        adapter = new SimpleAdapter(this,
                                arrayList,
                                R.layout.angsuran_item,
                                new String[] {"noPembiayaan" , "bayarangsuran" , "nikKaryawan" , "tanggalcicilan"} ,
                                new int[]{R.id.textViewNoCicilan , R.id.textViewSaldoCicilan , R.id.textViewCO, R.id.textViewTanggalCicilan});
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