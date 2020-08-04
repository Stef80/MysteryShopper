package com.example.misteryshopper.activity.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.misteryshopper.R;
import com.example.misteryshopper.activity.MapsActivity;
import com.example.misteryshopper.activity.ShopperProfileActivity;
import com.example.misteryshopper.datbase.DBHelper;
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper;
import com.example.misteryshopper.models.HiringModel;
import com.example.misteryshopper.models.StoreModel;
import com.example.misteryshopper.utils.RecyclerViewConfig;
import com.example.misteryshopper.utils.SharedPrefConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListHiringFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListHiringFragment extends Fragment implements RecyclerViewConfig.OnItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_COLUMN_COUNT = "column-count";
    private DBHelper mDBHelper = FirebaseDBHelper.getInstance();
    private int column = 1;
    private SharedPrefConfig prefConfig;
    private List<HiringModel> hiringModelList;

    public ListHiringFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListHiringFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListHiringFragment newInstance(int columnConut) {
        ListHiringFragment fragment = new ListHiringFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnConut);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            column = getArguments().getInt(ARG_COLUMN_COUNT);
             prefConfig=  new SharedPrefConfig(getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String mail = prefConfig.readLoggedUser().getEmail();
        View view = inflater.inflate(R.layout.fragment_list_hiring, container, false);
        TextView listEmpty = view.findViewById(R.id.emptyState_hiring_list);
        RecyclerView recyclerView = view.findViewById(R.id.list_hires);
        mDBHelper.getHireByMail(mail, new DBHelper.DataStatus() {
            @Override
            public void dataIsLoaded(List<?> obj, List<String> keys) {
                double amount = 0.0;
                if(obj.isEmpty()) {
                    listEmpty.setVisibility(View.VISIBLE);
                }else{
                   hiringModelList = (List<HiringModel>)obj;
                  amount =  setTotalAmount(hiringModelList);
                    Collections.sort(hiringModelList);
                    new RecyclerViewConfig(null).setConfigList(recyclerView, container.getContext(), obj,
                            keys, ListHiringFragment.this);
                    listEmpty.setVisibility(View.GONE);
                }
                mDBHelper.setTotalForUserId(prefConfig.readLoggedUser().getId(), amount);
            }
        });
     return view;
    }

    public double setTotalAmount(List<HiringModel> modelList){
        double totalAmount = 0.0;
        for (HiringModel model:modelList) {
            if(model.getAccepted() != null) {
                if (model.getAccepted().equals("accepted")) {
                    totalAmount += model.getFee();
                }
            }
        }
      return totalAmount;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), MapsActivity.class);
        intent.putExtra("address",hiringModelList.get(position).getAddress());
        startActivity(intent);
    }
}