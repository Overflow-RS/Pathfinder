package src.pathfinder.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Author: Tom
 * Date: 03/09/13
 * Time: 00:04
 */
public class GameRegion {

    private static final HashMap<Integer, GameRegion> GAME_REGION_MAP = new HashMap<>();
    private static final HashMap<Integer, RegionData> REGION_DATA_MAP = new HashMap<>();
    private static final LinkedList<GameRegion> LOADED = new LinkedList<>();

    private static boolean loaded = false;

    public static int regionLoadLimit = 50;

    private final RegionData regionData;
    private final int hash;
    private final int baseX;
    private final int baseY;
    private int[][][] mapData = null;


    private GameRegion(final int hash, final RegionData data) {
        this.hash = hash;
        this.regionData = data;
        this.baseX = Structure.REGION.getX(hash) * 64;
        this.baseY = Structure.REGION.getY(hash) * 64;
    }

    public int getBaseX() {
        return baseX;
    }

    public int getBaseY() {
        return baseY;
    }

    public int[][][] getMapData() {
        ensureCapacity();
        if (mapData != null) {
            return mapData;
        }
        try (final FileInputStream dataStream = new FileInputStream(new File("MapData" + File.separator + "MapData.dat"))) {
            mapData = new int[4][64][64];
            if (regionData != null) {
                dataStream.skip(regionData.getIndex());
                byte[] data = new byte[regionData.getSize()];
                dataStream.read(data);
                ByteBuffer buffer = ByteBuffer.wrap(data);
                for (int plane = 0; plane < 4; plane++) {
                    for (int tx = 0; tx < 64; tx++) {
                        for (int ty = 0; ty < 64; ty++) {
                            mapData[plane][tx][ty] = buffer.getInt();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapData;
    }

    public int getWalkData(final int x, final int y, final int plane) {
        return getMapData()[plane & 0x3][x & 63][y & 63];
    }

    public int getWalkData(final int hash) {
        return getWalkData(Structure.TILE.getX(hash), Structure.TILE.getY(hash), Structure.TILE.getZ(hash));
    }

    private void ensureCapacity() {
        LOADED.remove(this);
        LOADED.addLast(this);
        if (LOADED.size() > regionLoadLimit) {
            LOADED.poll().mapData = null;
        }
    }

    public static LinkedList<GameRegion> getLoaded() {
        return LOADED;
    }

    public static int getFlag(final int x, final int y, final int plane) {
        return getGameRegion(x, y, plane).getWalkData(x, y, plane);
    }

    public static GameRegion getGameRegion(final int x, final int y, final int plane) {
        return getGameRegion(Structure.REGION.getHash(x >> 6, y >> 6, plane));
    }

    public static synchronized GameRegion getGameRegion(final int regionHash) {
        loaded = loaded || init();
        GameRegion r = GAME_REGION_MAP.get(regionHash);
        if (r == null) {
            GAME_REGION_MAP.put(regionHash, (r = new GameRegion(regionHash, REGION_DATA_MAP.get(regionHash))));
        }
        return r;
    }

    public static boolean init() {
        try (final FileInputStream indexStream = new FileInputStream(new File("MapData" + File.separator + "MapData.idx"))) {
            byte[] bytes = new byte[12];
            while (indexStream.read(bytes) != -1) {
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                RegionData data = new RegionData(buffer.getInt(), buffer.getInt(), buffer.getInt());
                REGION_DATA_MAP.put(data.getHash(), data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private static class RegionData {

        private final int hash;
        private final int index;
        private final int size;

        public RegionData(int hash, int index, int size) {
            this.hash = hash;
            this.index = index;
            this.size = size;
        }

        public int getHash() {
            return hash;
        }

        public int getIndex() {
            return index;
        }

        public int getSize() {
            return size;
        }
    }
}
