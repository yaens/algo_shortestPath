package ch.zhaw.shortestPath.view;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.WorldMapLayer;
import gov.nasa.worldwind.util.StatusBar;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.util.layertree.LayerTree;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import gov.nasa.worldwindx.examples.LayerPanel;
import gov.nasa.worldwindx.examples.util.HotSpotController;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class WorldWindView {
    public static class AppPanel extends JPanel
    {
        private WorldWindowGLCanvas wwd;

        // Constructs a JPanel to hold the WorldWindow
        public AppPanel(Dimension canvasSize, boolean includeStatusBar)
        {
            super(new BorderLayout());

            // Create the WorldWindow and set its preferred size.
            this.wwd = new WorldWindowGLCanvas();
            this.wwd.setPreferredSize(canvasSize);

            // THIS IS THE TRICK: Set the panel's minimum size to (0,0);
            this.setMinimumSize(new Dimension(0, 0));

            // Create the default model as described in the current worldwind properties.
            Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
            this.wwd.setModel(m);

            // Setup a select listener for the worldmap click-and-go feature
            this.wwd.addSelectListener(new ClickAndGoSelectListener(this.wwd, WorldMapLayer.class));

            // Add the WorldWindow to this JPanel.
            this.add(this.wwd, BorderLayout.CENTER);

            // Add the status bar if desired.
            if (includeStatusBar)
            {
                StatusBar statusBar = new StatusBar();
                this.add(statusBar, BorderLayout.PAGE_END);
                statusBar.setEventSource(wwd);
            }
        }
    }
    
    public static class CommandPanel extends JPanel
    {
    	public CommandPanel(){
    		super(new FlowLayout());
    		this.setMinimumSize(new Dimension(0, 0));
    		
    		JButton button = new JButton("bla");
    		this.add(button);
    	}
    }

    private static class AppFrame extends JFrame
    {
        private Dimension canvasSize = new Dimension(800, 600); // the desired WorldWindow size

        public AppFrame()
        {
            // Create the WorldWindow.
            final AppPanel wwjPanel = new AppPanel(this.canvasSize, true);
            //LayerPanel layerPanel = new LayerPanel(wwjPanel.wwd);
            final CommandPanel cmdPanel = new CommandPanel();
            
            // Create a horizontal split pane containing the layer panel and the WorldWindow panel.
            JSplitPane horizontalSplitPane = new JSplitPane();
            horizontalSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            horizontalSplitPane.setLeftComponent(cmdPanel);
            horizontalSplitPane.setRightComponent(wwjPanel);
            horizontalSplitPane.setOneTouchExpandable(true);
            horizontalSplitPane.setContinuousLayout(true); // prevents the pane's being obscured when expanding right

            // Create a panel for the bottom component of a vertical split-pane.
            JPanel bottomPanel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("Bottom Panel");
            label.setBorder(new EmptyBorder(10, 10, 10, 10));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            bottomPanel.add(label, BorderLayout.CENTER);

            // Add the vertical split-pane to the frame.
            this.getContentPane().add(horizontalSplitPane, BorderLayout.CENTER);
            this.pack();

            // Center the application on the screen.
            Dimension prefSize = this.getPreferredSize();
            Dimension parentSize;
            java.awt.Point parentLocation = new java.awt.Point(0, 0);
            parentSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
            int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;
            this.setLocation(x, y);
            this.setResizable(true);
        }
    }

    public static void start(String appName)
    {
        if (Configuration.isMacOS() && appName != null)
        {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
        }

        try
        {
            final AppFrame frame = new AppFrame();
            frame.setTitle(appName);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            java.awt.EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {
                    frame.setVisible(true);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
