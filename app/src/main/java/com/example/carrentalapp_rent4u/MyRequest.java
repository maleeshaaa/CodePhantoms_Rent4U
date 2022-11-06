package com.example.carrentalapp_rent4u;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MyRequest extends AppCompatActivity {
    TextView a, b, c, d, e;
    Button btn,btndelete,btnedit;
    DatabaseReference reff;
    Toolbar toolbar;
    FirebaseDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        a = (TextView) findViewById(R.id.tview);
        b = (TextView) findViewById(R.id.cview);
        c = (TextView) findViewById(R.id.viewdate);
        d = (TextView) findViewById(R.id.viewfrom);
        e = (TextView) findViewById(R.id.viewto);
        btn = (Button) findViewById(R.id.showdata);
        btndelete =(Button) findViewById(R.id.deletebtn);
        btnedit =(Button) findViewById(R.id.editbtn);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reff = FirebaseDatabase.getInstance().getReference().child("Request").child("1");
                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String vehicleType = snapshot.child("vehicleType").getValue().toString();
                        String vehicleCategory = snapshot.child("vehicleCategory").getValue().toString();
                        String pickUpdate = snapshot.child("pickUpdate").getValue().toString();
                        String timeTo = snapshot.child("timeTo").getValue().toString();
                        String timeFrom = snapshot.child("timeFrom").getValue().toString();

                        a.setText(vehicleType);
                        b.setText(vehicleCategory);
                        c.setText(pickUpdate);
                        d.setText(timeTo);
                        e.setText(timeFrom);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Request").child("1").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MyRequest.this,"Booking Canclled", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MyRequest.this,Request.class);
                        startActivity(intent);
                        clearControls();
                    }
                });
            }
        });
        //update data
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vehicleType = a.getText().toString();
                String vehicleCategory = b.getText().toString();
                String pickUpdate = c.getText().toString();
                String timeTo = d.getText().toString();
                String timeFrom =e.getText().toString();

                updateData(vehicleType,vehicleCategory,pickUpdate,timeTo,timeFrom);
            }
        });

    }

    private void updateData(String vehicleType, String vehicleCategory, String pickUpdate, String timeTo, String timeFrom) {
        HashMap<String,Object> Request = new HashMap<>();
        Request.put("vehicleType",vehicleType);
        Request.put("vehicleCategory", vehicleCategory);
        Request.put("pickUpdate",pickUpdate);
        Request.put("timeTo",timeTo);
        Request.put("timeFrom",timeFrom);

        reff = FirebaseDatabase.getInstance().getReference("Request");

        reff.child("1").updateChildren(Request).addOnCompleteListener(task ->{
            if(task.isSuccessful()){
                Toast.makeText(MyRequest.this,"Successfully Update", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(MyRequest.this,"Failed to Update",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void clearControls(){
        a.setText("");
        b.setText("");
        c.setText("");
        d.setText("");
        e.setText("");
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