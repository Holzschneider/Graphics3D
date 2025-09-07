import io.github.dualuse.awt.Graphics3D;
import io.github.dualuse.awt.event.EulerCameraMouseAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class EulerMouseAdapterDemo {

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
