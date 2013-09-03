package src.pathfinder.display;

import src.pathfinder.display.swing.DisplayPane;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Author: Tom
 * Date: 03/09/13
 * Time: 03:03
 */
public class PathfinderDisplay {

    public static void main(final String... args) {
        Logger.getGlobal().setLevel(Level.INFO);
        JFrame frame = new JFrame("Rs Pathfinder by Overflow");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(new DisplayPane()));
        frame.pack();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}
