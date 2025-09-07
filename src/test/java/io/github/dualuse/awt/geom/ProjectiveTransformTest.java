package io.github.dualuse.awt.geom;

import io.github.dualuse.awt.math.Matrix4d;
import io.github.dualuse.awt.math.Matrix4f;
import io.github.dualuse.awt.math.Vector4d;
import io.github.dualuse.awt.math.Vector4f;
import org.junit.jupiter.api.Test;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.*;

class ProjectiveTransformTest {

    private static void assertArrEquals(double[] expected, double[] actual, double eps) {
        assertEquals(expected.length, actual.length, "length");
        for (int i=0;i<expected.length;i++) assertEquals(expected[i], actual[i], eps, "idx="+i);
    }

    private static Point2D.Double p(double x, double y) { return new Point2D.Double(x,y); }

    @Test
    void constructorFromMatrixCopiesAndSetsAffinePart() {
        Matrix4d m = new Matrix4d(
                2, 0.5, 0, 10,
                0.25, 3, 0, -5,
                0, 0, 1, 0,
                0, 0, 0, 1);
        ProjectiveTransform pt = new ProjectiveTransform(m);

        // getMatrix returns a copy equal to original
        Matrix4d back = pt.getMatrix();
        assertEquals(m.toString(), back.toString());
        assertNotSame(m, back);

        // Affine part in super should match 2x3 from matrix
        Point2D src = p(1,2);
        Point2D dst = new Point2D.Double();
        pt.transform(src, dst);

        AffineTransform at = new AffineTransform(m.m00, m.m10, m.m01, m.m11, m.m03, m.m13);
        Point2D chk = new Point2D.Double();
        at.transform(src, chk);
        assertEquals(chk.getX(), dst.getX(), 1e-9);
        assertEquals(chk.getY(), dst.getY(), 1e-9);

        // determinant should equal the wrapped 4x4 determinant
        assertEquals(m.determinant(), pt.getDeterminant(), 1e-12);
    }

    @Test
    void getMatrixOverloadsAndFlattening() {
        Matrix4d m = new Matrix4d(
                1,2,3,4,
                5,6,7,8,
                9,10,11,12,
                13,14,15,16);
        ProjectiveTransform pt = new ProjectiveTransform(m);

        // Copy into provided Matrix4d/Matrix4f
        Matrix4d d = new Matrix4d();
        Matrix4f f = new Matrix4f();
        pt.getMatrix(d);
        pt.getMatrix(f);
        assertEquals(m.toString(), d.toString());
        assertEquals((float)m.m00, f.m00, 0f);
        assertEquals((float)m.m13, f.m13, 0f);
        assertEquals((float)m.m33, f.m33, 0f);

        // Flatten in column-major order as implemented
        double[] flat = new double[16];
        pt.getMatrix(flat);
        double[] expected = new double[]{
                m.m00, m.m10, m.m20, m.m30,
                m.m01, m.m11, m.m21, m.m31,
                m.m02, m.m12, m.m22, m.m32,
                m.m03, m.m13, m.m23, m.m33
        };
        assertArrEquals(expected, flat, 0.0);
    }

    @Test
    void basic2DOperationsMatchAffineTransform() {
        Matrix4d id = new Matrix4d();
        ProjectiveTransform pt = new ProjectiveTransform(id);

        // Apply operations and mirror with AffineTransform
        pt.translate(10, -5);
        pt.scale(2, 3);
        pt.rotate(Math.PI/6);
        pt.shear(0.1, -0.2);

        AffineTransform at = new AffineTransform();
        at.translate(10, -5);
        at.scale(2, 3);
        at.rotate(Math.PI/6);
        at.shear(0.1, -0.2);

        Point2D src = p(7, -3);
        Point2D a = new Point2D.Double();
        Point2D b = new Point2D.Double();
        pt.transform(src, a);
        at.transform(src, b);
        assertEquals(b.getX(), a.getX(), 1e-6);
        assertEquals(b.getY(), a.getY(), 1e-6);

        // Wrapped's 2x3 should mirror Affine
        assertEquals(at.getScaleX(), pt.getScaleX(), 1e-5);
        assertEquals(at.getScaleY(), pt.getScaleY(), 1e-5);
        assertEquals(at.getShearX(), pt.getShearX(), 1e-5);
        assertEquals(at.getShearY(), pt.getShearY(), 1e-5);
        assertEquals(at.getTranslateX(), pt.getTranslateX(), 1e-5);
        assertEquals(at.getTranslateY(), pt.getTranslateY(), 1e-5);
    }

    @Test
    void setToOperationsResetAndSetProperly() {
        ProjectiveTransform pt = new ProjectiveTransform(new Matrix4d());
        pt.setToIdentity();
        assertTrue(pt.isIdentity());

        pt.setToTranslation(3, 4);
        Point2D out = new Point2D.Double();
        pt.transform(p(1,2), out);
        assertEquals(4, out.getX(), 1e-9);
        assertEquals(6, out.getY(), 1e-9);

        pt.setToScale(2, 5);
        pt.transform(p(1,2), out);
        assertEquals(2, out.getX(), 1e-9);
        assertEquals(10, out.getY(), 1e-9);

        pt.setToRotation(Math.PI/2);
        pt.transform(p(1,0), out);
        assertEquals(0, out.getX(), 1e-9);
        assertEquals(1, out.getY(), 1e-9);
    }

    @Test
    void sixteenParameterSetTransformSupportsProjectiveAndThrowsFor2DArrays() {
        ProjectiveTransform pt = new ProjectiveTransform(new Matrix4d());
        // A simple perspective-like matrix (affects w via last row)
        pt.setTransform(
                1,0,0,0,
                0,1,0,0,
                0,0,1,-0.2, // move camera along z
                0,0,-0.1,1   // projective row
        );

        // transform(Point2D) still uses affine top-left 2x3
        Point2D src = p(2,3);
        Point2D dst = new Point2D.Double();
        pt.transform(src, dst);
        assertEquals(2, dst.getX(), 1e-9);
        assertEquals(3, dst.getY(), 1e-9);

        // But 2D array-based transform should assert non-affine
        double[] s = new double[]{1,2, 3,4};
        double[] d = new double[4];
        assertThrows(RuntimeException.class, () -> pt.transform(s, 0, d, 0, 2));
        assertThrows(RuntimeException.class, () -> pt.inverseTransform(s, 0, d, 0, 2));
    }

    @Test
    void concatenateAndPreConcatenateOrderMatchesAffine() {
        ProjectiveTransform a = new ProjectiveTransform(new Matrix4d());
        ProjectiveTransform b = new ProjectiveTransform(new Matrix4d());
        a.setToTranslation(5, 0);
        b.setToScale(2, 3);

        // this = this * Tx
        ProjectiveTransform c1 = new ProjectiveTransform(a.getMatrix());
        c1.concatenate(b);

        // Affine comparison
        AffineTransform at1 = new AffineTransform();
        at1.translate(5, 0);
        at1.scale(2, 3);

        Point2D p = p(1,2);
        Point2D r1 = new Point2D.Double();
        c1.transform(p, r1);
        Point2D r2 = new Point2D.Double();
        at1.transform(p, r2);
        assertEquals(r2.getX(), r1.getX(), 1e-9);
        assertEquals(r2.getY(), r1.getY(), 1e-9);

        // Pre-concatenate: this = Tx * this
        ProjectiveTransform c2 = new ProjectiveTransform(a.getMatrix());
        c2.preConcatenate(b);
        AffineTransform at2 = new AffineTransform();
        at2.scale(2,3);
        at2.translate(5,0);
        Point2D r3 = new Point2D.Double();
        c2.transform(p, r3);
        Point2D r4 = new Point2D.Double();
        at2.transform(p, r4);
        assertEquals(r4.getX(), r3.getX(), 1e-9);
        assertEquals(r4.getY(), r3.getY(), 1e-9);
    }

    @Test
    void inverseAndInvertRoundTripFor4D() {
        // A general 4x4 (invertible)
        Matrix4d m = new Matrix4d(
                1.2, 0.3, -0.1, 2.0,
                0.5, 0.9,  0.2, -1.0,
                -0.4, 0.1, 1.5, 0.7,
                0.0, 0.0,  0.0, 1.0);
        ProjectiveTransform pt = new ProjectiveTransform(m);

        // createInverse returns new instance
        ProjectiveTransform inv = pt.createInverse();
        Vector4d v = new Vector4d(1,2,3,1);
        Vector4d t = new Vector4d();
        Vector4d u = new Vector4d();
        pt.transform(v, t);
        inv.transform(t, u);
        assertEquals(v.x, u.x, 1e-9);
        assertEquals(v.y, u.y, 1e-9);
        assertEquals(v.z, u.z, 1e-9);
        assertEquals(v.w, u.w, 1e-9);

        // In-place invert behaves similarly
        ProjectiveTransform cp = new ProjectiveTransform(m);
        cp.invert();
        Vector4f vf = new Vector4f(0.5f,-1f,2f,1f);
        Vector4f vt = new Vector4f();
        Vector4f vu = new Vector4f();
        // forward then inverted should round trip
        new ProjectiveTransform(m).transform(vf, vt);
        cp.transform(vt, vu);
        assertEquals(vf.x, vu.x, 1e-5);
        assertEquals(vf.y, vu.y, 1e-5);
        assertEquals(vf.z, vu.z, 1e-5);
        assertEquals(vf.w, vu.w, 1e-5);
    }

    @Test
    void equalityHashCodeCloneAndIdentity() {
        ProjectiveTransform a = new ProjectiveTransform(new Matrix4d());
        ProjectiveTransform b = a.clone();
        assertNotSame(a, b);
        // equals uses wrapped.equals (reference equality), so only same instance compares equal
        assertEquals(a, a);
        assertNotEquals(a, b);
        // hashCode is based on wrapped identity; different instances will likely differ
        assertTrue(a.isIdentity());

        a.translate(1, 0);
        assertNotEquals(a, b);
        assertFalse(a.isIdentity());

        // equals only with same type
        assertNotEquals(a, new AffineTransform());
    }

    @Test
    void createTransformedShapeReturnsNonNullShape() {
        ProjectiveTransform a = new ProjectiveTransform(new Matrix4d());
        Shape s = new java.awt.geom.Rectangle2D.Double(0,0,10,10);
        Shape ts = a.createTransformedShape(s);
        assertNotNull(ts);
    }
}
