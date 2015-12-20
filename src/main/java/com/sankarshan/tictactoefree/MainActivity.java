package com.sankarshan.tictactoefree;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sankarshan.tictactoefree.util.GameLogic;
import com.sankarshan.tictactoefree.util.GameSettings;
import com.sankarshan.tictactoefree.util.GeneralUtility;
import com.sankarshan.tictactoefree.util.ScoreBoard;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_SETTINGS = "com.sankarshan.tictactoe.EXTRA_SETTINGS";
    public final static String EXTRA_GAME_STARTED = "com.sankarshan.tictactoe.EXTRA_GAME_STARTED";
    public final static String EXTRA_SCORE = "com.sankarshan.tictactoe.EXTRA_SCORE";
    final static String STATE_GAME = "STATE_GAME";
    final static String STATE_SETTINGS = "STATE_SETTINGS";
    final static String STATE_SCORE = "STATE_SCORE";
    final static int PICK_GAME_SETTINGS = 1;
    final static int PICK_GAME_SCORE = 2;
    GameSettings settings = new GameSettings();
    public GameSettings getSettings(){
        return settings;
    }

    int[]score;

    GameLogic gameLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Restore last game & settings
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        String ft = sharedPref.getString(getString(R.string.saved_first_turn), getString(R.string.default_first_turn));
        settings.firstTurn = GameSettings.FirstTurn.valueOf(ft);

        String dl = sharedPref.getString(getString(R.string.saved_difficulty_level), getString(R.string.default_difficulty_level));
        settings.difficultyLevel = GameSettings.DifficultyLevel.valueOf(dl);

        updateSettingsStatus();

        gameLogic = new GameLogic();
        gameLogic.setSymbol(settings.firstTurn);

        String sg = sharedPref.getString(getString(R.string.saved_game), getString(R.string.default_game));
        gameLogic.setGameString(sg);
        gameLogic.updateGridUI(this);

        String strScore = sharedPref.getString(getString(R.string.saved_score), null);
        score = ScoreBoard.getScore4Sting(strScore);

        updatePlayerRating();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if( id == R.id.info){
            showInfoDialog();
            return true;
        }
        else if( id == R.id.rating){
            showRatingDialog();
            return true;
        }
        else if(id == R.id.game_restart){
            onRestartClick(null);
            return true;
        }
        else if (id == R.id.action_settings) {
            onSettingsClick(null);
            return true;
        }
        else if(id == R.id.action_score){
            onScoreClick(null);
            return true;
        }
        else if(id==R.id.app_exit) {
            onExitClick(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current game & settings
        savedInstanceState.putString(STATE_GAME, gameLogic.getGameString());
        savedInstanceState.putSerializable(STATE_SETTINGS, settings);
        savedInstanceState.putIntArray(STATE_SCORE, score);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        settings = (GameSettings)savedInstanceState.getSerializable(STATE_SETTINGS);
        updateSettingsStatus();
        gameLogic.setSymbol(settings.firstTurn);
        gameLogic.setGameString(savedInstanceState.getString(STATE_GAME));
        gameLogic.updateGridUI(this);
        score = savedInstanceState.getIntArray(STATE_SCORE);
        updatePlayerRating();
    }

    @Override
    public void onBackPressed() {
        onExitClick(null);
    }

    public void onGridCellClick(View v){
        gameLogic.UpdateClickedCell((ImageButton) v);
        gameLogic.AndroidTurn(this);
    }

    public void  onSettingsClick(View v){
        Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
        intent.putExtra(EXTRA_SETTINGS, settings);
        intent.putExtra(EXTRA_GAME_STARTED, gameLogic.isGameStarted());
        startActivityForResult(intent, PICK_GAME_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == PICK_GAME_SETTINGS) {
            // Make sure the request was successful
            settings = (GameSettings)data.getSerializableExtra(MainActivity.EXTRA_SETTINGS);
            boolean restart_needed = data.getBooleanExtra(SettingsActivity.EXTRA_RESTART_NEEDED, false);
            if(restart_needed
                    || (!gameLogic.isGameStarted() && settings.firstTurn == GameSettings.FirstTurn.FIRST_TURN_ANDROID)){
                ResetGame();
            }
            updateSettingsStatus();
        }
        else if(requestCode == PICK_GAME_SCORE){
            boolean reset_score = data.getBooleanExtra(ScoreboardActivity.EXTRA_RESET_SCORE, false);
            if(reset_score){
                score = new int[ScoreBoard.SCORE_SIZE];
                updatePlayerRating();
            }
        }
    }

    private void showInfoDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_information, null);

        new AlertDialog.Builder(this)
                .setTitle(R.string.info_title)
                .setView(dialoglayout)
                .setNeutralButton("Got it!", null)
                .show();
    }

    private void showRatingDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_rating_request, null);
        new AlertDialog.Builder(this)
                .setTitle(R.string.rate_title)
                .setView(dialoglayout)
                .setPositiveButton("Rate It Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri marketUri = Uri.parse("market://details?id=com.sankarshan.tictactoefree");
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                        startActivity(marketIntent);
                    }
                })
                .setNeutralButton("Not now", null)
                .show();
    }

    public void onScoreClick(View v){
        Intent intent = new Intent(getApplicationContext(),ScoreboardActivity.class);
        intent.putExtra(EXTRA_SCORE, score);
        startActivityForResult(intent, PICK_GAME_SCORE);
    }

    public void onExitClick(View v){
        // Save current game & settings before exit
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_game), gameLogic.getGameString());
        editor.putString(getString(R.string.saved_first_turn), settings.firstTurn.toString());
        editor.putString(getString(R.string.saved_difficulty_level), settings.difficultyLevel.toString());
        editor.putString(getString(R.string.saved_score), ScoreBoard.getString4Score(score));
        editor.apply();

        finish();
    }

    public void onRestartClick(View v) {
        if(!gameLogic.isGameStarted()){
            return;
        }

        if(!gameLogic.isGameDisabled()){
            new AlertDialog.Builder(this)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle(R.string.reset_title)
                    .setMessage(R.string.reset_game_message)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ResetGame();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
        else {
            ResetGame();
        }
    }

    private void ResetGame(){
        gameLogic.ResetGame(this);
        gameLogic.updateGridUI(this);
    }

    private void updateSettingsStatus(){
        TextView txt_first_turn = (TextView)findViewById(R.id.status_first_turn);
        String msg_first_turn = null;
        switch (settings.firstTurn){
            case FIRST_TURN_HUMAN:
                msg_first_turn = getString(R.string.symbol_x);
                break;

            case FIRST_TURN_ANDROID:
                msg_first_turn = getString(R.string.symbol_o);
                break;
        }

        txt_first_turn.setText("You are playing with " + msg_first_turn.split(" ")[0]);

        TextView txt_difficulty = (TextView)findViewById(R.id.status_difficulty);
        String msg_difficulty = null;
        switch (settings.difficultyLevel){
            case LEVEL_EASY:
                msg_difficulty = getString(R.string.level_easy);
                break;

            case LEVEL_NORMAL:
                msg_difficulty = getString(R.string.level_normal);
                break;

            case LEVEL_HARD:
                msg_difficulty = getString(R.string.level_hard);
        }

        txt_difficulty.setText(msg_difficulty+" Level");
    }

    public void updateScore(GameLogic.CellEntry entry){
        if(score != null){
            score[ScoreBoard.getScoreIndex(settings, entry)]++;
            updatePlayerRating();
        }
    }
    public void updatePlayerRating(){
        float ratingValue = ScoreBoard.getPlayerRating(score);
        TextView ratingTextView = (TextView)findViewById(R.id.player_rating_value);
        ratingTextView.setText(String.format("%.1f", ratingValue));

        int intVal = (int)ratingValue;
        boolean isHalf = (intVal != ratingValue);

        for(int i = 1; i <= intVal; i++){
            String strId = "star_" + Integer.toString(i);
            int imageViewId = GeneralUtility.getResId(strId, R.id.class);
            ImageView imageView = (ImageView)findViewById(imageViewId);
            imageView.setImageResource(R.drawable.star);
        }
        if(isHalf){
            String strId = "star_" + Integer.toString(intVal + 1);
            int imageViewId = GeneralUtility.getResId(strId, R.id.class);
            ImageView imageView = (ImageView)findViewById(imageViewId);
            imageView.setImageResource(R.drawable.star_half);
            intVal++;
        }

        for(int i = intVal + 1; i <= 5; i++){
            String strId = "star_" + Integer.toString(i);
            int imageViewId = GeneralUtility.getResId(strId, R.id.class);
            ImageView imageView = (ImageView)findViewById(imageViewId);
            imageView.setImageResource(R.drawable.star_0);
        }
    }

}
