package com.pratham.prathamdigital.content_playing;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.RadioGroup;

import com.pratham.prathamdigital.activities.Activity_Main;
import com.pratham.prathamdigital.interfaces.Interface_Score;

import java.io.File;
import java.util.Locale;


public class JSInterface extends Activity {
    static Context mContext;
    String path;
    Audio recordAudio;
    public static int groupId = 0;
    public int UserId = 1;
    MediaRecorder myAudioRecorder;
    public static MediaPlayer mp, mp2 = null;
    WebView w;
    RadioGroup radioGroup;
    public static int flag = 0, VideoFlag = 0;
    public static Boolean MediaFlag = false;
    static Boolean pdfFlag = false;
    static Boolean audioFlag = false;
    static Boolean trailerFlag = false;
    static Boolean completeFlag = false;
    public String gamePath;
    static TextToSp textToSp;
    //    private TextToSpeech textToSp;
    private String resId;
    private String audio_directory_path = "";
    private Interface_Score interface_score;


    JSInterface(Context c, WebView w, String gamePath, String resId, Interface_Score score) {
        mContext = c;
        this.interface_score = score;
        this.textToSp = Activity_Main.ttspeech;
        this.resId = resId;
//        ttsGreater21("Welcome kids", "eng");
        this.gamePath = gamePath;
        createRecordingFolder();
        mp = new MediaPlayer();
        this.w = w;
        VideoFlag = 0;
    }

    private void createRecordingFolder() {
        File file = mContext.getDir("PrathamRecordings", Context.MODE_PRIVATE);
        if (!file.exists()) {
            file.mkdirs();
        }
        audio_directory_path = file.getAbsolutePath();
    }

    @JavascriptInterface
    public void toggleVolume(String volume) {
        if (volume.equals("false"))
            mp.setVolume(0, 0);
        else {
            mp.setVolume(1, 1);
        }
    }

    @JavascriptInterface
    public String getLevel() {
        return "";
    }///*CardAdapter.nodeDesc*/null


    //Ketan
    @JavascriptInterface
    public String getMediaPath(String gameFolder) {
//        String path = "";
//        path = ContentScreen.fpath+"Media/"+gameFolder+"/";
        return gamePath;
    }

    // Ketan
    @JavascriptInterface
    public void startRecording(String recName) {
        try {
            recordAudio = new Audio(recName, audio_directory_path);
            recordAudio.start();
        } catch (Exception e) {
        }
    }

    @JavascriptInterface
    public void stopRecording() {
        audioFlag = false;
        try {
            recordAudio.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @JavascriptInterface
    public void getPath(String gameFolder) {
        path = gamePath + "/";
        w.post(new Runnable() {
            public void run() {
                String jsString = "javascript:Utils.setPath('" + path + "')";
                w.loadUrl(jsString);
            }
        });
    }


    /*@JavascriptInterface
    public void sendBackTojavascript() {
        w.post(new Runnable() {
            public void run() {
                String str1 = MainActivity.jsonstrOfNewVideos;
                String jsString = "javascript:loadNewJson('" + str1 + "')";
                w.loadUrl(jsString);
            }
        });

    }*/


    @JavascriptInterface
    public void showPdf(String filename, String resId) {
        try {
//            File file = new File(ContentScreen.fpath + filename);

//            if (file.exists()) {
//                Uri path = Uri.fromFile(file);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(path, "application/pdf");
//                try {
//                    ((Activity) mContext).startActivityForResult(intent, 1);
//                } catch (ActivityNotFoundException e) {
//                   }
//            } else {
//            }
        } catch (Exception e) {
        }
    }


    // Ketan's Code
    @JavascriptInterface
    public void audioPlayerForStory(String filename, String storyName) {
        try {
            mp = new MediaPlayer();

            if (mp.isPlaying())
                mp.stop();
            mp.reset();

            if (textToSp.textToSpeech.isSpeaking()) {
                textToSp.stopSpeakerDuringJS();
            }

            String path = "";
            audioFlag = true;
            flag = 0;
            String mp3File;

            try {
                if (storyName != null) {
                    File file = mContext.getDir(audio_directory_path + "/" + storyName, Context.MODE_PRIVATE);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    mp3File = audio_directory_path + "/" + storyName + "/" + filename;
                } else
                    mp3File = audio_directory_path + "/" + filename;

                if (filename.charAt(0) == '/') {
                    path = audio_directory_path + filename;//check for recording game and then change
                    mp.setDataSource(path);
                } else
                    mp.setDataSource(path);

                if (mp.isPlaying())
                    mp.stop();

                mp.prepare();
                mp.start();

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        audioFlag = false;
                        try {
                            w.post(new Runnable() {
                                @Override
                                public void run() {
                                    w.loadUrl("javascript:temp()");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
/*            log.error("Exception occurred at : " + e.getMessage());*/
        }
    }

    // Ketan's Code
    @JavascriptInterface
    public void audioPlayer(String filename) {
        try {
            String path = null;
            if (filename.charAt(0) == '/') {
                path = filename;//check for recording game and then change
            } else {
                //path="/storage/sdcard1/.prathamMarathi/"+filename;
//                path = ContentScreen.fpath+"Media" + filename;
                path = gamePath;
            }
            mp = new MediaPlayer();

            try {
                mp.setDataSource(path);
                mp.prepare();
                mp.start();

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.stop();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @JavascriptInterface
    public void audioPause() {
        if (MediaFlag == true) {
            mp.pause();
            MediaFlag = false;
        }
    }

    @JavascriptInterface
    public void audioResume() {
        if (MediaFlag == false) {
            mp.start();
            MediaFlag = true;
        }
        try {
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.reset();
                    MediaFlag = false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @JavascriptInterface
    public void stopAudioPlayer() {
        try {
            if (mp != null) {
                mp.stop();
                mp.reset();
                //mp = null;
            }
        } catch (Exception e) {
        }
    }

    @JavascriptInterface
    public void showVideo(String filename, String resId) {
        try {
            String vidPath = gamePath + filename;
            Intent intent = new Intent(mContext, VideoPlayer.class);
            intent.putExtra("path", vidPath);
            MediaFlag = true;
            ((Activity) mContext).startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void returnFunction() {
        try {
            pdfFlag = false;
            MediaFlag = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @JavascriptInterface
    public void playTts(String theWordWasAndYouSaid, String ttsLanguage) {
        mp.stop();
        mp.reset();
        if (textToSp.textToSpeech.isSpeaking()) {
            textToSp.stopSpeakerDuringJS();
        }
        if (ttsLanguage == null) {
            textToSp.ttsFunction(theWordWasAndYouSaid, "eng");
        }
        if (ttsLanguage.equals("eng") || ttsLanguage.equals("hin")) {
            textToSp.ttsFunction(theWordWasAndYouSaid, ttsLanguage);
        }
    }

    @JavascriptInterface
    public void stopTts() {
        textToSp.stopSpeakerDuringJS();
    }

    @JavascriptInterface
    public void playTts(final String toSpeak) {
        textToSp.ttsFunction(toSpeak, "eng");
    }

    @JavascriptInterface
    public static boolean informCompletion() {
        return completeFlag;
    }

    public void stopTtsBackground() {
        textToSp.stopSpeakerDuringJS();
    }

    @JavascriptInterface
    public void addScore(String tempResId, int questionId, int scorefromGame, int totalMarks, int level, String startTime) {
        tempResId = "";
        try {
            interface_score.setScore(scorefromGame, totalMarks);
//            Activity_WebView.scoredMarks += scorefromGame;
//            Activity_WebView.totalMarks += totalMarks;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}