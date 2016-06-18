package de.dualuse.commons.awt;

import static de.dualuse.commons.awt.Graphics3D.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;

import de.dualuse.commons.awt.geom.TransformedPathIterator;

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
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {
		
		JFrame f = new JFrame();
		
		f.setContentPane(new JComponent() {
			private static final long serialVersionUID = 1L;
//			Shape s = 
//				AffineTransform.getScaleInstance(.001, .001).createTransformedShape(
//						Font.decode("sans-bold-84").createGlyphVector(new FontRenderContext(new AffineTransform(), false, true), "hallo").getOutline()
//				);
			
			Shape s = new RoundRectangle2D.Double(0,0,1,1,.3,.3);//new Ellipse2D.Double(0,0,1,1);
			
			
			Matrix4f transformation = new Matrix4f();
			{
				transformation.setIdentity();
				transformation.mul(createMatrixWithViewport(0, 0, 800, 800));
				transformation.mul(createMatrixWithFrustum(-1,1,-1,1,3,1000));
				
				transformation.mul(new Matrix4f(
						1,0,0,0,
						0,1,0,0,
						0,0,1,-5,
						0,0,0,1));
				
				Matrix4f rot = new Matrix4f();
				rot.rotX(1);
				
				transformation.mul(rot);
				
//				transformation.mul(arg0)
			}
			
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

				Shape t = new TransformedShape(transformation, s);
				((Graphics2D)g).draw( t );
				
				
			}
			
		});
		
		f.setBounds(400,300,800,800);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
	}
}








