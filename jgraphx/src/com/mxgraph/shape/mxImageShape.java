/**
 * $Id: mxImageShape.java,v 1.4 2010-08-11 10:33:49 david Exp $
 * Copyright (c) 2007-2010, Gaudenz Alder, David Benson
 */
package com.mxgraph.shape;

import java.awt.Color;
import java.awt.Rectangle;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

/**
 * A rectangular shape that contains a single image
 */
public class mxImageShape extends mxRectangleShape
{

	/**
	 * 
	 */
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		super.paintShape(canvas, state);

		canvas.drawImage(getImageBounds(canvas, state), getImageForStyle(
				canvas, state));
	}

	/**
	 * 
	 */
	public Rectangle getImageBounds(mxGraphics2DCanvas canvas, mxCellState state)
	{
		return state.getRectangle();
	}

	/**
	 * 
	 */
	public String getImageForStyle(mxGraphics2DCanvas canvas, mxCellState state)
	{
		return canvas.getImageForStyle(state.getStyle());
	}

	/**
	 * 
	 */
	public Color getFillColor(mxGraphics2DCanvas canvas, mxCellState state)
	{
		return mxUtils.getColor(state.getStyle(),
				mxConstants.STYLE_IMAGE_BACKGROUND);
	}

	/**
	 * 
	 */
	public Color getStrokeColor(mxGraphics2DCanvas canvas, mxCellState state)
	{
		return mxUtils.getColor(state.getStyle(),
				mxConstants.STYLE_IMAGE_BORDER);
	}

}
