package com.ayaavdews.appsistatib.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ayaavdews.appsistatib.Adapter.AdapterRiwayatAdmin;
import com.ayaavdews.appsistatib.AdapterAutoComplete.AdapterAutocompleteGuru;
import com.ayaavdews.appsistatib.AdapterAutoComplete.AdapterAutocompletePeraturan;
import com.ayaavdews.appsistatib.AdapterAutoComplete.AdapterAutocompleteSiswa;
import com.ayaavdews.appsistatib.Model.ModelGuru;
import com.ayaavdews.appsistatib.Model.ModelPeraturan;
import com.ayaavdews.appsistatib.Model.ModelRiwayatAdmin;
import com.ayaavdews.appsistatib.Model.ModelSiswa;
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

public class FragmentDashboard extends Fragment {

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    // Popup Panel di Fragment Dashboard
    private Dialog dialogHapusRiwayat;
    private Dialog dialogTambahGuru, dialogUbahGuru, dialogHapusGuru;
    private Dialog dialogTambahSiswa, dialogUbahSiswa, dialogHapusSiswa;
    private Dialog dialogTambahPeraturan, dialogUbahPeraturan, dialogHapusPeraturan;

    // Button Panel di Fragment Dashboard
    private Button btnTambahGuru, btnUbahGuru, btnHapusGuru;
    private Button btnTambahSiswa, btnUbahSiswa, btnHapusSiswa;

    // Untuk RefreshLayout
    private SwipeRefreshLayout refreshLayout;
    private TextView tvLastUpdate, tvPelanggar;

    // ib = ImageButton
    private ImageButton ibPeraturan;

    // AutoCompleteTextView
    // ======================================================
    // Autocomplete Siswa
    private AutoCompleteTextView Siswa;
    private ArrayList<ModelSiswa> siswaList;

    // Autocomplete Guru
    private AutoCompleteTextView Guru;
    private ArrayList<ModelGuru> guruList;

    // Autocomplete Peraturan
    private AutoCompleteTextView Peraturan;
    private ArrayList<ModelPeraturan> peraturanList;
    // ======================================================

    // CardView Riwayat
    // =================================
    private ListView listView;
    private CardView cardViewRiwayat;
    private ProgressDialog progressDialog;

    private AdapterRiwayatAdmin adapterRiwayatAdmin;
    private ArrayList<ModelRiwayatAdmin> modelRiwayatAdmin = new ArrayList<ModelRiwayatAdmin>();
    // =================================

    // For Chart Siswa
    private ProgressBar chartSiswaLakiLaki, chartSiswaPerempuan;

    int siswalakilaki  = 0;
    int siswaperempuan = 0;

    // For Chart Guru
    private ProgressBar chartGuruAdmin, chartGuruNonAdmin;
    int guruadmin    = 0;
    int gurunonadmin = 0;

    // Total Siswa & Guru
    private TextView tvTotalSiswa, tvTotalGuru;
    int totalSiswa, totalGuru;

    // Keterangan siswa individual :
    private TextView siswaLaki, siswaPerempuan, guruAdmin, guruNonAdmin;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Menyambungkan Button dengan Layout pada halaman fragment
        // Button pada panel Siswa
        btnTambahSiswa = (Button) root.findViewById(R.id.btn_tambah_siswa);
        btnUbahSiswa   = (Button) root.findViewById(R.id.btn_ubah_siswa);
        btnHapusSiswa  = (Button) root.findViewById(R.id.btn_hapus_siswa);

        // Button pada panel Guru
        btnTambahGuru = (Button) root.findViewById(R.id.btn_tambah_guru);
        btnUbahGuru   = (Button) root.findViewById(R.id.btn_ubah_guru);
        btnHapusGuru  = (Button) root.findViewById(R.id.btn_hapus_guru);

        // Menyambungkan ImageButton dengan layout pada halaman fragment_dahsboard
        ibPeraturan = (ImageButton) root.findViewById(R.id.imageButtonPeraturan);

        // Refresh Layout
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.refreshData);

        // Deklarasi CardView
        cardViewRiwayat = (CardView) root.findViewById(R.id.cv_riwayat);

        // Deklarasi dialog (Siswa)
        dialogTambahSiswa = new Dialog(getActivity());
        dialogUbahSiswa   = new Dialog(getActivity());
        dialogHapusSiswa  = new Dialog(getActivity());

        // Deklarasi dialog (Guru)
        dialogTambahGuru = new Dialog(getActivity());
        dialogHapusGuru  = new Dialog(getActivity());
        dialogUbahGuru   = new Dialog(getActivity());

        // Deklarasi dialog (Peraturan)
        dialogTambahPeraturan = new Dialog(getActivity());
        dialogHapusPeraturan  = new Dialog(getActivity());
        dialogUbahPeraturan   = new Dialog(getActivity());

        // Deklarasi dialog (Riwayat)
        dialogHapusRiwayat = new Dialog(getActivity());

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ChartProcess(root);
            }
        });

        ibPeraturan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.inflate(R.menu.menu_peraturan_admin);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_tambah:
                                popupTambahPeraturan();
                                break;

                            case R.id.menu_ubah:
                                String url = getResources().getString(R.string.url_peraturan);
                                AsyncHttpClient client = new AsyncHttpClient();
                                RequestParams params = new RequestParams();
                                params.put("token", getResources().getString(R.string.API_KEY));

                                client.get(url, params, new JsonHttpResponseHandler(){
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        popupUbahPeraturan(response);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        super.onFailure(statusCode, headers, throwable, errorResponse);
                                    }
                                });
                                break;

                            case R.id.menu_hapus:
                                String url1 = getResources().getString(R.string.url_peraturan);
                                AsyncHttpClient client1 = new AsyncHttpClient();
                                RequestParams params1 = new RequestParams();
                                params1.put("token", getResources().getString(R.string.API_KEY));

                                client1.get(url1, params1, new JsonHttpResponseHandler(){
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        popupHapusPeraturan(response);
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        super.onFailure(statusCode, headers, throwable, errorResponse);
                                    }
                                });
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        // Panel Hapus Riwayat
        cardViewRiwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getResources().getString(R.string.url_siswa);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", getResources().getString(R.string.API_KEY));

                client.get(url, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        popupHapusRiwayat(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
            }
        });

        // Panel Siswa
        // Button Pada panel (tambah, ubah, hapus) Siswa
        // =========================================================================
        btnTambahSiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpTambahSiswa();
            }
        });

        btnUbahSiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getResources().getString(R.string.url_siswa);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", getResources().getString(R.string.API_KEY));

                client.get(url, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        popupUbahSiswa(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
            }
        });

        btnHapusSiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getResources().getString(R.string.url_siswa);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", getResources().getString(R.string.API_KEY));

                client.get(url, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        popupHapusSiswa(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
            }
        });
        // =========================================================================

        // Panel Guru
        // Button Pada panel (tambah, ubah, hapus) Guru
        // =========================================================================
        btnTambahGuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpTambahGuru();
            }
        });

        btnUbahGuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getResources().getString(R.string.url_guru);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", getResources().getString(R.string.API_KEY));

                client.get(url, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        popupUbahGuru(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
            }
        });

        btnHapusGuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getResources().getString(R.string.url_guru);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", getResources().getString(R.string.API_KEY));

                client.get(url, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        popupHapusGuru(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });
            }
        });
        // =========================================================================

        ChartProcess(root);
        return root;
    }

    private void ChartProcess(final View root) {
        final String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

        String url = getResources().getString(R.string.url_kelas);;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token", getResources().getString(R.string.API_KEY));
        params.put("date", date);

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    // Menyambugkan View/Layout dengan Logic(Fragment)
                    tvTotalGuru = (TextView) root.findViewById(R.id.tv_totalGuru);
                    tvTotalSiswa= (TextView) root.findViewById(R.id.tv_totalSiswa);

                    chartSiswaLakiLaki  = root.findViewById(R.id.background_progressbar); // progress yang tetap
                    chartSiswaPerempuan = root.findViewById(R.id.stats_progressbar);      // progress yang bergerak

                    chartGuruAdmin      = root.findViewById(R.id.stats_progressbar2);      // progress yang bergerak
                    chartGuruNonAdmin   = root.findViewById(R.id.background_progressbar2); // progress yang tetap

                    siswaLaki      = root.findViewById(R.id.tv_siswalaki);
                    siswaPerempuan = root.findViewById(R.id.tv_siswaperempuan);
                    guruAdmin      = root.findViewById(R.id.tv_guruadmin);
                    guruNonAdmin   = root.findViewById(R.id.tv_gurunon);
                    tvLastUpdate   = root.findViewById(R.id.tv_lastUpdate);
                    tvPelanggar    = root.findViewById(R.id.tv_totalPelanggaran);

                    // Memasukkan data hasil post Loopj kedalam variabel
                    siswalakilaki = response.getInt("siswaLakiLaki");
                    siswaperempuan= response.getInt("siswaPerempuan");
                    guruadmin     = response.getInt("guruAdmin");
                    gurunonadmin  = response.getInt("guruNonAdmin");

                    siswaLaki.setText("laki-laki \n"+ String.valueOf(siswalakilaki) + " orang");
                    siswaPerempuan.setText("perempuan \n"+ String.valueOf(siswaperempuan) + " orang");
                    guruAdmin.setText("Admin \n"+ String.valueOf(guruadmin) + " orang");
                    guruNonAdmin.setText("Non-Admin \n"+ String.valueOf(gurunonadmin) + " orang");
                    tvPelanggar.setText(response.getString("totalPelanggar"));

                    String time= new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                    totalSiswa = siswalakilaki + siswaperempuan;
                    totalGuru  = guruadmin + gurunonadmin;

                    tvLastUpdate.setText("Last Update "+ date + " " + time);
                    tvTotalGuru.setText("total guru \n"+ String.valueOf(totalGuru) + " orang");
                    tvTotalSiswa.setText("total siswa \n"+ String.valueOf(totalSiswa) + " orang");

                    // Mengubah persentasi pada halaman dashboard untuk info tentang siswa
                    double dataSiswa = (double) siswaperempuan / (double) totalSiswa;
                    int progress1 = (int) (dataSiswa * 100);
                    chartSiswaPerempuan.setProgress(progress1);

                    // Mengubah persentasi pada halaman dashboard untuk info tentang guru
                    double dataGuru = (double) guruadmin / (double) totalGuru;
                    int progress2 = (int) (dataGuru * 100);
                    chartGuruAdmin.setProgress(progress2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

        // Close RefreshLayout ProgressDialog after get All Data From REST SERVER
        refreshLayout.setRefreshing(false);
    }

    // Panel Siswa
    // =========================================================================================
    private void popUpTambahSiswa() {
        dialogTambahSiswa.setContentView(R.layout.popup_siswa_tambah);
        dialogTambahSiswa.setCancelable(true);
        dialogTambahSiswa.show();

        // Button
        Button btnAdd, btnCancel;
        btnAdd    = (Button) dialogTambahSiswa.findViewById(R.id.btn_add);
        btnCancel = (Button) dialogTambahSiswa.findViewById(R.id.btn_cancel);

        // For RadioGroup Gender (Male & Female)
        RadioGroup radioGroup = (RadioGroup) dialogTambahSiswa.findViewById(R.id.gender_radio_group);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        final RadioButton radioButton = (RadioButton) dialogTambahSiswa.findViewById(selectedId);

        // For Spinner (Dropdown) kelas
        final Spinner spKelas = (Spinner) dialogTambahSiswa.findViewById(R.id.sp_kelas);

        final EditText etNama, etNis;

        etNama = (EditText) dialogTambahSiswa.findViewById(R.id.et_nama);
        etNis  = (EditText) dialogTambahSiswa.findViewById(R.id.et_nis);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                // Create params to send to rest_api
                String nama, nis, kelas, gender;

                nama   = etNama.getText().toString();
                nis    = etNis.getText().toString();
                kelas  = spKelas.getSelectedItem().toString();
                gender = radioButton.getText().toString();

                String url = getResources().getString(R.string.url_siswa);;
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", getResources().getString(R.string.API_KEY));
                params.put("nama", nama);
                params.put("nis", nis);
                params.put("kelas", kelas);
                params.put("jenis_kelamin", gender);

                client.post(url, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();
                        dialogTambahSiswa.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.insert_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        progressDialog.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.insert_failed), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTambahSiswa.dismiss();
            }
        });

    }

    private void popupUbahSiswa(JSONObject response) {
        dialogUbahSiswa.setContentView(R.layout.popup_siswa_ubah);
        dialogUbahSiswa.setCancelable(true);

        final EditText etNama, etNis;
        Button btnCancel, btnEdit;
        final Spinner spKelas;
        final RadioGroup gender;

        etNama  = (EditText) dialogUbahSiswa.findViewById(R.id.et_nama);
        etNis   = (EditText) dialogUbahSiswa.findViewById(R.id.et_nis);
        spKelas = (Spinner) dialogUbahSiswa.findViewById(R.id.sp_kelas);
        gender  = (RadioGroup) dialogUbahSiswa.findViewById(R.id.gender_radio_group);

        btnEdit   = (Button) dialogUbahSiswa.findViewById(R.id.btn_edit);
        btnCancel = (Button) dialogUbahSiswa.findViewById(R.id.btn_cancel);

        Siswa     = dialogUbahSiswa.findViewById(R.id.autocompleteSiswa);
        siswaList = new ArrayList<>();

        ArrayList<ModelSiswa> list = new ArrayList<>();

        try {
            JSONArray array = response.getJSONArray("data");
            for(int i =0; i<array.length(); i++){
                JSONObject object = array.getJSONObject(i);

                String nis           = object.getString("nis");
                String nama          = object.getString("nama");
                String kelas         = object.getString("kelas");
                String jenis_kelamin = object.getString("jenis_kelamin");

                ModelSiswa modelSiswa = new ModelSiswa(nis, nama, kelas, jenis_kelamin);
                list.add(modelSiswa);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        siswaList = list;

        AdapterAutocompleteSiswa adapterAutocompleteSiswa = new AdapterAutocompleteSiswa(getActivity(), R.layout.raw_result_autocomplete_siswa, siswaList);

        Siswa.setAdapter(adapterAutocompleteSiswa);
        Siswa.setThreshold(2);

        // Saat ada item di autocomplete siswa yang dipilih

        Siswa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ModelSiswa modelSiswa = (ModelSiswa) parent.getItemAtPosition(position);
                etNama.setText(modelSiswa.getNama());
                etNis.setText(modelSiswa.getNis());

                ArrayAdapter<String> array_spinner=(ArrayAdapter<String>)spKelas.getAdapter();
                spKelas.setSelection(array_spinner.getPosition(modelSiswa.getKelas()));

                if(modelSiswa.getJenis_kelamin().equals("perempuan")){
                    gender.check(R.id.female_radio_btn);
                } else {
                    gender.check(R.id.male_radio_btn);
                }
            }
        });

        dialogUbahSiswa.show();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                String nama, nis, kelas, jenis_kelamin;

                // For RadioGroup Gender (Male & Female)
                RadioGroup radioGroup = (RadioGroup) dialogUbahSiswa.findViewById(R.id.gender_radio_group);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton) dialogUbahSiswa.findViewById(selectedId);

                nama          = etNama.getText().toString();
                nis           = etNis.getText().toString();
                kelas         = spKelas.getSelectedItem().toString();
                jenis_kelamin = radioButton.getText().toString();

                UpdateData(nama, nis, kelas, jenis_kelamin);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUbahSiswa.dismiss();
            }
        });
    }

    private void popupHapusSiswa(JSONObject response) {
        dialogHapusSiswa.setContentView(R.layout.popup_siswa_hapus);
        dialogHapusSiswa.setCancelable(true);

        final TextView tvNis;
        Button btnDelete, btnCancel;

        tvNis     = (TextView) dialogHapusSiswa.findViewById(R.id.tv_nis);
        btnDelete = (Button) dialogHapusSiswa.findViewById(R.id.btn_delete);
        btnCancel = (Button) dialogHapusSiswa.findViewById(R.id.btn_cancel);

        Siswa     = dialogHapusSiswa.findViewById(R.id.autocompleteSiswa);
        siswaList = new ArrayList<>();

        ArrayList<ModelSiswa> list = new ArrayList<>();

        try {
            JSONArray array = response.getJSONArray("data");
            for(int i =0; i<array.length(); i++){
                JSONObject object = array.getJSONObject(i);

                String nis           = object.getString("nis");
                String nama          = object.getString("nama");
                String kelas         = object.getString("kelas");
                String jenis_kelamin = object.getString("jenis_kelamin");

                ModelSiswa modelSiswa = new ModelSiswa(nis, nama, kelas, jenis_kelamin);
                list.add(modelSiswa);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        siswaList = list;

        AdapterAutocompleteSiswa adapterAutocompleteSiswa = new AdapterAutocompleteSiswa(getActivity(), R.layout.raw_result_autocomplete_siswa, siswaList);

        Siswa.setAdapter(adapterAutocompleteSiswa);
        Siswa.setThreshold(2);

        // Saat ada item yang dipilih
        Siswa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ModelSiswa modelSiswa = (ModelSiswa) parent.getItemAtPosition(position);
                tvNis.setText(modelSiswa.getNis());
            }
        });

        dialogHapusSiswa.show();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                String url = getResources().getString(R.string.url_siswa);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", getResources().getString(R.string.API_KEY));
                params.put("method", getResources().getString(R.string.delete));
                params.put("nis", tvNis.getText().toString());

                client.get(url, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();
                        dialogHapusSiswa.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        progressDialog.dismiss();
                        dialogHapusSiswa.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHapusSiswa.dismiss();
            }
        });
    }

    private void UpdateData(String nama, String nis, String kelas, String jenis_kelamin) {
        String url = getResources().getString(R.string.url_siswa);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token", getResources().getString(R.string.API_KEY));
        params.put("method", getResources().getString(R.string.update));
        params.put("nama", nama);
        params.put("nis", nis);
        params.put("kelas", kelas);
        params.put("jenis_kelamin", jenis_kelamin);

        client.post(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                dialogUbahSiswa.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressDialog.dismiss();
                dialogUbahSiswa.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // =========================================================================================


    //Panel Guru
    // ===================================================================================================
    private void popUpTambahGuru() {
        dialogTambahGuru.setContentView(R.layout.popup_guru_tambah);
        dialogTambahGuru.setCancelable(true);
        dialogTambahGuru.show();

        // Button
        Button btnAdd, btnCancel;
        btnAdd    = (Button) dialogTambahGuru.findViewById(R.id.btn_add);
        btnCancel = (Button) dialogTambahGuru.findViewById(R.id.btn_cancel);

        final Spinner spMapel, spRole;
        spMapel = (Spinner) dialogTambahGuru.findViewById(R.id.sp_mapel);
        spRole  = (Spinner) dialogTambahGuru.findViewById(R.id.sp_role);

        final EditText etNama, etNip, etPassword1, etPassword2;
        etNama      = (EditText) dialogTambahGuru.findViewById(R.id.et_nama);
        etNip       = (EditText) dialogTambahGuru.findViewById(R.id.et_nip);
        etPassword1 = (EditText) dialogTambahGuru.findViewById(R.id.et_password1);
        etPassword2 = (EditText) dialogTambahGuru.findViewById(R.id.et_password2);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                String nama, nip, mapel, role, password;
                String dummyRole;

                nama     = etNama.getText().toString();
                nip      = etNip.getText().toString();
                mapel    = spMapel.getSelectedItem().toString();

                dummyRole = spRole.getSelectedItem().toString();

                if(dummyRole.equals("Admin")) {
                    role = "1";
                } else {
                    role = "2";
                }
                password = etPassword1.getText().toString();

                String url = getResources().getString(R.string.url_guru);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", getResources().getString(R.string.API_KEY));
                params.put("nama", nama);
                params.put("nip", nip);
                params.put("mapel", mapel);
                params.put("role", role);
                params.put("password", password);

                client.post(url, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();
                        dialogTambahGuru.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.insert_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        progressDialog.dismiss();
                        dialogTambahGuru.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.insert_failed), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTambahGuru.dismiss();
            }
        });
    }

    private void popupUbahGuru(JSONObject response) {
        dialogUbahGuru.setContentView(R.layout.popup_guru_ubah);
        dialogUbahGuru.setCancelable(true);

        final TextView tvCurrentPassword;
        final EditText etNama, etNip, etPassword1, etPassword2;
        Button btnCancel, btnUpdate;
        final Spinner spMapel, spRole;

        tvCurrentPassword = (TextView) dialogUbahGuru.findViewById(R.id.current_password);
        etPassword1       = (EditText) dialogUbahGuru.findViewById(R.id.et_password1);
        etPassword2       = (EditText) dialogUbahGuru.findViewById(R.id.et_password2);

        etNama    = (EditText) dialogUbahGuru.findViewById(R.id.et_nama);
        etNip     = (EditText) dialogUbahGuru.findViewById(R.id.et_nip);
        spMapel   = (Spinner) dialogUbahGuru.findViewById(R.id.sp_mapel);
        spRole    = (Spinner) dialogUbahGuru.findViewById(R.id.sp_role);
        btnUpdate = (Button) dialogUbahGuru.findViewById(R.id.btn_update);
        btnCancel = (Button) dialogUbahGuru.findViewById(R.id.btn_cancel);

        Guru     = dialogUbahGuru.findViewById(R.id.autocompleteGuru);
        guruList = new ArrayList<>();

        ArrayList<ModelGuru> list = new ArrayList<>();

        try {
            JSONArray array = response.getJSONArray("data");
            for(int i =0; i<array.length(); i++){
                JSONObject object = array.getJSONObject(i);

                String nama     = object.getString("nama");
                String nip      = object.getString("nip");
                String mapel    = object.getString("mapel");
                String role     = object.getString("role");
                String password = object.getString("password");

                ModelGuru modelGuru = new ModelGuru(nama, nip, mapel, role, password);
                list.add(modelGuru);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        guruList = list;

        AdapterAutocompleteGuru adapterAutocompleteGuru = new AdapterAutocompleteGuru(getActivity(), R.layout.raw_result_autocomplete_guru, guruList);
        Guru.setAdapter(adapterAutocompleteGuru);
        Guru.setThreshold(2);

        Guru.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ModelGuru modelGuru = (ModelGuru) parent.getItemAtPosition(position);
                etNama.setText(modelGuru.getNama());
                etNip.setText(modelGuru.getNip());

                if(modelGuru.getRole().equals("1")){
                    spRole.setSelection(0);
                } else {
                    spRole.setSelection(1);
                }

                ArrayAdapter<String> array_spinner_mapel = (ArrayAdapter<String>)spMapel.getAdapter();
                spMapel.setSelection(array_spinner_mapel.getPosition(modelGuru.getMapel()));

                tvCurrentPassword.setText(modelGuru.getPassword());
            }
        });

        dialogUbahGuru.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etPassword1.getText().toString().equals(etPassword2.getText().toString())){
                    String nama, nip, mapel, role, password;

                    nama  = etNama.getText().toString();
                    nip   = etNip.getText().toString();
                    mapel = spMapel.getSelectedItem().toString();

                    if(spRole.getSelectedItem().toString().equals("Admin")) {
                        role = "1";
                    } else {
                        role = "2";
                    }

                    if(etPassword1.getText().toString().equals("") || etPassword2.getText().toString().equals("")) {
                        password = tvCurrentPassword.getText().toString();
                    } else {
                        password = etPassword1.getText().toString();
                    }
                    UpdateDataGuru(nama, nip, mapel, role, password);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.password_not_match), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUbahGuru.dismiss();
            }
        });
    }

    private void popupHapusGuru(JSONObject response) {
        dialogHapusGuru.setContentView(R.layout.popup_guru_hapus);
        dialogHapusGuru.setCancelable(true);

        final TextView tvNip;
        Button btnDelete, btnCancel;

        tvNip     = (TextView) dialogHapusGuru.findViewById(R.id.tv_nip);
        btnDelete = (Button) dialogHapusGuru.findViewById(R.id.btn_delete);
        btnCancel = (Button) dialogHapusGuru.findViewById(R.id.btn_cancel);

        Guru     = dialogHapusGuru.findViewById(R.id.autocompleteGuru);
        guruList = new ArrayList<>();

        ArrayList<ModelGuru> list = new ArrayList<>();

        try {
            JSONArray array = response.getJSONArray("data");
            for(int i =0; i<array.length(); i++){
                JSONObject object = array.getJSONObject(i);

                String nama     = object.getString("nama");
                String nip      = object.getString("nip");
                String mapel    = object.getString("mapel");
                String role     = object.getString("role");
                String password = object.getString("password");

                ModelGuru modelGuru = new ModelGuru(nama, nip, mapel, role, password);
                list.add(modelGuru);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        guruList = list;

        AdapterAutocompleteGuru adapterAutocompleteGuru = new AdapterAutocompleteGuru(getActivity(), R.layout.raw_result_autocomplete_guru, guruList);
        Guru.setAdapter(adapterAutocompleteGuru);
        Guru.setThreshold(2);

        // Saat ada item yang dipilih
        Guru.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ModelGuru modelGuru = (ModelGuru) parent.getItemAtPosition(position);
                tvNip.setText(modelGuru.getNip());
            }
        });

        dialogHapusGuru.show();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                String url = getResources().getString(R.string.url_guru);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", getResources().getString(R.string.API_KEY));
                params.put("method", getResources().getString(R.string.delete));
                params.put("nip", tvNip.getText().toString());

                client.get(url, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();
                        dialogHapusGuru.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        progressDialog.dismiss();
                        dialogHapusGuru.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHapusGuru.dismiss();
            }
        });
    }

    private void UpdateDataGuru(String nama, String nip, String mapel, String role, String password) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        String url = getResources().getString(R.string.url_guru);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token", getResources().getString(R.string.API_KEY));
        params.put("method", getResources().getString(R.string.update));
        params.put("nama", nama);
        params.put("nip", nip);
        params.put("mapel", mapel);
        params.put("role", role);
        params.put("password", password);

        client.post(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                dialogUbahGuru.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressDialog.dismiss();
                dialogUbahGuru.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // ===================================================================================================


    // Panel Peraturan
    // ===================================================================================================
    private void popupTambahPeraturan() {
        dialogTambahPeraturan.setContentView(R.layout.popup_peraturan_tambah);
        dialogTambahPeraturan.setCancelable(true);
        dialogTambahPeraturan.show();

        // Button
        Button btnAdd, btnCancel;
        btnAdd    = (Button) dialogTambahPeraturan.findViewById(R.id.btn_add);
        btnCancel = (Button) dialogTambahPeraturan.findViewById(R.id.btn_cancel);

        // EdiText
        final EditText etKode, etJenisPelanggaran, etSkor;
        etKode             = (EditText) dialogTambahPeraturan.findViewById(R.id.et_kode);
        etJenisPelanggaran = (EditText) dialogTambahPeraturan.findViewById(R.id.et_jenisPelanggaran);
        etSkor             = (EditText) dialogTambahPeraturan.findViewById(R.id.et_skor);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kode, jenisPelanggaran, skor;

                kode             = etKode.getText().toString();
                jenisPelanggaran = etJenisPelanggaran.getText().toString();
                skor             = etSkor.getText().toString();

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                String url = getResources().getString(R.string.url_peraturan);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", getResources().getString(R.string.API_KEY));
                params.put("kode", kode);
                params.put("jenis_pelanggaran", jenisPelanggaran);
                params.put("skor", skor);

                client.post(url, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();
                        dialogTambahPeraturan.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.insert_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        progressDialog.dismiss();
                        dialogTambahPeraturan.dismiss();
                        try {
                            String message = errorResponse.getString("message");
                            Toast.makeText(getActivity().getApplicationContext(), ""+message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTambahPeraturan.dismiss();
            }
        });

    }

    private void popupUbahPeraturan(JSONObject response) {
        dialogUbahPeraturan.setContentView(R.layout.popup_peraturan_ubah);
        dialogUbahPeraturan.setCancelable(true);

        Button btnUpdate, btnCancel;
        final EditText etKode, etJenisPelanggaran, etSkor;

        etKode             = (EditText) dialogUbahPeraturan.findViewById(R.id.et_kode);
        etJenisPelanggaran = (EditText) dialogUbahPeraturan.findViewById(R.id.et_jenisPelanggaran);
        etSkor             = (EditText) dialogUbahPeraturan.findViewById(R.id.et_skor);

        btnUpdate = (Button) dialogUbahPeraturan.findViewById(R.id.btn_update);
        btnCancel = (Button) dialogUbahPeraturan.findViewById(R.id.btn_cancel);

        Peraturan     = dialogUbahPeraturan.findViewById(R.id.autocompletePeraturan);
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

        AdapterAutocompletePeraturan adapterAutocompletePeraturan = new AdapterAutocompletePeraturan(getActivity(), R.layout.raw_result_add_violation, peraturanList);

        Peraturan.setAdapter(adapterAutocompletePeraturan);
        Peraturan.setThreshold(2);

        Peraturan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ModelPeraturan modelPeraturan = (ModelPeraturan) parent.getItemAtPosition(position);
                etKode.setText(modelPeraturan.getKode());
                etJenisPelanggaran.setText(modelPeraturan.getJenis_pelanggaran());
                etSkor.setText(modelPeraturan.getSkor());
            }
        });

        dialogUbahPeraturan.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kode, jenisPelanggaran, skor;

                kode             = etKode.getText().toString();
                jenisPelanggaran = etJenisPelanggaran.getText().toString();
                skor             = etSkor.getText().toString();
                updateDataPeraturan(kode, jenisPelanggaran, skor);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUbahPeraturan.dismiss();
            }
        });
    }

    private void popupHapusPeraturan(JSONObject response) {
        dialogHapusPeraturan.setContentView(R.layout.popup_peraturan_hapus);
        dialogHapusPeraturan.setCancelable(true);

        final TextView tvKode;
        Button btnDelete, btnCancel;

        tvKode    = (TextView) dialogHapusPeraturan.findViewById(R.id.tv_kode);
        btnDelete = (Button) dialogHapusPeraturan.findViewById(R.id.btn_delete);
        btnCancel = (Button) dialogHapusPeraturan.findViewById(R.id.btn_cancel);

        Peraturan = dialogHapusPeraturan.findViewById(R.id.autocompletePeraturan);
        peraturanList  = new ArrayList<>();

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

        AdapterAutocompletePeraturan adapterAutocompletePeraturan = new AdapterAutocompletePeraturan(getActivity(), R.layout.raw_result_add_violation, peraturanList);
        Peraturan.setAdapter(adapterAutocompletePeraturan);
        Peraturan.setThreshold(2);

        Peraturan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ModelPeraturan modelPeraturan = (ModelPeraturan) parent.getItemAtPosition(position);
                tvKode.setText(modelPeraturan.getKode());
            }
        });

        dialogHapusPeraturan.show();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                String url = getResources().getString(R.string.url_peraturan);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", getResources().getString(R.string.API_KEY));
                params.put("method", "delete");
                params.put("kode", tvKode.getText().toString());

                client.get(url, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressDialog.dismiss();
                        dialogHapusPeraturan.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        progressDialog.dismiss();
                        dialogHapusPeraturan.dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHapusPeraturan.dismiss();
            }
        });
    }

    private void updateDataPeraturan(String kode, String jenisPelanggaran, String skor) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        String url = getResources().getString(R.string.url_peraturan);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token", getResources().getString(R.string.API_KEY));
        params.put("method", getResources().getString(R.string.update));
        params.put("kode", kode);
        params.put("jenis_pelanggaran", jenisPelanggaran);
        params.put("skor", skor);

        client.post(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                dialogUbahPeraturan.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressDialog.dismiss();
                dialogUbahPeraturan.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // ===================================================================================================

    // Panel Riwayat
    // =============================================================================================
    private void popupHapusRiwayat(JSONObject response) {
        dialogHapusRiwayat.setContentView(R.layout.popup_riwayat_delete);
        dialogHapusRiwayat.setCancelable(true);

        Siswa     = dialogHapusRiwayat.findViewById(R.id.autocompleteSiswa);
        siswaList = new ArrayList<>();

        ArrayList<ModelSiswa> list = new ArrayList<>();

        try {
            JSONArray array = response.getJSONArray("data");
            for(int i =0; i<array.length(); i++){
                JSONObject object = array.getJSONObject(i);

                String nis           = object.getString("nis");
                String nama          = object.getString("nama");
                String kelas         = object.getString("kelas");
                String jenis_kelamin = object.getString("jenis_kelamin");

                ModelSiswa modelSiswa = new ModelSiswa(nis, nama, kelas, jenis_kelamin);
                list.add(modelSiswa);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        siswaList = list;

        AdapterAutocompleteSiswa adapterAutocompleteSiswa = new AdapterAutocompleteSiswa(getActivity(), R.layout.raw_result_autocomplete_siswa, siswaList);

        Siswa.setAdapter(adapterAutocompleteSiswa);
        Siswa.setThreshold(2);

        Siswa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ModelSiswa modelSiswa = (ModelSiswa) parent.getItemAtPosition(position);

                // Set ListView
                listView = (ListView) dialogHapusRiwayat.findViewById(R.id.lv_riwayat);

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please wait");
                progressDialog.show();

                String nis = modelSiswa.getNis().toString();
                showRiwayat(nis);
            }
        });

        dialogHapusRiwayat.show();
    }

    private void showRiwayat(String nis) {
        String url = getResources().getString(R.string.url_riwayat);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("token", getResources().getString(R.string.API_KEY));
        params.put("nis", nis);

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    JSONArray array = response.getJSONArray("riwayat");
                    ArrayList<ModelRiwayatAdmin> modelRiwayatAdminArrayList = new ArrayList<ModelRiwayatAdmin>();

                    for(int i=0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        ModelRiwayatAdmin modelRiwayatAdmin = new ModelRiwayatAdmin();

                        modelRiwayatAdmin.setJenis_pelanggaran(object.getString("jenis_pelanggaran"));
                        modelRiwayatAdmin.setNis_siswa(object.getString("nis_siswa"));
                        modelRiwayatAdmin.setDate(object.getString("date"));
                        modelRiwayatAdmin.setTime(object.getString("time"));
                        modelRiwayatAdmin.setId(object.getString("id"));

                        modelRiwayatAdminArrayList.add(modelRiwayatAdmin);
                    }

                    adapterRiwayatAdmin = new AdapterRiwayatAdmin(getActivity(), modelRiwayatAdminArrayList);
                    listView.setAdapter(adapterRiwayatAdmin);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressDialog.dismiss();
            }
        });
    }
    //==============================================================================================

}