package com.chaitanyav.anywherenotes;

import android.content.Context;
import android.util.DisplayMetrics;

public class Globals {
    public static DatabaseHelper dbHelper;
    public static NoteListFragment noteListFragment;
    public static NoteFragment noteFragment;
    public static NoteDeleteFragment noteDeleteFragment;

    public static int dpToPx(int dp,Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(int px,Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
