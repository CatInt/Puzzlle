package com.whalespool.puzzlle.event;

/**
 * Created by yodazone on 2016/11/22.
 */

public class CompetitionResultEvent {
    public boolean isVictory;
    public CompetitionResultEvent(boolean isVictory){
        this.isVictory = isVictory;
    }
}
