package com.example.misteryshopper.activity.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.misteryshopper.R;
import com.example.misteryshopper.models.HiringModel;
import com.example.misteryshopper.models.ShopperModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopperProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopperProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private ShopperModel mParam1;
    private TextView name;
    private TextView surname;
    private TextView city;
    private TextView address;
    private TextView cf;
    private TextView email;


    public ShopperProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShopperProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShopperProfileFragment newInstance(ShopperModel model) {
        ShopperProfileFragment fragment = new ShopperProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, model);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (ShopperModel) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopper_profile, container, false);
        name = view.findViewById(R.id.profile_name_fragment);
        surname = view.findViewById(R.id.profile_surname_fragment);
        city = view.findViewById(R.id.profile_city_fragment);
        address = view.findViewById(R.id.profile_address_fragment);
        cf = view.findViewById(R.id.profile_cf_fragment);
        email = view.findViewById(R.id.profile_email_fragment);
        if(mParam1 != null){
            name.setText(mParam1.getName());
            surname.setText(mParam1.getSurname());
            city.setText(mParam1.getCity());
            address.setText(mParam1.getAddress());
            cf.setText(mParam1.getCf());
            email.setText(mParam1.getEmail());
        }
        return view;
    }
}