package src.pathfinder.display.swing;

import src.pathfinder.impl.Pathfinder;
import src.pathfinder.core.util.GameRegion;
import src.pathfinder.core.util.Structure;
import src.pathfinder.core.wrapper.PathNode;
import src.pathfinder.core.wrapper.TilePath;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

/**
 * Author: Tom
 * Date: 03/09/13
 * Time: 03:03
 */
public class DisplayPane extends JPanel {

    public static final String IMAGE_URL = "http://www.runescape.com/img/main/downloads_and_media/downloads_and_wallpapers/rs_map/rsmap-08july2013-en.png";
    public static BufferedImage IMAGE;
    private static final Pathfinder PATHFINDER = new Pathfinder();

    public double scale = 1.0;
    private TilePath tilePath;

    private int prevClick = -1;

    public DisplayPane() {
        this.add(new JLabel(new ImageIcon(IMAGE)));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int tile = toTile(e.getPoint());
                if (prevClick != -1) {
                    tilePath = PATHFINDER.findPath(prevClick, tile, 1000, true);
                    prevClick = -1;
                    repaint();
                    return;
                }
                prevClick = tile;
                repaint();
            }
        });
        tilePath = new Pathfinder().findPath(Structure.TILE.getHash(3186, 3437, 0), Structure.TILE.getHash(3237, 3160, 0), 1000, false);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (GameRegion region : new LinkedList<>(GameRegion.getLoaded())) {
            for (int tx = 0; tx < 64; tx++) {
                for (int ty = 0; ty < 64; ty++) {
                    Point point = toPixel(Structure.TILE.getHash(region.getBaseX() + tx, region.getBaseY() + ty, 0));
                    g.setColor(Color.WHITE);
                    // g.fillRect(point.x, point.y, 4, 4);
                    int flag = region.getWalkData(tx, ty, 0);
                    g.setColor(Color.RED);
                    if ((flag & UNWALKABLE) == UNWALKABLE) {
                        g.fillRect(point.x, point.y, 1, 1);
                    } else {
                        if ((flag & WALK_NORTH) != WALK_NORTH) {
                            g.fillRect(point.x, point.y, 1, 1);
                        }
                        if ((flag & WALK_EAST) != WALK_EAST) {
                            g.fillRect(point.x + 1, point.y, 1, 1);
                        }
                        if ((flag & WALK_SOUTH) != WALK_SOUTH) {
                            g.fillRect(point.x, point.y + 1, 1, 1);
                        }
                        if ((flag & WALK_WEST) != WALK_WEST) {
                            g.fillRect(point.x, point.y, 1, 1);
                        }
                        g.setColor(Color.GREEN);
                        if ((flag & DOOR_NORTH) == DOOR_NORTH) {
                            g.fillRect(point.x, point.y, 1, 1);
                        }
                        if ((flag & DOOR_EAST) == DOOR_EAST) {
                            g.fillRect(point.x + 1, point.y, 1, 1);
                        }
                        if ((flag & DOOR_SOUTH) == DOOR_SOUTH) {
                            g.fillRect(point.x, point.y + 1, 1, 1);
                        }
                        if ((flag & DOOR_WEST) == DOOR_WEST) {
                            g.fillRect(point.x, point.y, 1, 1);
                        }

                    }
                }
            }
        }

        if (prevClick != -1) {
            Point p = toPixel(prevClick);
            g.setColor(Color.RED);
            g.drawRect(p.x - 1, p.y - 1, 2, 2);
        }
        if (tilePath != null) {
            for (PathNode pathNode : tilePath) {
                Point p = toPixel(pathNode.getHash());
                g.setColor(Color.ORANGE);
                g.drawRect(p.x - 1, p.y - 1, 2, 2);
            }
        }

    }


    public static final int WALK_NORTH = 1;
    public static final int WALK_NORTH_EAST = 2;
    public static final int WALK_EAST = 4;
    public static final int WALK_SOUTH_EAST = 8;
    public static final int WALK_SOUTH = 16;
    public static final int WALK_SOUTH_WEST = 32;
    public static final int WALK_WEST = 0b1000000;
    public static final int WALK_NORTH_WEST = 0b10000000;
    public static final int UNWALKABLE = 0b100000000;
    public static final int DOOR_NORTH = 512;
    public static final int DOOR_EAST = 1024;
    public static final int DOOR_SOUTH = 2048;
    public static final int DOOR_WEST = 4096;

    static {
        File f = new File("RSMap2.png");
        try {
            if (f.exists()) {
                IMAGE = ImageIO.read(f);
            } else {
                System.out.println("Downloading image");
                IMAGE = ImageIO.read(new URL(IMAGE_URL));
                ImageIO.write(IMAGE, "png", f);
                System.out.println("Downloaded image");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public int toTile(final Point point) {
        final double worldMapTilesX = 2047.5D;
        final double worldMapTilesY = 4159.0D;
        final double worldMapTilesWidth = 3904.0D - worldMapTilesX;
        final double worldMapTilesHeight = 2494.5D - worldMapTilesY;
        final double worldMapPixelsWidth = IMAGE.getWidth();
        final double worldMapPixelsHeight = IMAGE.getHeight();
        final double pixelsPerTileWidth = worldMapPixelsWidth
                / worldMapTilesWidth;
        final double pixelsPerTileHeight = worldMapPixelsHeight
                / worldMapTilesHeight;
        return Structure.TILE.getHash((int) (Math.nextUp(worldMapTilesX + point.x
                / pixelsPerTileWidth / scale)), (int) (Math.nextUp(worldMapTilesY
                + point.y / pixelsPerTileHeight / scale)), 0);
    }

    public Point toPixel(final int hash) {
        final Point point = new Point(Structure.TILE.getX(hash), Structure.TILE.getY(hash));
        final double worldMapTilesX = 2047.5D;
        final double worldMapTilesY = 4159.0D;
        final double worldMapTilesWidth = 3904.0D - worldMapTilesX;
        final double worldMapTilesHeight = 2494.5D - worldMapTilesY;
        final double worldMapPixelsWidth = IMAGE.getWidth();
        final double worldMapPixelsHeight = IMAGE.getHeight();
        final double pixelsPerTileWidth = worldMapPixelsWidth
                / worldMapTilesWidth;
        final double pixelsPerTileHeight = worldMapPixelsHeight
                / worldMapTilesHeight;
        final Point tile = new Point(
                (int) (Math.nextUp((point.x - worldMapTilesX)
                        * pixelsPerTileWidth * scale)),
                (int) (Math.nextUp((point.y - worldMapTilesY)
                        * pixelsPerTileHeight * scale)));
        return tile;
    }


}
