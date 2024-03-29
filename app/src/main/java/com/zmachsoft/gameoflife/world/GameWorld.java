package com.zmachsoft.gameoflife.world;

import android.graphics.Canvas;
import android.graphics.Color;

import com.zmachsoft.gameoflife.world.setting.WorldSetting;

/**
 * Master class of all game worlds.
 *
 * @author Master
 */
public abstract class GameWorld {
    public enum WorldType {
        CONWAY,
        SHELLING,
        EPIDEMIC,
        WAR,
        EXCITABLE_MEDIA,
        BOIDS
    }

    protected static int[] allColors = new int[]{Color.GRAY, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.WHITE, Color.CYAN};

    private static int UNIQUE_ID = 1;
    protected int uniqueId = UNIQUE_ID++;

    protected WorldSetting setting;
    protected int boardWidth;
    protected int boardheight;

    protected GameWorld(WorldSetting setting) {
        this.setting = setting;
    }

    /**
     * A world should be able to init its content
     */
    public abstract void initContent();

    /**
     * A world should be able to compute next step of its state
     *
     * @throws NoChangeException
     */
    public abstract void nextStep() throws NoChangeException;

    /**
     * A world should be able to render itself on the input canvas
     */
    public abstract void render(Canvas canvas);

    public WorldSetting getSetting() {
        return setting;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public void setBoardWidth(int boardWidth) {
        this.boardWidth = boardWidth;
    }

    public int getBoardheight() {
        return boardheight;
    }

    public void setBoardheight(int boardheight) {
        this.boardheight = boardheight;
    }

}
