package com.sankarshan.tictactoefree.util;

import java.io.Serializable;

/**
 * Created by Sankarshan on 8/6/2015.
 */
public class GameSettings implements Serializable {
    public enum FirstTurn{
        FIRST_TURN_HUMAN,
        FIRST_TURN_ANDROID
    }

    public enum DifficultyLevel{
        LEVEL_EASY,
        LEVEL_NORMAL,
        LEVEL_HARD
    }

    public FirstTurn firstTurn;
    public DifficultyLevel difficultyLevel;
}
