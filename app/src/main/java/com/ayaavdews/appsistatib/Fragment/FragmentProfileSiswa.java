package com.ayaavdews.appsistatib.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ayaavdews.appsistatib.Adapter.AdapterRiwayatSiswa;
import com.ayaavdews.appsistatib.Model.ModelRiwayat;
import com.ayaavdews.appsistatib.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FragmentProfileSiswa extends Fragment {

    // Created by :
    // Aya Avdews
    // XII RPL A / 17006892
    // SMKN 2 Surakarta

    private TextView tvnama, tvnis, tvgender, tvkelas;
    private Button process;
    private String nis;

    // Untuk popup riwayat siswa
    private TextView result_nama, result_nis, result_kelas, result_id;
    private ImageView profile;
    private ListView listView;
    Dialog dialog;

    private ProgressDialog progressDialog;

    private AdapterRiwayatSiswa adapterRiwayatSiswa;
    private ArrayList<ModelRiwayat> modelRiwayat = new ArrayList<ModelRiwayat>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_profile_siswa, container, false);

        tvnama   = (TextView) root.findViewById(R.id.tv_nama);
        tvnis    = (TextView) root.findViewById(R.id.tv_nis);
        tvgender = (TextView) root.findViewById(R.id.tv_gender);
        tvkelas  = (TextView) root.findViewById(R.id.tv_kelas);
        process  = (Button) root.findViewById(R.id.btn_profile);
        profile  = (ImageView) root.findViewById(R.id.iv_user);

        dialog     = new Dialog(getActivity());

        Intent intent = getActivity().getIntent();
        String dataUser = intent.getStringExtra("stringSiswa");

        try {
            JSONObject user = new JSONObject(dataUser);
            nis = user.getString("nis");

            tvnama.setText(user.getString("nama"));
            tvnis. setText(user.getString("nis"));
            tvgender.setText(user.getString("jenis_kelamin"));
            tvkelas.setText(user.getString("kelas"));

            if(user.getString("jenis_kelamin").equals("laki-laki")){
                profile.setImageResource(R.drawable.student_male);
            } else {
                profile.setImageResource(R.drawable.student_female);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Deklarasi untuk melakukan pengecekan jaringan internet (mobile data/wifi)
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo.State internet = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
                NetworkInfo.State wifi     = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

                if(internet == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {
                    resultRiwayat(nis);
                } else {
                   Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    private void resultRiwayat(String nis) {
        dialog.setContentView(R.layout.popup_cek_riwayat);

        listView = (ListView) dialog.findViewById(R.id.lvRiwayat);
        profile  = (ImageView) dialog.findViewById(R.id.iv_profile);

        modelRiwayat.clear();
        adapterRiwayatSiswa = new AdapterRiwayatSiswa(getActivity(), modelRiwayat);

        progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialog);
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        String url = getResources().getString(R.string.url_riwayat);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token", getResources().getString(R.string.API_KEY));
        params.put("nis", nis);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                try {
                    if(response.getString("message").equals("Siswa belum pernah melakukan pelanggaran")){
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.siswa_tertib), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.v("check point", "1");
                        result_nama = (TextView) dialog.findViewById(R.id.result_nama);
                        result_nis  = (TextView) dialog.findViewById(R.id.result_nis);
                        result_kelas= (TextView) dialog.findViewById(R.id.result_kelas);
                        result_id   = (TextView) dialog.findViewById(R.id.result_id);

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
                            profile.setImageResource(R.drawable.student_male);
                        } else {
                            profile.setImageResource(R.drawable.student_female);
                        }

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

                        adapterRiwayatSiswa = new AdapterRiwayatSiswa(getActivity(), modelRiwayatArrayList);
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
                    Toast.makeText(getActivity().getApplicationContext(), ""+errorResponse.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}