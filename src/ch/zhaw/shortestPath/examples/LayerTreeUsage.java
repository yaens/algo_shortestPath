package ch.zhaw.shortestPath.examples;

/*
 * Copyright (C) 2001, 2010 United States Government
 * as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwindx.examples.util.HotSpotController;
import gov.nasa.worldwind.util.layertree.LayerTree;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.util.WWUtil;

import java.awt.*;

import ch.zhaw.shortestPath.view.ApplicationTemplate;

/**
 * Example of using {@link gov.nasa.worldwind.util.tree.BasicTree} to display a list of layers.
 *
 * @author pabercrombie
 * @version $Id: LayerTreeUsage.java 1 2011-07-16 23:22:47Z dcollins $
 */
public class LayerTreeUsage extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        protected LayerTree layerTree;
        protected RenderableLayer hiddenLayer;

        protected HotSpotController controller;

        public AppFrame()
        {
            super(true, false, false); // Don't include the layer panel; we're using the on-screen layer tree.

            this.layerTree = new LayerTree();

            // Set up a layer to display the on-screen layer tree in the WorldWindow.
            this.hiddenLayer = new RenderableLayer();
            this.hiddenLayer.addRenderable(this.layerTree);
            this.getWwd().getModel().getLayers().add(this.hiddenLayer);
            
        	//Layer layer = (Layer) new OpenStreetMapWMSLayer();
            LayerList layers = this.getWwd().getModel().getLayers();
            //layer.setEnabled(true);
            
            //ApplicationTemplate.insertBeforeCompass(this.getWwd(), layer);
            //this.firePropertyChange("LayersPanelUpdated", null, layer);

            // Mark the layer as hidden to prevent it being included in the layer tree's model. Including the layer in
            // the tree would enable the user to hide the layer tree display with no way of bringing it back.
            this.hiddenLayer.setValue(AVKey.HIDDEN, true);

            // Refresh the tree model with the WorldWindow's current layer list.
            this.layerTree.getModel().refresh(this.getWwd().getModel().getLayers());

            // Add a controller to handle input events on the layer tree.
            this.controller = new HotSpotController(this.getWwd());

            // Size the World Window to take up the space typically used by the layer panel. This illustrates the
            // screen space gained by using the on-screen layer tree.
            Dimension size = new Dimension(1000, 600);
            this.setPreferredSize(size);
            this.pack();
            WWUtil.alignComponent(null, this, AVKey.CENTER);
        }
    }
    
    

    public static void main(String[] args)
    {
        ApplicationTemplate.start("World Wind Layer Tree", AppFrame.class);
    }
}