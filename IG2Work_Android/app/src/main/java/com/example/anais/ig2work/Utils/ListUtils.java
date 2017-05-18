package com.example.anais.ig2work.Utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * La classe ListUtils fournit des outils de gestion des ListView
 */
public class ListUtils {

    /*
    Permet de définir une hauteur dynamiue
     */
    public static void setDynamicHeight(ListView mListView) {

        ListAdapter mListAdapter = mListView.getAdapter();
        if (mListAdapter == null) {
            return; // Liste vide
        }

        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);

        for (int i = 0; i < mListAdapter.getCount(); i++) {
            View listItem = mListAdapter.getView(i, null, mListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }
}