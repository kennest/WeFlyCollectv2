package com.wefly.wecollect.activities;

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
        AhoyOnboarderCard welcomeCard = new AhoyOnboarderCard("Bienvenue sur WeFlyCollect", "Cette application sert à la collecte de données,un petit tour sur les fonctionnalités", R.drawable.ic_logo);
        welcomeCard.setBackgroundColor(R.color.black_transparent);
        welcomeCard.setTitleColor(R.color.white);
        welcomeCard.setDescriptionColor(R.color.grey_200);
        welcomeCard.setTitleTextSize(dpToPixels(10, this));
        welcomeCard.setDescriptionTextSize(dpToPixels(8, this));
        welcomeCard.setIconLayoutParams(250, 250, 50, 50, 50, 50);

        AhoyOnboarderCard databaseCard = new AhoyOnboarderCard("RealTime Technology Database", "Partager des informations en temps reel", R.drawable.database);
        databaseCard.setBackgroundColor(R.color.black_transparent);
        databaseCard.setTitleColor(R.color.white);
        databaseCard.setDescriptionColor(R.color.grey_200);
        databaseCard.setTitleTextSize(dpToPixels(10, this));
        databaseCard.setDescriptionTextSize(dpToPixels(8, this));
        databaseCard.setIconLayoutParams(250, 250, 50, 50, 50, 50);

        AhoyOnboarderCard emailCard = new AhoyOnboarderCard("Communication entre acteurs", "Gardez tout vos collaborateurs informer à l'aide de la messagerie intégré", R.drawable.email);
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
        pages.add(welcomeCard);
        pages.add(databaseCard);
        pages.add(emailCard);
        pages.add(lastCard);
        setOnboardPages(pages);
        setImageBackground(R.drawable.img_background);
    }

    @Override
    public void onFinishButtonPressed() {
        Intent splash = new Intent(this, SplashScreensActivity.class);
        startActivity(splash);
    }
}
