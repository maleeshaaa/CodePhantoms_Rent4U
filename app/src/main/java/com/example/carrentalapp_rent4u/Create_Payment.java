package com.example.carrentalapp_rent4u;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Create_Payment extends AppCompatActivity {

    EditText cardNo;
    EditText cardHolderName;
    EditText exDate;
    EditText cvc;
    Button payNow;

    DatabaseReference dbRef;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_payment);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cardNo = findViewById(R.id.cardNoLabel);
        cardHolderName = findViewById(R.id.cardNameLabel);
        exDate = findViewById(R.id.exDatelbl);
        cvc = findViewById(R.id.cvcLabel);
        payNow = findViewById(R.id.buttonPay);

        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("Payments");

        //Insert Data
        payNow.setOnClickListener(v -> {
            String cardNumber = cardNo.getText().toString();
            String cardHName = cardHolderName.getText().toString();
            String expDate = exDate.getText().toString();
            String cvcCode = cvc.getText().toString();

            if (!cardNumber.isEmpty() && !cardHName.isEmpty() && !expDate.isEmpty() && !cvcCode.isEmpty()) {

                String paymentId = dbRef.push().getKey();
                Payment payment = new Payment();
                payment.setCardNumber(cardNumber);
                payment.setCardHName(cardHName);
                payment.setExpDate(expDate);
                payment.setCvcCode(cvcCode);

                dbRef.child(paymentId).setValue(payment, (error, ref) -> {
                    if (error == null){
                        Toast.makeText(Create_Payment.this, "Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Create_Payment.this, Show_Payment.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(Create_Payment.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                Toast.makeText(Create_Payment.this, "Empty Fields", Toast.LENGTH_SHORT).show();
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