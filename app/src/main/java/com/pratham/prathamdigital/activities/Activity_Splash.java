package com.pratham.prathamdigital.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.VolleyResult_JSON;
import com.pratham.prathamdigital.models.GoogleCredentials;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class Activity_Splash extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, VolleyResult_JSON {

    @BindView(R.id.btn_google_login)
    Button btn_google_login;
    @BindView(R.id.img_logo)
    ImageView img_logo;

    DatabaseHandler gdb;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 46;
    private Animation animeWobble;
    String personPhotoUrl = "", email = "", personName = "", googleId = "";
    private boolean isInitialized = false;
    private AlertDialog dialog;
    private GoogleCredentials gObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity__splash);
        ButterKnife.bind(this);
        // Database Initialization
        dialog = PD_Utility.showLoader(this);
        gdb = new DatabaseHandler(this);
        isInitialized = false;
//        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        if (googleApiClient == null || !googleApiClient.isConnected()) {
//            try {
//                googleApiClient = new GoogleApiClient.Builder(this)
//                        .enableAutoManage(this, this)
//                        .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
//                        .build();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void onStart() {
        super.onStart();

//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
//        if (opr.isDone()) {
//            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
//            // and the GoogleSignInResult will be available instantly.
//            Log.d("splash:::", "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
//            // If the user has not previously signed in on this device or the sign-in has expired,
//            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
//            // single sign-on will occur in this branch.
//            Log.d("splash:::", "else sign-in");
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isInitialized) {
            if (gdb.getGoogleID().equalsIgnoreCase("")) {
//                                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
//                                startActivityForResult(signInIntent, RC_SIGN_IN);
                insertGoogleData();
            } else {
                updateUI(true, null);
            }
//            img_logo.getPathAnimator()
//                    .delay(500)
//                    .duration(3000)
//                    .listenerStart(new PathView.AnimatorBuilder.ListenerStart() {
//                        @Override
//                        public void onAnimationStart() {
//
//                        }
//                    })
//                    .listenerEnd(new PathView.AnimatorBuilder.ListenerEnd() {
//                        @Override
//                        public void onAnimationEnd() {
//
//                        }
//                    })
//                    .interpolator(new AccelerateDecelerateInterpolator())
//                    .start();
//            img_logo.useNaturalColors();
//            img_logo.setFillAfter(true);
            isInitialized = true;
        }
    }

    private void insertGoogleData() {
        gObj = new GoogleCredentials();
        String deviceId = Build.SERIAL;
        gObj.GoogleID = deviceId;
        gObj.Email = "";
        gObj.PersonName = "";
        gObj.IntroShown = 0;
        gObj.languageSelected = "Hindi";
        Map<String, Object> params = new HashMap<>();
        params.put(PD_Constant.GOOGLE_ID, deviceId);
        params.put(PD_Constant.EMAIL, "");
        params.put(PD_Constant.PERSON_NAME, "");
        params.put(PD_Constant.LANG, gObj.languageSelected);
        final JSONObject jsonObject = new JSONObject(params);
        Log.d("google_data::", jsonObject.toString());
        if (PD_Utility.isInternetAvailable(Activity_Splash.this)) {
            showDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new PD_ApiRequest(Activity_Splash.this, Activity_Splash.this).postDataVolley(Activity_Splash.this,
                            "POST", PD_Constant.URL.POST_GOOGLE_DATA.toString(), jsonObject);
                }
            }, 2000);
        } else {
            Toast.makeText(Activity_Splash.this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_google_login)
    public void next() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            Log.d("status::", "" + result.getStatus().getStatusCode());
//            handleSignInResult(result);
//        } else {
//            Animation expandIn = AnimationUtils.loadAnimation(Activity_Splash.this, R.anim.pop_in);
//            btn_google_login.startAnimation(expandIn);
//            btn_google_login.setVisibility(View.VISIBLE);
//        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("result::", result.toString());
        Log.d("result::", result.isSuccess() + "");
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = result.getSignInAccount();
            Log.e("display name: ", account.getDisplayName());
            if (account.getId() != null) {
                googleId = account.getId();
            }
            if (account.getDisplayName() != null) {
                personName = account.getDisplayName();
            }
            if (account.getPhotoUrl() != null) {
                personPhotoUrl = account.getPhotoUrl().toString();
            }
            if (account.getEmail() != null) {
                email = account.getEmail();
            }
            Log.e("details:::", "Name: " + personName + ", Email: " + email + ", GoogleId: " + googleId);
            if ((personName.length() > 0) && (email.length() > 0) && (googleId.length() > 0)) {
                boolean userExists = gdb.CheckGoogleLogin(googleId);
                if (!userExists) {
                    gObj = new GoogleCredentials();
                    gObj.GoogleID = googleId;
                    gObj.Email = email;
                    gObj.PersonName = personName;
                    gObj.IntroShown = 0;
                    gObj.languageSelected = "Hindi";
                    Map<String, Object> params = new HashMap<>();
                    params.put(PD_Constant.GOOGLE_ID, googleId);
                    params.put(PD_Constant.EMAIL, email);
                    params.put(PD_Constant.PERSON_NAME, personName);
                    params.put(PD_Constant.LANG, gObj.languageSelected);
                    final JSONObject jsonObject = new JSONObject(params);
                    Log.d("google_data::", jsonObject.toString());
                    if (PD_Utility.isInternetAvailable(Activity_Splash.this)) {
                        showDialog();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new PD_ApiRequest(Activity_Splash.this, Activity_Splash.this).postDataVolley(Activity_Splash.this,
                                        "POST", PD_Constant.URL.POST_GOOGLE_DATA.toString(), jsonObject);
                            }
                        }, 2000);
                    } else {
                        Toast.makeText(Activity_Splash.this, "Check Internet Connectivity", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void showDialog() {
        if (dialog == null)
            dialog = PD_Utility.showLoader(Activity_Splash.this);
        dialog.show();
    }

    private void updateUI(boolean isSignedIn, final Bundle bundle) {
        if (isSignedIn) {
            PD_Utility.showLoader(Activity_Splash.this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent activityChangeIntent = new Intent(Activity_Splash.this, Activity_Main.class);
                    startActivity(activityChangeIntent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.nothing);
                    finish();
                }
            }, 3000);
        } else {
            btn_google_login.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void notifySuccess(String requestType, String response) {
        try {
            Log.d("response:::", response);
            Log.d("response:::", "requestType:: " + requestType);
            gdb.insertNewGoogleUser(gObj);
            Toast.makeText(Activity_Splash.this, "Welcome " + personName, Toast.LENGTH_SHORT).show();
            updateUI(true, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public void notifyError(String requestType, VolleyError error) {
        try {
            Log.d("response:::", "requestType:: " + requestType);
            Log.d("error:::", error.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }
}
