Graphics3D
==========
A lightweight extension to java.awt.Graphics2D featuring OpenGL-style 3D transformations and primitives.

Project status: actively maintained. Target Java 8 (compiled via Gradle toolchains). No external vecmath dependency.

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

Recent releases
---------------
- 1.4.0 (2025-09-07)
  - Removed javax.vecmath dependency; replaced with internal math (Matrix4f/Matrix4d/Vector4f/Vector4d, AxisAngle4d).
  - Switched build to Gradle and reorganized sources into standard directories.
  - Updated package path to io.github.dualuse.* and added texture demo.
  - Perspective texture drawing: add recursion limit to improve performance and avoid runaway subdivision.
  - Increased test coverage (math and core components).
- 1.3.x (2017-01-18)
  - Simplified project structure and migrated publishing to dualuse.github.io's Maven repo.
  - Fixed deployment configuration and adjusted vecmath dependency version.

How to add to your project (Gradle Kotlin DSL, Maven Central)
------------------------------------------------------------
Add Maven Central and the dependency:

repositories {
    mavenCentral()
}

dependencies {
    // Replace 1.4.0 with the latest released version
    implementation("io.github.dualuse:Graphics3D:1.4.0")
}

How to compile
--------------
Prerequisites: JDK 8+.

Build everything (compile + tests):

    ./gradlew build

Run tests only:

    ./gradlew test

Generate wrapper scripts again (if needed):

    ./gradlew wrapper --gradle-version 8.14.2

Contributing
------------
Contributions are welcome! Feel free to open issues and pull requests. Please run the test suite before submitting changes.

License
-------
LGPL-3.0-or-later. See LICENSE and the header in each source file. Graphics3D is distributed in the hope that it will be useful, but without any warranty; see the GNU LGPL for details.


