package src.pathfinder.core.util;

/**
 * Author: Tom
 * Date: 03/09/13
 * Time: 01:14
 */
public enum WalkDirection {
    NORTH(0, 1, 1, 512),
    //NORTH_EAST(1, 1, 2),
    EAST(1, 0, 4, 1024),
    //SOUTH_EAST(1, -1, 8),
    SOUTH(0, -1, 16, 2048),
    //SOUTH_WEST(-1, -1, 32),
    WEST(-1, 0, 64, 4096);
    //NORTH_WEST(-1, 1, 128);

    private final int offsetX;
    private final int offsetY;
    private final int canWalkFlag;
    private final int passableObjectFlag;

    private WalkDirection(final int offsetX, final int offsetY, final int canWalkFlag, final int passableObjectFlag) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.canWalkFlag = canWalkFlag;
        this.passableObjectFlag = passableObjectFlag;
    }

    private WalkDirection(final int offsetX, final int offsetY, int canWalkFlag) {
        this(offsetX, offsetY, canWalkFlag, 0);
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public WalkType getWalkType(final int flag) {
        return ((flag & 256) == 256) ? WalkType.FAIL : (flag & canWalkFlag) == canWalkFlag ? WalkType.WALK : (flag & passableObjectFlag) == passableObjectFlag ? WalkType.OBJECT : WalkType.FAIL;
    }

    public enum WalkType {
        FAIL, WALK, OBJECT
    }
}
