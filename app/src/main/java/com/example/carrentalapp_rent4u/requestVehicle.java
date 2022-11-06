package com.example.carrentalapp_rent4u;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Member;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class requestVehicle extends AppCompatActivity {
    //date
    TextView dateRangeText;
    Button date_pickup_btn;

    //database connect
    EditText vehicletype,vehiclecategory,pickUpdate,timeTo,timeFrom;
    Button continuebtn;
    DatabaseReference reff;
    Request request;
    long maxid=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requestvehicle);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateRangeText=findViewById(R.id.pickUpdate);
        date_pickup_btn = findViewById(R.id.date_pickup_btn);
        MaterialDatePicker materialDatePicker=MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(),MaterialDatePicker.todayInUtcMilliseconds())).build();

        date_pickup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(),"Tag_picker");
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        dateRangeText.setText(materialDatePicker.getHeaderText());
                    }
                });
            }
        });
            vehicletype = (EditText) findViewById(R.id.vehicletype);
            vehiclecategory = (EditText) findViewById(R.id.vehiclecategory);
            pickUpdate = (EditText) findViewById(R.id.pickUpdate);
            timeTo = (EditText) findViewById(R.id.timeTo);
            timeFrom = (EditText) findViewById(R.id.timeFrom);
            continuebtn = (Button) findViewById(R.id.continuebtn);
            request=new Request();
            reff= FirebaseDatabase.getInstance().getReference().child("Request");
            reff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                        maxid=(snapshot.getChildrenCount());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            continuebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    request.setVehicleType(vehicletype.getText().toString().trim());
                    request.setVehicleCategory(vehiclecategory.getText().toString().trim());
                    request.setPickUpdate(pickUpdate.getText().toString().trim());
                    request.setTimeTo(timeTo.getText().toString().trim());
                    request.setTimeFrom(timeFrom.getText().toString().trim());
                    reff.child(String.valueOf(maxid+1)).setValue(request);
                    Toast.makeText(requestVehicle.this,"Data Inserted successfully",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(requestVehicle.this,MyRequest.class);
                    startActivity(intent);

                }
            });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}