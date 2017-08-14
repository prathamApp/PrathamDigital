package com.pratham.prathamdigital.content_playing;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Abc on 27-Apr-17.
 */

public class TextToSp {
    public static TextToSpeech textToSpeech;
    public static TextToSpeech textToSpeechEng;
    static Context c;
    HashMap<String, String> map;

    public TextToSp(Context mContext) {
        super();
        c=mContext;
        try {
            textToSpeech=new TextToSpeech(c,new ttsInitListener());
            textToSpeech.setOnUtteranceProgressListener(new ttsUtteranceListener());
/*
            textToSpeechEng=new TextToSpeech(c,new ttsInitListenerEnglish());
            textToSpeechEng.setOnUtteranceProgressListener(new ttsUtteranceListener());
*/
        }
        catch ( Exception e){
            e.printStackTrace();
        }

    }

    public void ttsFunction(final String toSpeak, String lang){
        //textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(toSpeak,lang);
        } else {
            ttsUnder20(toSpeak,lang);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text, String lang) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");

        if(lang.equals("hin"))
            textToSpeech.setLanguage(new Locale("hi", "IN"));
        else
            textToSpeech.setLanguage(new Locale("en","IN"));

        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text, String lang) {
        String utteranceId=this.hashCode() + "";

        if(lang.equals("hin"))
            textToSpeech.setLanguage(new Locale("hi", "IN"));
        else
            textToSpeech.setLanguage(new Locale("en","IN"));

        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    public static void stopSpeaker() {
        if (textToSpeech != null) {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        }
/*
        if (textToSpeechEng.isSpeaking())
            textToSpeechEng.stop();
*/

    }

    public void stopSpeakerDuringJS() {
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

}