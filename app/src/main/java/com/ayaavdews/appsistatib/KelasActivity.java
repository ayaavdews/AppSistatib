package com.ayaavdews.appsistatib;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.ayaavdews.appsistatib.Adapter.AdapterPelajar;
import com.ayaavdews.appsistatib.Model.ModelPelajar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class KelasActivity extends AppCompatActivity {

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    private ListView listView;
    private TextView tvTitle;

    private AdapterPelajar adapterPelajar;
    private ArrayList<ModelPelajar> modelPelajar = new ArrayList<ModelPelajar>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas);

        String kelas = getIntent().getStringExtra("kelas");
        tvTitle  = (TextView) findViewById(R.id.title_kelas);
        listView = (ListView) findViewById(R.id.lv_siswa);

        modelPelajar.clear();
        adapterPelajar = new AdapterPelajar(this, modelPelajar);

        try {
            JSONObject object1 = new JSONObject(kelas);
            JSONArray array = object1.getJSONArray("data");

            ArrayList<ModelPelajar> modelPelajarArrayList = new ArrayList<>();
            for(int i=0; i<array.length(); i++){
                JSONObject object2 = array.getJSONObject(i);
                ModelPelajar modelPelajar = new ModelPelajar();

                modelPelajar.setNis(object2.getString("nis"));
                modelPelajar.setNama(object2.getString("nama"));
                modelPelajar.setKelas(object2.getString("kelas"));
                modelPelajar.setJenis_kelamin(object2.getString("jenis_kelamin"));

                modelPelajarArrayList.add(modelPelajar);
            }

            JSONObject object3 = array.getJSONObject(1);
            String kelasSiswa  = object3.getString("kelas");
            tvTitle.setText("( " + kelasSiswa + " )");

            adapterPelajar = new AdapterPelajar(KelasActivity.this, modelPelajarArrayList);
            listView.setAdapter(adapterPelajar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
