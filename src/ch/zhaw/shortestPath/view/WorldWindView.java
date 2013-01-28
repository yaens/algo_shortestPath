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
import gov.nasa.worldwind.layers.*;

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

import ch.zhaw.shortestPath.model.DijkstraAlgorithm;

public class WorldWindView extends AVListImpl
{
    // ===================== Control Panel ======================= //
    // The following code is an example program illustrating LineBuilder usage. It is not required by the
    // LineBuilder class, itself.

    private static class LinePanel extends JPanel
    {
        private final WorldWindow wwd;
        
        private NodeBuilder nodeBuilder;
        private ConnectorBuilder connectorBuilder;
        private JButton newButton;
        private JButton newNodeButton;
        private JButton pauseButton;
        private JButton endButton;
        private JLabel[] pointLabels;
        private JLabel[] pointNodeLabels;

		private JButton endNodeButton;

		private JButton buttonStartAlgoDijkstra;

        public LinePanel(WorldWindow wwd)
        {
            super(new BorderLayout());
            
            this.wwd = wwd;
            LayerList layers = this.wwd.getModel().getLayers();
            layers.add(new OSMMapnikLayer());
            
            
            RenderableLayer nodeLayer = new RenderableLayer();
            nodeLayer.setName("Node Layer");
            RenderableLayer connectorLayer = new RenderableLayer();
            nodeLayer.setName("connector Layer");
            
            layers.add(connectorLayer);
            layers.add(nodeLayer);
            
            layers = this.wwd.getModel().getLayers();
            
            this.connectorBuilder = new ConnectorBuilder(wwd, connectorLayer);
            this.nodeBuilder = new NodeBuilder(wwd,nodeLayer);
            

            
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

            this.addExtraLayer();
        }
        
        private void addExtraLayer(){
        	//Layer layer = (Layer) new OpenStreetMapWMSLayer();
            LayerList layers = this.wwd.getModel().getLayers();
            //layer.setEnabled(true);
            
            //ApplicationTemplate.insertBeforeCompass(this.wwd, layer);
            //this.firePropertyChange("LayersPanelUpdated", null, layer);
            for(Layer layer:layers){
            	//layer.setEnabled(false);
            	if(layer.getName().contains("Place")){
            		layer.setEnabled(false);
            	}
            	if(layer.getName().contains("Landsat")){
            		layer.setEnabled(false);
            	}
            }
            
            wwd.redraw();
        }

        private void makePanel(Dimension size)
        {
        	JPanel buttonPanelNode = new JPanel(new GridLayout(1, 2, 5, 5));
        	buttonStartAlgoDijkstra = new JButton("Dijkstra");
        	newNodeButton = new JButton("new Node");
        	endNodeButton = new JButton("Stop");
        	
        	
        	buttonStartAlgoDijkstra.addActionListener(new ActionListener(){
        		public void actionPerformed(ActionEvent actionEvent)
                {
        			DijkstraAlgorithm.work(connectorBuilder.getAllConnector(), nodeBuilder.getNodes().get(0),nodeBuilder.getNodes());
        			DijkstraAlgorithm.getShortestPath(nodeBuilder.getNodes().get(0), nodeBuilder.getNodes().get(nodeBuilder.getNodes().size()-1));
                }
        	});
        	
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
        	endNodeButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                	
                    //lineBuilder.clear();
                    nodeBuilder.setArmed(false);
                    connectorBuilder.setArmed(false);
                    //pauseButton.setText("Pause");
                    //pauseButton.setEnabled(true);
                    //endButton.setEnabled(true);
                    //newButton.setEnabled(false);
                    ((Component) wwd).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            });
        	
        	buttonPanelNode.add(newNodeButton);
        	buttonPanelNode.add(endNodeButton);
        	buttonPanelNode.add(buttonStartAlgoDijkstra);
        	
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
            /*int i = 0;
            for (Position pos : connectorBuilder.getLine().getPositions())
            {
                if (i == this.pointLabels.length)
                    break;

                String las = String.format("Lat %7.4f\u00B0", pos.getLatitude().getDegrees());
                String los = String.format("Lon %7.4f\u00B0", pos.getLongitude().getDegrees());
                pointLabels[i++].setText(las + "  " + los);
            }
            for (; i < this.pointLabels.length; i++)
                pointLabels[i++].setText("");*/
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
            LayerList layers = this.getWwd().getModel().getLayers();
            AnnotationLayer anLayer = new AnnotationLayer();
            anLayer.setName("Label Layer");
            layers.add(anLayer);
            this.getContentPane().add(new LinePanel(this.getWwd()), BorderLayout.WEST);
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
        ApplicationTemplate.start("Shortest Path Demo Application", WorldWindView.AppFrame.class);
    }
}
