package com.example.carrentalapp_rent4u;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.carrentalapp_rent4u.databinding.ActivityPdfAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class Pdf_Add extends AppCompatActivity {

    private ActivityPdfAddBinding binding;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private ArrayList<String> categoryTitleArrayList,categoryIdArrayList;

    private Uri pdfUri;

    private static final int PDF_PICK_CODE = 1000;

    private static final String TAG = "ADD_PDF_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        loadPdfCategories();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
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
                validateData();
            }
        });
    }

    private String name = "",description = "",location = "",contact = "",fee = "";

    private void validateData() {

        Log.d(TAG,"validateData: validating data...");

        name = binding.Titl1.getText().toString().trim();
        description = binding.Titl2.getText().toString().trim();
        location = binding.Titl3.getText().toString().trim();
        contact = binding.Titl4.getText().toString().trim();
        fee = binding.Titl5.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Enter Name...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this,"Enter Description...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(selectedCategoryTitle)){
            Toast.makeText(this,"Pick a Category...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(location)){
            Toast.makeText(this,"Enter Location...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(contact)){
            Toast.makeText(this,"Enter Contact Number...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fee)){
            Toast.makeText(this,"Enter Rental Fee...",Toast.LENGTH_SHORT).show();
        }
        else if(pdfUri == null){
            Toast.makeText(this,"Pick Document",Toast.LENGTH_SHORT).show();
        }
        else{
            uploadPdfToStorage();
        }

    }

    private void uploadPdfToStorage() {
        Log.d(TAG,"uploadPdfToStorage: uploading to storage...");

        progressDialog.setMessage("Uploading Document...");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        String filePathAndName = "Books/" + timestamp;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG,"onSuccess: PDF uploaded to storage...");
                        Log.d(TAG,"onSuccess:getting pdf url");

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadedPdfUrl = "" + uriTask.getResult();

                        uploadPdfInfoToDb(uploadedPdfUrl , timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onFailure: PDF upload failed due to"+ e.getMessage());
                        Toast.makeText(Pdf_Add.this,"PDF upload failed due to"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPdfInfoToDb(String uploadedPdfUrl, long timestamp) {
        Log.d(TAG,"uploadPdfToStorage: uploading Pdf info to firebase db...");

        progressDialog.setMessage("Uploading pdf info...");

        String uid = firebaseAuth.getUid();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("id",""+timestamp);
        hashMap.put("name",""+name);
        hashMap.put("description",""+description);
        hashMap.put("CategoryId",""+selectedCategoryId);
        hashMap.put("location",""+location);
        hashMap.put("contact",""+contact);
        hashMap.put("rentalFee",""+fee);
        hashMap.put("url",""+uploadedPdfUrl);
        hashMap.put("timestamp",""+timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Vehicles");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Successfully uploaded...");
                        Toast.makeText(Pdf_Add.this,"Succcessfully uploaded...",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onFailure: Failed to uploaded to db due to" + e.getMessage());
                        Toast.makeText(Pdf_Add.this,"Failed to upload to db due to"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadPdfCategories() {
        Log.d(TAG,"loadPdfCategories:Loading pdf categories...");
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private String selectedCategoryId, selectedCategoryTitle;

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


    private void pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent");

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Document"),PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == PDF_PICK_CODE){
                Log.d(TAG,"onActivityResult : PDF Picked");

                pdfUri = data.getData();

                Log.d(TAG,"onActivityResult : URI:"+pdfUri);
            }
        }
        else{
            Log.d(TAG,"onActivityResult:cancelled picking document");
            Toast.makeText(this,"cancelled picking document",Toast.LENGTH_SHORT).show();
        }
    }
}