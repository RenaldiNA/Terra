package com.example.terraproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

public class TabunganActivity extends AppCompatActivity {
    ListView listView;
    SearchView searchView;
    ArrayAdapter adapter;

    //Data static nasabah
    String[] nasabah = {"Renaldi" , "Halim" , "Faris" , "Naufal",
            "Dolos" , "Anre" , "Ihsan" , "Aldi"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabungan);

        //set navigation icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //binding variabel
        listView = findViewById(R.id.listViewLayoutTabungan);
        searchView = findViewById(R.id.searchViewTabungan);
        searchView.onActionViewExpanded();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nasabah);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
}