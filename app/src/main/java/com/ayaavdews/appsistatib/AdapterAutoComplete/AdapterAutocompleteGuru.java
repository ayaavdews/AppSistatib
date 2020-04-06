package com.ayaavdews.appsistatib.AdapterAutoComplete;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ayaavdews.appsistatib.Model.ModelGuru;
import com.ayaavdews.appsistatib.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterAutocompleteGuru extends ArrayAdapter<ModelGuru> {

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    private List<ModelGuru> guruList;
    private List<ModelGuru> tempList;
    private List<ModelGuru> suggestionList;

    public AdapterAutocompleteGuru(@NonNull Context context, int resource, @NonNull List<ModelGuru> objects) {
        super(context, resource, objects);

        guruList       = objects;
        tempList       = new ArrayList<>(guruList);
        suggestionList = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.raw_result_autocomplete_guru, parent, false);

        TextView textView = convertView.findViewById(R.id.raw_guru);

        ModelGuru modelGuru = guruList.get(position);

        textView.setText("["+modelGuru.getRole()+"]" + " " + modelGuru.getNama());

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return userFilter;
    }

    Filter userFilter = new Filter() {

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            ModelGuru modelGuru = (ModelGuru) resultValue;


            return "["+modelGuru.getRole()+"]" + " " + modelGuru.getNama();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if(constraint != null && constraint.length() > 0){
                suggestionList.clear();
                constraint = constraint.toString().trim().toLowerCase();

                for(ModelGuru modelGuru : tempList){
                    if(     modelGuru.getRole().toLowerCase().contains(constraint) ||
                            modelGuru.getNama().toLowerCase().contains(constraint)
                    ){
                        suggestionList.add(modelGuru);
                    }
                }

                filterResults.count = suggestionList.size();
                filterResults.values = suggestionList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            ArrayList<ModelGuru> uList = (ArrayList<ModelGuru>) results.values;

            if(results != null && results.count > 0){
                clear();
                for(ModelGuru s : uList){
                    add(s);
                    notifyDataSetChanged();
                }
            }
        }
    };

}
