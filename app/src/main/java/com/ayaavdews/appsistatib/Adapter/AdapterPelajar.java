package com.ayaavdews.appsistatib.Adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayaavdews.appsistatib.Model.ModelPelajar;
import com.ayaavdews.appsistatib.Model.ModelRiwayat;
import com.ayaavdews.appsistatib.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class AdapterPelajar extends BaseAdapter {

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    private Context context;
    ArrayList<ModelPelajar> modelPelajar;

    // Untuk popup detail siswa :
    // ============================================================================
    Dialog dialog;
    ListView listView;
    private ImageView iv_profile;
    private ProgressDialog progressDialog;
    private TextView result_nama, result_nis, result_kelas, result_id;

    private AdapterRiwayatSiswa adapterRiwayatSiswa;
    private ArrayList<ModelRiwayat> modelRiwayat = new ArrayList<ModelRiwayat>();
    // ============================================================================

    public AdapterPelajar(Context context, ArrayList<ModelPelajar> modelPelajar) {
        this.context = context;
        this.modelPelajar = modelPelajar;
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
        return modelPelajar.size();
    }

    @Override
    public Object getItem(int position) {
        return modelPelajar.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.raw_result_pelajar, null, true);

            holder.result_nis   = (TextView) convertView.findViewById(R.id.tv_nis);
            holder.result_nama  = (TextView) convertView.findViewById(R.id.tv_nama);
            holder.result_kelas = (TextView) convertView.findViewById(R.id.tv_kelas);
            holder.detail       = (LinearLayout) convertView.findViewById(R.id.ll_detail);

            convertView.setTag(holder);
        } else {
            holder = (AdapterPelajar.ViewHolder) convertView.getTag();
        }

        holder.result_nis.setText(modelPelajar.get(position).getNis());
        holder.result_nama.setText(modelPelajar.get(position).getNama());
        holder.result_kelas.setText(modelPelajar.get(position).getKelas());

        holder.detail.setTag(modelPelajar.get(position).getNis());
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(context, R.style.MyAlertDialog);
                progressDialog.setMessage("Please wait");
                progressDialog.show();
                String nis = (String) view.getTag();
                showRiwayatSiswa(nis);
            }
        });


        return convertView;
    }

    // Method ini digunakan sebagai method peralihan (untuk check koneksi internet)
    private void showRiwayatSiswa(String nis) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State internet = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            NetworkInfo.State wifi     = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

            if(internet == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {
                dialog     = new Dialog(context);
                dialog.setContentView(R.layout.popup_cek_riwayat);
                listView = (ListView) dialog.findViewById(R.id.lvRiwayat);

                String url = context.getResources().getString(R.string.url_riwayat);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", context.getResources().getString(R.string.API_KEY));
                params.put("nis", nis);

                modelRiwayat.clear();
                adapterRiwayatSiswa = new AdapterRiwayatSiswa(context, modelRiwayat);

                client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        if(response.getString("message").equals("Siswa belum pernah melakukan pelanggaran")){
                            progressDialog.dismiss();
                            Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.siswa_tertib), Toast.LENGTH_SHORT).show();
                        } else {
                            result_nama = (TextView) dialog.findViewById(R.id.result_nama);
                            result_nis  = (TextView) dialog.findViewById(R.id.result_nis);
                            result_kelas= (TextView) dialog.findViewById(R.id.result_kelas);
                            result_id   = (TextView) dialog.findViewById(R.id.result_id);

                            iv_profile =  (ImageView) dialog.findViewById(R.id.iv_profile);

                            // Get Data dari json object
                            // Apabila siswa pernah melakukan pelanggaran
                            JSONArray array1 = response.getJSONArray("data");
                            JSONArray array2 = response.getJSONArray("riwayat");

                            JSONObject object1 = array1.getJSONObject(0);
                            result_kelas.setText(object1.getString("kelas"));
                            result_nama.setText(object1.getString("nama"));
                            result_nis.setText(object1.getString("nis"));
                            result_id.setText(object1.getString("id"));

                            if(object1.getString("jenis_kelamin").equals("laki-laki")){
                                iv_profile.setImageResource(R.drawable.student_male);
                            } else {
                                iv_profile.setImageResource(R.drawable.student_female);
                            }

                            progressDialog.dismiss();
                            dialog.show();

                            ArrayList<ModelRiwayat> modelRiwayatArrayList = new ArrayList<>();
                            for (int i = 0; i < array2.length(); i++){
                                JSONObject object2 = array2.getJSONObject(i);
                                ModelRiwayat modelRiwayat = new ModelRiwayat();
                                modelRiwayat.setJenis_pelanggaran(object2.getString("jenis_pelanggaran"));
                                modelRiwayat.setNama_guru(object2.getString("nama_guru"));
                                modelRiwayat.setSkor(object2.getString("skor"));
                                modelRiwayat.setId_siswa(object2.getString("id"));

                                String date = object2.getString("date");
                                String time = object2.getString("time");
                                modelRiwayat.setWaktu(date + " | " + time);

                                modelRiwayatArrayList.add(modelRiwayat);
                            }

                            adapterRiwayatSiswa = new AdapterRiwayatSiswa(context, modelRiwayatArrayList);
                            listView.setAdapter(adapterRiwayatSiswa);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    progressDialog.dismiss();
                    try {
                        // Menampilkan pesan error ketika gagal get data dari api
                        Toast.makeText(context.getApplicationContext(), ""+errorResponse.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            } else {
                progressDialog.dismiss();
                Toast.makeText(context, context.getResources().getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
            }
    }

    private class ViewHolder{
        protected TextView result_nis, result_nama, result_kelas;
        protected LinearLayout detail;
    }
}
