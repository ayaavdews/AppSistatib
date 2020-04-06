package com.ayaavdews.appsistatib.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ayaavdews.appsistatib.Model.ModelRiwayatAdmin;
import com.ayaavdews.appsistatib.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class AdapterRiwayatAdmin extends BaseAdapter {

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    private Context context;
    private ArrayList<ModelRiwayatAdmin> modelRiwayatAdmin;

    public AdapterRiwayatAdmin(Context context, ArrayList<ModelRiwayatAdmin> modelRiwayatAdmin) {
        this.context = context;
        this.modelRiwayatAdmin = modelRiwayatAdmin;
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
        return modelRiwayatAdmin.size();
    }

    @Override
    public Object getItem(int position) {
        return modelRiwayatAdmin.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.raw_result_riwayat_admin, null, true);

            holder.result_jenis = (TextView) convertView.findViewById(R.id.result_jenis);
            holder.result_time  = (TextView) convertView.findViewById(R.id.result_time);
            holder.result_skor  = (TextView) convertView.findViewById(R.id.result_skor);
            holder.btnDelete    = (Button) convertView.findViewById(R.id.btn_delete_riwayat);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        String date = modelRiwayatAdmin.get(position).getDate();
        String time = modelRiwayatAdmin.get(position).getTime();

        final String nis = modelRiwayatAdmin.get(position).getNis_siswa();
        final String id  = modelRiwayatAdmin.get(position).getId();

        holder.result_jenis.setText(modelRiwayatAdmin.get(position).getJenis_pelanggaran());
        holder.result_time.setText(date + "|" + time);
        holder.result_skor.setText(modelRiwayatAdmin.get(position).getSkor());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(context)
                .setTitle("Title")
                .setMessage("Anda yakin ingin menghapus riwayat pelanggaran ini ?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("IYA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String url = context.getResources().getString(R.string.url_riwayat);
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("token", context.getResources().getString(R.string.API_KEY));
                        params.put("method", context.getResources().getString(R.string.delete));
                        params.put("nis", nis);
                        params.put("id", id);

                        client.get(url, params, new JsonHttpResponseHandler(){
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                Toast.makeText(context, context.getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                Toast.makeText(context, context.getResources().getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("TIDAK", null).show();

            }
        });

        return convertView;
    }

    private class ViewHolder{
        protected TextView result_jenis, result_skor, result_time;
        protected Button btnDelete;
    }
}
