package de.dualuse.commons.awt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.ArrayDeque;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;

import de.dualuse.commons.awt.geom.PrimitivePathIterator;
import de.dualuse.commons.awt.geom.ProjectiveTransform;

public class Graphics3D extends Graphics2D {
	
	
	public static double squareSizeOfBestFittingOrientedBoundingBox( final double ax, final double ay, final double bx, final double by, final double cx, final double cy, final double dx, final double dy ) {
		
		final double mx = (ax + bx + cx + dx);
		final double my = (ay + by + cy + dy);
		
		final double culx = 4.*ax-mx, culy = 4.*ay-my, curx = 4.*bx-mx, cury = 4.*by-my;
		final double cllx = 4.*cx-mx, clly = 4.*cy-my, clrx = 4.*dx-mx, clry = 4.*dy-my;
		
		final double r00 = (culx*culx + curx*curx + cllx*cllx + clrx*clrx);
		final double r01 = (culx*culy + curx*cury + cllx*clly + clrx*clry);
		final double r11 = (culy*culy + cury*cury + clly*clly + clry*clry);
		
		final double detA = (r00*r11-r01*r01)*0.000244140625;//  1/4096 == 1/(4^6) == 1/(2^12) == 1>>12; 
		
		return detA;
	}
	
	final private Graphics2D g;
	
	public Graphics3D(Graphics wrapped) {
		this.g = (Graphics2D) wrapped.create();
	}
		
	private Matrix4f modelviewprojection = new Matrix4f(
												1,0,0,0,
												0,1,0,0,
												0,0,1,0,
												0,0,0,1	);
	
	public void getModelViewProjection(Matrix4d fillme) {
		fillme.set(modelviewprojection);
	}
	private ArrayDeque<Matrix4f> pushed = new ArrayDeque<Matrix4f>();
	private ArrayDeque<Matrix4f> free = new ArrayDeque<Matrix4f>();
	
	public void pushTransform() {
		Matrix4f m = free.poll();
		if (m==null)
			m = new Matrix4f();
		
		m.set(modelviewprojection);
		pushed.push(m);
	}
	
	public void popTransform() {
		Matrix4f m = pushed.poll();
		modelviewprojection.set(m);
		free.push(m);
	}

	public float originX() { return modelviewprojection.m03/modelviewprojection.m33; };
	public float originY() { return modelviewprojection.m13/modelviewprojection.m33; };
	public float originZ() { return modelviewprojection.m23/modelviewprojection.m33; };
	
	
//	public float diffuse(float ax, float ay, float az) {
//		Matrix4f m = modelviewprojection;
//		
//		float al = (float)Math.sqrt(ax*ax+ay*ay+az*az), ml = (float)Math.sqrt(m.m02*m.m02+m.m12*m.m12+m.m22*m.m22);
//		float bx = ax/al, by = ay/al, bz = az/al;
//		float nx = m.m02/ml, ny = m.m12/ml, nz = m.m22/ml;
//		
//		return Math.min(1,Math.max(0,(bx*nx+by*ny+bz*nz)));
//	}
	

	public boolean isVisible(double px, double py) {
		return isVisible(px, py, 0);
	}

	public boolean isVisible(double px, double py, double pz) {
		final float ovecx=(float)px,ovecy=(float)py,ovecz=(float)pz,ovecw=1;

		Matrix4f m = modelviewprojection;
		final float z = (m.m20 * ovecx + m.m21 * ovecy + m.m22 * ovecz + m.m23 * ovecw);
		if (z<-1) return false;
		final float ooow = 1f/(m.m30 * ovecx + m.m31 * ovecy + m.m32 * ovecz + m.m33 * ovecw);
		final float ox = (m.m00 * ovecx + m.m01 * ovecy + m.m02 * ovecz + m.m03 * ovecw)*ooow;
		final float oy = (m.m10 * ovecx + m.m11 * ovecy + m.m12 * ovecz + m.m13 * ovecw)*ooow;
		
		return getClip().contains(ox,oy);
	}
	
	public boolean isFrontFacing() {
		
		final float ovecx=0,ovecy=0,ovecz=0,ovecw=1;
		final float nvecx = 1, nvecy=0, nvecz=0, nvecw=1;
		final float mvecx = 0, mvecy=1, mvecz=0, mvecw=1;
		
		Matrix4f m = modelviewprojection;
		final float ooow = 1f/(m.m30 * ovecx + m.m31 * ovecy + m.m32 * ovecz + m.m33 * ovecw);
		final float ox = (m.m00 * ovecx + m.m01 * ovecy + m.m02 * ovecz + m.m03 * ovecw)*ooow;
		final float oy = (m.m10 * ovecx + m.m11 * ovecy + m.m12 * ovecz + m.m13 * ovecw)*ooow;
//		final float oz = (m.m20 * ovecx + m.m21 * ovecy + m.m22 * ovecz + m.m23 * ovecw)*ooow;
//		final float ow = (m.m30 * ovecx + m.m31 * ovecy + m.m32 * ovecz + m.m33 * ovecw)*ooow;

		final float noow = 1f/(m.m30 * nvecx + m.m31 * nvecy + m.m32 * nvecz + m.m33 * nvecw);
		final float nx = (m.m00 * nvecx + m.m01 * nvecy + m.m02 * nvecz + m.m03 * nvecw)*noow;
		final float ny = (m.m10 * nvecx + m.m11 * nvecy + m.m12 * nvecz + m.m13 * nvecw)*noow;
//		final float nz = (m.m20 * nvecx + m.m21 * nvecy + m.m22 * nvecz + m.m23 * nvecw)*noow;
//		final float nw = (m.m30 * nvecx + m.m31 * nvecy + m.m32 * nvecz + m.m33 * nvecw)*noow;

		final float moow = 1f/(m.m30 * mvecx + m.m31 * mvecy + m.m32 * mvecz + m.m33 * mvecw);
		final float mx = (m.m00 * mvecx + m.m01 * mvecy + m.m02 * mvecz + m.m03 * mvecw)*moow;
		final float my = (m.m10 * mvecx + m.m11 * mvecy + m.m12 * mvecz + m.m13 * mvecw)*moow;
//		final float mz = (m.m20 * mvecx + m.m21 * mvecy + m.m22 * mvecz + m.m23 * mvecw)*moow;
//		final float mw = (m.m30 * mvecx + m.m31 * mvecy + m.m32 * mvecz + m.m33 * mvecw)*moow;

		final float ax = nx-ox, ay = ny-oy;
		final float bx = mx-ox, by = my-oy;
		final float cross = (ax*by)-(ay*bx); 
		
		return cross<0;
	}
	
//	public float zAxisX() { return modelviewprojection.m12+modelviewprojection.m03)/(modelviewprojection.m32 + modelviewprojection.m33); };
//	public float zAxisY() { return modelviewprojection.m12+modelviewprojection.m13)/(modelviewprojection.m32 + modelviewprojection.m33); };
//	public float zAxisZ() { return modelviewprojection.m22+modelviewprojection.m23)/(modelviewprojection.m32 + modelviewprojection.m33); };
	
//	public Vector3f origin() {
//		Matrix4f m = modelviewprojection;
//		return new Vector3f(m.m03,m.m13,m.m23);
//	}
//	
//	public Vector3f normal() {
//		Matrix4f m = modelviewprojection;
//		return new Vector3f(m.m02+m.m03,m.m12+m.m13,m.m12+m.m23);
//	}
	
	private AffineTransform approximateTransform(float x, float y) {
		Matrix4f n = new Matrix4f();
		n.set(modelviewprojection);
		
		Vector4f vp = new Vector4f(x,y,0,1), vx = new Vector4f(x+1,y,0,1), vy = new Vector4f(x,y+1,0,1);
		
		n.transform(vp);
		n.transform(vx);
		n.transform(vy);
		
		vp.scale(1f/vp.w);
		vx.scale(1f/vx.w);
		vy.scale(1f/vy.w);
		
		AffineTransform at = g.getTransform();
		
		float dx = vx.x-vp.x, dy = vx.y-vp.y;
		float cx = vy.x-vp.x, cy = vy.y-vp.y;
		
		at.setTransform( dx,dy,cx,cy, vp.x, vp.y );
		return at;
	}
	
	static public Matrix4f createMatrixWithTransform(AffineTransform at) {
		Matrix4f m = new Matrix4f();
		m.setIdentity();
		
		m.m00 = (float)at.getScaleX();
		m.m01 = (float)at.getShearX();
		
		m.m10 = (float)at.getShearY();
		m.m11 = (float)at.getScaleY();
		
		m.m03 = (float)at.getTranslateX();
		m.m13 = (float)at.getTranslateY();
		
		return m;
	}
	
	static public Matrix4f createMatrixWithViewport(int x, int y, int width, int height) {
		Matrix4f m = new Matrix4f();
		m.setIdentity();
		
		m.mul(Graphics3D.createMatrixWithTransform(AffineTransform.getTranslateInstance(x, y)));
		m.mul(Graphics3D.createMatrixWithTransform(AffineTransform.getScaleInstance(width, height)));
		m.mul(Graphics3D.createMatrixWithTransform(AffineTransform.getScaleInstance(.5, -.5)));
		m.mul(Graphics3D.createMatrixWithTransform(AffineTransform.getTranslateInstance(1, -1)));
		
		return m;
	}
	
	static public Matrix4f createMatrixWithFrustum( float left, float right, float bottom, float top, float nearVal, float farVal ) {
		Matrix4f m = new Matrix4f();

		float A = (right + left)/(right - left);
		float B = (top + bottom)/(top - bottom);
		float C = -(farVal + nearVal)/(farVal - nearVal);
		float D = -(2 * farVal * nearVal) /( farVal -nearVal);
		
		m.m00 = (2*nearVal)/(right-left);	m.m01 = 0; 							m.m02 = A; m.m03 = 0;
		m.m10 = 0; 							m.m11 = (2*nearVal)/(top-bottom); 	m.m12 = B; m.m13 = 0;
		m.m20 = 0; 							m.m21 = 0; 							m.m22 = C; m.m23 = D;
		m.m30 = 0; 							m.m31 = 0; 							m.m32 = -1; m.m33 = 0;
		
		return m;
	}
	
	public void translate(double tx, double ty, double tz) {
		modelviewprojection.mul(
				new Matrix4f(
						1,0,0,(float)tx,
						0,1,0,(float)ty,
						0,0,1,(float)tz,
						0,0,0,1));
	}
	
	public void rotate(double theta, double x, double y, double z) {
		Matrix4f r = new Matrix4f();
		r.set(new AxisAngle4d( x, y, z, theta));
		modelviewprojection.mul( r );
	}
	
	public void scale(double sx, double sy, double sz) {
		modelviewprojection.mul(
				new Matrix4f(
						(float)sx,0,0,0,
						0,(float)sy,0,0,
						0,0,(float)sz,0,
						0,0,0,1));
	}
	
	

//	private Matrix4f inversemodelviewprojection = new Matrix4f();
//	
//	public Vector4f unproject(double winX, double winY, double winZ) {
//		Vector4f v = new Vector4f((float)winX, (float)winY, (float)winZ, 1);
//		inversemodelviewprojection.invert(modelviewprojection);
//		inversemodelviewprojection.transform(v);
//		v.scale(1/v.w);
//		return v;
//	}
//	
//
//	public Vector4f unproject(double winX, double winY, double winZ, double winW) {
//		Vector4f v = new Vector4f((float)winX, (float)winY, (float)winZ, (float)winW);
//		inversemodelviewprojection.invert(modelviewprojection);
//		inversemodelviewprojection.transform(v);
//		v.scale(1/v.w);
//		return v;
//	}
//	
//	public double unprojectX(double winX, double winY, double winZ) { return unproject(winX,winY,winZ).x; }
//	public double unprojectY(double winX, double winY, double winZ) { return unproject(winX,winY,winZ).y; }
//	public double unprojectZ(double winX, double winY, double winZ) { return unproject(winX,winY,winZ).z; }
//	public Point2D project(Vector4f v) { return project(v.x, v.y, v.z); }
	
	public Point2D project(Point2D p) {
		return project((float)p.getX(),(float)p.getY());
	}
	
	public Point2D project(double x, double y) {
		Vector4f v = new Vector4f((float)x,(float)y,0,1);
		modelviewprojection.transform(v);
		
		return new Point2D.Float(v.x/v.w,v.y/v.w);
	}
	
	public Point2D project(double x, double y, double z) {
		Vector4f v = new Vector4f((float)x,(float)y,(float)z,1);
		modelviewprojection.transform(v);
		
		return new Point2D.Float(v.x/v.w,v.y/v.w);
	}
	
	public double projectX(double x, double y, double z) {
		double x_ = modelviewprojection.m00*x+modelviewprojection.m01*y+modelviewprojection.m02*z+modelviewprojection.m03;
		double w_ = modelviewprojection.m30*x+modelviewprojection.m31*y+modelviewprojection.m32*z+modelviewprojection.m33;
		
		return x_/w_;
	}

	public double projectY(double x, double y, double z) {
		double y_ = modelviewprojection.m10*x+modelviewprojection.m11*y+modelviewprojection.m12*z+modelviewprojection.m13;
		double w_ = modelviewprojection.m30*x+modelviewprojection.m31*y+modelviewprojection.m32*z+modelviewprojection.m33;
		
		return y_/w_;
	}

	public double projectZ(double x, double y, double z) {
		double z_ = modelviewprojection.m20*x+modelviewprojection.m21*y+modelviewprojection.m22*z+modelviewprojection.m23;
		double w_ = modelviewprojection.m30*x+modelviewprojection.m31*y+modelviewprojection.m32*z+modelviewprojection.m33;
		
		return z_/w_;
	}

	
	public void viewport(int x, int y, int width, int height) {
		modelviewprojection.mul(createMatrixWithViewport(x, y, width, height));
	}
	
	public void frustum(double left, double right, double bottom, double top, double nearVal, double farVal ) {
		modelviewprojection.mul(createMatrixWithFrustum((float)left, (float)right, (float)bottom, (float)top, (float)nearVal, (float)farVal));
	}
	
	
	public void transform(Matrix4f transform) { modelviewprojection.mul(transform); }
	
	public void translate(double tx, double ty) { modelviewprojection.mul(createMatrixWithTransform(AffineTransform.getTranslateInstance(tx, ty))); }
	public void rotate(double theta) { modelviewprojection.mul(createMatrixWithTransform(AffineTransform.getRotateInstance(theta))); }
	public void rotate(double theta, double x, double y) { modelviewprojection.mul(createMatrixWithTransform(AffineTransform.getRotateInstance(theta,x,y))); }
	public void scale(double sx, double sy) { modelviewprojection.mul(createMatrixWithTransform(AffineTransform.getScaleInstance(sx, sy))); }
	public void shear(double shx, double shy) { modelviewprojection.mul(createMatrixWithTransform(AffineTransform.getRotateInstance(shx, shy))); }
	public void transform(AffineTransform Tx) { modelviewprojection.mul(createMatrixWithTransform(Tx)); }
	public void transform(ProjectiveTransform Tx) { modelviewprojection.mul(Tx.getMatrix(new Matrix4f())); }
	public void setTransform(AffineTransform Tx) { modelviewprojection.set(createMatrixWithTransform(Tx)); }
	public void setTransform(ProjectiveTransform Tx) { modelviewprojection.set(Tx.getMatrix()); }
	public ProjectiveTransform getTransform() { return new ProjectiveTransform(modelviewprojection); } 
	
	public void draw(Shape s) { g.draw(new TransformedShape(modelviewprojection, s)); }
	public void fill(Shape s) { g.fill(new TransformedShape(modelviewprojection, s)); }
	
	
	final static float EPSILON = 0.005f; 
	public boolean drawImage(Image im, AffineTransform xform, ImageObserver obs) {
		int W = im.getWidth(obs), H = im.getHeight(obs);

		Matrix4f m = new Matrix4f();
		m.set(modelviewprojection);
		m.mul(createMatrixWithTransform(xform));
		AffineTransform at = new AffineTransform(), bt = g.getTransform();
		
//		if (im instanceof MipMapBufferedImage) {
//			float ax = 0, ay = 0, bx = W, by = 0, cx = W, cy = H, dx = W, dy = H;
//			
//			float aoow = 1f/(m.m30* ax+ m.m31*ay + m.m32 * 0 + m.m33 *1 );
//			float ax_ = (m.m00*ax+ m.m01*ay+m.m02*0+m.m03*1)*aoow;
//			float ay_ = (m.m10*ax+ m.m11*ay+m.m12*0+m.m13*1)*aoow;
//			
//			float boow = 1f/(m.m30* bx+ m.m31*by + m.m32 * 0 + m.m33 *1 );
//			float bx_ = (m.m00*bx+ m.m01*by+m.m02*0+m.m03*1)*boow;
//			float by_ = (m.m10*bx+ m.m11*by+m.m12*0+m.m13*1)*boow;
//			
//			float coow = 1f/(m.m30* cx+ m.m31*cy + m.m32 * 0 + m.m33 *1 );
//			float cx_ = (m.m00*cx+ m.m01*cy+m.m02*0+m.m03*1)*coow;
//			float cy_ = (m.m10*cx+ m.m11*cy+m.m12*0+m.m13*1)*coow;
//			
//			float doow = 1f/(m.m30* dx+ m.m31*dy + m.m32 * 0 + m.m33 *1 );
//			float dx_ = (m.m00*dx+ m.m01*dy+m.m02*0+m.m03*1)*doow;
//			float dy_ = (m.m10*dx+ m.m11*dy+m.m12*0+m.m13*1)*doow;
//			
//			float perspectiveArea = triangleArea(ax_, ay_, bx_, by_, cx_, cy_)+triangleArea(dx_, dy_, bx_, by_, cx_, cy_);
//			float orthoArea = W*H;
//			
//			
//			MipMapBufferedImage mmi = (MipMapBufferedImage) im;
//			
//			for (;orthoArea/4>perspectiveArea;orthoArea/=4)
//				mmi = mmi.mipmap;
//			
//			m.mul(createMatrixWithTransform(AffineTransform.getScaleInstance(W*1f/mmi.width, H*1f/mmi.height)));
//			drawImageTiled(mmi, m, at, bt, 0, 0, mmi.width, mmi.height);
//		} else 
			drawImageTiled(im, m, at, bt, 0, 0, W, H);
		
		return false;
	}

	
	public float triangleArea(float ax, float ay, float bx, float by, float cx, float cy) {
		float ux = bx-ax, uy = by-ay;
		float vx = cx-ax, vy = cy-ay;
		float area = (ux*vy-vx*uy)/2;  
		
		return (area>0?area:-area);
	}
	
	static public boolean triangleContains(float px, float py, float ax, float ay, float bx, float by, float cx, float cy) {
		final float v0x = cx-ax, v0y = cy-ay;
		final float v1x = bx-ax, v1y = by-ay;
		final float v2x = px-ax, v2y = py-ay;
		
		final float dot00 = v0x*v0x+v0y*v0y;
		final float dot01 = v0x*v1x+v0y*v1y;
		final float dot02 = v0x*v2x+v0y*v2y;
		final float dot11 = v1x*v1x+v1y*v1y;
		final float dot12 = v1x*v2x+v1y*v2y;
		
		final float invDenom = 1 / (dot00*dot11-dot01*dot01);
		final float u = (dot11*dot02-dot01*dot12)*invDenom;
		final float v = (dot00*dot12-dot01*dot02)*invDenom;
		
		return (u>0) && (v>0) && (u+v<1);
	}


	
	
	int counter = 0;
	
	int recursions = 0;
	float PERSPECTIVE_RIM = 0, MAX_PERSPECTIVE_DEVIATION = 2, MAX_PERSPECTIVE_ERROR = MAX_PERSPECTIVE_DEVIATION*MAX_PERSPECTIVE_DEVIATION;
	private void drawImageTiled(Image im, Matrix4f m, AffineTransform at, AffineTransform bt, int x1, int y1, int x2, int y2) {
		
		if (x1==x2 || y1==y2)
			return;
		
		float ax = x1, ay = y1, bx = x2, by = y1, cx = x2, cy = y2, dx = x1, dy = y2;
		
		float aoow = 1f/(m.m30* ax+ m.m31*ay + m.m32 * 0 + m.m33 *1 );
		float ax_ = (m.m00*ax+ m.m01*ay+m.m02*0+m.m03*1)*aoow;
		float ay_ = (m.m10*ax+ m.m11*ay+m.m12*0+m.m13*1)*aoow;
		
		float boow = 1f/(m.m30* bx+ m.m31*by + m.m32 * 0 + m.m33 *1 );
		float bx_ = (m.m00*bx+ m.m01*by+m.m02*0+m.m03*1)*boow;
		float by_ = (m.m10*bx+ m.m11*by+m.m12*0+m.m13*1)*boow;
		
		float coow = 1f/(m.m30* cx+ m.m31*cy + m.m32 * 0 + m.m33 *1 );
		float cx_ = (m.m00*cx+ m.m01*cy+m.m02*0+m.m03*1)*coow;
		float cy_ = (m.m10*cx+ m.m11*cy+m.m12*0+m.m13*1)*coow;
		
		float doow = 1f/(m.m30* dx+ m.m31*dy + m.m32 * 0 + m.m33 *1 );
		float dx_ = (m.m00*dx+ m.m01*dy+m.m02*0+m.m03*1)*doow;
		float dy_ = (m.m10*dx+ m.m11*dy+m.m12*0+m.m13*1)*doow;
		float tileW = x2-x1, tileH = y2-y1, error = 0;

		float ax__ = dx_+bx_-cx_, ay__ = dy_+by_-cy_;
		float bx__ = ax_+cx_-dx_, by__ = ay_+cy_-dy_;
		float cx__ = bx_+dx_-ax_, cy__ = by_+dy_-ay_;
		float dx__ = ax_+cx_-bx_, dy__ = ay_+cy_-by_;
		
		if (triangleContains(cx_, cy_, dx_, dy_, bx_, by_, cx__, cy__)) { //paDAB > all (A ist Anker)
			float deltaX = cx__-cx_, deltaY = cy__-cy_;
			error = deltaX*deltaX + deltaY*deltaY;
			at.setTransform( (bx_-ax_)/tileW, (by_-ay_)/tileW,   (dx_-ax_)/tileH, (dy_-ay_)/tileH,    ax_, ay_);
			
		} else
		if (triangleContains(dx_,dy_, ax_,ay_, cx_,cy_, dx__,dy__)) { //paABC > all (B ist Anker)
			float deltaX = dx__-dx_, deltaY = dy__-dy_;
			error = deltaX*deltaX + deltaY*deltaY;
	
			at.setTransform( (bx_-ax_)/tileW, (by_-ay_)/tileW,    (dx__-ax_)/tileH, (dy__-ay_)/tileH,    ax_, ay_);
		} else
		if (triangleContains(ax_,ay_, dx_,dy_,bx_,by_,ax__,ay__)) { //paBCD > all (C ist Anker)
			float deltaX = ax__-ax_, deltaY = ay__-ay_;
			error = deltaX*deltaX + deltaY*deltaY;
			at.setTransform((bx_-ax__)/tileW, (by_-ay__)/tileW,    (dx_-ax__)/tileH, (dy_-ay__)/tileH,    ax__, ay__);
		} else { //paCDA > all (D ist Anker)
			float deltaX = bx__-bx_, deltaY = by__-by_;
			error = deltaX*deltaX + deltaY*deltaY;
	
			at.setTransform( (bx__-ax_)/tileW, (by__-ay_)/tileW,    (dx_-ax_)/tileH, (dy_-ay_)/tileH,    ax_, ay_);
		}
 
		
		if (error<MAX_PERSPECTIVE_ERROR) {
			g.transform(at);
			g.drawImage(im, 0, 0, (int)(x2-x1), (int)(y2-y1), (int)x1, (int)y1, (int)x2, (int)y2, null);
//			Stroke s = g.getStroke();
//			g.setStroke(new BasicStroke(0.1f));
//			g.draw(new Rectangle2D.Double(0,0,(int)(x2-x1), (int)(y2-y1)));
//			g.setStroke(s);
			g.setTransform(bt);
		} else {
			recursions++;
			int mx = (int)((x1+x2)/2), my = (int)((y1+y2)/2);
			drawImageTiled(im, m, at, bt, x1, y1, mx, my );
			drawImageTiled(im, m, at, bt, mx, y1, x2, my);
			drawImageTiled(im, m, at, bt, mx, my, x2, y2 );
			drawImageTiled(im, m, at, bt, x1, my, mx, y2 );
			recursions--;
		}
	}
	
	
	
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) { }
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) { }
	
	public void drawString(String str, float x, float y) {
		AffineTransform at = g.getTransform();
		g.transform(approximateTransform(x, y));
		g.drawString(str, 0,0);
		g.setTransform(at);
	}
	
	public void drawString(AttributedCharacterIterator iterator, float x, float y) { 
		AffineTransform at = g.getTransform();
		g.transform(approximateTransform(x, y));
		g.drawString(iterator, 0,0);
		g.setTransform(at);
	}

	
	public void drawGlyphVector(GlyphVector gv, float x, float y) { 
		AffineTransform at = g.getTransform();
		g.transform(approximateTransform(x, y));
		g.drawGlyphVector(gv, 0,0);
		g.setTransform(at);
	}

	public boolean hit(Rectangle rect, Shape s, boolean onStroke) { return false; }
	

	/// Mapped Methods
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) { drawImage(img, AffineTransform.getTranslateInstance(x, y), null); }
	public void drawString(String str, int x, int y) { drawString (str,(float)x,(float)y); }
	public void drawString(AttributedCharacterIterator iterator, int x, int y) { drawString(iterator, (float)x, (float)y); }
	
	
	public void drawLine(int x1, int y1, int x2, int y2) { draw(new Line2D.Double(x1,y1,x2,y2)); }
	public void drawRect(int x, int y, int width, int height) { draw(new Rectangle2D.Double(x,y,width,height)); }
	public void fillRect(int x, int y, int width, int height) { fill(new Rectangle2D.Double(x,y,width,height)); }
	public void drawPolygon(Polygon p) { draw(p); }
	
	
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) { draw(new RoundRectangle2D.Float(x,y,width,height,arcWidth,arcHeight)); }
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) { fill(new RoundRectangle2D.Float(x,y,width,height,arcWidth,arcHeight)); }
	public void drawOval(int x, int y, int width, int height) { draw(new Ellipse2D.Float(x,y,width,height)); }
	public void fillOval(int x, int y, int width, int height) { fill(new Ellipse2D.Float(x,y,width,height)); }
	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) { draw(new Arc2D.Float(x,y,width,height,startAngle,arcAngle,Arc2D.OPEN)); }
	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) { fill(new Arc2D.Float(x,y,width,height,startAngle,arcAngle,Arc2D.OPEN)); }
	
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) { if (nPoints==0) return; Path2D.Float p = new Path2D.Float(); p.moveTo(xPoints[0], yPoints[0]); for (int i=1;i<nPoints;i++) p.lineTo(xPoints[0], yPoints[0]); draw(p); }
	
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) { if (nPoints==0) return; Path2D.Float p = new Path2D.Float(); p.moveTo(xPoints[0], yPoints[0]); for (int i=1;i<nPoints;i++) p.lineTo(xPoints[0], yPoints[0]); p.closePath(); draw(p); }
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) { if (nPoints==0) return; Path2D.Float p = new Path2D.Float(); p.moveTo(xPoints[0], yPoints[0]); for (int i=1;i<nPoints;i++) p.lineTo(xPoints[0], yPoints[0]); p.closePath(); fill(p); }
	
	
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) { return drawImage(img,AffineTransform.getTranslateInstance(x, y),observer); }
	
	
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) { throw new RuntimeException("unsupported"); }
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) { throw new RuntimeException("unsupported"); }
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) { throw new RuntimeException("unsupported"); }
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) { throw new RuntimeException("unsupported"); };
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) { throw new RuntimeException("unsupported"); }
	
	
	/// Transparent Methods
	public GraphicsConfiguration getDeviceConfiguration() { return g.getDeviceConfiguration(); }
	public void setComposite(Composite comp) { g.setComposite(comp); }
	public void setPaint(Paint paint) { g.setPaint(paint); }
	public void setStroke(Stroke s) { g.setStroke(s); }
	public void setRenderingHint(Key hintKey, Object hintValue) { g.setRenderingHint(hintKey, hintValue); }
	public Object getRenderingHint(Key hintKey) { return g.getRenderingHint(hintKey); }
	public void setRenderingHints(Map<?, ?> hints) { g.setRenderingHints(hints); }
	public void addRenderingHints(Map<?, ?> hints) { g.addRenderingHints(hints); }
 	public RenderingHints getRenderingHints() { return g.getRenderingHints(); }
 	
 	public void translate(int x, int y) { translate((double)x,(double)y); }
 	
	public Paint getPaint() { return g.getPaint(); }
	public Composite getComposite() { return g.getComposite(); }
	public void setBackground(Color color) { g.setBackground(color); }
	public Color getBackground() { return g.getBackground(); }
	public Stroke getStroke() { return g.getStroke(); }
	public void clip(Shape s) { g.clip(s); }
	public FontRenderContext getFontRenderContext() { return g.getFontRenderContext(); }
	public Graphics3D create() { Graphics3D g3 = new Graphics3D(g.create()); g3.modelviewprojection.set(modelviewprojection); return g3; }
	public Color getColor() { return g.getColor(); }
	public void setColor(Color c) { g.setColor(c); };
 	public void setPaintMode() { g.setPaintMode(); }
	public void setXORMode(Color c1) { g.setXORMode(c1); }
	public Font getFont() { return g.getFont(); }
	public void setFont(Font font) { g.setFont(font); }
	public FontMetrics getFontMetrics(Font f) { return g.getFontMetrics(); }
	public Rectangle getClipBounds() { return g.getClipBounds(); } 
	public void clipRect(int x, int y, int width, int height) { g.clipRect(x,y,width,height); }
	public void setClip(int x, int y, int width, int height) { g.setClip(x,y,width,height); }
	public Shape getClip() { return g.getClip(); }
	public void setClip(Shape clip) { g.setClip(clip); }
	public void copyArea(int x, int y, int width, int height, int dx, int dy) { g.copyArea(x, y, width, height, dx, dy); }
	
	public void drawLine(double x1, double y1, double z1, double x2, double y2, double z2) {
		float dx = (float)(x2-x1);
		float dy = (float)(y2-y1);
		float dz = (float)(z2-z1);
		
		float tx = (float)x1;
		float ty = (float)y1;
		float tz = (float)z1;
		
		Matrix4f n = new Matrix4f(
			dx, 0, 0, tx,
			dy, 1, 0, ty,
			dz, 0, 1, tz,
			0 , 0, 0,  1
		);
		n.mul(modelviewprojection, n);
		
		g.draw(new TransformedShape(n,new Line2D.Double(0,0,1,0))); 
	}
	
	
	public void clearRect(int x, int y, int width, int height) { g.clearRect(x, y, width, height); }
	
	public void dispose() { g.dispose(); }
	
	
	public static final int LINES = PrimitivePathIterator.LINES;
	public static final int TRIANGLES = PrimitivePathIterator.TRIANGLES;
	public static final int QUADS = PrimitivePathIterator.QUADS;
	public static final int LINE_STRIP = PrimitivePathIterator.LINE_STRIP;
	public static final int LINE_LOOP = PrimitivePathIterator.LINE_LOOP;
	public static final int POLYGON = -PrimitivePathIterator.LINE_LOOP;
	
	public static final int FRONT = 1;
	public static final int BACK = 2;
	public static final int FRONT_AND_BACK = FRONT | BACK;
	
	public static final int POINT = 1;
	public static final int LINE = 2;
	public static final int FILL = 3;	
	

	private Primitive cached = new Primitive();
	private Primitive p = null;
	
	@SuppressWarnings("unused")
	private int backPolygonMode = LINE;
	private int frontPolygonMode = LINE;
	
	public void polygonMode(int mode) { polygonMode(FRONT_AND_BACK, mode); }
	private void polygonMode(int face, int mode) {
		if ((face&FRONT)!=0)
			frontPolygonMode = mode;
		
		if ((face&BACK)!=0)
			backPolygonMode = mode;
	}
	
	public void begin(int type) {
		p = cached.reset(modelviewprojection, type);
	}

	public void vertex(int x, int y) { p.addVertex(x, y, 0f);}
	public void vertex(float x, float y) { p.addVertex(x, y, 0f);}
	public void vertex(double x, double y) { p.addVertex((float)x, (float)y, 0f); }
	
	public void vertex(int x, int y, int z) { p.addVertex(x, y, z); }
	public void vertex(float x, float y, float z) { p.addVertex(x, y, z); }
	public void vertex(double x, double y, double z) { p.addVertex((float)x, (float)y, (float)z); }
	
	public void end() {
		switch (p.getType()) {
		case LINES:
		case LINE_STRIP:
		case LINE_LOOP:
			g.draw(p.clone());
			break;
			
		case POLYGON:
		case TRIANGLES:
		case QUADS:
			if (frontPolygonMode==LINE)
				g.draw( p.clone() );
			else
				g.fill( p.clone() );
			break;
		}
		
		p = null;
	}
	
	
	public static void main(String[] args) throws IOException {

//		final BufferedImage im = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		final BufferedImage im = ImageIO.read(new File("/Library/Desktop Pictures/lake.jpg")).getSubimage(1024, 1024, 512, 512);
		
		
		JFrame f = new JFrame();
		
		
		final Matrix4f rotation = new Matrix4f();
		rotation.setIdentity();
		
		
		
		f.setContentPane(new JComponent() {

			MouseEvent last = null;
			MouseAdapter ma = new MouseAdapter() {
				public void mouseMoved(MouseEvent e) { last = null; }
				
				public void mouseDragged(MouseEvent e) {
					if (last!=null) {
						
						Matrix4f rotM = new Matrix4f();
						rotM.rotY((e.getX()-last.getX())*0.01f);
						
						rotation.mul(rotM,rotation);

						rotM.rotX((e.getY()-last.getY())*0.01f);
						rotation.mul(rotM,rotation);
						
						repaint();
					}
					
					last = e;
				}
			};
			
			{
				addMouseListener(ma);
				addMouseMotionListener(ma);
			}
			
			
			
			private static final long serialVersionUID = 1L;
			
//			Image test = getToolkit().createImage("NSImage://NSCaution");
			
			
//			private long start = System.nanoTime();
			protected void paintComponent(Graphics g) {

				Graphics2D g2 = ((Graphics2D)g);
				Graphics3D g3 = new Graphics3D(g2);
				g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g3.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
				g3.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//				g3.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				
				
				
				
				g3.viewport(0, 0, getWidth(), getHeight());
				double S = .85,A = getHeight()*S/getWidth();
				g3.frustum(-S, S, -A, A, 2*S, 100);
				
				
				g3.translate(0, 0, -3.5);
				
//				g3.rotate(System.nanoTime()/1e9, 01, 1, 0);
				
				g3.begin(Graphics3D.QUADS);
				
//				g3.vertex(+1, +1, +1);
//				g3.vertex(+1, -1, +1);
//				g3.vertex(-1, -1, +1);
//				
//				g3.vertex(-1, -1, +1);
//				g3.vertex(-1, +1, +1);
//				g3.vertex(+1, +1, +1);
				
				g3.vertex(+1, +1, +1);
				g3.vertex(+1, -1, +1);
				
				g3.vertex(-1, -1, +1);
				g3.vertex(-1, +1, +1);
				
				g3.vertex(+1, +1, -1);
				g3.vertex(+1, -1, -1);
				
				g3.vertex(-1, -1, -1);
				g3.vertex(-1, +1, -1);
				
				g3.end();
				
//				g3.draw(new Line2D.Double(-1,-1,1,1));
//				g3.drawLine(-1,-1, -1, 1,-1,-1);
//				g3.drawLine( 1,-1, -1, 1, 1,-1);
//				g3.drawLine( 1, 1, -1,-1, 1,-1);
//				g3.drawLine(-1, 1, -1,-1,-1,-1);
//
//				g3.drawLine(-1,-1, +1, 1,-1,+1);
//				g3.drawLine( 1,-1, +1, 1, 1,+1);
//				g3.drawLine( 1, 1, +1,-1, 1,+1);
//				g3.drawLine(-1, 1, +1,-1,-1,+1);
//
//				g3.drawLine(-1,-1, -1, -1,-1,+1);
//				g3.drawLine( 1,-1, -1,  1,-1,+1);
//				g3.drawLine( 1, 1, -1,  1, 1,+1);
//				g3.drawLine(-1, 1, -1, -1, 1,+1);
				
//				if (true)
//					return;
				
				
//				double theta = (System.nanoTime()-start)/1e9;
				g3.transform(rotation);
//				g3.rotate(theta*.4, 1, 1, 0);
				
				Stroke s = g3.getStroke();
				g3.setStroke(new BasicStroke(8,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1,new float[]{32,40},0));
				g3.drawRoundRect(-1, -1, 2, 2, 1, 1);
				g3.rotate(Math.PI/2,0,1,0);
				g3.drawRoundRect(-1, -1, 2, 2, 1, 1);
				g3.rotate(Math.PI/2,1,0,0);
				g3.drawRoundRect(-1, -1, 2, 2, 1, 1);
				g3.setStroke(s);
				
				
				
				g3.scale(.03333, .03333,.03333);
				g3.drawLine(0, 0, 0, 30);
				
				g3.drawString("hallo", 0, 30);
				
				g3.translate(-30, 0);
				g3.scale(.13333, .13333,.13333);

				g3.drawImage(im, 210, -120, null);

//				g3.setColor(Color.ORANGE);
//				g3.setStroke(new BasicStroke(5));
//				g3.drawLine(0, 0,0, 100, 100,0);

				
				/*
				Matrix4f m = g3.modelviewprojection;
				
				int S = 40, s=-1;
				{
					final float vecx = 0, vecy = 0, vecz = 0, vecw=1, vecxP1 = vecx+im.getWidth(), vecyP1 = vecy+im.getHeight();
					float oow = 1f/(m.m30 * vecx + m.m31 * vecy + m.m32 * vecz + m.m33 * vecw);
					float x = (m.m00 * vecx + m.m01 * vecy + m.m02 * vecz + m.m03 * vecw)*oow;
					float y = (m.m10 * vecx + m.m11 * vecy + m.m12 * vecz + m.m13 * vecw)*oow;
					
					float oowdx = 1f/(m.m30 * (vecxP1) + m.m31 * vecy + m.m32 * vecz + m.m33 * vecw);
					float xdx = (m.m00 * (vecxP1) + m.m01 * vecy + m.m02 * vecz + m.m03 * vecw)*oowdx-x;
					float ydx = (m.m10 * (vecxP1) + m.m11 * vecy + m.m12 * vecz + m.m13 * vecw)*oowdx-y;
					
					float oowdy = 1f/(m.m30 * (vecx) + m.m31 * (vecyP1) + m.m32 * vecz + m.m33 * vecw);
					float xdy = (m.m00 * (vecx) + m.m01 * (vecyP1) + m.m02 * vecz + m.m03 * vecw)*oowdy-x;
					float ydy = (m.m10 * (vecx) + m.m11 * (vecyP1) + m.m12 * vecz + m.m13 * vecw)*oowdy-y;
					
//					S = Math.max(3,Math.min(1000,1+(int)(7*Math.hypot(im.getWidth(), im.getHeight())/Math.sqrt(Math.sqrt(squareSizeOfBestFittingOrientedBoundingBox(x, y, x+xdx, y+ydx, x+xdy, y+ydy, x+xdx+xdy, y+ydx+ydy))))));
					S = Math.max(3,Math.min(1000,1+(int)Math.sqrt(30*7*Math.hypot(im.getWidth(), im.getHeight())/Math.sqrt(Math.sqrt(squareSizeOfBestFittingOrientedBoundingBox(x, y, x+xdx, y+ydx, x+xdy, y+ydy, x+xdx+xdy, y+ydx+ydy))))));
					s = S/4;
				}
				
				AffineTransform at = new AffineTransform();
				AffineTransform bt = g2.getTransform();
				for (int v=0,V=im.getHeight();v<V;v+=S) {
					for (int u=0,U=im.getWidth();u<U;u+=S) {
						final float vecx = u, vecy = v, vecz = 0, vecw=1, vecxP1 = vecx+1, vecyP1 = vecy+1;
						float oow = 1f/(m.m30 * vecx + m.m31 * vecy + m.m32 * vecz + m.m33 * vecw);
						float x = (m.m00 * vecx + m.m01 * vecy + m.m02 * vecz + m.m03 * vecw)*oow;
						float y = (m.m10 * vecx + m.m11 * vecy + m.m12 * vecz + m.m13 * vecw)*oow;
						
						float oowdx = 1f/(m.m30 * (vecxP1) + m.m31 * vecy + m.m32 * vecz + m.m33 * vecw);
						float xdx = (m.m00 * (vecxP1) + m.m01 * vecy + m.m02 * vecz + m.m03 * vecw)*oowdx-x;
						float ydx = (m.m10 * (vecxP1) + m.m11 * vecy + m.m12 * vecz + m.m13 * vecw)*oowdx-y;
						
						float oowdy = 1f/(m.m30 * (vecx) + m.m31 * (vecyP1) + m.m32 * vecz + m.m33 * vecw);
						float xdy = (m.m00 * (vecx) + m.m01 * (vecyP1) + m.m02 * vecz + m.m03 * vecw)*oowdy-x;
						float ydy = (m.m10 * (vecx) + m.m11 * (vecyP1) + m.m12 * vecz + m.m13 * vecw)*oowdy-y;
						
						at.setTransform(xdx,ydx,xdy,ydy,x,y);
//						g2.setTransform(bt);
						g2.transform(at);
						g2.drawImage(im, 0, 0, S+s, S+s, u, v, u+S+s, v+S+s, null);
						
						g2.setTransform(bt);
					}
				}*/
				
//				g3.drawImage(im, 0, 0, 100, 100, 0,0, 100,100, null);
//				g3.drawRect(0, 0, 10, 10);
				
//				for (int x=0;x<;x++)
				
//				g2.draw(new Line2D.Double(new Point2D.Double(0,0),g3.pinPoint(0,30)));
				repaint();
				
			}
		});

		f.setBounds(400,300,500,500);
		
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

//		BufferedImage jm = ImageIO.read(new File("../LWJGL2/res/Cougar.jpg"));
//		im.createGraphics().drawImage(jm, 0, 0, im.getWidth(), im.getHeight(), 0, 0, jm.getWidth(), jm.getHeight(), null);
	} 
	
}




