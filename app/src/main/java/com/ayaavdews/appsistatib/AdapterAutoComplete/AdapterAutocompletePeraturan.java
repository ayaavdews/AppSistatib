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

import com.ayaavdews.appsistatib.Model.ModelPeraturan;
import com.ayaavdews.appsistatib.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterAutocompletePeraturan extends ArrayAdapter<ModelPeraturan> {

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    private List<ModelPeraturan> peraturanList;
    private List<ModelPeraturan> tempList;
    private List<ModelPeraturan> suggestionList;

    public AdapterAutocompletePeraturan(@NonNull Context context, int resource, @NonNull List<ModelPeraturan> objects) {
        super(context, resource, objects);

        peraturanList  = objects;
        tempList       = new ArrayList<>(peraturanList);
        suggestionList = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.raw_result_add_violation, parent, false);

        TextView textView = convertView.findViewById(R.id.raw_peraturan);

        ModelPeraturan modelPeraturan = peraturanList.get(position);

        textView.setText("["+modelPeraturan.getKode()+"]" + " " + modelPeraturan.getJenis_pelanggaran());

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
            ModelPeraturan modelPeraturan = (ModelPeraturan) resultValue;


            return "["+modelPeraturan.getKode()+"]" + " " + modelPeraturan.getJenis_pelanggaran();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if(constraint != null && constraint.length() > 0){
                suggestionList.clear();
                constraint = constraint.toString().trim().toLowerCase();

                for(ModelPeraturan modelPeraturan : tempList){
                    if(     modelPeraturan.getKode().toLowerCase().contains(constraint) ||
                            modelPeraturan.getJenis_pelanggaran().toLowerCase().contains(constraint)
                    ){
                        suggestionList.add(modelPeraturan);
                    }
                }

                filterResults.count = suggestionList.size();
                filterResults.values = suggestionList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            ArrayList<ModelPeraturan> uList = (ArrayList<ModelPeraturan>) results.values;

            if(results != null && results.count > 0){
                clear();
                for(ModelPeraturan s : uList){
                    add(s);
                    notifyDataSetChanged();
                }
            }
        }
    };

}
