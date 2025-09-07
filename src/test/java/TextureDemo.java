import de.dualuse.commons.awt.Graphics3D;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.vecmath.Matrix4f;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TextureDemo {

    public static void main(String[] args) throws IOException {

//		final BufferedImage im = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);

        final URL iu = TextureDemo.class.getResource("/MonaLisa-tiny.jpg");
        final BufferedImage im = ImageIO.read(iu).getSubimage(0, 0, 460, 460);


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
