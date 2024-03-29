/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwindx.examples.tutorial;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickSupport;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.OGLUtil;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import javax.media.opengl.GL;
import java.awt.*;

/**
 * Example of a custom {@link Renderable} that draws a cube at a geographic position. This class shows the simplest
 * possible example of a Renderable, while still following World Wind best practices.
 * <p/>
 * What happens when the cube is rendered:
 * <p/>
 * 1) Scene controller calls render in picking mode. 2) Scene controller calls render in normal rendering mode. 3) Scene
 * controller calls render in ordered rendering mode.
 *
 * @author pabercrombie
 * @version $Id: Cube.java 691 2012-07-12 19:17:17Z pabercrombie $
 */
public class Cube extends ApplicationTemplate implements OrderedRenderable
{
    /** Geographic position of the cube. */
    protected Position position;
    /** Length of each face, in meters. */
    protected double size;

    /** Support object to help with pick resolution. */
    protected PickSupport pickSupport = new PickSupport();

    // Determined each frame
    protected long frameTimestamp = -1L;
    /** Cartesian position of the cube, computed from {@link #position}. */
    protected Vec4 placePoint;
    /** Distance from the eye point to the cube. */
    protected double eyeDistance;
    protected Extent extent;

    public Cube(Position position, double sizeInMeters)
    {
        this.position = position;
        this.size = sizeInMeters;
    }

    public void render(DrawContext dc)
    {
        // Render is called three times:
        // 1) During picking. The cube is drawn in a single color.
        // 2) As a normal renderable. The cube is added to the ordered renderable queue.
        // 3) As an OrderedRenderable. The cube is drawn.

        if (this.extent != null)
        {
            if (!this.intersectsFrustum(dc))
                return;

            // If the shape is less that a pixel in size, don't render it.
            if (dc.isSmall(this.extent, 1))
                return;
        }

        if (dc.isOrderedRenderingMode())
            this.drawOrderedRenderable(dc, this.pickSupport);
        else
            this.makeOrderedRenderable(dc);
    }

    /**
     * Determines whether the cube intersects the view frustum.
     *
     * @param dc the current draw context.
     *
     * @return true if this cube intersects the frustum, otherwise false.
     */
    protected boolean intersectsFrustum(DrawContext dc)
    {
        if (this.extent == null)
            return true; // don't know the visibility, shape hasn't been computed yet

        if (dc.isPickingMode())
            return dc.getPickFrustums().intersectsAny(this.extent);

        return dc.getView().getFrustumInModelCoordinates().intersects(this.extent);
    }

    public double getDistanceFromEye()
    {
        return this.eyeDistance;
    }

    public void pick(DrawContext dc, Point pickPoint)
    {
        // Use same code for rendering and picking.
        this.render(dc);
    }

    /**
     * Setup drawing state in preparation for drawing the cube. State changed by this method must be restored in
     * endDrawing.
     *
     * @param dc Active draw context.
     */
    protected void beginDrawing(DrawContext dc)
    {
        GL gl = dc.getGL();

        int attrMask = GL.GL_CURRENT_BIT | GL.GL_COLOR_BUFFER_BIT;

        gl.glPushAttrib(attrMask);

        if (!dc.isPickingMode())
        {
            dc.beginStandardLighting();
            gl.glEnable(GL.GL_BLEND);
            OGLUtil.applyBlending(gl, false);

            // Were applying a scale transform on the modelview matrix, so the normal vectors must be re-normalized
            // before lighting is computed.
            gl.glEnable(GL.GL_NORMALIZE);
        }

        // Multiply the modelview matrix by a surface orientation matrix to set up a local coordinate system with the
        // origin at the cube's center position, the Y axis pointing North, the X axis pointing East, and the Z axis
        // normal to the globe.
        gl.glMatrixMode(GL.GL_MODELVIEW);

        Matrix matrix = dc.getGlobe().computeSurfaceOrientationAtPosition(this.position);
        matrix = dc.getView().getModelviewMatrix().multiply(matrix);

        double[] matrixArray = new double[16];
        matrix.toArray(matrixArray, 0, false);
        gl.glLoadMatrixd(matrixArray, 0);
    }

    /**
     * Restore drawing state changed in beginDrawing to the default.
     *
     * @param dc Active draw context.
     */
    protected void endDrawing(DrawContext dc)
    {
        GL gl = dc.getGL();

        if (!dc.isPickingMode())
            dc.endStandardLighting();

        gl.glPopAttrib();
    }

    /**
     * Compute per-frame attributes, and add the ordered renderable to the ordered renderable list.
     *
     * @param dc Current draw context.
     */
    protected void makeOrderedRenderable(DrawContext dc)
    {
        // This method is called twice each frame: once during picking and once during rendering. We only need to
        // compute the placePoint and eye distance once per frame, so check the frame timestamp to see if this is a
        // new frame.
        if (dc.getFrameTimeStamp() != this.frameTimestamp)
        {
            // Convert the cube's geographic position to a position in Cartesian coordinates.
            this.placePoint = dc.getGlobe().computePointFromPosition(this.position);

            // Compute the distance from the eye to the cube's position.
            this.eyeDistance = dc.getView().getEyePoint().distanceTo3(this.placePoint);

            // Compute a sphere that encloses the cube. We'll use this sphere for intersection calculations to determine
            // if the cube is actually visible.
            this.extent = new Sphere(this.placePoint, Math.sqrt(3.0) * this.size / 2.0);

            this.frameTimestamp = dc.getFrameTimeStamp();
        }

        // Add the cube to the ordered renderable list. The SceneController sorts the ordered renderables by eye
        // distance, and then renders them back to front. render will be called again in ordered rendering mode, and at
        // that point we will actually draw the cube.
        dc.addOrderedRenderable(this);
    }

    /**
     * Set up drawing state, and draw the cube. This method is called when the cube is rendered in ordered rendering
     * mode.
     *
     * @param dc Current draw context.
     */
    protected void drawOrderedRenderable(DrawContext dc, PickSupport pickCandidates)
    {
        this.beginDrawing(dc);
        try
        {
            GL gl = dc.getGL();
            if (dc.isPickingMode())
            {
                Color pickColor = dc.getUniquePickColor();
                pickCandidates.addPickableObject(pickColor.getRGB(), this, this.position);
                gl.glColor3ub((byte) pickColor.getRed(), (byte) pickColor.getGreen(), (byte) pickColor.getBlue());
            }

            // Render a unit cube and apply a scaling factor to scale the cube to the appropriate size.
            gl.glScaled(this.size, this.size, this.size);
            this.drawUnitCube(dc);
        }
        finally
        {
            this.endDrawing(dc);
        }
    }

    /**
     * Draw a unit cube, using the active modelview matrix to orient the shape.
     *
     * @param dc Current draw context.
     */
    protected void drawUnitCube(DrawContext dc)
    {
        // Vertices of a unit cube, centered on the origin.
        float[][] v = {{-0.5f, 0.5f, -0.5f}, {-0.5f, 0.5f, 0.5f}, {0.5f, 0.5f, 0.5f}, {0.5f, 0.5f, -0.5f},
            {-0.5f, -0.5f, 0.5f}, {0.5f, -0.5f, 0.5f}, {0.5f, -0.5f, -0.5f}, {-0.5f, -0.5f, -0.5f}};

        // Array to group vertices into faces
        int[][] faces = {{0, 1, 2, 3}, {2, 5, 6, 3}, {1, 4, 5, 2}, {0, 7, 4, 1}, {0, 7, 6, 3}, {4, 7, 6, 5}};

        // Normal vectors for each face
        float[][] n = {{0, 1, 0}, {1, 0, 0}, {0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {0, -1, 0}};

        // Note: draw the cube in OpenGL immediate mode for simplicity. Real applications should use vertex arrays
        // or vertex buffer objects to achieve better performance.
        GL gl = dc.getGL();
        gl.glBegin(GL.GL_QUADS);
        try
        {
            for (int i = 0; i < faces.length; i++)
            {
                gl.glNormal3f(n[i][0], n[i][1], n[i][2]);

                for (int j = 0; j < faces[0].length; j++)
                {
                    gl.glVertex3f(v[faces[i][j]][0], v[faces[i][j]][1], v[faces[i][j]][2]);
                }
            }
        }
        finally
        {
            gl.glEnd();
        }
    }

    protected static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, false, false);

            RenderableLayer layer = new RenderableLayer();
            Cube cube = new Cube(Position.fromDegrees(35.0, -120.0, 3000), 1000);
            layer.addRenderable(cube);

            getWwd().getModel().getLayers().add(layer);
        }
    }

    public static void main(String[] args)
    {
        Configuration.setValue(AVKey.INITIAL_LATITUDE, 35.0);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, -120.0);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 15500);
        Configuration.setValue(AVKey.INITIAL_PITCH, 45);
        Configuration.setValue(AVKey.INITIAL_HEADING, 45);

        ApplicationTemplate.start("World Wind Custom Renderable Tutorial", AppFrame.class);
    }
}
