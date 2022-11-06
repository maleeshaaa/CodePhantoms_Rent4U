package com.example.carrentalapp_rent4u;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.carrentalapp_rent4u.databinding.ActivityVehicleDisplayBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Vehicle_Display extends AppCompatActivity {

    private ActivityVehicleDisplayBinding binding;

    private ArrayList<ModelVehicle> vehicleArrayList;

    private AdapterVehicle adapterVehicle;

    private String categoryId , categoryTitle;

    private static final String TAG = "PDF_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVehicleDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");

        binding.subTitle2.setText(categoryTitle);

        loadVehicleList();

        binding.search2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try{
                    adapterVehicle.getFilter().filter(charSequence);
                }
                catch (Exception e){
                    Log.d(TAG,"onTextChanged"+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadVehicleList() {
        vehicleArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Vehicles");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        vehicleArrayList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelVehicle modelVehicle = ds.getValue(ModelVehicle.class);

                            vehicleArrayList.add(modelVehicle);

                            Log.d(TAG,"onDataChange:"+modelVehicle.getId()+""+ modelVehicle.getName());
                        }
                        adapterVehicle = new AdapterVehicle(Vehicle_Display.this,vehicleArrayList);
                        binding.VehicleRv.setAdapter(adapterVehicle);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}