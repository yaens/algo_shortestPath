/*
Copyright (C) 2001, 2006 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package ch.zhaw.shortestPath.view;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.SurfaceCircle;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

/**
 * A utility class to interactively build a polyline. When armed, the class monitors mouse events and adds new positions
 * to a polyline as the user identifies them. The interaction sequence for creating a line is as follows: <ul> <li> Arm
 * the line builder by calling its {@link #setArmed(boolean)} method with an argument of true. </li> <li> Place the
 * cursor at the first desired polyline position. Press and release mouse button one. </li> <li> Press button one near
 * the next desired position, drag the mouse to the exact position, then release the button. The proposed line segment
 * will echo while the mouse is dragged. Continue selecting new positions this way until the polyline contains all
 * desired positions. </li> <li> Disarm the <code>LineBuilder</code> object by calling its {@link #setArmed(boolean)}
 * method with an argument of false. </li> </ul>
 * <p/>
 * While the line builder is armed, pressing and immediately releasing mouse button one while also pressing the control
 * key (Ctl) removes the last position from the polyline. </p>
 * <p/>
 * Mouse events the line builder acts on while armed are marked as consumed. These events are mouse pressed, released,
 * clicked and dragged. These events are not acted on while the line builder is not armed. The builder can be
 * continuously armed and rearmed to allow intervening maneuvering of the globe while building a polyline. A user can
 * add positions, pause entry, maneuver the view, then continue entering positions. </p>
 * <p/>
 * Arming and disarming the line builder does not change the contents or attributes of the line builder's layer. </p>
 * <p/>
 * The polyline and a layer containing it may be specified when a <code>LineBuilder</code> is constructed. </p>
 * <p/>
 * This class contains a <code>main</code> method implementing an example program illustrating use of
 * <code>LineBuilder</code>. </p>
 *
 * @author tag
 * @version $Id: LineBuilder.java 1 2011-07-16 23:22:47Z dcollins $
 */
public class WorldWindView extends AVListImpl
{
    // ===================== Control Panel ======================= //
    // The following code is an example program illustrating LineBuilder usage. It is not required by the
    // LineBuilder class, itself.

    private static class LinePanel extends JPanel
    {
        private final WorldWindow wwd;
        
        private final NodeBuilder nodeBuilder;
        private final ConnectorBuilder connectorBuilder;
        private JButton newButton;
        private JButton newNodeButton;
        private JButton pauseButton;
        private JButton endButton;
        private JLabel[] pointLabels;
        private JLabel[] pointNodeLabels;

        public LinePanel(WorldWindow wwd, NodeBuilder nodeBuilder,ConnectorBuilder connectorBuilder)
        {
            super(new BorderLayout());
            this.wwd = wwd;
            this.nodeBuilder = nodeBuilder;
            this.connectorBuilder = connectorBuilder;
            this.makePanel(new Dimension(200, 400));
            connectorBuilder.addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent propertyChangeEvent)
                {
                    fillPointsPanel();
                }
            });
            nodeBuilder.addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent propertyChangeEvent)
                {
                    fillPointsNodePanel();
                }
            });
        }

        private void makePanel(Dimension size)
        {
        	JPanel buttonPanelNode = new JPanel(new GridLayout(1, 2, 5, 5));
        	newNodeButton = new JButton("new Node");
        	newNodeButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                	
                    //lineBuilder.clear();
                    nodeBuilder.setArmed(true);
                    connectorBuilder.setArmed(false);
                    //pauseButton.setText("Pause");
                    //pauseButton.setEnabled(true);
                    //endButton.setEnabled(true);
                    //newButton.setEnabled(false);
                    ((Component) wwd).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                }
            });
        	
        	buttonPanelNode.add(newNodeButton);
        	
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
            newButton = new JButton("New");
            newButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                	
                	connectorBuilder.clear();
                    nodeBuilder.setArmed(false);
                    connectorBuilder.setArmed(true);
                    pauseButton.setText("Pause");
                    pauseButton.setEnabled(true);
                    endButton.setEnabled(true);
                    newButton.setEnabled(false);
                    ((Component) wwd).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                }
            });
            buttonPanel.add(newButton);
            newButton.setEnabled(true);

            pauseButton = new JButton("Pause");
            pauseButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                	connectorBuilder.setArmed(!connectorBuilder.isArmed());
                    pauseButton.setText(!connectorBuilder.isArmed() ? "Resume" : "Pause");
                    ((Component) wwd).setCursor(Cursor.getDefaultCursor());
                }
            });
            buttonPanel.add(pauseButton);
            pauseButton.setEnabled(false);

            endButton = new JButton("End");
            endButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                	connectorBuilder.setArmed(false);
                    newButton.setEnabled(true);
                    pauseButton.setEnabled(false);
                    pauseButton.setText("Pause");
                    endButton.setEnabled(false);
                    ((Component) wwd).setCursor(Cursor.getDefaultCursor());
                }
            });
            buttonPanel.add(endButton);
            endButton.setEnabled(false);

            JPanel pointPanel = new JPanel(new GridLayout(0, 1, 0, 10));
            pointPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            this.pointLabels = new JLabel[20];
            for (int i = 0; i < this.pointLabels.length; i++)
            {
                this.pointLabels[i] = new JLabel("");
                pointPanel.add(this.pointLabels[i]);
            }

            // Put the point panel in a container to prevent scroll panel from stretching the vertical spacing.
            JPanel dummyPanel = new JPanel(new BorderLayout());
            dummyPanel.add(pointPanel, BorderLayout.NORTH);

            // Put the point panel in a scroll bar.
            JScrollPane scrollPane = new JScrollPane(dummyPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            if (size != null)
                scrollPane.setPreferredSize(size);
            
            ///////
            JPanel pointNodePanel = new JPanel(new GridLayout(0, 1, 0, 10));
            pointNodePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            this.pointNodeLabels = new JLabel[20];
            for (int i = 0; i < this.pointNodeLabels.length; i++)
            {
                this.pointNodeLabels[i] = new JLabel("");
                pointNodePanel.add(this.pointNodeLabels[i]);
            }

            // Put the point panel in a container to prevent scroll panel from stretching the vertical spacing.
            JPanel dummyNodePanel = new JPanel(new BorderLayout());
            dummyNodePanel.add(pointNodePanel, BorderLayout.NORTH);

            // Put the point panel in a scroll bar.
            JScrollPane scrollNodePane = new JScrollPane(dummyNodePanel);
            scrollNodePane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            if (size != null)
            	scrollNodePane.setPreferredSize(size);
            
            ////////////////

            // Add the buttons, scroll bar and inner panel to a titled panel that will resize with the main window.
            JPanel outerPanel = new JPanel(new BorderLayout());
            outerPanel.setBorder(
                new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("Line")));
            outerPanel.setToolTipText("Line control and info");
            outerPanel.add(buttonPanel, BorderLayout.NORTH);
            outerPanel.add(scrollPane, BorderLayout.CENTER);
            
            
            JPanel outerNodePanel = new JPanel(new BorderLayout());
            outerNodePanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(9,9,9,9), new TitledBorder("Node")));
            outerNodePanel.add(buttonPanelNode,BorderLayout.SOUTH);
            outerNodePanel.add(scrollNodePane);
            
            this.add(outerPanel, BorderLayout.CENTER);
            this.add(outerNodePanel,BorderLayout.SOUTH);
        }

        private void fillPointsPanel()
        {
            int i = 0;
            for (Position pos : connectorBuilder.getLine().getPositions())
            {
                if (i == this.pointLabels.length)
                    break;

                String las = String.format("Lat %7.4f\u00B0", pos.getLatitude().getDegrees());
                String los = String.format("Lon %7.4f\u00B0", pos.getLongitude().getDegrees());
                pointLabels[i++].setText(las + "  " + los);
            }
            for (; i < this.pointLabels.length; i++)
                pointLabels[i++].setText("");
        }
        
        private void fillPointsNodePanel()
        {
            int i = 0;
            for (SurfaceCircle pos : nodeBuilder.getNodes())
            {
                if (i == this.pointLabels.length)
                    break;

                String las = String.format("Lat %7.4f\u00B0", pos.getCenter().getLatitude().getDegrees());
                String los = String.format("Lon %7.4f\u00B0", pos.getCenter().getLongitude().getDegrees());
                pointNodeLabels[i++].setText(las + "  " + los);
            }
            for (; i < this.pointNodeLabels.length; i++)
            	pointNodeLabels[i++].setText("");
        }
    }

    /**
     * Marked as deprecated to keep it out of the javadoc.
     *
     * @deprecated
     */
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, false, false);
            ConnectorBuilder connectorBuilder = new ConnectorBuilder(this.getWwd(), null);
            NodeBuilder nodeBuilder = new NodeBuilder(this.getWwd(),null);
            this.getContentPane().add(new LinePanel(this.getWwd(),nodeBuilder,connectorBuilder), BorderLayout.WEST);
        }
    }

    /**
     * Marked as deprecated to keep it out of the javadoc.
     *
     * @param args the arguments passed to the program.
     * @deprecated
     */
    public static void main(String[] args)
    {
        //noinspection deprecation
        ApplicationTemplate.start("World Wind Line Builder", WorldWindView.AppFrame.class);
    }
}
