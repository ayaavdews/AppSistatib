package com.ayaavdews.appsistatib.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayaavdews.appsistatib.Model.ModelRiwayat;
import com.ayaavdews.appsistatib.R;

import java.util.ArrayList;

public class AdapterRiwayatSiswa extends BaseAdapter {

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    private Context context;
    private ArrayList<ModelRiwayat> modelRiwayat;

    public AdapterRiwayatSiswa(Context context, ArrayList<ModelRiwayat> modelRiwayat) {
        this.context = context;
        this.modelRiwayat = modelRiwayat;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return modelRiwayat.size();
    }

    @Override
    public Object getItem(int position) {
        return modelRiwayat.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.raw_result_riwayat, null, true);

            holder.result_jenis = (TextView) convertView.findViewById(R.id.result_jenis);
            holder.result_guru  = (TextView) convertView.findViewById(R.id.result_guru);
            holder.result_skor  = (TextView) convertView.findViewById(R.id.result_skor);
            holder.result_time  = (TextView) convertView.findViewById(R.id.result_time);
            holder.result_id    = (TextView) convertView.findViewById(R.id.result_id);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.result_jenis.setText(modelRiwayat.get(position).getJenis_pelanggaran());
        holder.result_guru.setText(modelRiwayat.get(position).getNama_guru());
        holder.result_skor.setText("skor : " + modelRiwayat.get(position).getSkor());
        holder.result_time.setText(modelRiwayat.get(position).getWaktu());
        holder.result_id.setText(modelRiwayat.get(position).getId_siswa());

        return convertView;
    }

    private class ViewHolder{
        protected ImageView profile;
        protected TextView result_jenis, result_guru, result_skor, result_time, result_id;
    }
}
