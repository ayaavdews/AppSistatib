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

import com.ayaavdews.appsistatib.Model.ModelSiswa;
import com.ayaavdews.appsistatib.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterAutocompleteSiswa extends ArrayAdapter<ModelSiswa> {

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    private List<ModelSiswa> siswaList;
    private List<ModelSiswa> tempList;
    private List<ModelSiswa> suggestionList;

    public AdapterAutocompleteSiswa(@NonNull Context context, int resource, @NonNull List<ModelSiswa> objects) {
        super(context, resource, objects);

        siswaList      = objects;
        tempList       = new ArrayList<>(siswaList);
        suggestionList = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.raw_result_autocomplete_siswa, parent, false);

        TextView textView = convertView.findViewById(R.id.raw_siswa);

        ModelSiswa modelSiswa = siswaList.get(position);

        textView.setText("["+modelSiswa.getKelas() + "/" + modelSiswa.getNis()+"]" + " " + modelSiswa.getNama());

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
            ModelSiswa modelSiswa = (ModelSiswa) resultValue;


            return "["+modelSiswa.getKelas() + "/" + modelSiswa.getNis()+"]" + " " + modelSiswa.getNama();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if(constraint != null && constraint.length() > 0){
                suggestionList.clear();
                constraint = constraint.toString().trim().toLowerCase();

                for(ModelSiswa modelSiswa : tempList){
                    if(     modelSiswa.getNis().toLowerCase().contains(constraint) ||
                            modelSiswa.getNama().toLowerCase().contains(constraint)
                    ){
                        suggestionList.add(modelSiswa);
                    }
                }

                filterResults.count = suggestionList.size();
                filterResults.values = suggestionList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            ArrayList<ModelSiswa> uList = (ArrayList<ModelSiswa>) results.values;

            if(results != null && results.count > 0){
                clear();
                for(ModelSiswa s : uList){
                    add(s);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
