package com.example.memorableplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static ArrayList<String> places = new ArrayList<>();
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ListView list = findViewById(R.id.list);

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.memorableplaces", Context.MODE_PRIVATE);
        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();

        places.clear();
        latitudes.clear();
        longitudes.clear();
        locations.clear();

        try{
            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes=(ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("long",ObjectSerializer.serialize(new ArrayList<String>())));


        }catch(Exception e){
            e.printStackTrace();
        }
        if(places.size()>0 && longitudes.size()>0 && latitudes.size()>0){
            if (places.size() == latitudes.size() && places.size() == longitudes.size()) {
                for(int i =0;i<latitudes.size();i++) {
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)), Double.parseDouble(longitudes.get(i))));
                }
            }
        }else{
            places.add("ADD new places.....");
            locations.add(new LatLng(0,0));
        }


        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,places);
        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("number",i);
                startActivity(intent);

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                int itemtodelete=i;
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Do you want to delete this")
                        .setMessage("Are you sure")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                places.remove(itemtodelete);
                                locations.remove(itemtodelete);
                                arrayAdapter.notifyDataSetChanged();
                                try {
                                    ArrayList<String> latitudes = new ArrayList<>();
                                    ArrayList<String> longitudes = new ArrayList<>();
                                    for(LatLng cord: MainActivity.locations){
                                        latitudes.add(Double.toString(cord.latitude));
                                        longitudes.add(Double.toString(cord.longitude));
                                    }
                                    sharedPreferences.edit().putString("places",ObjectSerializer.serialize(places)).apply();
                                    sharedPreferences.edit().putString("lats",ObjectSerializer.serialize(latitudes)).apply();
                                    sharedPreferences.edit().putString("long",ObjectSerializer.serialize(longitudes)).apply();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }


                            }
                        })
                        .setNegativeButton("NO",null)
                        .show();

                return true;
            }
        });
    }
}