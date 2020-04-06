package com.ayaavdews.appsistatib;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.ayaavdews.appsistatib.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    // ============================
    // == Created By :            =
    // == Aya Avdews              =
    // == XII RPL A / 17006892    =
    // == Instagram : @aya_avdews =
    // ============================

    private Switch btnSwitch;
    private String controller;
    private FrameLayout signInBtn;
    private ActivityMainBinding mBinding;
    private EditText etNip, etNis, etPassword;
    private TextInputLayout ilNip, ilNis, ilPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        btnSwitch = (Switch) findViewById(R.id.btn_switch);
        signInBtn = (FrameLayout) findViewById(R.id.signInBtn);

        etNip      = (EditText) findViewById(R.id.et_nip);
        etNis      = (EditText) findViewById(R.id.et_nis);
        etPassword = (EditText) findViewById(R.id.et_password);

        ilNip      = (TextInputLayout) findViewById(R.id.il_nip);
        ilNis      = (TextInputLayout) findViewById(R.id.il_nis);
        ilPassword = (TextInputLayout) findViewById(R.id.il_password);

        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    controller = "guru";
                    etNip.setVisibility(View.VISIBLE);
                    etPassword.setVisibility(View.VISIBLE);
                    etNis.setVisibility(View.GONE);

                    ilNip.setVisibility(View.VISIBLE);
                    ilPassword.setVisibility(View.VISIBLE);
                    ilNis.setVisibility(View.GONE);
                } else {
                    controller = "siswa";
                    etNip.setVisibility(View.GONE);
                    etPassword.setVisibility(View.GONE);
                    etNis.setVisibility(View.VISIBLE);

                    ilNip.setVisibility(View.GONE);
                    ilPassword.setVisibility(View.GONE);
                    ilNis.setVisibility(View.VISIBLE);
                }
            }
        });

        // Jika tombol Login ditekan
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nip      = etNip.getText().toString();
                String nis      = etNis.getText().toString();
                String password = etPassword.getText().toString();

                if(controller == "guru") {
                    animateButtonGuru(nip, password);
                } else {
                    animateButtonSiswa(nis);
                }
            }
        });
    }

    private void animateButtonSiswa(final String nis){
        final ValueAnimator anim = ValueAnimator.ofInt(mBinding.signInBtn.getMeasuredWidth(), getFinalWidth());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mBinding.signInBtn.getLayoutParams();
                layoutParams.width = value;
                mBinding.signInBtn.requestLayout();
            }
        });

        anim.setDuration(250);
        mBinding.signInText.animate().alpha(0f).setDuration(250);
        mBinding.progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        mBinding.progressBar.setVisibility(View.VISIBLE);
        anim.start();

        if(nis.equals("")){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBinding.progressBar.setVisibility(View.INVISIBLE);
                    mBinding.signInText.animate().alpha(1f).setDuration(250);
                    anim.reverse();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_nis), Toast.LENGTH_SHORT).show();
                }
            }, 700);
        } else if(nis.length() < 8) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBinding.progressBar.setVisibility(View.INVISIBLE);
                    mBinding.signInText.animate().alpha(1f).setDuration(250);
                    anim.reverse();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.short_nis), Toast.LENGTH_SHORT).show();
                }
            }, 700);
        } else {
            // Deklarasi untuk melakukan pengecekan jaringan internet (mobile data/wifi)
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State internet = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            NetworkInfo.State wifi     = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

            // Cek apakah perangkat sekarang tersambung ke jaringan internet atau tidak
            if(internet == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {
                // Jika tersambung maka
                String url = getResources().getString(R.string.url_siswa);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", getResources().getString(R.string.API_KEY));
                params.put("nis", nis);

                client.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try{
                            JSONArray array1   = response.getJSONArray("data");
                            fadeOutTextAndSetProgressDialog();
                            nextAction2(array1);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.progressBar.setVisibility(View.INVISIBLE);
                                mBinding.signInText.animate().alpha(1f).setDuration(250);
                                anim.reverse();
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.wrong_nis), Toast.LENGTH_SHORT).show();
                            }
                        }, 100);
                    }
                });
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.progressBar.setVisibility(View.INVISIBLE);
                        mBinding.signInText.animate().alpha(1f).setDuration(250);
                        anim.reverse();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
                    }
                }, 700);
            }
        }
    }

    private void animateButtonGuru(final String nip, final String password) {
        final ValueAnimator anim = ValueAnimator.ofInt(mBinding.signInBtn.getMeasuredWidth(), getFinalWidth());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mBinding.signInBtn.getLayoutParams();
                layoutParams.width = value;
                mBinding.signInBtn.requestLayout();
            }
        });

        anim.setDuration(250);
        mBinding.signInText.animate().alpha(0f).setDuration(250);
        mBinding.progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        mBinding.progressBar.setVisibility(View.VISIBLE);
        anim.start();

        if(nip.equals("") || password.equals("")){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBinding.progressBar.setVisibility(View.INVISIBLE);
                    mBinding.signInText.animate().alpha(1f).setDuration(250);
                    anim.reverse();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_nip_or_password), Toast.LENGTH_SHORT).show();
                }
            }, 700);
        } else if(nip.length() < 21) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBinding.progressBar.setVisibility(View.INVISIBLE);
                    mBinding.signInText.animate().alpha(1f).setDuration(250);
                    anim.reverse();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.short_nip), Toast.LENGTH_SHORT).show();
                }
            }, 700);
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State internet = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            NetworkInfo.State wifi     = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

            if(internet == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTED) {
                String url = getResources().getString(R.string.login_guru);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("token", getResources().getString(R.string.API_KEY));
                params.put("nip", nip);
                params.put("password", password);

                client.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try{
                            JSONArray array1   = response.getJSONArray("data");
                            fadeOutTextAndSetProgressDialog();
                            nextAction(array1);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.progressBar.setVisibility(View.INVISIBLE);
                                mBinding.signInText.animate().alpha(1f).setDuration(250);
                                anim.reverse();
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.wrong_nip_or_password), Toast.LENGTH_SHORT).show();
                            }
                        }, 100);
                    }
                });
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.progressBar.setVisibility(View.INVISIBLE);
                        mBinding.signInText.animate().alpha(1f).setDuration(250);
                        anim.reverse();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_connection), Toast.LENGTH_SHORT).show();
                    }
                }, 700);
            }
        }
    }

    private void fadeOutTextAndSetProgressDialog() {
        mBinding.signInText.animate().alpha(0f).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showProgressDialog();
            }
        }).start();
    }

    private void showProgressDialog() {
        mBinding.progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    // Untuk next Action (Admin/Guru)
    // ==========================================================================================
    private void nextAction(JSONArray array1) throws JSONException {
        final JSONObject array2  = array1.getJSONObject(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                revealButton();
                fadeOutProgressDialog();
                delayedStartActivityGuruAdmin(array2);
            }
        }, 100);
    }

    // Untuk next Action (Siswa)
    // ==========================================================================================
    private void  nextAction2(JSONArray array1) throws  JSONException {
        final JSONObject array2 = array1.getJSONObject(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                revealButton();
                fadeOutProgressDialog();
                delayedStartActivitySiswa(array2);
            }
        }, 100);
    }

    private void revealButton() {
        mBinding.signInBtn.setElevation(0f);
        mBinding.revealView.setVisibility(View.VISIBLE);

        int x = mBinding.revealView.getWidth();
        int y = mBinding.revealView.getHeight();

        int startX = (int) (getFinalWidth() / 2 + mBinding.signInBtn.getX());
        int startY = (int) (getFinalWidth() / 2 + mBinding.signInBtn.getY());

        float radius = Math.max(x,y) * 1.2f;

        Animator reveal = ViewAnimationUtils.createCircularReveal(mBinding.revealView, startX, startY, getFinalWidth(), radius);
        reveal.setDuration(350);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finish();
            }
        });
        reveal.start();
    }

    private void fadeOutProgressDialog() {
        mBinding.progressBar.animate().alpha(0f).setDuration(200).start();
    }

    // Intent ke Dashboard untuk guru (admin) setelah verifikasi data success
    private void delayedStartActivityGuruAdmin(final JSONObject array2) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    // Diarahkan ke halaman admin (role 1 = admin)
                    if(array2.getString("role").equals("1")){
                        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                        intent.putExtra("stringGuru", array2.toString());
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        // Diarahkkan ke halaman guru (role 2 = guru)
                        Intent intent = new Intent(MainActivity.this, GuruActivity.class);
                        intent.putExtra("stringGuru", array2.toString());
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, 100);
    }
    // ==========================================================================================

    // Intent ke Dashboard untuk Siswa setelah verifikasi data success
    private void delayedStartActivitySiswa(final JSONObject array2) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, SiswaActivity.class);
                intent.putExtra("stringSiswa", array2.toString());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, 100);
    }

    // Menentukan perubahan ukuran width button saat di tekan
    private int getFinalWidth() {
        return (int) getResources().getDimension(R.dimen.get_width);
    }
}
