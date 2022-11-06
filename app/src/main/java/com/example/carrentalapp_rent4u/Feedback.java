package com.example.carrentalapp_rent4u;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Feedback extends AppCompatActivity {

    EditText name;
    EditText email;
    EditText subject;
    EditText message;
    Button btnFeedback;

    DatabaseReference dbRef;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        name = findViewById(R.id.editTextTextPersonName);
        email = findViewById(R.id.editTextTextEmailAddress);
        subject = findViewById(R.id.ediTextSubject);
        message = findViewById(R.id.editTextMassage);
        btnFeedback = findViewById(R.id.button);

        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("Feedbacks");

        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cusName = name.getText().toString();
                String cusEmail = email.getText().toString();
                String cusSubject = subject.getText().toString();
                String cusMessage = message.getText().toString();

                if (!cusName.isEmpty() && !cusEmail.isEmpty() && !cusSubject.isEmpty() && !cusMessage.isEmpty()){

                    Customer_Feedback feedback = new Customer_Feedback();
                    feedback.setName(cusName);
                    feedback.setEmail(cusEmail);
                    feedback.setSubject(cusSubject);
                    feedback.setMessage(cusMessage);

                    dbRef.child(cusName).setValue(feedback, (error, ref) -> {
                        if (error == null){
                            Toast.makeText(Feedback.this, "Successfully Send", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(Feedback.this, "Unsuccessful", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    Toast.makeText(Feedback.this, "Empty Fields", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}