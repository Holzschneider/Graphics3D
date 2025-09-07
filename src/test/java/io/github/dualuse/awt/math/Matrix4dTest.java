package io.github.dualuse.awt.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Matrix4dTest {

    private static void assertMatAlmostIdentity(Matrix4d m, double eps) {
        assertEquals(1.0, m.m00, eps); assertEquals(0.0, m.m01, eps); assertEquals(0.0, m.m02, eps); assertEquals(0.0, m.m03, eps);
        assertEquals(0.0, m.m10, eps); assertEquals(1.0, m.m11, eps); assertEquals(0.0, m.m12, eps); assertEquals(0.0, m.m13, eps);
        assertEquals(0.0, m.m20, eps); assertEquals(0.0, m.m21, eps); assertEquals(1.0, m.m22, eps); assertEquals(0.0, m.m23, eps);
        assertEquals(0.0, m.m30, eps); assertEquals(0.0, m.m31, eps); assertEquals(0.0, m.m32, eps); assertEquals(1.0, m.m33, eps);
    }

    @Test void defaultIsIdentityAndDeterminantIsOne() {
        Matrix4d m = new Matrix4d();
        assertMatAlmostIdentity(m, 0.0);
        assertEquals(1.0, m.determinant(), 1e-12);
    }

    @Test void determinantOfDiagonalIsProduct() {
        Matrix4d m = new Matrix4d(
                2,0,0,0,
                0,3,0,0,
                0,0,4,0,
                0,0,0,5);
        assertEquals(2*3*4*5, m.determinant(), 1e-12);
    }

    @Test void invertIdentityRemainsIdentity() {
        Matrix4d m = new Matrix4d();
        m.invert();
        assertMatAlmostIdentity(m, 1e-12);
    }

    @Test void invertDiagonal() {
        Matrix4d m = new Matrix4d(
                2,0,0,0,
                0,3,0,0,
                0,0,4,0,
                0,0,0,1);
        m.invert();
        Matrix4d expectedInv = new Matrix4d(
                0.5,0,0,0,
                0,1.0/3.0,0,0,
                0,0,0.25,0,
                0,0,0,1);
        // Multiply original by computed inverse should be identity
        Matrix4d orig = new Matrix4d(
                2,0,0,0,
                0,3,0,0,
                0,0,4,0,
                0,0,0,1);
        Matrix4d prod = new Matrix4d();
        prod.mul(orig, m);
        assertMatAlmostIdentity(prod, 1e-12);
        // And computed inverse equals expected along diagonal
        assertEquals(expectedInv.m00, m.m00, 1e-12);
        assertEquals(expectedInv.m11, m.m11, 1e-12);
        assertEquals(expectedInv.m22, m.m22, 1e-12);
        assertEquals(expectedInv.m33, m.m33, 1e-12);
    }

    @Test void invertThrowsForSingularMatrix() {
        Matrix4d m = new Matrix4d(
                1,0,0,0,
                0,0,0,0, // zero row -> singular
                0,0,1,0,
                0,0,0,1);
        assertThrows(IllegalStateException.class, m::invert);
    }

    @Test void transformVector4dAndVector4f() {
        Matrix4d d = new Matrix4d(
                2,0,0,0,
                0,3,0,0,
                0,0,4,0,
                0,0,0,1);
        Vector4d vd = new Vector4d(1,2,3,1);
        Vector4d outd = new Vector4d();
        d.transform(vd, outd);
        assertEquals(2.0, outd.x, 1e-12);
        assertEquals(6.0, outd.y, 1e-12);
        assertEquals(12.0, outd.z, 1e-12);
        assertEquals(1.0, outd.w, 1e-12);

        Vector4f vf = new Vector4f(1,2,3,1);
        Vector4f outf = new Vector4f();
        d.transform(vf, outf);
        assertEquals(2.0f, outf.x, 1e-6f);
        assertEquals(6.0f, outf.y, 1e-6f);
        assertEquals(12.0f, outf.z, 1e-6f);
        assertEquals(1.0f, outf.w, 1e-6f);
    }

    @Test void setFromMatrix4fProducesEquivalentTransform() {
        Matrix4f mf = new Matrix4f();
        mf.rotY((float)Math.PI/2);
        Matrix4d md = new Matrix4d();
        md.set(mf);

        Vector4f v = new Vector4f(1,0,0,1);
        Vector4f vfOut1 = new Vector4f();
        Vector4f vfOut2 = new Vector4f();
        mf.transform(v, vfOut1);
        md.transform(v, vfOut2);
        assertEquals(vfOut1.x, vfOut2.x, 1e-5f);
        assertEquals(vfOut1.y, vfOut2.y, 1e-5f);
        assertEquals(vfOut1.z, vfOut2.z, 1e-5f);
        assertEquals(vfOut1.w, vfOut2.w, 1e-5f);
    }
}
