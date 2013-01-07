package ch.zhaw.shortestPath.view;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.PositionListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import ch.zhaw.shortestPath.model.Node;


public class NodeBuilder extends AVListImpl
{
	private Node node;
	private ArrayList<Node> nodeList;
    private final WorldWindow wwd;
    private boolean armed = false;
    private ArrayList<Position> positions = new ArrayList<Position>();
    private final RenderableLayer layer;
    private boolean active = false;

    /**
     * Construct a new line builder using the specified polyline and layer and drawing events from the specified world
     * window. Either or both the polyline and the layer may be null, in which case the necessary object is created.
     *
     * @param wwd       the world window to draw events from.
     * @param nodeLayer the layer holding the polyline. May be null, in which case a new layer is created.
     * @param polyline  the polyline object to build. May be null, in which case a new polyline is created.
     */
    public NodeBuilder(final WorldWindow wwd, RenderableLayer nodeLayer)
    {
        this.wwd = wwd;
        this.nodeList = new ArrayList<Node>();
        this.layer = nodeLayer != null ? nodeLayer : new RenderableLayer();
        //maybe do this later
        //this.layer.addRenderable(this.node);
        this.wwd.getModel().getLayers().add(this.layer);

        this.wwd.getInputHandler().addMouseListener(new MouseAdapter()
        {
/*            public void mousePressed(MouseEvent mouseEvent)
            {
                if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1)
                {
                    if (armed && (mouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0)
                    {
                        if (!mouseEvent.isControlDown())
                        {
                            active = true;
                            addPosition();
                        }
                    }
                    mouseEvent.consume();
                }
            }

            public void mouseReleased(MouseEvent mouseEvent)
            {
                if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1)
                {
                    if (positions.size() == 1)
                        removePosition();
                    active = false;
                    mouseEvent.consume();
                }
            }*/

            public void mouseClicked(MouseEvent mouseEvent)
            {
                if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1)
                {
                	addPosition();
                	layer.addRenderable(node);

                    if (mouseEvent.isControlDown())
                        removePosition();
                    mouseEvent.consume();
                }
            }
        });

        this.wwd.getInputHandler().addMouseMotionListener(new MouseMotionAdapter()
        {
            public void mouseDragged(MouseEvent mouseEvent)
            {
                if (armed && (mouseEvent.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0)
                {
                    // Don't update the polyline here because the wwd current cursor position will not
                    // have been updated to reflect the current mouse position. Wait to update in the
                    // position listener, but consume the event so the view doesn't respond to it.
                    if (active)
                        mouseEvent.consume();
                }
            }
        });

        this.wwd.addPositionListener(new PositionListener()
        {
            public void moved(PositionEvent event)
            {
                if (!active)
                    return;

                if (positions.size() == 1)
                    addPosition();
                else
                    replacePosition();
            }
        });
    }

    /**
     * Returns the layer holding the polyline being created.
     *
     * @return the layer containing the polyline.
     */
    public RenderableLayer getLayer()
    {
        return this.layer;
    }

    /**
     * Returns the layer currently used to display the polyline.
     *
     * @return the layer holding the polyline.
     */
    public List<Node> getNodes()
    {
        //return this.line;
    	return this.nodeList;
    }

    /**
     * Removes all positions from the polyline.
     */
    public void clear()
    {
        while (this.positions.size() > 0)
            this.removePosition();
    }

    /**
     * Identifies whether the line builder is armed.
     *
     * @return true if armed, false if not armed.
     */
    public boolean isArmed()
    {
        return this.armed;
    }

    /**
     * Arms and disarms the line builder. When armed, the line builder monitors user input and builds the polyline in
     * response to the actions mentioned in the overview above. When disarmed, the line builder ignores all user input.
     *
     * @param armed true to arm the line builder, false to disarm it.
     */
    public void setArmed(boolean armed)
    {
        this.armed = armed;
    }

    private void addPosition()
    {
    	
    	
        Position curPos = this.wwd.getCurrentPosition();
        if (curPos == null)
            return;

        this.positions.add(curPos);
        //this.line.setPositions(this.positions);
        this.node = new Node(curPos, 100);
        Color color = new Color(0f, 0f, 1f);
        ShapeAttributes attr = new BasicShapeAttributes();
        attr.setDrawOutline(true);
        attr.setInteriorMaterial(new Material(color));
        attr.setInteriorOpacity(1.0);
        attr.setOutlineMaterial(new Material(color));
        attr.setOutlineOpacity(1.0);
        attr.setOutlineWidth(2.0);
        attr.setDrawInterior(true);
        
        this.node.setAttributes(attr);
        
        nodeList.add(this.node);
        
        this.firePropertyChange("LineBuilder.AddPosition", null, curPos);
        this.wwd.redraw();
    }

    private void replacePosition()
    {
        Position curPos = this.wwd.getCurrentPosition();
        if (curPos == null)
            return;

        int index = this.positions.size() - 1;
        if (index < 0)
            index = 0;

        Position currentLastPosition = this.positions.get(index);
        this.positions.set(index, curPos);
        //this.line.setPositions(this.positions);
        this.firePropertyChange("LineBuilder.ReplacePosition", currentLastPosition, curPos);
        this.wwd.redraw();
    }

    private void removePosition()
    {
        if (this.positions.size() == 0)
            return;

        Position currentLastPosition = this.positions.get(this.positions.size() - 1);
        this.positions.remove(this.positions.size() - 1);
        //this.line.setPositions(this.positions);
        this.firePropertyChange("LineBuilder.RemovePosition", currentLastPosition, null);
        this.wwd.redraw();
    }
}
