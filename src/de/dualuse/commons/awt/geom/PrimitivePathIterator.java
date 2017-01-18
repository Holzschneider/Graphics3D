package de.dualuse.commons.awt.geom;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.vecmath.Matrix4f;

import de.dualuse.commons.awt.Graphics3D;

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
	
	Random r = new Random(0);
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
	

	public static void main(String[] args) {
		JFrame f = new JFrame();
		
		f.setContentPane(new JComponent() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unused")
			final double vertices[][] = new double[][] {
					{ -1, -1, -1 },
					{ -1, -1, +1 },
					{ -1, +1, +1 },
					{ -1, +1, -1 },
					
					{ +1, -1, -1 },
					{ +1, -1, +1 },
					{ +1, +1, +1 },
					{ +1, +1, -1 },
					
					{ -1, -1, +1 },
					{ -1, +1, +1 },
					{ +1, +1, +1 },
					{ +1, -1, +1 },
					
					{ -1, -1, -1 },
					{ -1, +1, -1 },
					{ +1, +1, -1 },
					{ +1, -1, -1 },
					
					{ -1, +1, -1 },
					{ -1, +1, +1 },
					{ +1, +1, +1 },
					{ +1, +1, -1 },
					
					{ -1, -1, -1 },
					{ -1, -1, +1 },
					{ +1, -1, +1 },
					{ +1, -1, -1 },
			};
					
			EulerCameraMouseAdapter ecma = new EulerCameraMouseAdapter(new Point2D.Double(), new Point2D.Double(), new Point2D.Double()) {
				{
					addMouseMotionListener(this);
					addMouseListener(this);
				}
			};
			
			
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				Graphics3D g3 = new Graphics3D(g);
				g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g3.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
				int W = getWidth(), H = getHeight(), NEAR = 2;
				g3.viewport(0, 0, W, H);
				double ar = H*1./W, lw = 1;
				g3.frustum(-lw, lw, -lw*ar, lw*ar, NEAR, 15);
				g3.translate(0, 0, -6);
				
//				g3.rotate(.7, 0, 1, 0);
//				g3.rotate(.7, 1, 0, 0);
				ecma.apply(g3);


				int L = 5, S = 2, Y = -1;;
				g3.begin(Graphics3D.LINES);
				for (int i=-L;i<=L;i++) {
					g3.vertex(-S, Y, i*S*1./(L));
					g3.vertex(+S, Y, i*S*1./(L));

					g3.vertex(i*S*1./(L), Y, -S);
					g3.vertex(i*S*1./(L), Y, +S);
				}
				g3.end();
				
				
//				g3.begin(Graphics3D.QUADS);
//				
//				for (int i=0;i<vertices.length;i++)
//					g3.vertex(vertices[i][0],vertices[i][1],vertices[i][2]);
//				
//				g3.end();
				
			}
			
		});

		f.setBounds(300, 200, 800, 800);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
}






class EulerCameraMouseAdapter extends MouseAdapter {
	
	Point2D euler, shift, dist; 
	
	public EulerCameraMouseAdapter(Point2D euler, Point2D shift, Point2D dist) {
		this.euler = euler;
		this.shift = shift;
		this.dist = dist;
	}
	
	public void apply(Graphics3D g3) {
		g3.rotate(dist.getX(),0,0,1);
		g3.translate(0, 0, dist.getY());
		
		g3.rotate(euler.getY(),1,0,0);
		g3.rotate(euler.getX(),0,1,0);
		g3.translate(shift.getX(),0,shift.getY());
	}
	
	
	MouseEvent last = null;
	public void mouseMoved(MouseEvent e) { last = e; }
	public void mouseDragged(MouseEvent e) {
		if (last==null) return;
		
		double Z = 3./((Component)e.getSource()).getWidth();
		
		if ((e.getModifiers()&MouseEvent.BUTTON1_MASK)==0)
			if ((e.getModifiers()&MouseEvent.BUTTON3_MASK)==0) 
				dist.setLocation(dist.getX(),dist.getY()+(e.getY()-last.getY())*0.04);
			else
				shift.setLocation(shift.getX()+(e.getX()-last.getX())*Z, shift.getY()+(e.getY()-last.getY())*Z);
		else 
			euler.setLocation(euler.getX()+(e.getX()-last.getX())*Z, euler.getY()+(e.getY()-last.getY())*Z );
			
		last = e;

		((Component)e.getSource()).repaint();
	}
	
	public void reset() {
		this.euler.setLocation(0,0);
		this.shift.setLocation(0,0);
		this.dist.setLocation(0,0);
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getModifiers()==1)
			dist.setLocation(dist.getX()+e.getWheelRotation()*0.04, dist.getY());
		else
			dist.setLocation(dist.getX(),dist.getY()+e.getWheelRotation()*0.04);

		((Component)e.getSource()).repaint();
	}

}
