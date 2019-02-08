package com.eleganz.msafiri.model;

import ir.mirrajabi.searchdialog.core.Searchable;

/**
 * Created by eleganz on 7/2/19.
 */

public class ContactModel  implements Searchable {
    private String mName;
    private int mImageUrl;

    public ContactModel(String name, int imageUrl) {
        mName = name;
        mImageUrl = imageUrl;
    }

    @Override
    public String getTitle() {
        return mName;
    }

    public String getName() {
        return mName;
    }

    public ContactModel setName(String name) {
        mName = name;
        return this;
    }

    public int getImageUrl() {
        return mImageUrl;
    }

    public ContactModel setImageUrl(int imageUrl) {
        mImageUrl = imageUrl;
        return this;
    }
}
