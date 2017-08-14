package com.pratham.prathamdigital.content_playing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.RadioGroup;

import java.io.File;


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
    static TextToSp tts;
    public String gamePath;


    JSInterface(Context c, WebView w, String gamePath) {
        mContext = c;
        tts = new TextToSp(mContext);
        tts.ttsFunction("Welcome kids","eng");
        this.gamePath = gamePath;
        createRecordingFolder();


/*
        this.presentStudents = presentStudents;
        this.SessionId = SessionId;
*/
        mp = new MediaPlayer();
        this.w = w;
        VideoFlag = 0;
    }

    private void createRecordingFolder() {
        File file = mContext.getDir("PrathamRecordings", Context.MODE_PRIVATE);
        if (!file.exists()) {
            file.mkdirs();
        }
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
        return gamePath ;
    }

    // Ketan
    @JavascriptInterface
    public void startRecording(String recName) {
        try {
            recordAudio = new Audio(recName);
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
            mp.stop();
            mp.reset();
            if (tts.textToSpeech.isSpeaking()) {
                tts.stopSpeakerDuringJS();
            }
            String path = "";
            audioFlag = true;
            flag = 0;
            String mp3File;

//              path="/storage/sdcard1/.PrathamHindi/salana baal-katai divas/";
            try {
                if (storyName != null) {
                    mp3File = "storyGame/Raw/" + storyName + "/" + filename;
                } else {
                    mp3File = "/storage/sdcard0/.POSinternal/recordings" + filename;
                }
                if (filename.charAt(0) == '/') {
                    path = "/storage/sdcard0/.POSinternal/recordings" + filename;//check for recording game and then change
                    mp.setDataSource(path);
                } else {
                    mp.setDataSource(path);
                }
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
/*
                log.error("Exception occurred at : " + e.getMessage());
                showAlert.showDialogue(mContext, "Problem occurred in audio player. Please contact your administrator.");
                SyncActivityLogs syncActivityLogs = new SyncActivityLogs(mContext);
                syncActivityLogs.addToDB("audioPlayer-JSInterface", e, "Error");
                BackupDatabase.backup(mContext);
*/
                e.printStackTrace();
            }
        } catch (Exception e) {
/*
            log.error("Exception occurred at : " + e.getMessage());
*/
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
            File file = new File(gamePath);//ContentScreen.fpath + filename

            if (file.exists()) {
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent(mContext, VideoPlayer.class);
                intent.putExtra("path", path.toString());
                MediaFlag = true;
                ((Activity) mContext).startActivityForResult(intent, 1);
            } else {
            }
        } catch (Exception e) {

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
        if (tts.textToSpeech.isSpeaking()) {
            tts.stopSpeakerDuringJS();
        }
        if (ttsLanguage == null) {
            tts.ttsFunction(theWordWasAndYouSaid, "eng");
        }
        if (ttsLanguage.equals("eng") || ttsLanguage.equals("hin")) {
            tts.ttsFunction(theWordWasAndYouSaid, ttsLanguage);
        }
    }

    @JavascriptInterface
    public void stopTts() {
        tts.stopSpeakerDuringJS();
    }

    @JavascriptInterface
    public void playTts(final String toSpeak) {
        tts.ttsFunction(toSpeak, "eng");
    }

    @JavascriptInterface
    public static boolean informCompletion() {
        return completeFlag;
    }

    public static void stopTtsBackground() {
        tts.stopSpeakerDuringJS();
    }
}