package com.mindcoders.phial.internal.share.attachment;

import android.graphics.Bitmap;
import android.view.Window;

import com.mindcoders.phial.Attacher;
import com.mindcoders.phial.PhialListener;
import com.mindcoders.phial.internal.PhialErrorHandler;
import com.mindcoders.phial.internal.util.CurrentActivityProvider;
import com.mindcoders.phial.internal.util.FileUtil;
import com.mindcoders.phial.internal.util.ScreenShotUtil;

import java.io.File;

/**
 * Created by rost on 10/22/17.
 */

public class ScreenShotAttacher implements Attacher, PhialListener {
    private final CurrentActivityProvider activityProvider;
    private final File targetFile;
    private final int quality;

    public ScreenShotAttacher(CurrentActivityProvider activityProvider, File targetFile, int quality) {
        this.activityProvider = activityProvider;
        this.targetFile = targetFile;
        this.quality = quality;
    }

    @Override
    public File provideAttachment() throws Exception {
        return targetFile;
    }

    @Override
    public void onDebugWindowShow() {
        try {
            final Window window = activityProvider.getActivity().getWindow();
            final Bitmap bitmap = ScreenShotUtil.takeScreenShot(window);
            FileUtil.saveBitmap(bitmap, targetFile, quality);
        } catch (Exception ex) {
            PhialErrorHandler.onError(ex);
        }
    }

    @Override
    public void onDebugWindowHide() {
        targetFile.delete();
    }
}