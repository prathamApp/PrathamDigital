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
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.adapters.RV_AgeFilterAdapter;
import com.pratham.prathamdigital.adapters.RV_LibraryContentAdapter;
import com.pratham.prathamdigital.adapters.RV_RecommendAdapter;
import com.pratham.prathamdigital.adapters.RV_SubLibraryAdapter;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.content_playing.Activity_WebView;
import com.pratham.prathamdigital.custom.GalleryLayoutManager;
import com.pratham.prathamdigital.custom.ScaleTransformer;
import com.pratham.prathamdigital.custom.custom_fab.FloatingActionButton;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.interfaces.VolleyResult_JSON;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_DownloadContent;
import com.pratham.prathamdigital.util.ActivityManagePermission;
import com.pratham.prathamdigital.util.NetworkChangeReceiver;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;

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
        VolleyResult_JSON, Observer {

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
    private DatabaseHandler db;
    private Modal_DownloadContent download_content;
    private int selected_content;
    private ArrayList<Modal_ContentDetail> downloadContents = new ArrayList<>();
    private ArrayList<Modal_ContentDetail> subContents = new ArrayList<>();
    private RV_LibraryContentAdapter libraryContentAdapter;
    private RV_SubLibraryAdapter subLibraryAdapter;
    private boolean isLibrary;
    private GalleryLayoutManager layoutManager;
    String googleId;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_two);
        ButterKnife.bind(this);
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
            Boolean IntroStatus = db.CheckIntroShownStatus(googleId);
            if (!IntroStatus) {
                // Show Intro & then set flag as shown
                db.SetIntroFlagTrue(1, googleId);
                Log.d("IntroStatus:", db.CheckIntroShownStatus(googleId) + "");
                ShowIntro(SEARCH);
            }
            layoutManager = new GalleryLayoutManager(GalleryLayoutManager.VERTICAL);
            layoutManager.setItemTransformer(new ScaleTransformer());
            layoutManager.attach(gallery_rv);
            gallery_rv.getViewTreeObserver().addOnPreDrawListener(preDrawListenerBrowse);
            LinearLayoutManager layoutManager3 = new GridLayoutManager(Activity_Main.this, 3);
            content_rv.setLayoutManager(layoutManager3);
            content_rv.getViewTreeObserver().addOnPreDrawListener(preDrawListenerContent);
            fab_my_library.performClick();
            isInitialized = true;
        }

    }

    private void ShowIntro(final int target) {
        int id = 0;
        String text = "";
        String content_text = "";
        if (target == SEARCH) {
            id = R.id.c_fab_search;
            text = getResources().getString(R.string.search);
            content_text = getResources().getString(R.string.search_and_download);
        } else if (target == MY_LIBRARY) {
            id = R.id.fab_my_library;
            text = getResources().getString(R.string.my_library);
            content_text = getResources().getString(R.string.view_contents);
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
                                ShowIntro(MY_LIBRARY);
                            } else if (target == MY_LIBRARY) {
                                ShowIntro(RECOMMEND);
                            } else if (target == RECOMMEND) {
                                ShowIntro(LANGUAGE);
                            } else {
                                fab_recom.performClick();
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
        isLibrary = true;
        txt_title.setAlpha(0f);
        txt_title.setText(getResources().getString(R.string.my_library));
        txt_title.animate().alpha(1f).setStartDelay(100).setDuration(500).setInterpolator(new FastOutSlowInInterpolator());
        initializeGalleryAdapater(isLibrary);
    }

    @OnClick(R.id.fab_recom)
    public void setFabRecom() {
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
                if (!isLibrary)
                    fab_recom.performClick();
                else
                    fab_my_library.performClick();
            }
        } else if (requestCode == ACTIVITY_DOWNLOAD) {
            if (resultCode == Activity.RESULT_OK) {
                selected_content = data.getIntExtra("position", selected_content);
                arrayList_content.remove(data.getIntExtra("position", selected_content));
                rv_recommendAdapter.notifyItemRemoved(selected_content);
                rv_recommendAdapter.notifyItemRangeChanged(selected_content, arrayList_content.size());
                rv_recommendAdapter.setSelectedIndex(-1, null);
                rv_recommendAdapter.updateData(arrayList_content);
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
            if (arrayList_content.get(position).getResourcetype().equalsIgnoreCase("Video")) {
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
            } else {
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
            }
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
                    rv_recommendAdapter.setSelectedIndex(position, null);
                    Intent intent = new Intent(Activity_Main.this, Activity_DownloadDialog.class);
                    intent.putExtra("ID", arrayList_content.get(position).getNodeid());
                    intent.putExtra("title", arrayList_content.get(position).getNodetitle());
                    intent.putExtra("image", arrayList_content.get(position).getNodeserverimage());
                    intent.putExtra("position", position);
                    intent.putExtra("transition_name", "transition_recommend");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this,
                            holder, "transition_recommend");
                    startActivityForResult(intent, ACTIVITY_DOWNLOAD, options.toBundle());
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
            rv_recommendAdapter.setSelectedIndex(position, null);
            Intent intent = new Intent(Activity_Main.this, Activity_DownloadDialog.class);
            intent.putExtra("ID", arrayList_content.get(position).getNodeid());
            intent.putExtra("title", arrayList_content.get(position).getNodetitle());
            intent.putExtra("image", arrayList_content.get(position).getNodeserverimage());
            intent.putExtra("position", position);
            intent.putExtra("transition_name", "transition_recommend");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Main.this,
                    holder, "transition_recommend");
            startActivityForResult(intent, 2, options.toBundle());
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
}
