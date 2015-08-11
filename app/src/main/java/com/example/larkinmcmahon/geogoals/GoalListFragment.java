package com.example.larkinmcmahon.geogoals;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.view.ContextMenu.ContextMenuInfo;

import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class GoalListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private Context mContext;
    private List<Goal> mGoals;
    private List<String> mGoalStrings;
    private ArrayAdapter<String> mGoalAdapter;
    private Button mAddGoalButton;
    private String TAG = "GOAL_LIST_FRAGMENT";
    private GoalListCursorAdapter mGoalListAdapter;
    private static int LOADER_ID = 2;
    public static boolean mGoalSelected = false;

    private static final String[] GOAL_DETAIL_COLUMNS = {
            GoalDatabaseHelper.KEY_ID,
            GoalDatabaseHelper.KEY_GOALNAME,
            GoalDatabaseHelper.KEY_OCCURANCES,
            GoalDatabaseHelper.KEY_TIMEFRAME,
            GoalDatabaseHelper.KEY_COMMENTS,
            GoalDatabaseHelper.KEY_STARTDATE,
            GoalDatabaseHelper.KEY_STARTTIME,
            GoalDatabaseHelper.KEY_ENDDATE,
            GoalDatabaseHelper.KEY_ENDTIME,
    };

    private static final int COLUMN_ID = 0;

    public GoalListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();

       // mGoalStrings = new ArrayList<String>();

       // mGoals = ((GoalList) mContext).getGoals();

        //ArrayList<Goal> mListOfGoals = new ArrayList<Goal>();

//        for(int i = 0; i < mGoals.size(); i++){
//            Goal goal = mGoals.get(i);
//            mGoalStrings.add(goal.getTitle());
//            mListOfGoals.add(goal);
//        }

//        mGoalAdapter = new ArrayAdapter<String>(
//                getActivity(),
//                R.layout.list_item_goal,
//                R.id.list_item_goal_textview,
//                mGoalStrings
//        );

        mAddGoalButton = new Button(getActivity());

        mAddGoalButton.setText(R.string.new_goal_button);
        mAddGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GoalList) getActivity()).addGoalButtonClick();
            }
        });

        checkForIncomingIntents();
        final View rootView = inflater.inflate(R.layout.fragment_goal_list, container, false);

        ListView goal_list = (ListView) rootView.findViewById(R.id.fragment_goal_listview);

        goal_list.addFooterView(mAddGoalButton);


        //NOTIFICATION CODE
        // Prepare intent which is triggered if the notification is selected
        Intent intent = new Intent(getActivity(), GeoFenceDetectedActivity.class)
            .putExtra("dbID", 0); //this is set to 0 for now, should be whatever the id for the goal detected is
        PendingIntent pIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        // Build notification
        Notification noti = new Notification.Builder(getActivity())
                .setContentTitle("Goal Location Detected")
                .setContentText("If this is incorrect, please press the button below.").setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
//                .addAction(R.mipmap.ic_launcher, "Call", pIntent)
                .addAction(R.mipmap.ic_launcher, "Incorrect", pIntent).build();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);

        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);




        mGoalListAdapter = new GoalListCursorAdapter(getActivity(),null,0);
        goal_list.setAdapter(mGoalListAdapter);
        //goal_list.setAdapter(new MainCustomListAdapter(mContext, mListOfGoals));
        goal_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mGoalSelected = true;
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                int selectedID = cursor.getInt(COLUMN_ID);
                GoalDetailFragment detailFragment = (GoalDetailFragment) getFragmentManager().findFragmentById(R.id.fragment_goal_detail);
                if(detailFragment == null) { //handset layout
                    Intent intent = new Intent(getActivity(), GoalDetail.class)
                            .putExtra("dbID", selectedID);
                    startActivity(intent);
                }
                else { //tablet layout
                    Bundle args = new Bundle();
                    args.putInt("dbid",selectedID);

                    GoalDetailFragment newFragment = new GoalDetailFragment();
                    newFragment.setArguments(args);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_goal_detail, newFragment, "GOALDETAILFRAGMENT")
                            .commit();
                }
            }
        });
        registerForContextMenu(goal_list);
        return rootView;
    }

    public void checkForIncomingIntents() {
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("deleting")) {
            int deleteVal = intent.getIntExtra("deleting",-1);
            if(deleteVal == 1){
                Toast.makeText(getActivity(),
                        "Goal Deleted Successfully", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getActivity(),
                        "Goal Not Deleted", Toast.LENGTH_LONG).show();
            }
        }
    }
    public int getIdFromTitle(String title) {
        for(int a = 0; a < mGoals.size(); a++){
            Goal currentGoal = mGoals.get(a);
            if(currentGoal.getTitle() == title) {
                return currentGoal.getID();
            }
        }
        return -1;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.fragment_goal_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(mGoalStrings.get(info.position));
            String[] menuItems = getResources().getStringArray(R.array.activity_main_context_menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Intent intent;
        int dbid;
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.activity_main_context_menu);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = mGoalStrings.get(info.position);
        switch(menuItemName) {
            case "Edit":
                dbid = getIdFromTitle(listItemName);
                intent = new Intent(getActivity(), GoalEdit.class)
                        .putExtra("dbid", dbid);
                startActivity(intent);
                break;
            case "Delete":
                dbid = getIdFromTitle(listItemName);
                intent = new Intent(getActivity(), GoalDelete.class)
                        .putExtra("dbid", dbid);
                startActivity(intent);
                break;
        }
//        TextView text = (TextView)findViewById(R.id.footer);
//        text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
       // mGoals = ((GoalList) mContext).getGoals();
      //  updateListView(mGoals);
    }

    public void updateListView(List<Goal> newGoals){
        Log.i(TAG, "updating List View");
        for(int i = 0; i < mGoalStrings.size(); i++){
            mGoalStrings.remove(0);
        }
        for(int a = 0; a < newGoals.size(); a++){
            mGoalStrings.add(newGoals.get(a).getTitle());
        }
        mGoalAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Intent intent = getActivity().getIntent();
        return new CursorLoader(getActivity(),
                GoalsProvider.CONTENT_URI,
                GOAL_DETAIL_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mGoalListAdapter.swapCursor(data);
        //TODO: Add in saveInstanceState methods to save selected Goal on app rebuilds - see Sunshine example

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mGoalListAdapter.swapCursor(null);
    }
}
