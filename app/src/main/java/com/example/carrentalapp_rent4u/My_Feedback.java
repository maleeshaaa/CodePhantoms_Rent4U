package com.example.carrentalapp_rent4u;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public class My_Feedback extends AppCompatActivity {

    EditText feedback1;
    EditText email;
    EditText message;
    Button btnEdit;
    Button btnDelete;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_feedback);

        feedback1 = findViewById(R.id.textView3);
        email = findViewById(R.id.textView2);
        message = findViewById(R.id.textView5);
        btnEdit = findViewById(R.id.button2);
        btnDelete = findViewById(R.id.button3);

        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("My Feedback");

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cusEmail = email.getText().toString();
                String cusMessage = message.getText().toString();

                if (!cusEmail.isEmpty() && !cusMessage.isEmpty()){
                    Customer_Feedback feedback = new Customer_Feedback();
                    feedback.setEmail(cusEmail);
                    feedback.setMessage(cusMessage);

                    dbRef.child(cusName).setValue(feedback, (error,ref) ->{
                        if (error == null){
                            Toast.makeText(My_Feedback.this, "Successfully Send", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(My_Feedback.this, "Unsuccessfully Send", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    Toast.makeText(My_Feedback.this,"Empty Fields",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}