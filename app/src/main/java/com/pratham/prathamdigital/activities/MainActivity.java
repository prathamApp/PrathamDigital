package com.pratham.prathamdigital.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.adapters.RV_BrowseAdapter;
import com.pratham.prathamdigital.adapters.RV_ContentAdapter;
import com.pratham.prathamdigital.adapters.RV_LevelAdapter;
import com.pratham.prathamdigital.async.PD_ApiRequest;
import com.pratham.prathamdigital.async.ZipDownloader;
import com.pratham.prathamdigital.custom.ItemDecorator;
import com.pratham.prathamdigital.custom.chips.ChipCloud;
import com.pratham.prathamdigital.custom.chips.ChipListener;
import com.pratham.prathamdigital.custom.reveal.AnimatorPath;
import com.pratham.prathamdigital.custom.reveal.PathEvaluator;
import com.pratham.prathamdigital.custom.reveal.ViewAnimationUtils;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;
import com.pratham.prathamdigital.interfaces.ProgressUpdate;
import com.pratham.prathamdigital.interfaces.VolleyResult_JSON;
import com.pratham.prathamdigital.models.Modal_ContentDetail;
import com.pratham.prathamdigital.models.Modal_Level;
import com.pratham.prathamdigital.util.PD_Constant;
import com.pratham.prathamdigital.util.PD_Utility;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainActivityAdapterListeners, ProgressUpdate, VolleyResult_JSON {

    @BindView(R.id.rv_browse_contents)
    RecyclerView rv_browse_contents;
    @BindView(R.id.rv_contents)
    RecyclerView rv_contents;
    @BindView(R.id.rv_level)
    RecyclerView rv_level;
    @BindView(R.id.root_search)
    ConstraintLayout root_search;
    @BindView(R.id.img_content_search)
    ImageView img_content_search;
    @BindView(R.id.search_chipcloud)
    ChipCloud search_chipcloud;
    @BindView(R.id.et_edit_address)
    EditText et_edit_address;

    RV_BrowseAdapter rv_browseAdapter;
    RV_ContentAdapter rv_contentAdapter;
    RV_LevelAdapter rv_levelAdapter;
    String[] name = {"khelbadi", "goodmorning", "hello", "games", "maths", "english"};
    String[] sub_content = {"khelbadi1", "goodmorning1", "hello1", "games1", "maths1", "english1",
            "khelbadi2", "goodmorning2", "hello2", "game2", "maths2", "english2"};
    private ArrayList<String> search_tags = new ArrayList<>();
    private ArrayList<Modal_Level> arrayList_level = new ArrayList<>();
    private ArrayList<Modal_ContentDetail> arrayList_content = new ArrayList<>();
    private ItemDecorator itemDecorator;
    private boolean isInitialized;
    private int progress = 0;
    private AlertDialog dialog = null;
    private String TAG = MainActivity.class.getSimpleName();
    private int selected_level_position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dialog = PD_Utility.showLoader(MainActivity.this);
        isInitialized = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isInitialized) {
            //Initializing the adapters
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
    public void browserButtonClicked(int position) {
        rv_browseAdapter.setSelectedIndex(position);
    }

    @Override
    public void contentButtonClicked(final int position) {
        dialog.show();
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
        dialog.show();
        selected_level_position = position;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new PD_ApiRequest(MainActivity.this, MainActivity.this).getDataVolley("LEVEL",
                        PD_Constant.URL.BROWSE_BY_ID.toString() + arrayList_level.get(position).getId());
            }
        }, 2000);
    }

    private void showDialog() {
    }

    @Override
    public void downloadClick(int position, RV_ContentAdapter.ViewHolder holder) {
        rv_contentAdapter.setSelectedIndex(position, holder);
        if (PD_Utility.isInternetAvailable(getApplicationContext())) {
            new ZipDownloader(this);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
//        showProgressOnNotification(position);
    }

    private void showProgressOnNotification(final int position) {
        // configure the intent
        Intent intent = new Intent(this, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        // configure the notification
        final Notification notification = new Notification(R.drawable.ic_download_icon, "Downloading your file", System
                .currentTimeMillis());
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notification.contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.download_progress);
        notification.contentIntent = pendingIntent;
        notification.contentView.setImageViewResource(R.id.status_icon, R.drawable.ic_search);
        notification.contentView.setTextViewText(R.id.status_text, "Downloading....");
        notification.contentView.setProgressBar(R.id.status_progress, 100, progress, false);
        final NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(
                getApplicationContext().NOTIFICATION_SERVICE);

        notificationManager.notify(position, notification);
        // simulate progress
        Thread download = new Thread() {
            @Override
            public void run() {
                for (int i = 1; i < 100; i++) {
                    progress++;
                    notification.contentView.setProgressBar(R.id.status_progress, 100, progress, false);
                    // inform the progress bar of updates in progress
                    notificationManager.notify(position, notification);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                // remove the notification (we're done)
                notificationManager.cancel(position);
            }
        };
        download.run();
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
            dialog.show();
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
        rv_contentAdapter.setProgress(progress);
    }

    @Override
    public void lengthOfTheFile(int length) {
        Log.d("lenghtOfFile::", length + "");
    }

    @Override
    public void notifySuccess(String requestType, String response) {
        try {
            Log.d("response:::", response);
            Log.d("response:::", "requestType:: " + requestType);
            Gson gson = new Gson();
            if (requestType.equalsIgnoreCase("SEARCH")) {
                if (!search_tags.contains(et_edit_address.getText().toString())) {
                    search_tags.add(et_edit_address.getText().toString());
                }
                Type listType = new TypeToken<ArrayList<Modal_ContentDetail>>() {
                }.getType();
                arrayList_content = gson.fromJson(response, listType);
                PD_Utility.DEBUG_LOG(1, TAG, "content_length:::" + arrayList_content.size());

                //inflating the content recycler view
                LinearLayoutManager layoutManager2 = new GridLayoutManager(this, 3);
                rv_contents.setLayoutManager(layoutManager2);
                rv_contentAdapter = new RV_ContentAdapter(this, this, arrayList_content);
                rv_contents.getViewTreeObserver().addOnPreDrawListener(preDrawListenerContent);
                rv_contents.setAdapter(rv_contentAdapter);

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
                    itemDecorator = new ItemDecorator(-18);
                    LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                    rv_level.addItemDecoration(itemDecorator);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
            closeReveal();
        }
//        JSONObject jsonObject = new JSONObject(response);
//        Modal_Order order_item = gson.fromJson(jsonObject.toString(), Modal_Order.class);
    }

    @Override
    public void notifyError(String requestType, VolleyError error) {
        try {
            Log.d("error:::", error.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dialog != null) {
                dialog.dismiss();
            }
            closeReveal();
        }
    }
}