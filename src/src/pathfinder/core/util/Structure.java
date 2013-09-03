package src.pathfinder.core.util;

/**
 * Author: Tom
 * Date: 03/09/13
 * Time: 00:27
 */
public enum Structure {
    TILE() {
        @Override
        public int getHash(int x, int y, int z) {
            return (x << 18) | (y << 4) | z;
        }

        @Override
        public int getX(int hash) {
            return (hash >>> 18) & 0x3fff;
        }

        @Override
        public int getY(int hash) {
            return (hash >>> 4) & 0x3fff;
        }

        @Override
        public int getZ(int hash) {
            return hash & 0x3;
        }
    },
    REGION() {
        @Override
        public int getHash(int x, int y, int z) {
            return x | (y << 7);
        }

        @Override
        public int getX(int hash) {
            return hash & (0x7f);
        }

        @Override
        public int getY(int hash) {
            return (hash >> 7) & 0x7f;
        }
    };

    public abstract int getHash(final int x, final int y, final int z);

    public abstract int getX(final int hash);

    public abstract int getY(final int hash);

    public int getZ(final int hash) {
        return -1;
    }
}
