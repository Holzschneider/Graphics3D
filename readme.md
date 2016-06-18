Graphics3D
==========
*a lightweight extension to java.awt.Graphics2D featuring opengl style 3D transformations and primitives*


In Short
--------

Imagine a JComponents's paintComponent method like this ...

	protected void paintComponent(Graphics g) {
		Graphics3D g3 = new Graphics3D(g);
		
		g3.viewport(0, 0, getWidth(), getHeight());
		g3.frustum(-1, 1, 1, -1, 2, 100);
		
		g3.translate(0, 0, -5);
		
		double degrees = System.nanoTime()/1e9;
		g3.rotate(degrees, 1, 0, 0);
		g3.rotate(degrees, 0, 1, 0);
		
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
		
		g3.dispose();
		repaint();
	}
		
... would yield this  

<img src="doc/demo.gif" />





