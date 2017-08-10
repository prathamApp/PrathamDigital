package com.pratham.prathamdigital;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.pratham.prathamdigital.adapters.RV_BrowseAdapter;
import com.pratham.prathamdigital.adapters.RV_ContentAdapter;
import com.pratham.prathamdigital.adapters.RV_LevelAdapter;
import com.pratham.prathamdigital.custom.ItemDecorator;
import com.pratham.prathamdigital.custom.chips.ChipCloud;
import com.pratham.prathamdigital.custom.reveal.AnimatorPath;
import com.pratham.prathamdigital.custom.reveal.PathEvaluator;
import com.pratham.prathamdigital.custom.reveal.PathPoint;
import com.pratham.prathamdigital.custom.reveal.ViewAnimationUtils;
import com.pratham.prathamdigital.interfaces.MainActivityAdapterListeners;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainActivityAdapterListeners {

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

    RV_BrowseAdapter rv_browseAdapter;
    RV_ContentAdapter rv_contentAdapter;
    RV_LevelAdapter rv_levelAdapter;
    String[] name = {"khelbadi", "goodmorning", "hello", "games", "maths", "english"};
    String[] sub_content = {"khelbadi1", "goodmorning1", "hello1", "games1", "maths1", "english1",
            "khelbadi2", "goodmorning2", "hello2", "game2", "maths2", "english2"};
    String[] levels = {"level1", "level2", "level3", "level4"};
    private ItemDecorator itemDecorator;
    private boolean isInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        isInitialized = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isInitialized) {
            //Initializing the adapters
            rv_browseAdapter = new RV_BrowseAdapter(this, this, name);
            rv_contentAdapter = new RV_ContentAdapter(this, this, sub_content);
            rv_levelAdapter = new RV_LevelAdapter(this, this, levels);
            //Negative margin!----for overlapping
            itemDecorator = new ItemDecorator(-18);
            rv_browse_contents.getViewTreeObserver().addOnPreDrawListener(preDrawListenerBrowse);
            isInitialized = true;
        }
        //Defining the layouts for each recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_browse_contents.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManager2 = new GridLayoutManager(this, 3);
        rv_contents.setLayoutManager(layoutManager2);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_level.addItemDecoration(itemDecorator);
        rv_level.setLayoutManager(layoutManager3);

        //inflating the browser content recycler view
        rv_browse_contents.setAdapter(rv_browseAdapter);
    }

    @Override
    public void browserButtonClicked(int position) {
        rv_browseAdapter.setSelectedIndex(position);

        //inflating the content recycler view
        rv_contents.getViewTreeObserver().addOnPreDrawListener(preDrawListenerContent);
        rv_contents.setAdapter(rv_contentAdapter);
    }

    @Override
    public void contentButtonClicked(int position) {
//        rv_contentAdapter.setSelectedIndex(position);
//        rv_contentAdapter.notifyItemRemoved(4);

        //inflating the content recycler view
        rv_level.getViewTreeObserver().addOnPreDrawListener(preDrawListenerLevel);
        rv_level.setAdapter(rv_levelAdapter);
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
        search_chipcloud.addChips(sub_content);
    }

    @OnClick(R.id.img_search)
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
}