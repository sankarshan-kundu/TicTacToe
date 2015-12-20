package com.sankarshan.tictactoefree.util;

import java.util.Arrays;

/**
 * Created by Sankarshan on 8/12/2015.
 * This class is for Score related utility
 */
public class ScoreBoard {

    public static final int SCORE_SIZE = 18;

    public static String getString4Score(int[]score){
        return Arrays.toString(score).replaceAll("\\s", "");
    }

    public static int[]getScore4Sting(String strScore){
        int[]score = new int[SCORE_SIZE];
        if(strScore != null){
            String[] items = strScore.substring(1, strScore.length()-1).split(",");
            for (int i = 0; i < Math.min(items.length, score.length); i++) {
                try {
                    score[i] = Integer.parseInt(items[i]);
                } catch (NumberFormatException nfe) {
                    score[i] = 0;
                }
            }
        }
        return score;
    }

    public static int getScoreIndex(GameSettings settings, GameLogic.CellEntry entry){
        int first_turn_offset = 0;
        switch (settings.firstTurn){
            case FIRST_TURN_HUMAN:
                first_turn_offset = 0;
                break;

            case FIRST_TURN_ANDROID:
                first_turn_offset = 9;
                break;
        }

        int difficulty_offset = 0;
        switch (settings.difficultyLevel){
            case LEVEL_EASY:
                difficulty_offset = 0;
                break;

            case LEVEL_NORMAL:
                difficulty_offset = 3;
                break;

            case LEVEL_HARD:
                difficulty_offset = 6;
                break;
        }

        int entry_offset = 0;
        switch (entry){
            case CELL_HUMAN:
                entry_offset = 0;
                break;

            case CELL_EMPTY:
                entry_offset = 1;
                break;

            case CELL_ANDROID:
                entry_offset = 2;
                break;
        }

        return first_turn_offset + difficulty_offset + entry_offset;
    }

    public static float getPlayerRating(int[]score){
        final float []ratingWeight = new float[]{
                1.10f, 0.40f, -1.50f,
                1.30f, 0.50f, -0.70f,
                1.90f, 0.60f, -0.20f,
                1.50f, 0.50f, -1.00f,
                1.80f, 0.60f, -0.40f,
                2.00f, 0.70f, -0.10f
        } ;
        float ratingSum = 0.0f;
        int playCount = 0;
        for(int i = 0; i < score.length; i++){
            playCount += score[i];
            ratingSum += score[i] * ratingWeight[i];
        }

        if(ratingSum < 0.0f){
            return 0.0f;
        }
        int avg = Math.round((ratingSum / (playCount + score.length)) * 10.0f);
        float rating = (float)avg/2.0f;

        if(rating > 5.0f){
            return 5.0f;
        }
        else {
            return rating;
        }

    }
}
