package com.example.carrentalapp_rent4u;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterVehicle extends Filter {
    ArrayList<ModelVehicle> filterList;

    AdapterVehicle adapterVehicle;

    public FilterVehicle(ArrayList<ModelVehicle> filterList, AdapterVehicle adapterVehicle) {
        this.filterList = filterList;
        this.adapterVehicle = adapterVehicle;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();

        if(charSequence != null && charSequence.length() > 0){

            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelVehicle> filteredModels = new ArrayList<>();

            for(int i=0; i<filterList.size(); i++){

                if(filterList.get(i).getName().toUpperCase().contains(charSequence)){
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return  results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        AdapterVehicle.vehicleArrayList = (ArrayList<ModelVehicle>)filterResults.values;

        adapterVehicle.notifyDataSetChanged();
    }
}
