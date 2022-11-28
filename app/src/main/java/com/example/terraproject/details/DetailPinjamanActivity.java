package com.example.terraproject.details;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;

public class DetailPinjamanActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    ListView listView;
    String perfNikKTP , nik;
    ArrayList<HashMap<String, String>> arrayList;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pinjaman);
        getSupportActionBar().setTitle("Detail Pinjaman Nasabah");

        //set navigation icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //deklarasi variabel baru untuk menampung data yang berhasil di get
        perfNikKTP= getIntent().getExtras().getString("dataPinjaman");

        String[] data = perfNikKTP.split(",");
        String nama = data[0];
        String nikKTP = data[1];

        String getNik = nikKTP.split("=")[1];
        nik = getNik.substring(0 , getNik.length()-1);

        listView = findViewById(R.id.listViewLayoutDetailPembiayaan);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            String pos = listView.getAdapter().getItem(i).toString();

//            jika sukses ke halaman detail
            Intent intent = new Intent(getApplicationContext(), DetailAngsuranActivity.class);
            intent.putExtra("dataAngsuran" , pos);
            startActivity(intent);
        });


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
        progressDialog = new ProgressDialog(DetailPinjamanActivity.this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //fungsi volley
    private void getDetailNasabah(){
        dialog();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://192.168.1.9:8081/client/nasabah/summary/pinjaman/"+nik;

        Log.d("cek isi url", "url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url ,
                response -> {
                    try {
                        //get detailNasabah json
                        JSONObject jsonObject = new JSONObject(response);

                        //get detailNasabah json array
                        JSONArray detailPinjaman = jsonObject.getJSONArray("pembiayaan");

                        Log.d("Cek detail" , "pembiayaan" + detailPinjaman);

                        //looping pembiayaan
                        for (int i = 0; i < detailPinjaman.length(); i ++){
                            JSONObject object = detailPinjaman.getJSONObject(i);
                            String noKTP = object.getString("nikKtp");
                            String noPembiayaan = object.getString("noPembiayaan");
                            String jumlahPembiayaan = object.getString("jumlahPembiayaan");
                            String tenor = object.getString("tenor");
                            String tanggalPembiayaan = object.getString("tanggalPembiayaan");

                            String[] data = tanggalPembiayaan.split("T");
                            String tanggal = data[0];
                            String dataZero = data[1];

                            // tmp hash map for single allData
                            HashMap<String, String> allData = new HashMap<>();

                            // adding each child node to HashMap key => value
                            allData.put("nikKtp" , noKTP);
                            allData.put("noPembiayaan" , noPembiayaan);
                            allData.put("jumlahPembiayaan" , jumlahPembiayaan);
                            allData.put("tenor", tenor);
                            allData.put("tanggalPembiayaan", tanggal);

                            // adding contact to array list
                            arrayList.add(allData);

                            //progress dialog
                            if(progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        adapter = new SimpleAdapter(this,
                                arrayList,
                                R.layout.pembiayaan_item,
                                new String[] {"noPembiayaan" , "jumlahPembiayaan" , "tenor" , "tanggalPembiayaan"} ,
                                new int[]{R.id.textViewNoPembiayaan , R.id.textViewDetailPembiayaan , R.id.textViewTenor, R.id.textViewTanggalPembiayaan});
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