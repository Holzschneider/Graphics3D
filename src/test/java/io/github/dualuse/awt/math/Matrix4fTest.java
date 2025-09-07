package io.github.dualuse.awt.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Matrix4fTest {

    private static void assertMatEquals(Matrix4f m, float[][] e, float eps) {
        assertArrayEquals(new float[]{e[0][0], e[0][1], e[0][2], e[0][3]}, new float[]{m.m00, m.m01, m.m02, m.m03}, eps);
        assertArrayEquals(new float[]{e[1][0], e[1][1], e[1][2], e[1][3]}, new float[]{m.m10, m.m11, m.m12, m.m13}, eps);
        assertArrayEquals(new float[]{e[2][0], e[2][1], e[2][2], e[2][3]}, new float[]{m.m20, m.m21, m.m22, m.m23}, eps);
        assertArrayEquals(new float[]{e[3][0], e[3][1], e[3][2], e[3][3]}, new float[]{m.m30, m.m31, m.m32, m.m33}, eps);
    }

    private static void assertVecEq(float x, float y, float z, float w, Vector4f v, float eps) {
        assertEquals(x, v.x, eps);
        assertEquals(y, v.y, eps);
        assertEquals(z, v.z, eps);
        assertEquals(w, v.w, eps);
    }

    @Test void defaultIsIdentity() {
        Matrix4f m = new Matrix4f();
        assertMatEquals(m, new float[][]{
                {1,0,0,0},
                {0,1,0,0},
                {0,0,1,0},
                {0,0,0,1}
        }, 0f);
    }

    @Test void setIdentityResetsMatrix() {
        Matrix4f m = new Matrix4f();
        m.m03 = 5f; // mutate
        m.setIdentity();
        assertMatEquals(m, new float[][]{
                {1,0,0,0},
                {0,1,0,0},
                {0,0,1,0},
                {0,0,0,1}
        }, 0f);
    }

    @Test void rotX90TransformsYToZ() {
        Matrix4f m = new Matrix4f();
        m.rotX((float)Math.PI/2f);
        Vector4f v = new Vector4f(0f, 1f, 0f, 1f);
        m.transform(v);
        assertVecEq(0f, 0f, 1f, 1f, v, 1e-6f);
    }

    @Test void rotY90TransformsXToMinusZ() {
        Matrix4f m = new Matrix4f();
        m.rotY((float)Math.PI/2f);
        Vector4f v = new Vector4f(1f, 0f, 0f, 1f);
        m.transform(v);
        assertVecEq(0f, 0f, -1f, 1f, v, 1e-6f);
    }

    @Test void transformSrcDstDoesNotModifySource() {
        Matrix4f m = new Matrix4f();
        m.rotY((float)Math.PI/2f);
        Vector4f src = new Vector4f(0f, 0f, 1f, 1f);
        Vector4f dst = new Vector4f();
        m.transform(src, dst);
        // For rotY 90°, z-axis maps to x-axis
        assertVecEq(1f, 0f, 0f, 1f, dst, 1e-6f);
        // Source must remain unchanged
        assertVecEq(0f, 0f, 1f, 1f, src, 0f);
    }

    @Test void multiplyIdentityLeavesOtherMatrixUnchanged() {
        Matrix4f id = new Matrix4f();
        Matrix4f r = new Matrix4f(); r.rotX((float)Math.PI/3);
        Matrix4f out = new Matrix4f();
        out.mul(id, r);
        assertEquals(r.toString(), out.toString());
    }

    @Test void axisAngleZ90ProducesExpectedRotation() {
        AxisAngle4d aa = new AxisAngle4d(0, 0, 1, Math.PI/2);
        Matrix4f m = new Matrix4f();
        m.set(aa);
        // x-axis to y-axis
        Vector4f v = new Vector4f(1,0,0,1);
        m.transform(v);
        assertVecEq(0f, 1f, 0f, 1f, v, 1e-6f);
    }

    @Test void matrixMultiplicationOrderMatters() {
        // rotY(90) then rotX(90) applied to vector
        Matrix4f ry = new Matrix4f(); ry.rotY((float)Math.PI/2);
        Matrix4f rx = new Matrix4f(); rx.rotX((float)Math.PI/2);
        Matrix4f combined = new Matrix4f(); combined.mul(ry, rx); // combined = ry * rx

        Vector4f v = new Vector4f(0, 1, 0, 1);
        Vector4f v1 = new Vector4f(v.x, v.y, v.z, v.w);
        rx.transform(v1); // apply rx first
        ry.transform(v1); // then ry

        Vector4f v2 = new Vector4f(v.x, v.y, v.z, v.w);
        combined.transform(v2);

        assertVecEq(v1.x, v1.y, v1.z, v1.w, v2, 1e-5f);
    }

    @Test void translationViaLastColumnAffectsVector() {
        Matrix4f m = new Matrix4f();
        // set translation components in the last column
        m.m03 = 10f; // translate x by +10
        m.m13 = -5f; // translate y by -5
        m.m23 = 2f;  // translate z by +2
        Vector4f v = new Vector4f(1f, 2f, 3f, 1f);
        m.transform(v);
        assertEquals(11f, v.x, 1e-6f);
        assertEquals(-3f, v.y, 1e-6f);
        assertEquals(5f, v.z, 1e-6f);
        assertEquals(1f, v.w, 1e-6f);
    }
}