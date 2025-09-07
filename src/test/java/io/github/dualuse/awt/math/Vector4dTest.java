package io.github.dualuse.awt.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector4dTest {

    private static void assertVecEquals(double x, double y, double z, double w, Vector4d v, double eps) {
        assertEquals(x, v.x, eps, "x");
        assertEquals(y, v.y, eps, "y");
        assertEquals(z, v.z, eps, "z");
        assertEquals(w, v.w, eps, "w");
    }

    @Test void defaultConstructorInitializesToZero() {
        Vector4d v = new Vector4d();
        assertVecEquals(0.0, 0.0, 0.0, 0.0, v, 0.0);
    }

    @Test void valueConstructorSetsFields() {
        Vector4d v = new Vector4d(1.0, 2.0, 3.0, 4.0);
        assertVecEquals(1.0, 2.0, 3.0, 4.0, v, 0.0);
    }

    @Test void setValuesMutatesAndReturnsThis() {
        Vector4d v = new Vector4d();
        Vector4d r = v.set(5.0, 6.0, 7.0, 8.0);
        assertSame(v, r);
        assertVecEquals(5.0, 6.0, 7.0, 8.0, v, 0.0);
    }

    @Test void setFromVectorCopiesValues() {
        Vector4d a = new Vector4d(1.0, 2.0, 3.0, 4.0);
        Vector4d b = new Vector4d();
        b.set(a);
        assertVecEquals(1.0, 2.0, 3.0, 4.0, b, 0.0);
        // ensure independence after copy
        a.set(9.0, 8.0, 7.0, 6.0);
        assertVecEquals(1.0, 2.0, 3.0, 4.0, b, 0.0);
    }

    @Test void scaleMultipliesEachComponent() {
        Vector4d v = new Vector4d(1.0, -2.0, 0.5, 4.0);
        v.scale(2.0);
        assertVecEquals(2.0, -4.0, 1.0, 8.0, v, 1e-12);
    }
}
