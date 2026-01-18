package com.example.misteryshopper.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.misteryshopper.activity.fragments.ListHiringFragment
import com.example.misteryshopper.activity.fragments.ShopperProfileFragment
import com.example.misteryshopper.models.ShopperModel

class MyPagerAdapter(fm: FragmentManager, behavior: Int, model: ShopperModel?) :
    FragmentPagerAdapter(fm, behavior) {
    private val model: ShopperModel?

    init {
        this.model = model
    }

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return ShopperProfileFragment.newInstance(model)
            1 -> return ListHiringFragment.newInstance(1)
            else -> return null
        }
    }

    override fun getCount(): Int {
        return NUM_ITEMS
    }

    companion object {
        var NUM_ITEMS: Int = 2
    }
}
