/*
Copyright (C) 2001, 2006 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package ch.zhaw.shortestPath.view;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.SurfaceCircle;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwindx.applications.worldwindow.util.Util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import ch.zhaw.shortestPath.model.Connector;
import ch.zhaw.shortestPath.model.DijkstraAlgorithm;
import ch.zhaw.shortestPath.model.Node;

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
        
        private Map<String,Node> comboEntrys;

		private JButton endNodeButton;

		private JButton buttonStartAlgoDijkstra;

		private JButton buttonStartBellman;

		private JComboBox chooseEndPoint;

        public LinePanel(WorldWindow wwd)
        {
            super(new BorderLayout());
            
            this.comboEntrys = new HashMap<String, Node>();
            
            this.wwd = wwd;
            this.wwd.setValue(AVKey.INITIAL_LATITUDE, 49.06);
            this.wwd.setValue(AVKey.INITIAL_LONGITUDE, -122.77);
            this.wwd.setValue(AVKey.INITIAL_ALTITUDE, 22000);
            LayerList layers = this.wwd.getModel().getLayers();
            layers.add(new OSMMapnikLayer());
            
            
            RenderableLayer nodeLayer = new RenderableLayer();
            nodeLayer.setName("Node Layer");
            RenderableLayer connectorLayer = new RenderableLayer();
            nodeLayer.setName("connector Layer");
    		MarkerLayer markerLayer = new MarkerLayer();
            markerLayer.setName("marker Layer");
    		
            layers.add(connectorLayer);
            layers.add(nodeLayer);
            layers.add(markerLayer);
            ApplicationTemplate.insertBeforeCompass(wwd, connectorLayer);
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
                    fillNodePanel();
                }
            });

            this.addExtraLayer();
        }
        
        private void addExtraLayer(){
        	//Layer layer = (Layer) new OpenStreetMapWMSLayer();
            LayerList layers = this.wwd.getModel().getLayers();
       
            
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
            	if(layer.getName().contains("Open")){
            		layer.setEnabled(true);
            	}
            }
            
            wwd.redraw();
        }

        private void makePanel(Dimension size)
        {
        	JPanel algoPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        	buttonStartAlgoDijkstra = new JButton("Dijkstra");
        	buttonStartBellman = new JButton("Bellman");
        	chooseEndPoint = new JComboBox();
        	JLabel endpointLabel = new JLabel("Endpoint");
        	algoPanel.add(buttonStartAlgoDijkstra);
        	algoPanel.add(buttonStartBellman);
        	algoPanel.add(endpointLabel);
        	algoPanel.add(chooseEndPoint);
        	
        	
        	JPanel buttonPanelNode = new JPanel(new GridLayout(1, 2, 5, 5));
        	newNodeButton = new JButton("new Node");
        	endNodeButton = new JButton("Stop");
        	
        	
        	buttonStartAlgoDijkstra.addActionListener(new ActionListener(){
        		public void actionPerformed(ActionEvent actionEvent)
                {
        			Node endPoint = (Node)chooseEndPoint.getSelectedItem();
        			DijkstraAlgorithm.work(connectorBuilder.getAllConnector(), nodeBuilder.getNodes().get(0),nodeBuilder.getNodes());
        			List<Node> shortestPath = DijkstraAlgorithm.getShortestPath(nodeBuilder.getNodes().get(0), endPoint);
        			connectorBuilder.paintConnectors(shortestPath);
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

            this.pointLabels = new JLabel[30];
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
            scrollPane.setPreferredSize(new Dimension(100, 190));
            
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
            scrollNodePane.setPreferredSize(new Dimension(100, 190));
            
            //algoPanel.setPreferredSize(new Dimension(100, 25));
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
            outerNodePanel.add(buttonPanelNode,BorderLayout.NORTH);
            outerNodePanel.add(scrollNodePane,BorderLayout.CENTER);
            
            JPanel outerAlgoPanel = new JPanel(new BorderLayout());
            outerAlgoPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(9,9,9,9), new TitledBorder("Algorithms")));
            outerAlgoPanel.add(algoPanel,BorderLayout.CENTER);
            
            
            this.add(outerAlgoPanel, BorderLayout.SOUTH);
            this.add(outerPanel, BorderLayout.NORTH);
            this.add(outerNodePanel,BorderLayout.CENTER);
        }

        private void fillPointsPanel()
        {
            int i = 0;
            for (Connector pos : connectorBuilder.getAllConnector())
            {
                if (i == this.pointLabels.length)
                    break;
                
                String from = pos.getFrom().getName();
                String to = pos.getTo().getName();
                String dis = String.valueOf((int)pos.getDistance());
                
                pointLabels[i++].setText(from + " - " + to + ": Distance : " + dis + " m");
            }
            for (; i < this.pointLabels.length; i++)
                pointLabels[i++].setText("");
        }
        
        private void fillNodePanel()
        {
            int i = 0;
            for (Node pos : nodeBuilder.getNodes())
            {
                if (i == this.pointLabels.length)
                    break;
                String name = pos.getName();
                String las = String.format("Lat %7.4f\u00B0", pos.getCenter().getLatitude().getDegrees());
                String los = String.format("Lon %7.4f\u00B0", pos.getCenter().getLongitude().getDegrees());
                pointNodeLabels[i++].setText("Name: "+ name);
                
                if(!this.comboEntrys.containsKey(pos.getName())&&!pos.getName().equals("A")){
                	this.chooseEndPoint.addItem(pos);
                	this.comboEntrys.put(pos.getName(), pos);
                }
                
                
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
