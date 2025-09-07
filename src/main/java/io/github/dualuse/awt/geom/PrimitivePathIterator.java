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
package io.github.dualuse.awt.geom;

import io.github.dualuse.awt.math.Matrix4f;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.Random;


public 
class PrimitivePathIterator implements PathIterator {
	final float[] vertices;
	final int n;
	final AffineTransform at;
	final Matrix4f m;
	final int type;
	
	final public static int LINES = 2;
	final public static int TRIANGLES = 3;
	final public static int QUADS = 4;
	
	final public static int LINE_STRIP= 10000-1;
	final public static int LINE_LOOP = 10000;
	
	public PrimitivePathIterator(float vertices[], int count, int type, AffineTransform at, Matrix4f m) {
		this.vertices = vertices;
		this.n = count+(type>2?count/type:0)+(type==LINE_LOOP?1:0);
		
		this.at = at;
		this.m = m;
		this.type = type;
	}
	
	public int getWindingRule() { return PathIterator.WIND_NON_ZERO; }
	
	private int i = 0;
	
	public boolean isDone() { return i>=n; }
	public void next() { i++; }

	boolean clipped = false;
	private float lx = 0, ly=0, lz=0, lw = 0;
	public int currentSegment(float[] coords) {
		int mode = i%(this.type+(type>2?1:0));
		
		int seg = mode==0?SEG_MOVETO: SEG_LINETO;

		int o = i*3;
		switch (type) {
		case TRIANGLES: o = (i/4*3+i%3)*3;break;
		case QUADS: o = (i/5*4+i%4)*3;break;
		case LINE_LOOP: o = i%(n-1); break;
		}
		
		float vecx = vertices[o++];
		float vecy = vertices[o++];
		float vecz = vertices[o++];
		float vecw = 1;

		float x = m.m00 * vecx + m.m01 * vecy + m.m02 * vecz + m.m03 * vecw;
		float y = m.m10 * vecx + m.m11 * vecy + m.m12 * vecz + m.m13 * vecw;
		float z = m.m20 * vecx + m.m21 * vecy + m.m22 * vecz + m.m23 * vecw;
		float w = m.m30 * vecx + m.m31 * vecy + m.m32 * vecz + m.m33 * vecw;	
		
		if (lz>-1 && z<-1 || lz<-1 && z>-1) {
			float dx = x-lx, dy = y-ly, dz = z-lz, dw = w-lw;
			float t = pdIntersection(0, 0, -1, 0, 0, -1, lx, ly, lz, dx, dy, dz);
			
			x = lx+dx*t;
			y = ly+dy*t;
			z = lz+dz*t;
			w = lw+dw*t;
			
			i--;
		} 
		
		
		
		final float ooW = 1f/w;
		float x_ = x*ooW;
		float y_ = y*ooW;
		
		coords[0] = x_;
		coords[1] = y_;
		
		if (at!=null)
			at.transform(coords, 0, coords, 0, 1);
		
		if (z<-1.001 || lz<-1.001)
			seg = SEG_MOVETO;

		lx = x;
		ly = y;
		lz = z;
		lw = w;
		
		return seg;
	}

	float floatcoords[] = new float[6];
	
	public int currentSegment(double[] coords) {
		int type = this.currentSegment(floatcoords);
		
		for (int i=0;i<2;i++)
			coords[i] = floatcoords[i];
		
		return type;
	}

	static public float pdIntersection( 
    		final float px, final float py, final float pz, final float nx, final float ny, final float nz,
    		final float ax, final float ay, final float az, final float dx, final float dy, final float dz
		)
	{
		final float d = -(px*nx+py*ny+pz*nz);
		
		float t = (-d-(ax*nx+ay*ny+az*nz)) / (dx*nx+dy*ny + dz*nz);  
		
		return t;
	}
	

}






