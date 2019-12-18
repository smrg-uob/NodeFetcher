package xvh.gui;

import javax.swing.*;

public class GuiLauncher {
    public static void launch() {
        JFrame frame = new JFrame("Node Fetcher");
        frame. setContentPane(new NodeFetcherGui(frame).getPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
