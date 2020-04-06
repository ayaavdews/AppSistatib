package com.ayaavdews.appsistatib.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ayaavdews.appsistatib.Model.ModelPeraturan;
import com.ayaavdews.appsistatib.R;
import com.ayaavdews.appsistatib.ScannerActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FragmentProfileGuru extends Fragment {

    // Created by :
    // Aya Avdews
    // XII RPL A / 17006892
    // SMKN 2 Surakarta

    private TextView nama, nip, mapel, role;
    private ImageView profile;
    private Button btnPassword, btnScann;

    // For popup change password
    private Button btnChange, btnCancel;
    String newPassword, confirmPassword;
    private TextView password1, password2;

    Dialog dialog;
    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile_guru, container, false);

        // Deklarasi dialog (popup change password)
        dialog     = new Dialog(getActivity());

        // Menyambungkan variabel di layout dengan fragment
        nama = (TextView) root.findViewById(R.id.tv_nama);
        nip  = (TextView) root.findViewById(R.id.tv_id);
        mapel= (TextView) root.findViewById(R.id.tv_mapel);
        role = (TextView) root.findViewById(R.id.tv_role);

        profile = (ImageView) root.findViewById(R.id.iv_user);

        btnScann    = (Button) root.findViewById(R.id.btnScann);
        btnPassword = (Button) root.findViewById(R.id.btn_password);

        // Mendapatkan data yang didapat dari halaman login (MainActivity)
        Intent intent = getActivity().getIntent();
        final String dataUser = intent.getStringExtra("stringGuru");


        // Mengisi data di layout dari halaman login (MainActivity)
        try {
            JSONObject user = new JSONObject(dataUser);
            nama.setText(user.getString("nama"));
            nip.setText(user.getString("nip"));
            mapel.setText(user.getString("mapel"));
            if(user.getString("role").equals("1")){
                role.setText("Guru (Admin)");
                profile.setImageResource(R.drawable.teacher);
            } else {
                role.setText("Guru (non-Admin)");
                profile.setImageResource(R.drawable.teacher);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnScann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScannerActivity.class);
                intent.putExtra("guru", nama.getText().toString());
                intent.putExtra("nip", nip.getText().toString());
                startActivity(intent);
            }
        });
        
        // Fungsi ketika tombol change password ditekan 
        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.popup_change_password);
                dialog.setCancelable(false);
                dialog.show();

                password1 = dialog.findViewById(R.id.tv_new_password1);
                password2 = dialog.findViewById(R.id.tv_new_password2);

                btnChange = dialog.findViewById(R.id.btn_change);
                btnCancel = dialog.findViewById(R.id.btn_cancel);

                btnChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Please wait");
                        progressDialog.show();

                        newPassword     = password1.getText().toString();
                        confirmPassword = password2.getText().toString();

                        if(newPassword.equals("") || confirmPassword.equals("")){
                            progressDialog.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(), "Masih ada data yang kosong", Toast.LENGTH_SHORT).show();
                        } else {
                            if(!newPassword.equals(confirmPassword)){
                                progressDialog.dismiss();
                                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.password_not_match), Toast.LENGTH_SHORT).show();
                            } else {
                                changePassword(newPassword, dataUser);
                            }
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        password1.setText("");
                        password2.setText("");
                        dialog.dismiss();
                    }
                });
            }
        });

        return root;
    }

    private void changePassword(String newPassword, String dataUser) {
        try {
            JSONObject object = new JSONObject(dataUser);

            // LoopJ process :
            String url = getResources().getString(R.string.url_guru);
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("token", getResources().getString(R.string.API_KEY));
            params.put("method", "put");
            params.put("nama", object.getString("nama"));
            params.put("nip", object.getString("nip"));
            params.put("mapel", object.getString("mapel"));
            params.put("role", object.getString("role"));
            params.put("password", newPassword);

            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), "Password berhasil diganti", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    progressDialog.dismiss();
                    dialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), "Password gagal diganti", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}