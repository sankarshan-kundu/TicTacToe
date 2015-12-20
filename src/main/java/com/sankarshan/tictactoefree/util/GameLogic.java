package com.sankarshan.tictactoefree.util;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.sankarshan.tictactoefree.MainActivity;
import com.sankarshan.tictactoefree.R;
import java.util.Random;

/**
 * Created by Sankarshan on 7/25/2015.
 * This class is used for Game Logic
 */
public class GameLogic {

    public final int GRID_SIZE = 3;

    public enum CellEntry{
        CELL_EMPTY,
        CELL_ANDROID,
        CELL_HUMAN
    }

    private int HUMAN_CELL_IMAGE_ID;
    private int ANDROID_CELL_IMAGE_ID;

    private boolean gameDisabled = false;

    public void setSymbol(GameSettings.FirstTurn firstTurn){
        if(firstTurn== GameSettings.FirstTurn.FIRST_TURN_ANDROID){
            HUMAN_CELL_IMAGE_ID = R.drawable.o_e;
            ANDROID_CELL_IMAGE_ID = R.drawable.x_e;
        }
        else if(firstTurn== GameSettings.FirstTurn.FIRST_TURN_HUMAN){
            HUMAN_CELL_IMAGE_ID = R.drawable.x_e;
            ANDROID_CELL_IMAGE_ID = R.drawable.o_e;
        }
    }

    private CellEntry[][]GameBoard = new CellEntry[GRID_SIZE][GRID_SIZE];
    private class Location{
        private int row;
        private int column;

        public Location(){}
        public Location(int row, int column){
            this.row=row;
            this.column=column;
        }
        public int getButtonId(){
            String strBtnId = "_" + row + column;
            return GeneralUtility.getResId(strBtnId, R.id.class);
        }
    }

    public void ResetGame(MainActivity mainActivity){
        gameDisabled = false;
        for(int r = 0; r < GRID_SIZE; r++){
            for(int c = 0; c < GRID_SIZE; c++){
                GameBoard[r][c] = CellEntry.CELL_EMPTY;
            }
        }
        if(mainActivity.getSettings().firstTurn== GameSettings.FirstTurn.FIRST_TURN_ANDROID){
            AndroidTurn(mainActivity);
            HUMAN_CELL_IMAGE_ID = R.drawable.o_e;
            ANDROID_CELL_IMAGE_ID = R.drawable.x_e;
        }
        else if(mainActivity.getSettings().firstTurn== GameSettings.FirstTurn.FIRST_TURN_HUMAN){
            HUMAN_CELL_IMAGE_ID = R.drawable.x_e;
            ANDROID_CELL_IMAGE_ID = R.drawable.o_e;
        }
        updateGameMood(mainActivity, null);
    }

    public void UpdateClickedCell(ImageButton button){
        button.setImageResource(HUMAN_CELL_IMAGE_ID);
        button.setClickable(false);
        String strId = button.getResources().getResourceEntryName(button.getId());
        int row = Integer.parseInt(strId.substring(1, 2));
        int col = Integer.parseInt(strId.substring(2, 3));
        GameBoard[row][col] = CellEntry.CELL_HUMAN;
    }

    public void updateGridUI(MainActivity mainActivity){
        for(int r = 0; r < GRID_SIZE; r++){
            for(int c = 0; c < GRID_SIZE; c++){
                String strBtnId = "_" + r + c;
                int buttonId = GeneralUtility.getResId(strBtnId, R.id.class);
                ImageButton button = (ImageButton)mainActivity.findViewById(buttonId);
                button.setColorFilter(null);
                button.setAlpha(1.0f);
                switch(GameBoard[r][c]){
                    case CELL_EMPTY:
                        button.setImageResource(android.R.color.transparent);
                        button.setClickable(true);
                        break;

                    case CELL_HUMAN:
                        button.setImageResource(HUMAN_CELL_IMAGE_ID);
                        button.setClickable(false);
                        break;

                    case CELL_ANDROID:
                        button.setImageResource(ANDROID_CELL_IMAGE_ID);
                        button.setClickable(false);
                        break;
                }
            }
        }
        if(CheckGameStatus(mainActivity)){
            updateGameMood(mainActivity, null);
        }
    }

    public boolean isGameStarted(){
        for(int r = 0; r < GRID_SIZE; r++){
            for(int c = 0; c < GRID_SIZE; c++){
                if(GameBoard[r][c] != CellEntry.CELL_EMPTY){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isGameDisabled(){
        return gameDisabled;
    }

    /**
     * @return true(running), false(win or draw)
     */
    public boolean CheckGameStatus(MainActivity mainActivity){
        boolean isWin;
        boolean anyEmpty = false;
        for(int i = 0; i < GRID_SIZE; i++){
            if(GameBoard[i][i] == CellEntry.CELL_EMPTY){
                anyEmpty = true;
                continue;
            }
            isWin = true;
            for(int c = 0; c < GRID_SIZE;  c++){
                if(i == c)continue;
                if(GameBoard[i][c] == CellEntry.CELL_EMPTY){
                    anyEmpty = true;
                }
                if(GameBoard[i][i] != GameBoard[i][c]){
                    isWin = false;
                }
            }
            if(isWin){
                HighlightWin(new Location[]{new Location(i,0),new Location(i,1),new Location(i,2)},mainActivity);
                updateGameMood(mainActivity, GameBoard[i][i]);
                return false;
            }
            isWin = true;
            for(int r = 0; r < GRID_SIZE;  r++){
                if(i == r)continue;
                if(GameBoard[r][i] == CellEntry.CELL_EMPTY){
                    anyEmpty = true;
                }
                if(GameBoard[i][i] != GameBoard[r][i]){
                    isWin = false;
                }
            }
            if(isWin){
                HighlightWin(new Location[]{new Location(0,i),new Location(1,i),new Location(2,i)},mainActivity);
                updateGameMood(mainActivity, GameBoard[i][i]);
                return false;
            }
        }

        if(GameBoard[1][1] != CellEntry.CELL_EMPTY){
            if((GameBoard[0][0] == GameBoard[1][1])&& (GameBoard[1][1] == GameBoard[2][2])){
                updateGameMood(mainActivity, GameBoard[1][1]);
                HighlightWin(new Location[]{new Location(0,0),new Location(1,1),new Location(2,2)},mainActivity);
                return false;
            }
            if((GameBoard[0][2] == GameBoard[1][1]) && (GameBoard[1][1] == GameBoard[2][0])){
                updateGameMood(mainActivity, GameBoard[1][1]);
                HighlightWin(new Location[]{new Location(0,2),new Location(1,1),new Location(2,0)},mainActivity);
                return false;
            }
        }

        if(anyEmpty){
            return true; // Running
        }
        else {
            updateGameMood(mainActivity, CellEntry.CELL_EMPTY);
            HighlightWin(null,mainActivity);
            return false; // Draw
        }
    }



    public void AndroidTurn(MainActivity mainActivity){
        if(!CheckGameStatus(mainActivity)){
            return;
        }

        Location loc = new Location();
        switch (mainActivity.getSettings().difficultyLevel){
            case LEVEL_EASY:
                loc = playRandom();
                break;

            case LEVEL_NORMAL:
                loc = playNormal();
                break;

            case LEVEL_HARD:
                loc = playHard();
                break;
        }

        GameBoard[loc.row][loc.column] = CellEntry.CELL_ANDROID;
        int buttonId = loc.getButtonId();
        ImageButton button = (ImageButton)mainActivity.findViewById(buttonId);
        button.setImageResource(ANDROID_CELL_IMAGE_ID);
        button.setClickable(false);

        CheckGameStatus(mainActivity);
    }

    private Location playRandom(){
        Location loc = new Location();
        while (true){
            Random rand = new Random();
            int randomNum = rand.nextInt(GRID_SIZE * GRID_SIZE);
            loc.row = randomNum / GRID_SIZE;
            loc.column = randomNum % GRID_SIZE;
            if(GameBoard[loc.row][loc.column] == CellEntry.CELL_EMPTY){
                break;
            }
        }
        return loc;
    }

    private Location playNormal(){
        Location loc = playTemplate(CellEntry.CELL_HUMAN);
        if(loc == null) {
            return playRandom();
        }
        return loc;
    }

    private Location playHard(){
        Location loc = playTemplate(CellEntry.CELL_ANDROID);
        if(loc == null) {
            return playNormal();
        }
        return loc;
    }


    private Location playTemplate(CellEntry entry){
        Location loc = new Location();
        for(int r = 0; r < GRID_SIZE; r++){
            for(int c = 0; c < GRID_SIZE; c++){
                if(GameBoard[r][c] == CellEntry.CELL_EMPTY){
                    int rc = 0;
                    int cc = 0;
                    int dc = 0;
                    int rdc = 0;
                    for(int i = 0; i < GRID_SIZE; i++){
                        if(i != c && GameBoard[r][i] == entry){
                            rc++;
                        }
                        if(i != r && GameBoard[i][c] == entry){
                            cc++;
                        }
                        if(r == c && r != i && GameBoard[i][i] == entry){
                            dc++;
                        }
                        if((r+c) == (GRID_SIZE - 1) && r != i && GameBoard[i][GRID_SIZE - i -1] == entry){
                            rdc++;
                        }
                    }
                    if(rc == (GRID_SIZE - 1) || cc == (GRID_SIZE - 1) || dc == (GRID_SIZE - 1) || rdc == (GRID_SIZE - 1)){
                        loc.row = r;
                        loc.column = c;
                        return loc;
                    }
                }
            }
        }
        return null;
    }

    private void updateGameMood(MainActivity mainActivity, CellEntry entry){
        TextView game_msg = (TextView)mainActivity.findViewById(R.id.game_msg);

        if(entry == null){ // Running
            game_msg.setText(R.string.msg_running);
            return;
        }
        switch (entry){
            case CELL_EMPTY: // Draw
                game_msg.setText(R.string.msg_draw);
                break;

            case CELL_HUMAN: // Human Win
                game_msg.setText(R.string.msg_win_human);
                break;

            case CELL_ANDROID: // Android Win
                game_msg.setText(R.string.msg_win_android);
                break;
        }
        mainActivity.updateScore(entry);
        DisableGame(mainActivity);
    }

    private void DisableGame(MainActivity mainActivity){
        gameDisabled = true;
        for(int r = 0; r < GRID_SIZE; r++){
            for(int c = 0; c < GRID_SIZE; c++){
                if(GameBoard[r][c] == CellEntry.CELL_EMPTY){
                    String strBtnId = "_" + r + c;
                    int buttonId = GeneralUtility.getResId(strBtnId, R.id.class);
                    ImageButton button = (ImageButton)mainActivity.findViewById(buttonId);
                    button.setClickable(false);
                }
            }
        }
    }

    private void HighlightWin(Location[]cellIndices, MainActivity mainActivity){
        for (int c = 0; c < GRID_SIZE; c++){
            for (int r = 0; r < GRID_SIZE; r++){
                boolean found = false;
                if(cellIndices!=null) {
                    for (int i = 0; i < GRID_SIZE; i++) {
                        if (cellIndices[i].row == r && cellIndices[i].column == c) {
                            found = true;
                            break;
                        }
                    }
                }
                if(!found){
                    String strBtnId = "_" + r + c;
                    int buttonId = GeneralUtility.getResId(strBtnId, R.id.class);
                    ImageButton button = (ImageButton)mainActivity.findViewById(buttonId);

                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation(0);
                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                    button.setColorFilter(filter);
                    button.setAlpha(0.5f);
                }
            }
        }
    }

    public String getGameString(){
        String strGame = "";
        for(int r = 0; r < GRID_SIZE; r++){
            for(int c = 0; c < GRID_SIZE; c++) {
                switch (GameBoard[r][c]){
                    case CELL_EMPTY:
                        strGame+="E";
                        break;

                    case CELL_HUMAN:
                        strGame+="H";
                        break;

                    case CELL_ANDROID:
                        strGame+="A";
                        break;
                }
            }
        }
        return strGame;
    }

    public void setGameString(String gameString){
        int i = 0;
        for(int r = 0; r < GRID_SIZE; r++){
            for(int c = 0; c < GRID_SIZE; c++) {
                switch (gameString.charAt(i)){
                    case 'E':
                        GameBoard[r][c] = CellEntry.CELL_EMPTY;
                        break;

                    case 'H':
                        GameBoard[r][c] = CellEntry.CELL_HUMAN;
                        break;

                    case 'A':
                        GameBoard[r][c] = CellEntry.CELL_ANDROID;
                        break;
                }
                i++;
            }
        }
    }
}
