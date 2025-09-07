/*
 * This file is part of Graphics3D.
 *
 * Graphics3D is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Graphics3D is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Graphics3D.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.dualuse.awt;


import io.github.dualuse.awt.geom.TransformedPathIterator;

import io.github.dualuse.awt.math.Matrix4d;
import io.github.dualuse.awt.math.Matrix4f;
import java.awt.*;
import java.awt.geom.*;


public class TransformedShape implements Shape {
	final Shape s;
	final Matrix4f m;

	public TransformedShape(Matrix4f t, Shape s) {
		this.s = s;
		this.m = new Matrix4f(t);
	}

	public TransformedShape(Matrix4d t, Shape s) {
		this.s = s;
		this.m = new Matrix4f(t);
	}

	public Rectangle getBounds() { return getBounds2D().getBounds(); }
	public Rectangle2D getBounds2D() { throw new RuntimeException("Unsupported"); }

	public boolean contains(double x, double y) { return true; }
	public boolean contains(Point2D p) { return true; }
	public boolean intersects(double x, double y, double w, double h) { return true; }
	public boolean intersects(Rectangle2D r) { return true; }
	public boolean contains(double x, double y, double w, double h) { return true; }
	public boolean contains(Rectangle2D r) { return true; }
	
	
	public PathIterator getPathIterator(AffineTransform at) {
		return new FlatteningPathIterator(new TransformedPathIterator(s.getPathIterator(null), at, m),.2,14);
	}

	public PathIterator getPathIterator(final AffineTransform at, final double flatness) {
		return new TransformedPathIterator(s.getPathIterator(null, flatness), at, m);
	}
	
}








