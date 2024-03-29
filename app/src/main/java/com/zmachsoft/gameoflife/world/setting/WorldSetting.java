package com.zmachsoft.gameoflife.world.setting;

import com.zmachsoft.gameoflife.world.GameWorld.WorldType;

import java.io.Serializable;

/**
 * Super class of all world settings
 *
 * @author Master
 */
public abstract class WorldSetting implements Serializable {
    public static int NB_TILES = 60;
    public static int TILE_SIZE = 10;
    private int nbTiles;
    private int tileSize;
    private WorldType worldType;

    protected WorldSetting(WorldType worldType) {
        this(worldType, NB_TILES, TILE_SIZE);
    }

    protected WorldSetting(WorldType worldType, int nbTiles, int tileSize) {
        this.nbTiles = nbTiles;
        this.tileSize = tileSize;
        this.worldType = worldType;
    }

    /**
     * @return the rules help
     */
    public abstract String getRulesHelp();

    /**
     * Default setting factory
     *
     * @param worldType
     * @return
     */
    public static WorldSetting getDefaultSetting(WorldType worldType) {
        switch (worldType) {
            case CONWAY:
                return new ConwaySetting();
            case SHELLING:
                return new ShellingSetting();
            case EPIDEMIC:
                return new EpidemicSetting();
            case WAR:
                return new WarSetting();
            case EXCITABLE_MEDIA:
                return new ExcitableMediaSetting();
            case BOIDS:
                return new BoidsSetting();
            default:
                throw new IllegalArgumentException("Unknown world type : " + worldType);
        }
    }

    /**
     * @param setting
     * @return true if there is any difference between curent setting end given one
     */
    public boolean hasChanged(WorldSetting setting) {
        return (setting.getNbTiles() != nbTiles) || (setting.getTileSize() != tileSize) || !setting.getWorldType().equals(worldType);
    }

    public int getNbTiles() {
        return nbTiles;
    }

    public void setNbTiles(int nbTiles) {
        this.nbTiles = nbTiles;
    }

    public int getTileSize() {
        return tileSize;
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }

    public WorldType getWorldType() {
        return worldType;
    }

    public void setWorldType(WorldType worldType) {
        this.worldType = worldType;
    }
}
