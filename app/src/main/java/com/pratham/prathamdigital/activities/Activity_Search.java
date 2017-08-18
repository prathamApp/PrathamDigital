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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ArcMotion;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.adapters.RV_ContentAdapter;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.custom.chips.ChipCloud;
import com.pratham.prathamdigital.custom.chips.ChipListener;
import com.pratham.prathamdigital.custom.morphing.MorphDialogToFab;
import com.pratham.prathamdigital.custom.morphing.MorphFabToDialog;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.interfaces.VolleyResult_JSON;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Level;
import com.pratham.prathamdigital.util.ActivityManagePermission;
import com.pratham.prathamdigital.util.NetworkChangeReceiver;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HP on 18-08-2017.
 */

public class Activity_Search extends ActivityManagePermission implements VolleyResult_JSON, MainActivityAdapterListeners, Observer {

    private static final String TAG = Activity_Search.class.getSimpleName();
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.img_cross)
    ImageView img_cross;
    @BindView(R.id.text)
    EditText text;
    @BindView(R.id.view_search)
    ViewGroup view_search;
    @BindView(R.id.search_chipcloud)
    ChipCloud search_chipcloud;
    @BindView(R.id.rv_search)
    RecyclerView rv_search;
    @BindView(R.id.rl_search_no_content)
    RelativeLayout rl_search_no_content;
    @BindView(R.id.rl_search_no_internet)
    RelativeLayout rl_search_no_internet;
    @BindView(R.id.img_search_no_net)
    ImageView img_search_no_net;

    private AnimatedVectorDrawable searchToBar;
    private AnimatedVectorDrawable barToSearch;
    private float offset;
    private Interpolator interp;
    private int duration = 800;
    private boolean expanded = false;
    private ArrayList<String> search_tags = new ArrayList<>();
    private boolean isInitialized;
    private AlertDialog dialog = null;
    private DatabaseHandler db;
    private ArrayList<Modal_ContentDetail> arrayList_content = new ArrayList<>();
    private RV_ContentAdapter rv_contentAdapter;
    private int selected_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        dialog = PD_Utility.showLoader(this);
        db = new DatabaseHandler(this);
        setupEnterTransitions();
        isInitialized = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.getObservable().addObserver(this);
        if (!isInitialized) {
            searchToBar = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_search_to_bar);
            barToSearch = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_bar_to_search);
            interp = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
            offset = -71f * (int) getResources().getDisplayMetrics().scaledDensity;
            search.performClick();
            //Instialize chip Cloud
            String[] tags = getResources().getStringArray(R.array.search_tags);
            for (int i = 0; i < tags.length; i++) {
                search_tags.add(tags[i]);
            }
            search_chipcloud.removeAllViews();
            for (int i = 0; i < search_tags.size(); i++) {
                search_chipcloud.addChip(search_tags.get(i));
            }
            search_chipcloud.setChipListener(new ChipListener() {
                @Override
                public void chipSelected(int index) {
                    text.setText(search_tags.get(index));
                    if (!expanded)
                        search.performClick();
                }

                @Override
                public void chipDeselected(int index) {

                }
            });
            isInitialized = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        NetworkChangeReceiver.getObservable().deleteObserver(this);
    }


    @OnClick({R.id.search, R.id.img_cross})
    public void animate() {
        if (!expanded) {
            search.setImageDrawable(searchToBar);
            searchToBar.start();
            search.animate().translationX(0f).setDuration(duration).setInterpolator(interp);
            text.setVisibility(View.VISIBLE);
            text.setOnEditorActionListener(editorActionListener);
            text.animate().alpha(1f).setStartDelay(duration - 100).setDuration(100).setInterpolator(interp);
            img_cross.setVisibility(View.VISIBLE);
            img_cross.animate().alpha(1f).setDuration(100).setInterpolator(interp);
        } else {
            img_cross.animate().alpha(0f).setDuration(100).setInterpolator(interp);
            img_cross.setVisibility(View.GONE);
            search.setImageDrawable(barToSearch);
            barToSearch.start();
            search.animate().translationX(offset).setDuration(duration).setInterpolator(interp);
            text.setAlpha(0f);
            text.setVisibility(View.GONE);
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        expanded = !expanded;
    }

    private void setupEnterTransitions() {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

        MorphFabToDialog sharedEnter = new MorphFabToDialog();
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphDialogToFab sharedReturn = new MorphDialogToFab();
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        if (view_search != null) {
            sharedEnter.addTarget(view_search);
            sharedReturn.addTarget(view_search);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    EditText.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(final TextView textView, int actionId, KeyEvent keyEvent) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (text.getText().toString().length() > 0) {
                    if (PD_Utility.isInternetAvailable(getApplicationContext())) {
                        showDialog();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new PD_ApiRequest(Activity_Search.this, Activity_Search.this).getDataVolley("SEARCH",
                                        PD_Constant.URL.SEARCH_BY_KEYWORD.toString() + "lang=" + db.GetUserLanguage() + "&keyw=" + textView.getText().toString());
                            }
                        }, 2000);
                    } else {
                        updteInternetConnection();
                    }
                }
            }
            return false;
        }
    };

    private void showDialog() {
        if (dialog == null)
            dialog = PD_Utility.showLoader(Activity_Search.this);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finishAfterTransition();
    }

    ViewTreeObserver.OnPreDrawListener preDrawListenerContent = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            rv_search.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < rv_search.getChildCount(); i++) {
                View view = rv_search.getChildAt(i);
                view.animate().cancel();
                view.setTranslationY(100);
                view.setAlpha(0);
                view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(i * 50);
            }
            return true;
        }
    };

    @Override
    public void notifySuccess(String requestType, String response) {
        try {
            Log.d("response:::", response);
            Log.d("response:::", "requestType:: " + requestType);
            Gson gson = new Gson();
            if (requestType.equalsIgnoreCase("SEARCH")) {
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
                //inflating the content recycler view
                if (arrayList_content.size() > 0) {
                    rl_search_no_content.setVisibility(View.GONE);
                    if (rv_contentAdapter == null) {
                        LinearLayoutManager layoutManager2 = new GridLayoutManager(this, 4);
                        rv_search.setLayoutManager(layoutManager2);
                        rv_contentAdapter = new RV_ContentAdapter(Activity_Search.this, this, arrayList_content);
                        rv_search.getViewTreeObserver().addOnPreDrawListener(preDrawListenerContent);
                        rv_search.setAdapter(rv_contentAdapter);
                    } else {
                        rv_contentAdapter.updateData(arrayList_content);
                    }
                } else {
                    rl_search_no_content.setVisibility(View.VISIBLE);
                }
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

    @Override
    public void browserButtonClicked(int position) {

    }

    @Override
    public void contentButtonClicked(final int position) {
        showDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new PD_ApiRequest(Activity_Search.this, Activity_Search.this).getDataVolley("SEARCH",
                        PD_Constant.URL.BROWSE_BY_ID.toString() + arrayList_content.get(position).getNodeid());
            }
        }, 2000);
    }

    @Override
    public void levelButtonClicked(int position) {

    }

    @Override
    public void downloadClick(final int position, final View holder) {
        if (!isPermissionsGranted(Activity_Search.this, new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_READ_EXTERNAL_STORAGE})) {
            askCompactPermissions(new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                    PermissionUtils.Manifest_READ_EXTERNAL_STORAGE}, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    rv_contentAdapter.setSelectedIndex(position, null);
                    Intent intent = new Intent(Activity_Search.this, Activity_DownloadDialog.class);
                    intent.putExtra("ID", arrayList_content.get(position).getNodeid());
                    intent.putExtra("title", arrayList_content.get(position).getNodetitle());
                    intent.putExtra("image", arrayList_content.get(position).getNodeserverimage());
                    intent.putExtra("position", position);
                    intent.putExtra("transition_name", "transition_search");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Search.this,
                            holder, "transition_search");
                    startActivityForResult(intent, 1, options.toBundle());
                }

                @Override
                public void permissionDenied() {
                }

                @Override
                public void permissionForeverDenied() {
                    Toast.makeText(Activity_Search.this, R.string.provide_storage_permission, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            rv_contentAdapter.setSelectedIndex(position, null);
            Intent intent = new Intent(Activity_Search.this, Activity_DownloadDialog.class);
            intent.putExtra("ID", arrayList_content.get(position).getNodeid());
            intent.putExtra("title", arrayList_content.get(position).getNodetitle());
            intent.putExtra("image", arrayList_content.get(position).getNodeserverimage());
            intent.putExtra("position", position);
            intent.putExtra("transition_name", "transition_search");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Activity_Search.this,
                    holder, "transition_recommend");
            startActivityForResult(intent, 1, options.toBundle());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                selected_content = data.getIntExtra("position", selected_content);
                arrayList_content.remove(data.getIntExtra("position", selected_content));
                rv_contentAdapter.notifyItemRemoved(selected_content);
                rv_contentAdapter.notifyItemRangeChanged(selected_content, arrayList_content.size());
                rv_contentAdapter.setSelectedIndex(-1, null);
                rv_contentAdapter.updateData(arrayList_content);
            }
        }
    }

    @Override
    public void downloadComplete(int position) {

    }

    @Override
    public void update(Observable observable, Object o) {
        Log.d("update:::", "called");
        updteInternetConnection();
    }

    private void updteInternetConnection() {
        if (!PD_Utility.isInternetAvailable(Activity_Search.this)) {
            rv_search.setVisibility(View.GONE);
            rl_search_no_content.setVisibility(View.GONE);
            rl_search_no_internet.setVisibility(View.VISIBLE);
            AnimatedVectorDrawable avd = (AnimatedVectorDrawable)
                    Activity_Search.this.getDrawable(R.drawable.avd_no_connection);
            img_search_no_net.setImageDrawable(avd);
            Drawable animation = img_search_no_net.getDrawable();
            if (animation instanceof Animatable) {
                ((Animatable) animation).start();
            }
        } else {
            rv_search.setVisibility(View.VISIBLE);
            rl_search_no_internet.setVisibility(View.GONE);
        }
    }
}
