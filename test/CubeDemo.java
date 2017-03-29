import java.awt.BasicStroke;
import static java.awt.BasicStroke.*;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;

import de.dualuse.commons.awt.Graphics3D;

public class CubeDemo extends JComponent {
	private static final long serialVersionUID = 1L;
	long start = System.nanoTime();
	
	
	/**
	 * super important improvement 
	 */
	protected void paintComponent(Graphics g) {
		Graphics3D g3 = new Graphics3D(g);
		g3.setStroke(new BasicStroke(10,1,1,1,new float[]{ 25,40 },0));
		
		double r = getHeight()*1.0/getWidth();
		g3.viewport(0, 0, getWidth(), getHeight());
		g3.frustum(-1, 1, +r, -r, 2, 100);
		
		g3.translate(0, 0, -5);

		double degrees = System.nanoTime()/1e9;
		g3.rotate(degrees, 1, 0, 0);
		g3.rotate(degrees, 0, 0, 1);

		g3.begin(Graphics3D.QUADS);
			g3.vertex(-1, -1, -1);
			g3.vertex( 1, -1, -1);
			g3.vertex( 1,  1, -1);
			g3.vertex(-1,  1, -1);

			g3.vertex(-1, -1, +1);
			g3.vertex( 1, -1, +1);
			g3.vertex( 1,  1, +1);
			g3.vertex(-1,  1, +1);
		g3.end();
		
		g3.setStroke(new BasicStroke(2));		
		g3.draw(new RoundRectangle2D.Double(-1, -1, 2, 2, 0.5, 0.5));
		
		g3.scale(0.02, 0.02);
		g3.setFont(g3.getFont().deriveFont(Font.BOLD));
		g3.drawString("Graphics3D", -50, 0);
		
		g3.dispose();
		repaint();
	}
	
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.add(new CubeDemo());
		f.setBounds(100, 100, 800, 800);
		f.setVisible(true);
	}
}
