package com.pratham.prathamdigital.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aloj.progress.DownloadProgressView;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.adapters.RV_AgeFilterAdapter;
import com.pratham.prathamdigital.adapters.RV_LibraryContentAdapter;
import com.pratham.prathamdigital.adapters.RV_RecommendAdapter;
import com.pratham.prathamdigital.adapters.RV_SubLibraryAdapter;
import com.pratham.prathamdigital.async.ImageDownload;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.async.ZipDownloader;
import com.pratham.prathamdigital.content_playing.Activity_WebView;
import com.pratham.prathamdigital.content_playing.TextToSp;
import com.pratham.prathamdigital.custom.GalleryLayoutManager;
import com.pratham.prathamdigital.custom.ScaleTransformer;
import com.pratham.prathamdigital.custom.custom_fab.FloatingActionButton;
import com.pratham.prathamdigital.custom.fancy_toast.TastyToast;
import com.pratham.prathamdigital.custom.progress_indicators.ProgressWheel;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.interfaces.ProgressUpdate;
import com.pratham.prathamdigital.interfaces.VolleyResult_JSON;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_DownloadContent;
import com.pratham.prathamdigital.util.ActivityManagePermission;
import com.pratham.prathamdigital.util.NetworkChangeReceiver;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * Created by HP on 17-08-2017.
 */

public class Activity_Main extends ActivityManagePermission implements MainActivityAdapterListeners,
        VolleyResult_JSON, Observer, ProgressUpdate {

    private static final String TAG = Activity_Main.class.getSimpleName();
    private static final int SEARCH = 1;
    private static final int MY_LIBRARY = 2;
    private static final int RECOMMEND = 3;
    private static final int LANGUAGE = 4;
    private static final int ACTIVITY_LANGUAGE = 1;
    private static final int ACTIVITY_DOWNLOAD = 2;
    private static final int ACTIVITY_SEARCH = 3;
    private static final int ACTIVITY_PDF = 4;
    private static final int ACTIVITY_VPLAYER = 5;
    @BindView(R.id.content_rv)
    RecyclerView content_rv;
    @BindView(R.id.gallery_rv)
    RecyclerView gallery_rv;
    @BindView(R.id.c_fab_language)
    FloatingActionButton fab_language;
    @BindView(R.id.c_fab_search)
    FloatingActionButton fab_search;
    @BindView(R.id.fab_recom)
    FloatingActionButton fab_recom;
    @BindView(R.id.fab_my_library)
    FloatingActionButton fab_my_library;
    @BindView(R.id.txt_title)
    TextView txt_title;
    @BindView(R.id.rl_no_data)
    RelativeLayout rl_no_data;
    @BindView(R.id.rl_no_content)
    RelativeLayout rl_no_content;
    @BindView(R.id.rl_no_internet)
    RelativeLayout rl_no_internet;
    @BindView(R.id.img_no_net)
    ImageView img_no_net;
    @BindView(R.id.main_fab_download)
    DownloadProgressView main_fab_download;
    @BindView(R.id.main_download_title)
    TextView main_download_title;
    @BindView(R.id.main_rl_download)
    RelativeLayout main_rl_download;

    int[] hindi_age_id = {20, 21, 22, 23};
    int[] marathi_age_id = {25, 26, 27, 28};
    int[] kannada_age_id = {30, 31, 32, 33};
    int[] telugu_age_id = {35, 36, 37, 38};
    int[] bengali_age_id = {40, 41, 42, 43};
    int[] gujarati_age_id = {45, 46, 47, 48};
    int[] assamese_age_id = {50, 51, 52, 53};
    int[] punjabi_age_id = {55, 56, 57, 58};
    int[] odiya_age_id = {60, 61, 62, 63};
    int[] tamil_age_id = {65, 66, 67, 68};
    int[] childs = {R.drawable.ic_baby_wrapped, R.drawable.ic_boy_wrapped, R.drawable.ic_10year_boy_wrapped, R.drawable.ic_adult_boy_wrapped};
    private String[] age;
    private boolean isInitialized;
    RV_AgeFilterAdapter ageFilterAdapter;
    RV_RecommendAdapter rv_recommendAdapter;
    private AlertDialog dialog;
    private ArrayList<Modal_ContentDetail> arrayList_content = new ArrayList<>();
    private ArrayList<Modal_ContentDetail> to_be_downloaded = new ArrayList<>();
    private DatabaseHandler db;
    private Modal_DownloadContent download_content;
    private int selected_content;
    private ArrayList<Modal_ContentDetail> downloadContents = new ArrayList<>();
    private ArrayList<Modal_ContentDetail> subContents = new ArrayList<>();
    private RV_LibraryContentAdapter libraryContentAdapter;
    private RV_SubLibraryAdapter subLibraryAdapter;
    private boolean isLibrary = false;
    private GalleryLayoutManager layoutManager;
    String googleId;
    private String url;
    public static TextToSp ttspeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_two);
        ButterKnife.bind(this);
        ttspeech = new TextToSp(this);
        dialog = PD_Utility.showLoader(Activity_Main.this);
        db = new DatabaseHandler(Activity_Main.this);
        googleId = db.getGoogleID();
        Log.d("googleId::", googleId);
        isInitialized = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.getObservable().addObserver(this);
        if ((!isInitialized)) {
            layoutManager = new GalleryLayoutManager(GalleryLayoutManager.VERTICAL);
            layoutManager.setItemTransformer(new ScaleTransformer());
            layoutManager.attach(gallery_rv);
            gallery_rv.getViewTreeObserver().addOnPreDrawListener(preDrawListenerBrowse);
            LinearLayoutManager layoutManager3 = new GridLayoutManager(Activity_Main.this, 3);
            content_rv.setLayoutManager(layoutManager3);
            content_rv.getViewTreeObserver().addOnPreDrawListener(preDrawListenerContent);
            isInitialized = true;
            Boolean IntroStatus = db.CheckIntroShownStatus(googleId);
            if (!IntroStatus) {
                // Show Intro & then set flag as shown
                Log.d("IntroStatus:", db.CheckIntroShownStatus(googleId) + "");
                ShowIntro(LANGUAGE);
            } else {
                fab_my_library.performClick();
            }
        }

    }

    private void ShowIntro(final int target) {
        int id = 0;
        String text = "";
        String content_text = "";
        if (target == MY_LIBRARY) {
            id = R.id.fab_my_library;
            text = getResources().getString(R.string.my_library);
            content_text = getResources().getString(R.string.view_contents);
        } else if (target == SEARCH) {
            id = R.id.c_fab_search;
            text = getResources().getString(R.string.search);
            content_text = getResources().getString(R.string.search_and_download);
        } else if (target == RECOMMEND) {
            id = R.id.fab_recom;
            text = getResources().getString(R.string.recommended);
            content_text = getResources().getString(R.string.download_recommended);
        } else {
            id = R.id.c_fab_language;
            text = getResources().getString(R.string.language);
            content_text = getResources().getString(R.string.select_language);
        }
        new MaterialTapTargetPrompt.Builder(Activity_Main.this)
                .setTarget(findViewById(id))
                .setPrimaryText(text)
                .setSecondaryText(content_text)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setBackButtonDismissEnabled(false)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                        if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                            if (target == SEARCH) {
//                                ShowIntro(LANGUAGE);
                                ShowIntro(RECOMMEND);
                            } else if (target == MY_LIBRARY) {
                                ShowIntro(SEARCH);
                            } else if (target == RECOMMEND) {
                                db.SetIntroFlagTrue(1, googleId);
                                fab_recom.performClick();
                            } else {
                                fab_language.performClick();
                            }
                        }
                    }
                })
                .show();
    }

    private void initializeGalleryAdapater(final boolean isLibrary) {
        if (isLibrary) {
            downloadContents = db.Get_Contents(PD_Constant.TABLE_PARENT, 0);
            if (downloadContents.size() > 0) {
                gallery_rv.setVisibility(View.VISIBLE);
                rl_no_internet.setVisibility(View.GONE);
                rl_no_data.setVisibility(View.GONE);
                rl_no_content.setVisibility(View.GONE);
                content_rv.setVisibility(View.VISIBLE);
                PD_Utility.DEBUG_LOG(1, TAG, "db_list_size::" + downloadContents.size());
                PD_Utility.DEBUG_LOG(1, TAG, "db_nodelist_size::" + downloadContents.size());
                libraryContentAdapter = new RV_LibraryContentAdapter(Activity_Main.this, this, downloadContents);
                gallery_rv.setAdapter(libraryContentAdapter);
            } else {
                gallery_rv.setVisibility(View.GONE);
                content_rv.setVisibility(View.GONE);
                rl_no_data.setVisibility(View.VISIBLE);
            }
        } else {
            gallery_rv.setVisibility(View.VISIBLE);
            rl_no_data.setVisibility(View.GONE);
            age = getResources().getStringArray(R.array.main_contents);
            ageFilterAdapter = new RV_AgeFilterAdapter(this, this, age, childs);
            gallery_rv.setAdapter(ageFilterAdapter);
        }
        layoutManager.setOnItemSelectedListener(new GalleryLayoutManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(RecyclerView recyclerView, View item, final int position) {
                if (isLibrary) {
                    content_rv.setVisibility(View.VISIBLE);
                    subContents = db.Get_Contents(PD_Constant.TABLE_CHILD, downloadContents.get(position).getNodeid());
                    if (subLibraryAdapter == null) {
                        subLibraryAdapter = new RV_SubLibraryAdapter(Activity_Main.this, Activity_Main.this, subContents);
                        content_rv.setAdapter(subLibraryAdapter);
                    } else {
                        content_rv.setAdapter(subLibraryAdapter);
                        content_rv.getViewTreeObserver().addOnPreDrawListener(preDrawListenerRecommend);
                        subLibraryAdapter.updateData(subContents);
                    }
                } else {
                    if (PD_Utility.isInternetAvailable(Activity_Main.this)) {
                        url = PD_Constant.URL.BROWSE_BY_ID.toString();
                        if (db.GetUserLanguage().equalsIgnoreCase("hindi"))
                            url += hindi_age_id[position];
                        if (db.GetUserLanguage().equalsIgnoreCase("Marathi"))
                            url += marathi_age_id[position];
                        if (db.GetUserLanguage().equalsIgnoreCase("Kannada"))
                            url += kannada_age_id[position];
                        if (db.GetUserLanguage().equalsIgnoreCase("Telugu"))
                            url += telugu_age_id[position];
                        if (db.GetUserLanguage().equalsIgnoreCase("Bengali"))
                            url += bengali_age_id[position];
                        if (db.GetUserLanguage().equalsIgnoreCase("Gujarati"))
                            url += gujarati_age_id[position];
                        if (db.GetUserLanguage().equalsIgnoreCase("Punjabi"))
                            url += punjabi_age_id[position];
                        if (db.GetUserLanguage().equalsIgnoreCase("Tamil"))
                            url += tamil_age_id[position];
                        /*
                        if (db.GetUserLanguage().equalsIgnoreCase("Odiya"))
                            url += tamil_age_id[position];
                        if (db.GetUserLanguage().equalsIgnoreCase("Malayalam"))
                            url += tamil_age_id[position];
                        if (db.GetUserLanguage().equalsIgnoreCase("Assamese"))
                            url += tamil_age_id[position];
                            */
                        showDialog();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new PD_ApiRequest(Activity_Main.this, Activity_Main.this).getDataVolley("BROWSE", url);
                            }
                        }, 2000);
                    } else {
                        updateInternetConnection();
                    }
                }
            }
        });
    }

    ViewTreeObserver.OnPreDrawListener preDrawListenerBrowse = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            gallery_rv.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < gallery_rv.getChildCount(); i++) {
                View view = gallery_rv.getChildAt(i);
                view.animate().cancel();
                view.setTranslationY(100);
                view.setAlpha(0);
                view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(i * 100);
            }
            return true;
        }
    };
    ViewTreeObserver.OnPreDrawListener preDrawListenerContent = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            content_rv.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < content_rv.getChildCount(); i++) {
                View view = content_rv.getChildAt(i);
                view.animate().cancel();
                view.setTranslationY(100);
                view.setAlpha(0);
                view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(i * 50);
            }
            return true;
        }
    };
    ViewTreeObserver.OnPreDrawListener preDrawListenerRecommend = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            content_rv.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < content_rv.getChildCount(); i++) {
                View view = content_rv.getChildAt(i);
                view.animate().cancel();
                view.setTranslationX(100);
                view.setAlpha(0);
                view.animate().alpha(1.0f).translationX(0).setDuration(300).setStartDelay(i * 50);
            }
            return true;
        }
    };

    @OnClick(R.id.c_fab_language)
    public void setLanguage() {
        Intent intent = new Intent(Activity_Main.this, Activity_LanguagDialog.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this,
                fab_language, "transition_dialog");
        startActivityForResult(intent, ACTIVITY_LANGUAGE, options.toBundle());
    }

    @OnClick(R.id.c_fab_search)
    public void setFabSearch() {
        Intent intent = new Intent(Activity_Main.this, Activity_Search.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this,
                fab_search, "transition_search");
        startActivityForResult(intent, ACTIVITY_SEARCH, options.toBundle());
    }

    @OnClick(R.id.fab_my_library)
    public void setFabLibrary() {
        if (!db.CheckIntroShownStatus(googleId)) {
            db.SetIntroFlagTrue(1, googleId);
        }
        isLibrary = true;
        txt_title.setAlpha(0f);
        txt_title.setText(getResources().getString(R.string.my_library));
        txt_title.animate().alpha(1f).setStartDelay(100).setDuration(500).setInterpolator(new FastOutSlowInInterpolator());
        initializeGalleryAdapater(isLibrary);
    }

    @OnClick(R.id.fab_recom)
    public void setFabRecom() {
        if (!db.CheckIntroShownStatus(googleId)) {
            db.SetIntroFlagTrue(1, googleId);
        }
        isLibrary = false;
        txt_title.setAlpha(0f);
        txt_title.setText(getResources().getString(R.string.recommended));
        txt_title.animate().alpha(1f).setStartDelay(100).setDuration(500).setInterpolator(new FastOutSlowInInterpolator());
        initializeGalleryAdapater(isLibrary);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_LANGUAGE) {
            if (resultCode == Activity.RESULT_OK) {
                String language = data.getStringExtra(PD_Constant.LANGUAGE);
                Log.d("language_before_insert:", language);
                db.SetUserLanguage(language, db.getGoogleID());
                Log.d("language_after_insert::", db.GetUserLanguage());
                PD_Utility.setLocale(this, db.GetUserLanguage());
                if (!db.CheckIntroShownStatus(googleId)) {
                    ShowIntro(MY_LIBRARY);
                } else {
                    if (!isLibrary)
                        fab_recom.performClick();
                    else
                        fab_my_library.performClick();
                }
            }
        } else if (requestCode == ACTIVITY_DOWNLOAD) {
            if (resultCode == Activity.RESULT_OK) {
//                selected_content = data.getIntExtra("position", selected_content);
//                arrayList_content.remove(data.getIntExtra("position", selected_content));
//                rv_recommendAdapter.notifyItemRemoved(selected_content);
//                rv_recommendAdapter.notifyItemRangeChanged(selected_content, arrayList_content.size());
//                rv_recommendAdapter.setSelectedIndex(-1, null);
//                rv_recommendAdapter.updateData(arrayList_content);
            }
        } else if (requestCode == ACTIVITY_SEARCH) {
            fab_my_library.performClick();
        }
    }

    @Override
    public void browserButtonClicked(int position) {

    }

    @Override
    public void contentButtonClicked(final int position, View holder) {
        if (isLibrary) {
            if (subContents.get(position).getNodetype().equalsIgnoreCase("Resource")) {
                if (subContents.get(position).getResourcetype().equalsIgnoreCase("Game")) {
                    Intent intent = new Intent(Activity_Main.this, Activity_WebView.class);
                    File directory = Activity_Main.this.getDir("PrathamGame", Context.MODE_PRIVATE);
                    intent.putExtra("index_path", directory.getAbsolutePath() + "/" + subContents.get(position).getResourcepath());
                    intent.putExtra("path", directory.getAbsolutePath() + "/" +
                            new StringTokenizer(subContents.get(position).getResourcepath(), "/").nextToken() + "/");
                    intent.putExtra("resId", subContents.get(position).getResourceid());
                    Runtime rs = Runtime.getRuntime();
                    rs.freeMemory();
                    rs.gc();
                    rs.freeMemory();
                    startActivity(intent);
                } else if (subContents.get(position).getResourcetype().equalsIgnoreCase("pdf")) {
                    Intent intent = new Intent(Activity_Main.this, Activity_PdfViewer.class);
                    File directory = Activity_Main.this.getDir("PrathamPdf", Context.MODE_PRIVATE);
                    Log.d("game_filepath:::", directory.getAbsolutePath() + "/" + subContents.get(position).getResourcepath());
                    intent.putExtra("pdfPath", "file:///" + directory.getAbsolutePath() + "/" + subContents.get(position).getResourcepath());
                    intent.putExtra("pdfTitle", subContents.get(position).getNodetitle());
                    intent.putExtra("resId", subContents.get(position).getResourceid());
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this,
                            holder, "transition_pdf");
                    Runtime rs = Runtime.getRuntime();
                    rs.freeMemory();
                    rs.gc();
                    rs.freeMemory();
                    startActivityForResult(intent, ACTIVITY_PDF, options.toBundle());
                }
            } else {
                ArrayList<Modal_ContentDetail> list = db.Get_Contents(PD_Constant.TABLE_CHILD, subContents.get(position).getNodeid());
                subContents.clear();
                subContents.addAll(list);
                Log.d("sub_content_size::", subContents.size() + "");
                subLibraryAdapter.updateData(subContents);
            }
        } else {
            /*if (arrayList_content.get(position).getResourcetype().equalsIgnoreCase("Video")) {
                Intent intent = new Intent(Activity_Main.this, Activity_VPlayer.class);
                Log.d("server_path:::", arrayList_content.get(position).getNodeserverpath());
                intent.putExtra("videoPath", PD_Utility.getYouTubeID(arrayList_content.get(position).getNodeserverpath()));
                intent.putExtra("title", arrayList_content.get(position).getNodetitle());
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this,
                        holder, "transition_recommend");
                Runtime rs = Runtime.getRuntime();
                rs.freeMemory();
                rs.gc();
                rs.freeMemory();
                startActivityForResult(intent, ACTIVITY_VPLAYER, options.toBundle());
            } else {*/
            if (PD_Utility.isInternetAvailable(Activity_Main.this)) {
                showDialog();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new PD_ApiRequest(Activity_Main.this, Activity_Main.this).getDataVolley("BROWSE",
                                PD_Constant.URL.BROWSE_BY_ID.toString() + arrayList_content.get(position).getNodeid());
                    }
                }, 2000);
            } else {
                updateInternetConnection();
            }
//            }
        }
    }

    @Override
    public void levelButtonClicked(int position) {

    }

    @Override
    public void downloadClick(final int position, final View holder) {
        selected_content = position;
        if (!isPermissionsGranted(Activity_Main.this, new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_READ_EXTERNAL_STORAGE})) {
            askCompactPermissions(new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                    PermissionUtils.Manifest_READ_EXTERNAL_STORAGE}, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    if (to_be_downloaded.size() > 2) {
                        TastyToast.makeText(getApplicationContext(), getString(R.string.let_them_download), TastyToast.LENGTH_LONG,
                                TastyToast.WARNING);
                    } else {
                        rv_recommendAdapter.reveal(holder);
                        to_be_downloaded.add(arrayList_content.get(position));
                        arrayList_content.get(position).setDownloading(true);
                        rv_recommendAdapter.setSelectedIndex(position, null);
                        if (to_be_downloaded.size() == 1) {
                            Log.d("pressed::", "again");
                            new PD_ApiRequest(Activity_Main.this, Activity_Main.this).getDataVolley("DOWNLOAD",
                                    PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + to_be_downloaded.get(0).getNodeid());
                        }
                    }
                }

                @Override
                public void permissionDenied() {

                }

                @Override
                public void permissionForeverDenied() {
                    Toast.makeText(Activity_Main.this, "Provide Permission for storage first", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            if (to_be_downloaded.size() > 3) {
                TastyToast.makeText(getApplicationContext(), getString(R.string.let_them_download), TastyToast.LENGTH_LONG,
                        TastyToast.WARNING);
            } else {
                rv_recommendAdapter.reveal(holder);
                to_be_downloaded.add(arrayList_content.get(position));
                arrayList_content.get(position).setDownloading(true);
                rv_recommendAdapter.setSelectedIndex(position, null);
                if (to_be_downloaded.size() == 1) {
                    Log.d("pressed::", "again");
                    new PD_ApiRequest(Activity_Main.this, Activity_Main.this).getDataVolley("DOWNLOAD",
                            PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + to_be_downloaded.get(0).getNodeid());
                }
            }
        }
    }

    @Override
    public void downloadComplete(int position) {
    }

    @Override
    public void notifySuccess(String requestType, String response) {
        try {
            Log.d("response:::", response);
            Log.d("response:::", "requestType:: " + requestType);
            Gson gson = new Gson();
            if (requestType.equalsIgnoreCase("BROWSE")) {
                arrayList_content.clear();
                Type listType = new TypeToken<ArrayList<Modal_ContentDetail>>() {
                }.getType();
                arrayList_content = gson.fromJson(response, listType);
                PD_Utility.DEBUG_LOG(1, TAG, "content_length:::" + arrayList_content.size());
                //retrieving ids of downloaded contents from database
                arrayList_content = removeContentIfDownloaded(arrayList_content);
                arrayList_content = checkIfAlreadyDownloading(arrayList_content);
                //inflating the recommended content recycler view
                if (arrayList_content.size() > 0) {
                    content_rv.setVisibility(View.VISIBLE);
                    rl_no_content.setVisibility(View.GONE);
                    if (rv_recommendAdapter == null) {
                        rv_recommendAdapter = new RV_RecommendAdapter(Activity_Main.this, this, arrayList_content);
                        content_rv.setAdapter(rv_recommendAdapter);
                    } else {
                        content_rv.setAdapter(rv_recommendAdapter);
                        content_rv.getViewTreeObserver().addOnPreDrawListener(preDrawListenerContent);
                        rv_recommendAdapter.updateData(arrayList_content);
                        Log.d("content_size::", arrayList_content.size() + "");
                    }
                } else {
                    content_rv.setVisibility(View.GONE);
                    rl_no_content.setVisibility(View.VISIBLE);
                }
            } else if (requestType.equalsIgnoreCase("DOWNLOAD")) {
                JSONObject jsonObject = new JSONObject(response);
                download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                PD_Utility.DEBUG_LOG(1, TAG, "nodelist_length:::" + download_content.getNodelist().size());
                PD_Utility.DEBUG_LOG(1, TAG, "foldername:::" + download_content.getFoldername());
                String fileName = download_content.getDownloadurl()
                        .substring(download_content.getDownloadurl().lastIndexOf('/') + 1);
                PD_Utility.DEBUG_LOG(1, TAG, "filename:::" + fileName);
                PowerManager pm = (PowerManager) Activity_Main.this.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
                if (download_content.getDownloadurl().length() > 0) {
                    main_download_title.setText("Downloading...\n" + to_be_downloaded.get(0).getNodetitle());
                    main_fab_download.setDownloading(true);
                    main_fab_download.setClickable(false);
                    main_rl_download.setVisibility(View.VISIBLE);
                    main_rl_download.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_scale_up));
                    new ZipDownloader(Activity_Main.this, Activity_Main.this, download_content.getDownloadurl(),
                            download_content.getFoldername(), fileName, wl);
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    private ArrayList<Modal_ContentDetail> checkIfAlreadyDownloading(ArrayList<Modal_ContentDetail> arrayList_content) {
        if (to_be_downloaded.size() > 0) {
            Log.d("to_be_downloaded::", to_be_downloaded.size() + "");
            for (int i = 0; i < to_be_downloaded.size(); i++) {
                for (int j = 0; j < arrayList_content.size(); j++) {
                    if (arrayList_content.get(j).getResourceid().equalsIgnoreCase(to_be_downloaded.get(i).getResourceid())) {
                        Log.d("to_be_downloaded::", "downloadeding content");
                        arrayList_content.get(j).setDownloading(true);
                    }
                }
            }
        }
        return arrayList_content;
    }

    private ArrayList<Modal_ContentDetail> removeContentIfDownloaded(ArrayList<Modal_ContentDetail> arrayList_content) {
        ArrayList<String> downloaded_ids = new ArrayList<>();
        downloaded_ids = db.getDownloadContentID(PD_Constant.TABLE_DOWNLOADED);
        if (downloaded_ids.size() > 0) {
            Log.d("contents_downloaded::", downloaded_ids.size() + "");
            for (int i = 0; i < downloaded_ids.size(); i++) {
                for (int j = 0; j < arrayList_content.size(); j++) {
                    if (arrayList_content.get(j).getResourceid().equalsIgnoreCase(downloaded_ids.get(i))) {
                        Log.d("contents_downloaded::", "downloaded content removed");
                        arrayList_content.remove(j);
                    }
                }
            }
        }
        return arrayList_content;
    }

    @Override
    public void notifyError(String requestType, VolleyError error) {
        try {
            Log.d("response:::", "requestType:: " + requestType);
            Log.d("error:::", error.toString());
            if (requestType.equalsIgnoreCase("BROWSE")) {
                content_rv.setVisibility(View.GONE);
                rl_no_content.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }


    @Override
    public void update(Observable observable, Object o) {
        Log.d("update:::", "called");
        if (!isLibrary) {
            updateInternetConnection();
        }
    }

    private void updateInternetConnection() {
        if (!PD_Utility.isInternetAvailable(Activity_Main.this)) {
            content_rv.setVisibility(View.GONE);
            rl_no_content.setVisibility(View.GONE);
            rl_no_internet.setVisibility(View.VISIBLE);
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable)
                    Activity_Main.this.getDrawable(R.drawable.avd_no_connection);
            img_no_net.setImageDrawable(avd);
            Drawable animation = img_no_net.getDrawable();
            if (animation instanceof Animatable) {
                ((Animatable) animation).start();
            }
        } else {
            content_rv.setVisibility(View.VISIBLE);
            rl_no_internet.setVisibility(View.GONE);
        }
    }

    private void showDialog() {
        if (dialog == null)
            dialog = PD_Utility.showLoader(Activity_Main.this);
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        NetworkChangeReceiver.getObservable().deleteObserver(this);
    }

    @Override
    protected void onDestroy() {
        Log.d("Destroyed Score Entry", "Destroyed Score Entry");
        if (ttspeech != null) {
            ttspeech.shutDownTTS();
            Log.d("tts_destroyed", "TTS Destroyed");
        }
        super.onDestroy();
    }

    @Override
    public void onProgressUpdate(int progress) {
        main_fab_download.setProgress((float) (progress * .01));
        if (progress == 100) {
            main_download_title.setText(R.string.please_wait);
            main_fab_download.setDownloading(true);
        }
    }

    @Override
    public void onZipDownloaded(boolean isDownloaded) {
        if (isDownloaded) {
            for (int i = 0; i < download_content.getNodelist().size(); i++) {
                String fileName = download_content.getNodelist().get(i).getNodeserverimage()
                        .substring(download_content.getNodelist().get(i).getNodeserverimage().lastIndexOf('/') + 1);
                new ImageDownload(Activity_Main.this, fileName)
                        .execute(download_content.getNodelist().get(i).getNodeserverimage());
            }
            addContentToDatabase(download_content);
        }
    }

    @Override
    public void onZipExtracted(boolean isExtracted) {
        if (isExtracted) {
            for (int i = 0; i < arrayList_content.size(); i++) {
                if (arrayList_content.get(i).getNodeid() == to_be_downloaded.get(0).getNodeid()) {
                    arrayList_content.remove(i);
                    rv_recommendAdapter.notifyItemRemoved(i);
                    rv_recommendAdapter.notifyItemRangeChanged(i, arrayList_content.size());
                    rv_recommendAdapter.setSelectedIndex(-1, null);
                    rv_recommendAdapter.updateData(arrayList_content);
                }
            }
            to_be_downloaded.remove(0);
            if (to_be_downloaded.size() > 0) {
                new PD_ApiRequest(Activity_Main.this, Activity_Main.this).getDataVolley("DOWNLOAD",
                        PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + to_be_downloaded.get(0).getNodeid());
            } else {
                main_rl_download.startAnimation(AnimationUtils.loadAnimation(Activity_Main.this, R.anim.fab_scale_down));
                main_rl_download.setVisibility(View.GONE);
            }
        }
    }

    private void addContentToDatabase(Modal_DownloadContent download_content) {
        ArrayList<String> p_ids = db.getDownloadContentID(PD_Constant.TABLE_PARENT);
        ArrayList<String> c_ids = db.getDownloadContentID(PD_Constant.TABLE_CHILD);
        for (int i = 0; i < download_content.getNodelist().size(); i++) {
            if (i == 0) {
                if (!p_ids.contains(String.valueOf(download_content.getNodelist().get(i).getNodeid())))
                    db.Add_Content(PD_Constant.TABLE_PARENT, download_content.getNodelist().get(i));
            } else {
                if (!c_ids.contains(String.valueOf(download_content.getNodelist().get(i).getNodeid())))
                    db.Add_Content(PD_Constant.TABLE_CHILD, download_content.getNodelist().get(i));
            }
        }
        db.Add_DOownloadedFileDetail(download_content.getNodelist().get(download_content.getNodelist().size() - 1));
    }

    @Override
    public void lengthOfTheFile(int length) {

    }

    int back = 0;

    @Override
    public void onBackPressed() {
        if (back == 0) {
            TastyToast.makeText(getApplicationContext(), getString(R.string.press_again_to_exit), TastyToast.LENGTH_LONG,
                    TastyToast.SUCCESS);
            back += 1;
        } else {
            super.onBackPressed();
        }
    }
}
