package com.wefly.wecollect.activity;

import android.content.Intent;
import android.os.Bundle;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.weflyagri.wecollect.R;

import java.util.ArrayList;
import java.util.List;

public class onboardActivity extends AhoyOnboarderActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create Cards for Onboard
        AhoyOnboarderCard databaseCard = new AhoyOnboarderCard("ReaTime Technology Database", "Partager des informations en temps reel", R.drawable.database);
        databaseCard.setBackgroundColor(R.color.black_transparent);
        databaseCard.setTitleColor(R.color.white);
        databaseCard.setDescriptionColor(R.color.grey_200);
        databaseCard.setTitleTextSize(dpToPixels(10, this));
        databaseCard.setDescriptionTextSize(dpToPixels(8, this));
        databaseCard.setIconLayoutParams(250, 250, 50, 50, 50, 50);

        AhoyOnboarderCard lastCard = new AhoyOnboarderCard("C'est parti !", "Vous Pouvez commencer", R.drawable.success);
        lastCard.setBackgroundColor(R.color.black_transparent);
        lastCard.setTitleColor(R.color.white);
        lastCard.setDescriptionColor(R.color.grey_200);
        lastCard.setTitleTextSize(dpToPixels(10, this));
        lastCard.setDescriptionTextSize(dpToPixels(8, this));
        lastCard.setIconLayoutParams(250, 250, 50, 50, 50, 50);

        //add cards to List
        List<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(databaseCard);
        pages.add(lastCard);
        setOnboardPages(pages);
        setGradientBackground();
    }

    @Override
    public void onFinishButtonPressed() {
        Intent splash = new Intent(this, SplashScreensActivity.class);
        startActivity(splash);
    }
}
