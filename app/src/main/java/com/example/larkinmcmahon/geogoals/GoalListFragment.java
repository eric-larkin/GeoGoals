package com.example.larkinmcmahon.geogoals;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class GoalListFragment extends Fragment {
    private Context mContext;
    private List<Geofence> mGeofenceList;
    private List<String> mGoals;
    private ArrayAdapter<String> mGoalAdapter;

    public GoalListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();


        mGoals = new ArrayList<String>();

        mGeofenceList = ((GoalList) mContext).getGeofences();

        for(int i = 0; i < mGeofenceList.size(); i++){
            Geofence goal = mGeofenceList.get(i);
            mGoals.add(goal.getRequestId());
        }

        mGoalAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_goal,
                R.id.list_item_goal_textview,
                mGoals
        );

        Button addGoalButton = new Button(getActivity());

        addGoalButton.setText(R.string.new_goal_button);
        addGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GoalList)mContext).addGoal();
            }
        });

        View rootView = inflater.inflate(R.layout.fragment_goal_list, container, false);



        ListView goal_list = (ListView) rootView.findViewById(R.id.fragment_goal_listview);


        goal_list.addFooterView(addGoalButton);

        goal_list.setAdapter(mGoalAdapter);

        return rootView;

    }

    public void updateListView(List<Geofence> newGeofences){
        for(int i = 0; i < newGeofences.size(); i++){
            mGoals.add(newGeofences.get(i).getRequestId());
        }
        mGoalAdapter.notifyDataSetChanged();
    }
}
