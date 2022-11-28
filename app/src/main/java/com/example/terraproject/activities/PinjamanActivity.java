package com.example.terraproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.terraproject.details.DetailPinjamanActivity;
import com.example.terraproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PinjamanActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    ListView listView;
    SearchView searchView;
    ArrayList<HashMap<String, String>> arrayList;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinjaman);
        getSupportActionBar().setTitle("Detail Pinjaman Nasabah");

        //set navigation icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //binding variabel
        listView = findViewById(R.id.listViewLayoutPinjaman);
        searchView = findViewById(R.id.searchViewPinjaman);
        searchView.onActionViewExpanded();

        arrayList = new ArrayList<>();
        getAllPinjaman();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String pos = listView.getAdapter().getItem(i).toString();

                //jika sukses ke halaman detail
                Intent intent = new Intent(getApplicationContext(), DetailPinjamanActivity.class);
                intent.putExtra("dataPinjaman" , pos);
                startActivity(intent);

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    // ketika navigation sudah diset, kita buat function nya
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void dialog(){
        progressDialog = new ProgressDialog(PinjamanActivity.this);
        progressDialog.setMessage("Mohon tunggu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //fungsi volley
    private void getAllPinjaman(){
        dialog();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://192.168.1.9:8080/api/pembiayaan/all";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url ,
                response -> {
                    try {
                        //get data json
                        JSONObject jsonObject = new JSONObject(response);

                        //get data json array
                        JSONArray data = jsonObject.getJSONArray("payload");

                        //looping data
                        for (int i = 0; i < data.length(); i ++){
                            JSONObject object = data.getJSONObject(i);
                            String nikKtp = object.getString("nikKtp");
                            String nama = object.getString("nama");

                            // tmp hash map for single allData
                            HashMap<String, String> allData = new HashMap<>();

                            // adding each child node to HashMap key => value
                            allData.put("nama", nama);
                            allData.put("nikKtp", nikKtp);

                            // adding contact to array list
                            arrayList.add(allData);

                            //progress dialog
                            if(progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        adapter = new SimpleAdapter(this,
                                arrayList,
                                R.layout.list_item_pinjaman,
                                new String[] {"nama", "nikKtp"} , new int[]{R.id.textViewNamaNasabah, R.id.textViewNoRek});
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