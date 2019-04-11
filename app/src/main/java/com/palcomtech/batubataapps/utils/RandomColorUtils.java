package com.palcomtech.batubataapps.utils;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.Random;

public abstract class RandomColorUtils {

    static final String[] mColors = {
            "#39add1", // light blue
            "#3079ab", // dark blue
            "#c25975", // mauve
            "#e15258", // red
            "#f9845b", // orange
            "#838cc7", // lavender
            "#7d669e", // purple
            "#53bbb4", // aqua
            "#51b46d", // green
            "#e0ab18", // mustard
            "#637a91", // dark gray
            "#f092b0", // pink
            "#b7c0c7"  // light gray
    };

    public static Drawable setDrawText(String text) {
        String charText = text.substring(0,1);
        return TextDrawable.builder().buildRound(charText,getColor());
    }

    static int getColor() {
        String color;
        int colorAsInt;

        Random randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(mColors.length);

        color = mColors[randomNumber];
        colorAsInt = Color.parseColor(color);
        return colorAsInt;
    }
}
