Graphics3D
==========
*a lightweight extension to java.awt.Graphics2D featuring opengl style 3D transformations and primitives*

Requires JDK 1.6, and comes with soon-to-be-gone javax.vecmath dependency.
 

In Short
--------

Imagine a suspiciously familiar looking JComponents's paintComponent method like this ...

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
		
... would yield this  

<img src="doc/demo.gif" />


Latest Release
-------

The current release 1.0.x. is the first to be considered finally stable with no changes made for a very long time.

Releases are deployed automatically to the deploy branch of this github repostory. 
To add a dependency on graphics3d using maven, modify your *repositories* section to include the git based repository.

	<repositories>
	 ...
	  <repository>
	    <id>dualuse repository</id>
	    <name>dualuse's git based repo</name>
	    <url>https://dualuse.github.io/maven/</url>
	  </repository>
	...
	</repositories>
	
and modify your *dependencies* section to include the Graphics3d dependency
 
	  <dependencies>
	  ...
	  	<dependency>
	  		<groupId>de.dualuse</groupId>
	  		<artifactId>Graphics3D</artifactId>
	  		<version>[1,)</version>
	  	</dependency>
	  ...
	  </dependencies>


To add the repository and the dependency using gradle refer to this

	repositories {
	    maven {
	        url "https://raw.githubusercontent.com/Holzschneider/Graphics3D/deploy/"
	    }
	}

and this

	dependencies {
	  compile 'de.dualuse:Graphics3D:1.+'
	}



