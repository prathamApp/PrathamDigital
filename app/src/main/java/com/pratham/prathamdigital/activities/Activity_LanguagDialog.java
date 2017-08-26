package com.pratham.prathamdigital.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.ArcMotion;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.custom.carousel.CarouselPicker;
import com.pratham.prathamdigital.custom.morphing.MorphDialogToFab;
import com.pratham.prathamdigital.custom.morphing.MorphFabToDialog;
import com.pratham.prathamdigital.custom.morphing.MorphTransition;
import com.pratham.prathamdigital.dbclasses.DatabaseHandler;
import com.pratham.prathamdigital.util.PD_Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HP on 17-08-2017.
 */

public class Activity_LanguagDialog extends AppCompatActivity {

    @BindView(R.id.card_language)
    ViewGroup card_language;
    @BindView(R.id.lang_picker)
    CarouselPicker lang_picker;
    @BindView(R.id.lang_okay)
    ImageView lang_okay;
    //    @BindView(R.id.lang_cancel)
//    TextView lang_cancel;
    @BindView(R.id.txt_language)
    TextView txt_language;

    List<CarouselPicker.PickerItem> languages = new ArrayList<>();
    String[] main_lang;
    int selected_lang = 0;
    private DatabaseHandler db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_dialog);
        ButterKnife.bind(this);
        db = new DatabaseHandler(this);
        setupEnterTransitions();
        String[] str = getResources().getStringArray(R.array.languages);
        main_lang = getResources().getStringArray(R.array.main_languages);
        for (int i = 0; i < str.length; i++) {
            languages.add(new CarouselPicker.TextItem(str[i], 0));
        }
        CarouselPicker.CarouselViewAdapter mixAdapter = new CarouselPicker.CarouselViewAdapter(this, languages, 0);
        lang_picker.setAdapter(mixAdapter);
        lang_picker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //position of the selected item
                Log.d("language_selected::", languages.get(position).getText());
                selected_lang = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        String lang = db.GetUserLanguage();
        for (int i = 0; i < main_lang.length; i++) {
            if (main_lang[i].equalsIgnoreCase(lang)) {
                lang_picker.setCurrentItem(i, true);
                return;
            }
        }
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

        if (card_language != null) {
            sharedEnter.addTarget(card_language);
            sharedReturn.addTarget(card_language);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    private void setupExitTransitions() {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);
        MorphTransition sharedEnter = new MorphTransition(ContextCompat.getColor(this, R.color.mustord_yellow),
                ContextCompat.getColor(this, R.color.dialog_background_color), 100, getResources().getDimensionPixelSize(R.dimen.size_5), true);
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphTransition sharedReturn = new MorphTransition(ContextCompat.getColor(this, R.color.dialog_background_color),
                ContextCompat.getColor(this, R.color.mustord_yellow), getResources().getDimensionPixelSize(R.dimen.size_5), 100, false);
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        if (card_language != null) {
            sharedEnter.addTarget(card_language);
            sharedReturn.addTarget(card_language);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @OnClick(R.id.lang_okay)
    public void setLang_okay() {
        Intent intent = new Intent();
        intent.putExtra(PD_Constant.LANGUAGE, main_lang[selected_lang]);
        setResult(Activity.RESULT_OK, intent);
        finishAfterTransition();
    }

//    @OnClick(R.id.lang_cancel)
//    public void setLang_Cancel() {
//        setResult(Activity.RESULT_CANCELED);
//        finishAfterTransition();
//    }
}
