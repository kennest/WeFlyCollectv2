package com.wefly.wealert.utils;

import com.nightonke.boommenu.BoomButtons.HamButton;
import com.weflyagri.wealert.R;

public class BuilderManager {

    private static int[] imageResources = new int[]{
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login,
            R.drawable.ic_login
    };

    private static int imageResourceIndex = 0;

    public static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }



    public static HamButton.Builder getHamButtonBuilder(String text, String subText) {
        return new HamButton.Builder()
                .normalText(text)
                .subNormalText(subText);
    }



    private static BuilderManager ourInstance = new BuilderManager();

    public static BuilderManager getInstance() {
        return ourInstance;
    }

    private BuilderManager() {
    }
}
