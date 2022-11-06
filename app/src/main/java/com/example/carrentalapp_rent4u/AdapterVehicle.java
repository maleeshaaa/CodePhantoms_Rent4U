package com.example.carrentalapp_rent4u;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrentalapp_rent4u.databinding.ActivityMyAddsBinding;
import com.example.carrentalapp_rent4u.databinding.ActivityPdfAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterVehicle extends RecyclerView.Adapter<AdapterVehicle.HolderVehicle> implements Filterable {

    
    private Context context;

    public static ArrayList<ModelVehicle> vehicleArrayList , filterList;

    private ActivityMyAddsBinding binding;

    private static final String TAG = "VEHICLE_ADAPTER_TAG";

    private FilterVehicle filterVehicle;

    private ProgressDialog progressDialog;

    public AdapterVehicle(Context context, ArrayList<ModelVehicle> vehicleArrayList){
        this.context = context;
        this.vehicleArrayList = vehicleArrayList;
        this.filterList = vehicleArrayList;

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderVehicle onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = ActivityMyAddsBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderVehicle(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderVehicle holder, int position) {

        ModelVehicle modelVehicle = vehicleArrayList.get(position);
        String name = modelVehicle.getName();
        String description = modelVehicle.getDescription();
        String location = modelVehicle.getLocation();
        String RentalFee = modelVehicle.getRentalFee();
        String contact = modelVehicle.getContact();

        holder.view1.setText(name);
        holder.view2.setText(description);
        holder.view3.setText(location);
        holder.view4.setText(contact);
        holder.view5.setText(RentalFee);

        loadCategory(modelVehicle,holder);

        holder.view4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionDialog(modelVehicle,holder);
            }
        });
    }

    private void moreOptionDialog(ModelVehicle modelVehicle, HolderVehicle holder) {

        String vehicleId = modelVehicle.getId();
        String vehicleUrl = modelVehicle.getUrl();
        String vehicleName = modelVehicle.getName();

        String[] options = {"Edit" , "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){

                            Intent intent = new Intent(context,VehicleEdit.class);
                            intent.putExtra("vehicleId",vehicleId);
                            context.startActivity(intent);
                        }else if(i == 1){
                            deleteVehicle(modelVehicle , holder);
                        }
                    }
                })
                .show();
    }

    private void deleteVehicle(ModelVehicle modelVehicle, HolderVehicle holder) {
        String vehicleId = modelVehicle.getId();
        String vehicleUrl = modelVehicle.getUrl();
        String vehicleName = modelVehicle.getName();

        Log.d(TAG,"deleteBook:Deleting...");
        progressDialog.setMessage("Deleting"+vehicleName+"...");
        progressDialog.show();

        Log.d(TAG,"deleteBook:Deleting from storage...");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(vehicleUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSuccess: Deleted from storage");
                        Log.d(TAG,"onSuccess: Now deleting info  from db");
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Vehicles");
                        reference.child(vehicleId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG,"onSuccess:Deleted from db too");
                                        progressDialog.dismiss();
                                        Toast.makeText(context,"Book Deleted Successfully...",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG,"onFailure: failed to delete from db due to"+e.getMessage());
                                        progressDialog.dismiss();
                                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure:Failed to delete from storage due to"+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadCategory(ModelVehicle modelVehicle, HolderVehicle holder) {

        String categoryId = modelVehicle.getCategoryId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String category = ""+snapshot.child("category").getValue();

                        holder.view6.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return vehicleArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filterVehicle == null){
            filterVehicle = new FilterVehicle(filterList, this);
        }
        return filterVehicle;
    }

    class HolderVehicle extends RecyclerView.ViewHolder{

        TextView view1 , view2 , view3 , view4 , view5 ,view6;

        public HolderVehicle(@NonNull View itemView) {
            super(itemView);

            view1 = binding.view1;
            view2 = binding.view2;
            view3 = binding.view3;
            view4 = binding.view4;
            view5 = binding.view5;
            view6 = binding.view6;
        }
    }
}
