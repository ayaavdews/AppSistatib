package com.ayaavdews.appsistatib.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ayaavdews.appsistatib.Model.ModelPeraturan;
import com.ayaavdews.appsistatib.R;
import java.util.ArrayList;

public class AdapterPeraturan extends BaseAdapter  implements Filterable{

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    private Context context;
    public ArrayList<ModelPeraturan> origin;
    ArrayList<ModelPeraturan> peraturanArrayList;

    public AdapterPeraturan(Context context, ArrayList<ModelPeraturan> peraturanArrayList) {
        super();
        this.context = context;
        this.peraturanArrayList = peraturanArrayList;
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<ModelPeraturan> results = new ArrayList<ModelPeraturan>();

                if (origin == null)
                    origin = peraturanArrayList;
                if (constraint != null) {
                    if (origin != null && origin.size() > 0) {
                        for (final ModelPeraturan modelPeraturan : origin) {
                            if (    modelPeraturan.getSkor().toLowerCase().contains(constraint.toString()) ||
                                    modelPeraturan.getJenis_pelanggaran().toLowerCase().contains(constraint.toString()) ||
                                    modelPeraturan.getKode().toLowerCase().contains(constraint.toString()))
                                results.add(modelPeraturan);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                peraturanArrayList = (ArrayList<ModelPeraturan>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return peraturanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return peraturanArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.raw_result_peraturan_siswa, null, true);

            holder.result_kode = (TextView) convertView.findViewById(R.id.tv_kode);
            holder.result_jenis= (TextView) convertView.findViewById(R.id.tv_jenis);
            holder.result_skor = (TextView) convertView.findViewById(R.id.tv_skor);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.result_kode.setText(peraturanArrayList.get(position).getKode());
        holder.result_jenis.setText(peraturanArrayList.get(position).getJenis_pelanggaran());
        holder.result_skor.setText(peraturanArrayList.get(position).getSkor());

        return convertView;
    }

    private class ViewHolder{
        protected TextView result_kode, result_jenis, result_skor;
    }
}
