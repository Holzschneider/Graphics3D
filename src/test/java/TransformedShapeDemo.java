import io.github.dualuse.awt.TransformedShape;

import javax.swing.*;
import io.github.dualuse.awt.math.Matrix4f;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import static io.github.dualuse.awt.Graphics3D.createMatrixWithFrustum;
import static io.github.dualuse.awt.Graphics3D.createMatrixWithViewport;

public class TransformedShapeDemo {

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
