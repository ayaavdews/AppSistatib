package com.ayaavdews.appsistatib.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayaavdews.appsistatib.AdapterAutoComplete.AdapterAutocompletePeraturan;
import com.ayaavdews.appsistatib.Model.ModelPelajar;
import com.ayaavdews.appsistatib.Model.ModelPeraturan;
import com.ayaavdews.appsistatib.Model.ModelRiwayat;
import com.ayaavdews.appsistatib.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class AdapterSiswaGuru extends BaseAdapter {

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
    Dialog dialogRiwayat;
    ListView listViewRiwayat;

    private ImageView profile;
    private ProgressDialog progressDialog;
    private TextView result_nama, result_nis, result_kelas, result_id;

    private AdapterRiwayatSiswa adapterRiwayatSiswa;
    private ArrayList<ModelRiwayat> modelRiwayat = new ArrayList<ModelRiwayat>();
    // ============================================================================

    // Untuk popup tambah pelanggaran
    // ============================================================================
    private String namaGuru, nipGuru;
    Dialog dialogViolation;
    ListView listViewViolation;

    private Button btnAdd;
    private String skor, jenis;
    private TextView tv_nama, tv_kelas, tv_skor;

    private AutoCompleteTextView Peraturan;
    private ArrayList<ModelPeraturan> peraturanList;
    // ============================================================================

    public AdapterSiswaGuru(Context context, ArrayList<ModelPelajar> modelPelajar) {
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
            convertView = inflater.inflate(R.layout.raw_result_siswa_guru, null, true);

            holder.profile = (ImageView) convertView.findViewById(R.id.iv_user);

            holder.result_nis   = (TextView) convertView.findViewById(R.id.tv_nis);
            holder.result_nama  = (TextView) convertView.findViewById(R.id.tv_nama);
            holder.result_kelas = (TextView) convertView.findViewById(R.id.tv_kelas);

            holder.btnRiwayat     = (Button) convertView.findViewById(R.id.btn_riwayat);
            holder.btnPelanggaran = (Button) convertView.findViewById(R.id.btn_pelanggaran);

            convertView.setTag(holder);
        } else {
            holder = (AdapterSiswaGuru.ViewHolder) convertView.getTag();
        }

        holder.result_nis.setText(modelPelajar.get(position).getNis());
        holder.result_nama.setText(modelPelajar.get(position).getNama());
        holder.result_kelas.setText(modelPelajar.get(position).getKelas());

        if(modelPelajar.get(position).getJenis_kelamin().equals("laki-laki")){
            holder.profile.setImageResource(R.drawable.student_male);
        } else {
            holder.profile.setImageResource(R.drawable.student_female);
        }

        holder.btnRiwayat.setTag(modelPelajar.get(position).getNis());

        // Menjalankan fungsi ketika tombol riwayat ditekan
        holder.btnRiwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please wait");
                progressDialog.show();
                String nis = (String) view.getTag();
                showRiwayatSiswa(nis);
            }
        });

        // Menjalankan fungsi ketika tombol pelanggaran ditekan
        // Input data pelanggaran baru :
        holder.btnPelanggaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ((Activity)context).getIntent();

                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                namaGuru = intent.getStringExtra("namaGuru");
                nipGuru  = intent.getStringExtra("nipGuru");

                addNewViolation(namaGuru, nipGuru, modelPelajar.get(position).getNis(), modelPelajar.get(position).getNama(), modelPelajar.get(position).getKelas());
            }
        });


        return convertView;
    }

    private void addNewViolation(final String namaGuru, final String nipGuru, final String nis, final String nama, final String kelas) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State internet = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi     = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        if(internet == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {

            String url = context.getResources().getString(R.string.url_peraturan);
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("token", context.getResources().getString(R.string.API_KEY));

            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    runAutoComplete(response, namaGuru, nipGuru, nis, nama, kelas);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(context, context.getResources().getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void runAutoComplete(JSONObject response, final String namaGuru, final String nipGuru, final String nis, String nama, String kelas) {
        dialogViolation = new Dialog(context);
        dialogViolation.setContentView(R.layout.popup_add_violation);

        btnAdd   = (Button) dialogViolation.findViewById(R.id.btnAdd);

        tv_nama  = (TextView) dialogViolation.findViewById(R.id.tv_nama);
        tv_kelas = (TextView) dialogViolation.findViewById(R.id.tv_kelas);
        tv_skor  = (TextView) dialogViolation.findViewById(R.id.tv_skor);

        tv_nama.setText(nama);
        tv_kelas.setText("[ " + kelas + "/" + nis + " ]");

        // set Autocomplete
        Peraturan     = dialogViolation.findViewById(R.id.autocompletePeraturan);
        peraturanList = new ArrayList<>();

        ArrayList<ModelPeraturan> list = new ArrayList<>();

        try {
            JSONArray array = response.getJSONArray("data");
            for(int i =0; i<array.length(); i++){
                JSONObject object = array.getJSONObject(i);

                String kode              = object.getString("kode");
                String jenis_pelanggaran = object.getString("jenis_pelanggaran");
                String skor              = object.getString("skor");

                ModelPeraturan modelPeraturan = new ModelPeraturan(kode, jenis_pelanggaran, skor);
                list.add(modelPeraturan);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        peraturanList = list;


        AdapterAutocompletePeraturan adapterAutocompletePeraturan = new AdapterAutocompletePeraturan(context, R.layout.raw_result_add_violation, peraturanList);

        Peraturan.setAdapter(adapterAutocompletePeraturan);
        Peraturan.setThreshold(2);

        Peraturan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ModelPeraturan modelPeraturan = (ModelPeraturan) parent.getItemAtPosition(position);
                tv_skor.setText("SKOR = " + modelPeraturan.getSkor());

                skor = modelPeraturan.getSkor();
                jenis= modelPeraturan.getJenis_pelanggaran();
            }
        });
        // akhir set Autocomplete

        progressDialog.dismiss();
        dialogViolation.show();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(skor.equals("") || jenis.equals("")){
                    Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.empty_data), Toast.LENGTH_SHORT).show();
                } else {
                    // close keyboard
                    final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    postViolation(namaGuru, nipGuru, nis, skor, jenis);
                }
            }
        });
    }

    private void postViolation(String namaGuru, String nipGuru, String nis, String skor, String jenis) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        // Get Current time
        String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());

        String url = context.getResources().getString(R.string.url_riwayat);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token", context.getResources().getString(R.string.API_KEY));
        params.put("nis_siswa", nis);
        params.put("nama_guru", namaGuru);
        params.put("nip_guru", nipGuru);
        params.put("jenis_pelanggaran", jenis);
        params.put("skor", skor);
        params.put("date", date);
        params.put("time", time);

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                dialogViolation.dismiss();
                Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.insert_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressDialog.dismiss();
                Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.insert_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method ini digunakan sebagai method peralihan (untuk check koneksi internet)
    private void showRiwayatSiswa(String nis) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State internet = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi     = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        if(internet == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {

            dialogRiwayat     = new Dialog(context);
            dialogRiwayat.setContentView(R.layout.popup_cek_riwayat);
            listViewRiwayat = (ListView) dialogRiwayat.findViewById(R.id.lvRiwayat);

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
                            profile = (ImageView) dialogRiwayat.findViewById(R.id.iv_profile);

                            result_nama = (TextView) dialogRiwayat.findViewById(R.id.result_nama);
                            result_nis  = (TextView) dialogRiwayat.findViewById(R.id.result_nis);
                            result_kelas= (TextView) dialogRiwayat.findViewById(R.id.result_kelas);
                            result_id   = (TextView) dialogRiwayat.findViewById(R.id.result_id);

                            // Get Data dari json object
                            // Apabila siswa pernah melakukan pelanggaran
                            JSONArray array1 = response.getJSONArray("data");
                            JSONArray array2 = response.getJSONArray("riwayat");

                            Log.v("check point", "2");
                            JSONObject object1 = array1.getJSONObject(0);
                            result_kelas.setText(object1.getString("kelas"));
                            result_nama.setText(object1.getString("nama"));
                            result_nis.setText(object1.getString("nis"));
                            result_id.setText(object1.getString("id"));

                            if(object1.getString("jenis_kelamin").equals("laki-laki")) {
                                profile.setImageResource(R.drawable.student_male);
                            } else {
                                profile.setImageResource(R.drawable.student_female);
                            }

                            progressDialog.dismiss();
                            dialogRiwayat.show();

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
                            listViewRiwayat.setAdapter(adapterRiwayatSiswa);
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
        protected Button btnRiwayat, btnPelanggaran;
        protected ImageView profile;
    }
}
