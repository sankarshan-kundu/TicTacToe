package com.sankarshan.tictactoefree;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sankarshan.tictactoefree.util.GameSettings;

/**
 * Created by Sankarshan on 7/20/2015.
 * This class used for modifying the game settings.
 */
public class SettingsActivity extends ActionBarActivity {

    int default_first_turn_radio_id;
    int default_difficulty_level_radio_id;
    boolean restart_needed = false;
    boolean game_started;
    final static String STATE_RESTART_NEEDED =  "STATE_RESTART_NEEDED";
    public final static String EXTRA_RESTART_NEEDED = "com.sankarshan.tictactoe.EXTRA_RESTART_NEEDED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent = getIntent();

        game_started = intent.getBooleanExtra(MainActivity.EXTRA_GAME_STARTED, false);
        GameSettings gameSettings  = (GameSettings)intent.getSerializableExtra(MainActivity.EXTRA_SETTINGS);
        RadioGroup rgFirstTurn = (RadioGroup)findViewById(R.id.first_turn);
        switch (gameSettings.firstTurn){
            case FIRST_TURN_HUMAN:
                default_first_turn_radio_id = R.id.radio_x;
                break;

            case FIRST_TURN_ANDROID:
                default_first_turn_radio_id = R.id.radio_o;
                break;
        }
        rgFirstTurn.check(default_first_turn_radio_id);

        RadioGroup rgDifficultyLevel = (RadioGroup)findViewById(R.id.difficulty_level);
        switch (gameSettings.difficultyLevel){
            case LEVEL_EASY:
                default_difficulty_level_radio_id = R.id.radio_easy;
                break;

            case LEVEL_NORMAL:
                default_difficulty_level_radio_id = R.id.radio_normal;
                break;

            case LEVEL_HARD:
                default_difficulty_level_radio_id = R.id.radio_hard;
                break;
        }
        rgDifficultyLevel.check(default_difficulty_level_radio_id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                BackWithResult();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if(game_started){
            // Save the current state
            savedInstanceState.putBoolean(STATE_RESTART_NEEDED, restart_needed);
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        if(game_started){
            // Restore state members from saved instance
            restart_needed = savedInstanceState.getBoolean(STATE_RESTART_NEEDED);

            TextView tv = (TextView) findViewById(R.id.reset_alert);
            if(restart_needed){
                tv.setVisibility(View.VISIBLE);
            }
            else {
                tv.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        BackWithResult();
    }

    private  void BackWithResult(){
        GameSettings gameSettings = new GameSettings();
        RadioGroup rgFirstTurn = (RadioGroup)findViewById(R.id.first_turn);
        switch (rgFirstTurn.getCheckedRadioButtonId()){
            case R.id.radio_x:
                gameSettings.firstTurn = GameSettings.FirstTurn.FIRST_TURN_HUMAN;
                break;

            case R.id.radio_o:
                gameSettings.firstTurn = GameSettings.FirstTurn.FIRST_TURN_ANDROID;
                break;
        }

        RadioGroup rgDifficultyLevel = (RadioGroup)findViewById(R.id.difficulty_level);
        switch (rgDifficultyLevel.getCheckedRadioButtonId()){
            case R.id.radio_easy:
                gameSettings.difficultyLevel = GameSettings.DifficultyLevel.LEVEL_EASY;
                break;

            case R.id.radio_normal:
                gameSettings.difficultyLevel = GameSettings.DifficultyLevel.LEVEL_NORMAL;
                break;

            case R.id.radio_hard:
                gameSettings.difficultyLevel = GameSettings.DifficultyLevel.LEVEL_HARD;
                break;
        }

        Intent output = new Intent();
        output.putExtra(MainActivity.EXTRA_SETTINGS, gameSettings);
        output.putExtra(EXTRA_RESTART_NEEDED, restart_needed);
        setResult(RESULT_OK, output);
        finish();
    }

    public void radioButtonClicked(View v){
        if(game_started){
            TextView tv = (TextView) findViewById(R.id.reset_alert);

            RadioGroup rgFirstTurn = (RadioGroup)findViewById(R.id.first_turn);
            RadioGroup rgDifficultyLevel = (RadioGroup)findViewById(R.id.difficulty_level);

            if(rgFirstTurn.getCheckedRadioButtonId() == default_first_turn_radio_id &&
                    rgDifficultyLevel.getCheckedRadioButtonId() == default_difficulty_level_radio_id){
                tv.setVisibility(View.INVISIBLE);
                restart_needed = false;
            }
            else {
                tv.setVisibility(View.VISIBLE);
                restart_needed = true;
            }
        }
    }
}
