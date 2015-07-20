package com.example.larkinmcmahon.geogoals;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class GoalListFragment extends Fragment {

    public GoalListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] goal_array = {
                "Gym - 3 times",
                "Class - Every day"
        };

        List<String> goals = new ArrayList<String>(Arrays.asList(goal_array));

        ArrayAdapter<String> goal_adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_goal,
                R.id.list_item_goal_textview,
                goals
        );

        View rootView = inflater.inflate(R.layout.fragment_goal_list, container, false);

        ListView goal_list = (ListView) rootView.findViewById(R.id.fragment_goal_listview);

        goal_list.setAdapter(goal_adapter);

        return rootView;

    }
}
