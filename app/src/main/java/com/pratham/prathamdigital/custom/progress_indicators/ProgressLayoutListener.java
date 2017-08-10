package com.pratham.prathamdigital.custom.progress_indicators;

/**
 * Created by HP on 10-08-2017.
 */

public interface ProgressLayoutListener {
    void onProgressCompleted();

    void onProgressChanged(int seconds);
}
