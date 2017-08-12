package com.pratham.prathamdigital.content_playing;

import android.annotation.TargetApi;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

import static com.pratham.prathamdigital.content_playing.TextToSp.textToSpeech;

/**
 * Created by PEF-2 on 23/06/2017.
 */

// --- TEXT TO SPEECH ---

public class ttsInitListener implements TextToSpeech.OnInitListener  {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            /*textToSpeech.setLanguage(new Locale("hin-IN"));
            textToSpeech.setSpeechRate((float)0.5);
            textToSpeech.setPitch((float) 1);*/
            textToSpeech.setLanguage(new Locale("hi","IN"));
          /*  Voice required = voicesList.get(205);
            textToSpeech.setVoice(required);*/
            textToSpeech.setSpeechRate((float)0.6);
            textToSpeech.setPitch((float) 1);

        } else {
            textToSpeech = null;
            Toast.makeText(TextToSp.c, "Failed to initialize TTS engine.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
