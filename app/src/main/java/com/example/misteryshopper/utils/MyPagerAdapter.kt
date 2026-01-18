package com.example.misteryshopper.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.misteryshopper.activity.fragments.ListHiringFragment;
import com.example.misteryshopper.activity.fragments.ShopperProfileFragment;
import com.example.misteryshopper.models.ShopperModel;

public class MyPagerAdapter extends FragmentPagerAdapter {

    public static int NUM_ITEMS = 2;
    private ShopperModel model;

    public MyPagerAdapter(@NonNull FragmentManager fm, int behavior , ShopperModel model) {
        super(fm, behavior);
        this.model = model;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return ShopperProfileFragment.newInstance(model);
            case 1:
                return ListHiringFragment.newInstance(1);
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
