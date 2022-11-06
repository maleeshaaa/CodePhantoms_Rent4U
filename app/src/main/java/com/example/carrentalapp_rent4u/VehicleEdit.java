package com.example.carrentalapp_rent4u;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.carrentalapp_rent4u.databinding.ActivityVehicleEditBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VehicleEdit extends AppCompatActivity {

    private ActivityVehicleEditBinding binding;

    private String vehicleId;

    private ProgressDialog progressDialog;

    private ArrayList<String> categoryTitleArrayList,categoryIdArrayList;

    private static final String TAG = "VEHICLE_EDIT_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVehicleEditBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_vehicle_edit);

        vehicleId = getIntent().getStringExtra("vehicleId");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadCategories();
        loadVehicleInfo();

        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.catgryselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryPickDialog();
            }
        });

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ;
            }
        });
    }

    private void loadVehicleInfo() {

        Log.d(TAG,"LoadBookInfo: Loading Book Info");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Vehicles");
        ref.child(vehicleId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        selectedCategoryId = ""+snapshot.child("categoryId").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String name = ""+snapshot.child("name").getValue();
                        String location = ""+snapshot.child("location").getValue();
                        String contact = ""+ snapshot.child("contact").getValue();
                        String fee = ""+snapshot.child("fee").getValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String selectedCategoryId="",selectedCategoryTitle="";

    private void categoryPickDialog() {

        Log.d(TAG,"pdfPickIntent: starting pdf pick intent");

        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for(int i = 0; i< categoryTitleArrayList.size(); i++){
            categoriesArray[i] = categoryTitleArrayList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedCategoryTitle = categoryTitleArrayList.get(i);
                        selectedCategoryId = categoryIdArrayList.get(i);

                        binding.catgryselect.setText(selectedCategoryTitle);

                        Log.d(TAG,"onClick: Selected Category:"+selectedCategoryId+" "+selectedCategoryTitle);
                    }
                })
                .show();
    }

    private void loadCategories() {
        Log.d(TAG,"loadCategories:Loading categories...");
        categoryIdArrayList = new ArrayList<>();
        categoryTitleArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryIdArrayList.clear();
                for(DataSnapshot DS: snapshot.getChildren()){

                    String categoryId = ""+DS.child("id").getValue();
                    String categoryName = "" + DS.child("category").getValue();

                    categoryTitleArrayList.add(categoryName);
                    categoryIdArrayList.add(categoryId);

                    Log.d(TAG,"onDataChange: ID"+categoryId);
                    Log.d(TAG,"onDataChange: Category"+ categoryName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}