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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Show_Payment extends AppCompatActivity {

    EditText cardNo;
    EditText cardHolderName;
    EditText exDate;
    EditText cvc;
    Button btnUpdate, btnCancel, btnContinue;

    DatabaseReference dbRef;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_payment);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cardNo = findViewById(R.id.cardNoLabel);
        cardHolderName = findViewById(R.id.cardNameLabel);
        exDate = findViewById(R.id.exDatelbl);
        cvc = findViewById(R.id.cvcLabel);
        btnUpdate = findViewById(R.id.updateButton);
        btnCancel = findViewById(R.id.buttonCancel);
        btnContinue = findViewById(R.id.continueButton);

        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("Payments");

        //Retrieve Data
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                    Payment payment = dataSnapshot.getValue(Payment.class);
                    String cardNumber = payment.getCardNumber();
                    String cardHName = payment.getCardHName();
                    String expDate = payment.getExpDate();
                    String cvcCode = payment.getCvcCode();

                    cardNo.setText(cardNumber);
                    cardHolderName.setText(cardHName);
                    exDate.setText(expDate);
                    cvc.setText(cvcCode);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(Show_Payment.this, "Failed", Toast.LENGTH_LONG).show();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Show_Payment.this, Booking_Details.class);
                startActivity(intent);
            }
        });

        //Delete Data
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Payments")
                        .child("paymentId").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Show_Payment.this, "Booking Cancelled", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Show_Payment.this, Reservation.class);
                                startActivity(intent);
                                clearControls();
                            }
                        });
            }
        });

        //Update Data
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cardNumber = cardNo.getText().toString();
                String cardHName = cardHolderName.getText().toString();
                String expDate = exDate.getText().toString();
                String cvcCode = cvc.getText().toString();

                updateData(cardNumber, cardHName, expDate, cvcCode);
            }
        });
    }

    private void updateData(String cardNumber, String cardHName, String expDate, String cvcCode) {

        HashMap<String, Object> Payment = new HashMap<>();
        Payment.put("cardNumber", cardNumber);
        Payment.put("cardHName", cardHName);
        Payment.put("expDate", expDate);
        Payment.put("cvcCode", cvcCode);

        dbRef = FirebaseDatabase.getInstance().getReference("Payments");

        dbRef.child("paymentId").updateChildren(Payment).addOnCompleteListener(task -> {

            if (task.isSuccessful()){
                Toast.makeText(Show_Payment.this, "Successfully Updated", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(Show_Payment.this, "Failed to Update", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void clearControls(){
        cardNo.setText("");
        cardHolderName.setText("");
        exDate.setText("");
        cvc.setText("");
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