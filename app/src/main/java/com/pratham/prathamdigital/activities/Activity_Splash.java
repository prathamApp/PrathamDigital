package com.pratham.prathamdigital.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.dbclasses.BackupDatabase;
import com.pratham.prathamdigital.dbclasses.GoogleDBHelper;
import com.pratham.prathamdigital.models.GoogleCredentials;
import com.pratham.prathamdigital.util.PD_Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_Splash extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.btn_google_login)
    Button btn_google_login;
    @BindView(R.id.img_logo)
    ImageView img_logo;

    GoogleDBHelper gdb;
    private GoogleApiClient googleApiClient;
    Context c;
    private static final int RC_SIGN_IN = 46;
    private Animation animeWobble;
    String personPhotoUrl = "", email = "", personName = "", googleId = "";
    private boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__splash);
        ButterKnife.bind(this);
        // Database Initialization
        c = this;
        gdb = new GoogleDBHelper(c);
        isInitialized = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isInitialized) {
            animeWobble = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.wobble);
            img_logo.startAnimation(animeWobble);
            new Handler().postDelayed(new Runnable() {
                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */
                @Override
                public void run() {
                    img_logo.clearAnimation();
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }, 2000);
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                    .build();
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
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else {
            Animation expandIn = AnimationUtils.loadAnimation(Activity_Splash.this, R.anim.pop_in);
            btn_google_login.startAnimation(expandIn);
            btn_google_login.setVisibility(View.VISIBLE);
        }
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
            Log.e("details:::", "Name: " + personName + ", Email: " + email
                    + ", Image: " + personPhotoUrl + ", GoogleId: " + googleId);
            if ((personName.length() > 0) && (email.length() > 0) && (googleId.length() > 0)) {
                // Check User's Existance
                boolean userExists = gdb.CheckLogin(googleId);
                // Action Based on User's Existance
                if (userExists == true) {
                    Toast.makeText(this, "Record Already Exists !!!", Toast.LENGTH_SHORT).show();
                } else {
                    // New Entry
                    GoogleCredentials gObj = new GoogleCredentials();
                    gObj.GoogleID = googleId;
//                    gObj.PersonPhotoUrl = "no_photo";
                    gObj.Email = email;
                    gObj.PersonName = personName;
                    gObj.IntroShown = 0;
                    gdb.insertNewUser(gObj);
                    Toast.makeText(Activity_Splash.this, "Welcome " + personName, Toast.LENGTH_SHORT).show();
//                    BackupDatabase.backup(c);
                }
            } else {
                Toast.makeText(this, "Please fill all the fields !!!", Toast.LENGTH_SHORT).show();
            }
            Bundle bundle = new Bundle();
            bundle.putString("GoogleID", googleId);
            bundle.putString("emailId", email);
            bundle.putString("PersonName", personName);
//            bundle.putString("PersonPhotoUrl", personPhotoUrl);
            updateUI(true, bundle);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false, null);
        }
    }

    private void updateUI(boolean isSignedIn, final Bundle bundle) {
        if (isSignedIn) {
            PD_Utility.showLoader(Activity_Splash.this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent activityChangeIntent = new Intent(Activity_Splash.this, DashBoard_Activity.class);
                    activityChangeIntent.putExtras(bundle);
                    startActivity(activityChangeIntent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.nothing);
                    finish();
                }
            }, 3000);
        } else {
            btn_google_login.setVisibility(View.VISIBLE);
        }
    }

}
