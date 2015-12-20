package com.sankarshan.tictactoefree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sankarshan.tictactoefree.util.GeneralUtility;
import com.sankarshan.tictactoefree.util.ScoreBoard;

/**
 * Created by Sankarshan on 7/20/2015.
 * This class used for displaying the scoreboard.
 */
public class ScoreboardActivity extends ActionBarActivity {

    final static String STATE_RESET_SCORE =  "STATE_RESET_SCORE";
    public final static String EXTRA_RESET_SCORE = "com.sankarshan.tictactoe.EXTRA_RESET_SCORE";
    private boolean reset_score = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        int[]score = getIntent().getIntArrayExtra(MainActivity.EXTRA_SCORE);

        displayScore(score);
    }

    private void displayScore(int[]score){
        for(int i = 0; i < score.length; i++){
            String strTextViewId = "score_" + i;
            int textViewId = GeneralUtility.getResId(strTextViewId, R.id.class);
            TextView textView = (TextView)findViewById(textViewId);
            textView.setText(Integer.toString(score[i]));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scoreboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                BackWithResult();
                return true;

            case R.id.action_clear_score:
                onScoreClearClick();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        BackWithResult();
    }
    private  void BackWithResult(){
        Intent output = new Intent();
        output.putExtra(EXTRA_RESET_SCORE, reset_score);
        setResult(RESULT_OK, output);
        finish();
    }

    public void onScoreClearClick(){
        new AlertDialog.Builder(this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.reset_title)
                .setMessage(R.string.reset_score_message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reset_score = true;
                        displayScore(new int[ScoreBoard.SCORE_SIZE]);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current state
        savedInstanceState.putBoolean(STATE_RESET_SCORE, reset_score);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        reset_score = savedInstanceState.getBoolean(STATE_RESET_SCORE);

        if(reset_score){
            displayScore(new int[ScoreBoard.SCORE_SIZE]);
        }
    }

}
