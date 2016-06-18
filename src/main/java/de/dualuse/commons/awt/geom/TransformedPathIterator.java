package de.dualuse.commons.awt.geom;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import javax.vecmath.Matrix4f;

public 
class TransformedPathIterator implements PathIterator {

	final PathIterator w;
	final AffineTransform at;
	final Matrix4f m;
	
	public TransformedPathIterator(PathIterator w, AffineTransform at, Matrix4f m) {
		this.w=w;
		this.at=at;
		this.m=m;
	}
	
	public int getWindingRule() { return w.getWindingRule(); }
	public boolean isDone() { return w.isDone(); }
	public void next() { w.next(); }

	public int currentSegment(float[] coords) {
		int type = w.currentSegment(coords);
		
		int size = 0;
		switch (type) {
		case SEG_CLOSE: size=0; break;
		case SEG_LINETO: 
		case SEG_MOVETO: size=1; break;
		case SEG_QUADTO: size=2; break;
		case SEG_CUBICTO: size=3; break;
		}
		
		for (int i=0,l=size*2;i<l;i+=2) {
			float vecx = coords[i+0];
			float vecy = coords[i+1];
			float vecz = 0;
			float vecw = 1;
	
			float x = m.m00 * vecx + m.m01 * vecy + m.m02 * vecz + m.m03 * vecw;
			float y = m.m10 * vecx + m.m11 * vecy + m.m12 * vecz + m.m13 * vecw;
//			float z = m.m20 * vecx + m.m21 * vecy + m.m22 * vecz + m.m23 * vecw;
			float w = m.m30 * vecx + m.m31 * vecy + m.m32 * vecz + m.m33 * vecw;

			final float ooW = 1f/w;
			x *= ooW;
			y *= ooW;
//			z *= ooW;
//			v.w *= ooW;
			
			coords[i+0] = x;
			coords[i+1] = y;
		}
		
		if (at!=null)
			at.transform(coords, 0, coords, 0, size);
		
		return type;
	}
	
	public int currentSegment(double [] coords) {
		int type = w.currentSegment(coords);
		
		int size = 0;
		switch (type) {
		case SEG_CLOSE: size=0; break;
		case SEG_LINETO: 
		case SEG_MOVETO: size=1; break;
		case SEG_QUADTO: size=2; break;
		case SEG_CUBICTO: size=3; break;
		}
		
		for (int i=0,l=size*2;i<l;i+=2) {
			double vecx = coords[i+0];
			double vecy = coords[i+1];
			double vecz = 0;
			double vecw = 1;
	
			double x = m.m00 * vecx + m.m01 * vecy + m.m02 * vecz + m.m03 * vecw;
			double y = m.m10 * vecx + m.m11 * vecy + m.m12 * vecz + m.m13 * vecw;
//			double z = m.m20 * vecx + m.m21 * vecy + m.m22 * vecz + m.m23 * vecw;
			double w = m.m30 * vecx + m.m31 * vecy + m.m32 * vecz + m.m33 * vecw;

			final double ooW = 1f/w;
			x *= ooW;
			y *= ooW;
//			z *= ooW;
//			v.w *= ooW;
			
			coords[i+0] = x;
			coords[i+1] = y;
		}
		
		if (at!=null)
			at.transform(coords, 0, coords, 0, size);
		
		return type;
	}

	
	
	static public double pdIntersection( 
    		final double px, final double py, final double pz, final double nx, final double ny, final double nz,
    		final double ax, final double ay, final double az, final double dx, final double dy, final double dz
		)
	{
		final double d = -(px*nx+py*ny+pz*nz);
		
		double t = (-d-(ax*nx+ay*ny+az*nz)) / (dx*nx+dy*ny + dz*nz);  
		
		return t;
	}

}
