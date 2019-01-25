package com.eleganz.msafiri.utils;

import ir.mirrajabi.searchdialog.core.Searchable;

/**
 * Created by eleganz on 16/1/19.
 */

public class SampleSearchModel   implements Searchable {
    private String mTitle;

    public SampleSearchModel(String title) {
        mTitle = title;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public SampleSearchModel setTitle(String title) {
        mTitle = title;
        return this;
    }
}



