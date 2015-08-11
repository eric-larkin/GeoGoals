package com.example.larkinmcmahon.geogoals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.Serializable;

public class GoalDetail extends AppCompatActivity {
    Goal activityGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.detailContainer, new GoalDetailFragment())
//                    .commit();
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_goal_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_editGoal) {
            Intent currentIntent = getIntent();

            int dbid = currentIntent.getIntExtra("dbID",-1); //this is what was causing EditFragment  to use same goal
            Intent intent = new Intent(this, GoalEdit.class)
                      .putExtra("dbid", dbid);

            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


}
