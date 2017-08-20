package com.pratham.prathamdigital.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.adapters.RV_BrowseAdapter;
import com.pratham.prathamdigital.adapters.RV_ContentAdapter;
import com.pratham.prathamdigital.adapters.RV_LevelAdapter;
import com.pratham.prathamdigital.async.ImageDownload;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.async.ZipDownloader;
import com.pratham.prathamdigital.custom.chips.ChipCloud;
import com.pratham.prathamdigital.custom.chips.ChipListener;
import com.pratham.prathamdigital.custom.reveal.AnimatorPath;
import com.pratham.prathamdigital.custom.reveal.PathEvaluator;
import com.pratham.prathamdigital.custom.reveal.ViewAnimationUtils;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.interfaces.PermissionResult;
import com.pratham.prathamdigital.interfaces.ProgressUpdate;
import com.pratham.prathamdigital.interfaces.VolleyResult_JSON;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_DownloadContent;
import com.pratham.prathamdigital.models.Modal_Level;
import com.pratham.prathamdigital.util.ActivityManagePermission;
import com.pratham.prathamdigital.util.NetworkChangeReceiver;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;
import com.pratham.prathamdigital.util.PermissionUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends ActivityManagePermission implements MainActivityAdapterListeners,
        ProgressUpdate, VolleyResult_JSON, Observer {

    @BindView(R.id.rv_browse_contents)
    RecyclerView rv_browse_contents;
    @BindView(R.id.rv_contents)
    RecyclerView rv_contents;
    @BindView(R.id.rv_level)
    RecyclerView rv_level;
    @BindView(R.id.root_search)
    RelativeLayout root_search;
    @BindView(R.id.img_content_search)
    ImageView img_content_search;
    @BindView(R.id.search_chipcloud)
    ChipCloud search_chipcloud;
    @BindView(R.id.et_edit_address)
    EditText et_edit_address;
    @BindView(R.id.rl_main_not_connected)
    RelativeLayout rl_main_not_connected;
    @BindView(R.id.img_main_no_connection)
    ImageView img_main_no_connection;
    @BindView(R.id.root_content)
    ConstraintLayout root_content;

    RV_BrowseAdapter rv_browseAdapter;
    RV_ContentAdapter rv_contentAdapter;
    RV_LevelAdapter rv_levelAdapter;
    private ArrayList<String> search_tags = new ArrayList<>();
    private ArrayList<Modal_Level> arrayList_level = new ArrayList<>();
    private ArrayList<Modal_ContentDetail> arrayList_content = new ArrayList<>();
    //    private ItemDecorator itemDecorator;
    private String[] name;
    private boolean isInitialized;
    private int progress = 0;
    private AlertDialog dialog = null;
    private String TAG = MainActivity.class.getSimpleName();
    private int selected_level_position = -1;
    private Modal_DownloadContent download_content;
    private DatabaseHandler db;
    private boolean isDownloading = false;
    int[] age_id = {60, 61, 62, 63};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dialog = PD_Utility.showLoader(MainActivity.this);
        db = new DatabaseHandler(MainActivity.this);
        isInitialized = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.getObservable().addObserver(this);
        if (!isInitialized) {
            //Initializing the adapters
            name = getResources().getStringArray(R.array.main_contents);
            rv_browseAdapter = new RV_BrowseAdapter(this, this, name);
            rv_browse_contents.getViewTreeObserver().addOnPreDrawListener(preDrawListenerBrowse);
            //Defining the layouts for each recycler view
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rv_browse_contents.setLayoutManager(layoutManager);

            String[] tags = getResources().getStringArray(R.array.search_tags);
            for (int i = 0; i < tags.length; i++) {
                search_tags.add(tags[i]);
            }
            //inflating the browser content recycler view
            rv_browse_contents.setAdapter(rv_browseAdapter);
            isInitialized = true;
        }
    }

    @Override
    public void browserButtonClicked(final int position) {
        rv_browseAdapter.setSelectedIndex(position);
        showDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new PD_ApiRequest(MainActivity.this, MainActivity.this).getDataVolley("SEARCH",
                        PD_Constant.URL.BROWSE_BY_ID.toString() + age_id[position]);
            }
        }, 2000);
    }

    @Override
    public void contentButtonClicked(final int position, View holder) {
        showDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Modal_Level modal_level = new Modal_Level();
                modal_level.setName(arrayList_content.get(position).getNodetitle());
                modal_level.setId(arrayList_content.get(position).getNodeid());
                arrayList_level.add(modal_level);
                new PD_ApiRequest(MainActivity.this, MainActivity.this).getDataVolley("BROWSE",
                        PD_Constant.URL.BROWSE_BY_ID.toString() + arrayList_content.get(position).getNodeid());
            }
        }, 2000);
    }

    @Override
    public void levelButtonClicked(final int position) {
        showDialog();
        selected_level_position = position;
        if (PD_Utility.isInternetAvailable(MainActivity.this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new PD_ApiRequest(MainActivity.this, MainActivity.this).getDataVolley("LEVEL",
                            PD_Constant.URL.BROWSE_BY_ID.toString() + arrayList_level.get(position).getId());
                }
            }, 2000);
        }
    }

    private void showDialog() {
        if (dialog == null)
            dialog = PD_Utility.showLoader(MainActivity.this);
        dialog.show();
    }

    @Override
    public void downloadClick(final int position, final View holder) {
        if (!isPermissionsGranted(MainActivity.this, new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                PermissionUtils.Manifest_READ_EXTERNAL_STORAGE})) {
            askCompactPermissions(new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE,
                    PermissionUtils.Manifest_READ_EXTERNAL_STORAGE}, new PermissionResult() {
                @Override
                public void permissionGranted() {
                    if (!isDownloading) {
                        rv_contentAdapter.setSelectedIndex(position, null);
                        if (PD_Utility.isInternetAvailable(getApplicationContext())) {
                            new PD_ApiRequest(MainActivity.this, MainActivity.this).getDataVolley("DOWNLOAD",
                                    PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + arrayList_content.get(position).getNodeid());
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void permissionDenied() {
                }

                @Override
                public void permissionForeverDenied() {
                    Toast.makeText(MainActivity.this, R.string.provide_storage_permission, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            rv_contentAdapter.setSelectedIndex(position, null);
            if (PD_Utility.isInternetAvailable(getApplicationContext())) {
                new PD_ApiRequest(MainActivity.this, MainActivity.this).getDataVolley("DOWNLOAD",
                        PD_Constant.URL.DOWNLOAD_RESOURCE.toString() + arrayList_content.get(position).getNodeid());
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void downloadComplete(int position) {
        isDownloading = false;
        arrayList_content.remove(position);
        rv_contentAdapter.notifyItemRemoved(position);
        rv_contentAdapter.notifyItemRangeChanged(position, arrayList_content.size());
        rv_contentAdapter.updateData(arrayList_content);
    }


    ViewTreeObserver.OnPreDrawListener preDrawListenerBrowse = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            rv_browse_contents.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < rv_browse_contents.getChildCount(); i++) {
                View view = rv_browse_contents.getChildAt(i);
                view.animate().cancel();
                view.setTranslationY(100);
                view.setAlpha(0);
                view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(i * 50);
            }
            return true;
        }
    };
    ViewTreeObserver.OnPreDrawListener preDrawListenerContent = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            rv_contents.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < rv_contents.getChildCount(); i++) {
                View view = rv_contents.getChildAt(i);
                view.animate().cancel();
                view.setTranslationY(100);
                view.setAlpha(0);
                view.animate().alpha(1.0f).translationY(0).setDuration(300).setStartDelay(i * 50);
            }
            return true;
        }
    };
    ViewTreeObserver.OnPreDrawListener preDrawListenerLevel = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            rv_level.getViewTreeObserver().removeOnPreDrawListener(this);
            for (int i = 0; i < rv_level.getChildCount(); i++) {
                View view = rv_level.getChildAt(i);
                view.animate().cancel();
                view.setTranslationX(-100);
                view.setAlpha(0);
                view.animate().alpha(1.0f).translationX(0).setDuration(300).setStartDelay(i * 50);
            }
            return true;
        }
    };


    @OnClick(R.id.img_content_search)
    public void reveal() {
        // get the center for the clipping circle
        int cx = (/*root_search.getLeft() + */root_search.getRight()) /*/ 2*/;
        int cy = (root_search.getTop() /*+ root_search.getBottom()*/) /*/ 2*/;

        // get the final radius for the clipping circle
        int dx = Math.max(cx, root_search.getWidth() - cx);
        int dy = Math.max(cy, root_search.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        // Android native animator
        Animator animator =
                ViewAnimationUtils.createCircularReveal(root_search, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(400);
        img_content_search.setVisibility(View.INVISIBLE);
        root_search.setVisibility(View.VISIBLE);
        animator.start();
        search_chipcloud.removeAllViews();
        for (int i = 0; i < search_tags.size(); i++) {
            search_chipcloud.addChip(search_tags.get(i));
        }
        search_chipcloud.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int index) {
                et_edit_address.setText(search_tags.get(index));
            }

            @Override
            public void chipDeselected(int index) {

            }
        });
    }

    @OnClick(R.id.img_search)
    public void search() {
        if (et_edit_address.getText().toString().length() > 0 &&
                PD_Utility.isInternetAvailable(getApplicationContext())) {
            showDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new PD_ApiRequest(MainActivity.this, MainActivity.this).getDataVolley("SEARCH",
                            PD_Constant.URL.SEARCH_BY_KEYWORD.toString() + "lang=Hindi&keyw=" + et_edit_address.getText().toString());
                }
            }, 2000);
        }
    }

    public void closeReveal() {
        final ViewGroup parent = (ViewGroup) root_search.getParent();
        final Rect bounds = new Rect();
        final Rect maskBounds = new Rect();
        img_content_search.getDrawingRect(bounds);
        root_search.getDrawingRect(maskBounds);
//        parent.offsetDescendantRectToMyCoords(img_content_search, bounds);
        parent.offsetDescendantRectToMyCoords(root_search, maskBounds);

        final int cX = maskBounds.right;
        final int cY = maskBounds.top;

        final Animator circularReveal = ViewAnimationUtils.createCircularReveal(root_search, cX, cY,
                (float) Math.hypot(maskBounds.width(), maskBounds.height()),
                img_content_search.getWidth() / 2f, View.LAYER_TYPE_HARDWARE);
        final float c0X = bounds.centerX() - maskBounds.centerX();
        final float c0Y = bounds.centerY() - maskBounds.centerY();
        AnimatorPath path = new AnimatorPath();
        path.moveTo(0, 0);
        path.curveTo(0, 0, 0, c0Y, c0X, c0Y);
        ObjectAnimator pathAnimator = ObjectAnimator.ofObject(this, "maskLocation", new PathEvaluator(),
                path.getPoints().toArray());

        AnimatorSet set = new AnimatorSet();
        set.playTogether(circularReveal, pathAnimator);
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.setDuration(400);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                root_search.setVisibility(View.INVISIBLE);
                img_content_search.setVisibility(View.VISIBLE);
                img_content_search.setEnabled(true);
            }
        });
        set.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onProgressUpdate(int progress) {
        isDownloading = true;
        rv_contentAdapter.setProgress(progress);
    }

    @Override
    public void lengthOfTheFile(int length) {
        Log.d("lenghtOfFile::", length + "");
    }

    @Override
    public void onZipDownloaded(boolean isDownloaded) {
        if (isDownloaded) {
            for (int i = 0; i < download_content.getNodelist().size(); i++) {
                String fileName = download_content.getNodelist().get(i).getNodeserverimage()
                        .substring(download_content.getNodelist().get(i).getNodeserverimage().lastIndexOf('/') + 1);
                new ImageDownload(MainActivity.this, fileName).execute(download_content.getNodelist().get(i).getNodeserverimage());
            }
        }
        addContentToDatabase(download_content);
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
    public void notifySuccess(String requestType, String response) {
        try {
            Log.d("response:::", response);
            Log.d("response:::", "requestType:: " + requestType);
            Gson gson = new Gson();
            if (requestType.equalsIgnoreCase("SEARCH")) {
                arrayList_content.clear();
                if (!search_tags.contains(et_edit_address.getText().toString())) {
                    search_tags.add(et_edit_address.getText().toString());
                }
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
                if (rv_contentAdapter == null) {
                    LinearLayoutManager layoutManager2 = new GridLayoutManager(this, 3);
                    rv_contents.setLayoutManager(layoutManager2);
                    rv_contentAdapter = new RV_ContentAdapter(this, this, arrayList_content);
                    rv_contents.getViewTreeObserver().addOnPreDrawListener(preDrawListenerContent);
                    rv_contents.setAdapter(rv_contentAdapter);
                } else {
                    rv_contentAdapter.updateData(arrayList_content);
                }

                //deselect selected content of browse adapter
                rv_browseAdapter.setSelectedIndex(-1);
            } else if (requestType.equalsIgnoreCase("BROWSE")) {
                arrayList_content.clear();
                Type listType = new TypeToken<ArrayList<Modal_ContentDetail>>() {
                }.getType();
                arrayList_content = gson.fromJson(response, listType);
                PD_Utility.DEBUG_LOG(1, TAG, "content_length:::" + arrayList_content.size());
                rv_contentAdapter.notifyDataSetChanged();
                //inflating the level recycler view
                //Negative margin!----for overlapping
                if (rv_levelAdapter == null) {
//                    itemDecorator = new ItemDecorator(-18);
//                    rv_level.addItemDecoration(itemDecorator);
                    LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                    rv_level.setLayoutManager(layoutManager3);
                    rv_level.getViewTreeObserver().addOnPreDrawListener(preDrawListenerLevel);
                    rv_levelAdapter = new RV_LevelAdapter(this, this, arrayList_level);
                    rv_level.setAdapter(rv_levelAdapter);
                } else {
                    rv_levelAdapter.notifyDataSetChanged();
                }
            } else if (requestType.equalsIgnoreCase("LEVEL")) {
                Type listType = new TypeToken<ArrayList<Modal_ContentDetail>>() {
                }.getType();
                arrayList_content = gson.fromJson(response, listType);
                PD_Utility.DEBUG_LOG(1, TAG, "content_length:::" + arrayList_content.size());
                rv_contentAdapter.notifyDataSetChanged();
                arrayList_level.subList(selected_level_position, arrayList_level.size()).clear();
                rv_levelAdapter.notifyDataSetChanged();
            } else if (requestType.equalsIgnoreCase("DOWNLOAD")) {
                JSONObject jsonObject = new JSONObject(response);
                download_content = gson.fromJson(jsonObject.toString(), Modal_DownloadContent.class);
                PD_Utility.DEBUG_LOG(1, TAG, "nodelist_length:::" + download_content.getNodelist().size());
                PD_Utility.DEBUG_LOG(1, TAG, "foldername:::" + download_content.getFoldername());
                String fileName = download_content.getDownloadurl().substring(download_content.getDownloadurl().lastIndexOf('/') + 1);
                PD_Utility.DEBUG_LOG(1, TAG, "filename:::" + fileName);
                if (download_content.getDownloadurl().length() > 0) {
                    new ZipDownloader(MainActivity.this, MainActivity.this, download_content.getDownloadurl(),
                            download_content.getFoldername(), fileName, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
            closeReveal();
        }
    }

    @Override
    public void notifyError(String requestType, VolleyError error) {
        try {
            Log.d("response:::", "requestType:: " + requestType);
            Log.d("error:::", error.toString());
            if (requestType.equalsIgnoreCase("LEVEL") || requestType.equalsIgnoreCase("BROWSE")) {
                arrayList_level.remove(arrayList_level.size() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
            closeReveal();
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        Log.d("update:::", "called");
        if (!PD_Utility.isInternetAvailable(MainActivity.this)) {
            root_content.setVisibility(View.GONE);
            root_search.setVisibility(View.GONE);
            rl_main_not_connected.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                AnimatedVectorDrawable avd = (AnimatedVectorDrawable)
                        getDrawable(R.drawable.avd_no_connection);
                img_main_no_connection.setImageDrawable(avd);
            } else {
//                img_main_no_connection.setImageResource(R.drawable.ic_no_connection_fix_wrapped);
            }
            Drawable animation = img_main_no_connection.getDrawable();
            if (animation instanceof Animatable) {
                ((Animatable) animation).start();
            }
        } else {
            root_content.setVisibility(View.VISIBLE);
            root_search.setVisibility(View.INVISIBLE);
            rl_main_not_connected.setVisibility(View.GONE);
            onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        NetworkChangeReceiver.getObservable().deleteObserver(this);
    }
}