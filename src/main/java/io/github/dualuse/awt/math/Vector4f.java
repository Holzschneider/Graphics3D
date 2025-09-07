package io.github.dualuse.awt.math;

public class Vector4f {
    public float x, y, z, w;

    public Vector4f() { this(0,0,0,0); }
    public Vector4f(float x, float y, float z, float w) {
        this.x = x; this.y = y; this.z = z; this.w = w;
    }

    public Vector4f set(float x, float y, float z, float w) {
        this.x = x; this.y = y; this.z = z; this.w = w; return this;
    }

    public Vector4f set(Vector4f v) {
        return set(v.x, v.y, v.z, v.w);
    }

    public void scale(float s) {
        this.x *= s; this.y *= s; this.z *= s; this.w *= s;
    }
}