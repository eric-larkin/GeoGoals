package com.example.larkinmcmahon.geogoals;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import org.w3c.dom.Text;

public class GoalEditFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    Context mContext;
    private static int LOADER_ID = 2;
    public static int mDbID = -1;
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

    //correlate with GOAL_DETAIL_COLUMNS
    private static final int COLUMN_GOALNAME = 1;

    private TextView mEditGoalTitleText;

    public GoalEditFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();

        View rootView = inflater.inflate(R.layout.fragment_goal_edit, container, false);
        mEditGoalTitleText = (TextView) rootView.findViewById(R.id.goal_title_editbox);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("dbid")) {
            String projection[] = { GoalDatabaseHelper.KEY_GOALNAME,
                                    GoalDatabaseHelper.KEY_COMMENTS,
                                    GoalDatabaseHelper.KEY_OCCURANCES,
                                    GoalDatabaseHelper.KEY_TIMEFRAME};
            int dbid = intent.getIntExtra("dbID", 0);
            Cursor cursor = getActivity().getContentResolver().query(
                    Uri.withAppendedPath(GoalsProvider.CONTENT_URI,
                            String.valueOf(dbid)),projection,null,null,null);
            if(cursor.moveToFirst()) {
                String mGoalName = cursor.getString(0);
                String mComments = cursor.getString(1);
                String mOccurances = cursor.getString(2);
                String mTimeFrame = cursor.getString(3);

                ((TextView) getActivity().findViewById(R.id.goal_title_editbox))
                        .setText(mGoalName);
                ((TextView) getActivity().findViewById(R.id.goal_comments_editbox))
                        .setText(mComments);
                ((TextView) getActivity().findViewById(R.id.goal_occurrences_editbox))
                        .setText(String.valueOf(mOccurances));
                ((TextView) getActivity().findViewById(R.id.goal_timeframe_editbox))
                        .setText(String.valueOf(mTimeFrame));
            }
        }
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        getLoaderManager().initLoader(LOADER_ID, null, this);
//        super.onActivityCreated(savedInstanceState);
//    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(mDbID == -1 && getArguments() != null && getArguments().containsKey("dbid")) {
            mDbID = getArguments().getInt("dbid");
        }
        if(mDbID != -1) {
            return new CursorLoader(getActivity(),
                    Uri.withAppendedPath(GoalsProvider.CONTENT_URI, String.valueOf(mDbID)),
                    GOAL_DETAIL_COLUMNS,
                    null,
                    null,
                    null);
        }
        else {
            return null;
        }
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToFirst();
        String mGoalNameText = cursor.getString(COLUMN_GOALNAME);

        mEditGoalTitleText.setText(mGoalNameText);

    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
