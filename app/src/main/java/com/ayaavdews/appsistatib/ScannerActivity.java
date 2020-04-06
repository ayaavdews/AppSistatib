package com.ayaavdews.appsistatib;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayaavdews.appsistatib.AdapterAutoComplete.AdapterAutocompletePeraturan;
import com.ayaavdews.appsistatib.Model.ModelPeraturan;
import com.google.zxing.Result;
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
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    private String namaGuru, nipGuru;
    private ZXingScannerView mScannerView;

    // Untuk popup tambah pelanggaran
    // ============================================================================
    Dialog dialogViolation;
    ListView listViewViolation;

    private ProgressDialog progressDialog;

    private Button btnAdd;
    private String skor, jenis;
    private TextView tv_nis, tv_skor;

    private AutoCompleteTextView Peraturan;
    private ArrayList<ModelPeraturan> peraturanList;
    // ============================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_scanner);

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        namaGuru = getIntent().getStringExtra("guru");
        nipGuru  = getIntent().getStringExtra("nip");
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String nis  = result.getText();
        showPopupPelanggaran(nis, namaGuru, nipGuru);
        mScannerView.resumeCameraPreview(this);
    }

    private void showPopupPelanggaran(final String nis, final String namaGuru, final String nipGuru) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State internet = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi     = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        if(internet == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {
            Log.v("Internet","connected");

            String url = getResources().getString(R.string.url_peraturan);
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("token", getResources().getString(R.string.API_KEY));

            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    runAutoComplete(response, namaGuru, nipGuru, nis);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, getResources().getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void runAutoComplete(JSONObject response, final String namaGuru, final String nipGuru, final String nis) {
        dialogViolation = new Dialog(this);
        dialogViolation.setContentView(R.layout.popup_scann_add_violation);

        btnAdd   = (Button) dialogViolation.findViewById(R.id.btnAdd);

        tv_nis  = (TextView) dialogViolation.findViewById(R.id.tv_nis);
        tv_skor = (TextView) dialogViolation.findViewById(R.id.tv_skor);

        tv_nis.setText("Siswa dengan nis " + nis + " melakukan pelanggaran :");

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


        AdapterAutocompletePeraturan adapterAutocompletePeraturan = new AdapterAutocompletePeraturan(this, R.layout.raw_result_add_violation, peraturanList);

        Peraturan.setAdapter(adapterAutocompletePeraturan);
        Peraturan.setThreshold(2);

        Peraturan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ModelPeraturan modelPeraturan = (ModelPeraturan) parent.getItemAtPosition(position);
                tv_skor.setText("skor = " + modelPeraturan.getSkor());

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
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_data), Toast.LENGTH_SHORT).show();
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        // Get Current time
        String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());

        String url = getResources().getString(R.string.url_riwayat);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token", getResources().getString(R.string.API_KEY));
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
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.insert_success), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ScannerActivity.this, GuruActivity.class));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.insert_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
