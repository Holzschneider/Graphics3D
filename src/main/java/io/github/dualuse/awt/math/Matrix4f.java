/*
 * This file is part of Graphics3D.
 *
 * Graphics3D is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Graphics3D is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Graphics3D.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.dualuse.awt.math;

public class Matrix4f {
    public float m00, m01, m02, m03;
    public float m10, m11, m12, m13;
    public float m20, m21, m22, m23;
    public float m30, m31, m32, m33;

    public Matrix4f() { setIdentity(); }

    public Matrix4f(float m00, float m01, float m02, float m03,
                    float m10, float m11, float m12, float m13,
                    float m20, float m21, float m22, float m23,
                    float m30, float m31, float m32, float m33) {
        this.m00=m00; this.m01=m01; this.m02=m02; this.m03=m03;
        this.m10=m10; this.m11=m11; this.m12=m12; this.m13=m13;
        this.m20=m20; this.m21=m21; this.m22=m22; this.m23=m23;
        this.m30=m30; this.m31=m31; this.m32=m32; this.m33=m33;
    }

    public Matrix4f(Matrix4f o) { set(o); }
    public Matrix4f(Matrix4d o) { set(o); }

    public void setIdentity() {
        m00=1; m01=0; m02=0; m03=0;
        m10=0; m11=1; m12=0; m13=0;
        m20=0; m21=0; m22=1; m23=0;
        m30=0; m31=0; m32=0; m33=1;
    }

    public void set(Matrix4f o) {
        this.m00=o.m00; this.m01=o.m01; this.m02=o.m02; this.m03=o.m03;
        this.m10=o.m10; this.m11=o.m11; this.m12=o.m12; this.m13=o.m13;
        this.m20=o.m20; this.m21=o.m21; this.m22=o.m22; this.m23=o.m23;
        this.m30=o.m30; this.m31=o.m31; this.m32=o.m32; this.m33=o.m33;
    }

    public void set(Matrix4d o) {
        this.m00=(float)o.m00; this.m01=(float)o.m01; this.m02=(float)o.m02; this.m03=(float)o.m03;
        this.m10=(float)o.m10; this.m11=(float)o.m11; this.m12=(float)o.m12; this.m13=(float)o.m13;
        this.m20=(float)o.m20; this.m21=(float)o.m21; this.m22=(float)o.m22; this.m23=(float)o.m23;
        this.m30=(float)o.m30; this.m31=(float)o.m31; this.m32=(float)o.m32; this.m33=(float)o.m33;
    }

    public void set(AxisAngle4d a) {
        // Normalize axis
        double len = Math.sqrt(a.x*a.x + a.y*a.y + a.z*a.z);
        double x = a.x, y = a.y, z = a.z;
        if (len != 0) { x/=len; y/=len; z/=len; }
        double c = Math.cos(a.angle);
        double s = Math.sin(a.angle);
        double t = 1.0 - c;

        double rm00 = t*x*x + c;
        double rm01 = t*x*y - s*z;
        double rm02 = t*x*z + s*y;
        double rm10 = t*x*y + s*z;
        double rm11 = t*y*y + c;
        double rm12 = t*y*z - s*x;
        double rm20 = t*x*z - s*y;
        double rm21 = t*y*z + s*x;
        double rm22 = t*z*z + c;

        this.m00=(float)rm00; this.m01=(float)rm01; this.m02=(float)rm02; this.m03=0f;
        this.m10=(float)rm10; this.m11=(float)rm11; this.m12=(float)rm12; this.m13=0f;
        this.m20=(float)rm20; this.m21=(float)rm21; this.m22=(float)rm22; this.m23=0f;
        this.m30=0f;          this.m31=0f;          this.m32=0f;          this.m33=1f;
    }

    public void mul(Matrix4f r) { // this = this * r
        mul(this, r, this);
    }

    public void mul(Matrix4f a, Matrix4f b) { // this = a * b
        mul(a, b, this);
    }

    private static void mul(Matrix4f a, Matrix4f b, Matrix4f dst) {
        float nm00 = a.m00*b.m00 + a.m01*b.m10 + a.m02*b.m20 + a.m03*b.m30;
        float nm01 = a.m00*b.m01 + a.m01*b.m11 + a.m02*b.m21 + a.m03*b.m31;
        float nm02 = a.m00*b.m02 + a.m01*b.m12 + a.m02*b.m22 + a.m03*b.m32;
        float nm03 = a.m00*b.m03 + a.m01*b.m13 + a.m02*b.m23 + a.m03*b.m33;

        float nm10 = a.m10*b.m00 + a.m11*b.m10 + a.m12*b.m20 + a.m13*b.m30;
        float nm11 = a.m10*b.m01 + a.m11*b.m11 + a.m12*b.m21 + a.m13*b.m31;
        float nm12 = a.m10*b.m02 + a.m11*b.m12 + a.m12*b.m22 + a.m13*b.m32;
        float nm13 = a.m10*b.m03 + a.m11*b.m13 + a.m12*b.m23 + a.m13*b.m33;

        float nm20 = a.m20*b.m00 + a.m21*b.m10 + a.m22*b.m20 + a.m23*b.m30;
        float nm21 = a.m20*b.m01 + a.m21*b.m11 + a.m22*b.m21 + a.m23*b.m31;
        float nm22 = a.m20*b.m02 + a.m21*b.m12 + a.m22*b.m22 + a.m23*b.m32;
        float nm23 = a.m20*b.m03 + a.m21*b.m13 + a.m22*b.m23 + a.m23*b.m33;

        float nm30 = a.m30*b.m00 + a.m31*b.m10 + a.m32*b.m20 + a.m33*b.m30;
        float nm31 = a.m30*b.m01 + a.m31*b.m11 + a.m32*b.m21 + a.m33*b.m31;
        float nm32 = a.m30*b.m02 + a.m31*b.m12 + a.m32*b.m22 + a.m33*b.m32;
        float nm33 = a.m30*b.m03 + a.m31*b.m13 + a.m32*b.m23 + a.m33*b.m33;

        dst.m00=nm00; dst.m01=nm01; dst.m02=nm02; dst.m03=nm03;
        dst.m10=nm10; dst.m11=nm11; dst.m12=nm12; dst.m13=nm13;
        dst.m20=nm20; dst.m21=nm21; dst.m22=nm22; dst.m23=nm23;
        dst.m30=nm30; dst.m31=nm31; dst.m32=nm32; dst.m33=nm33;
    }

    public void transform(Vector4f v) {
        float x = v.x, y = v.y, z = v.z, w = v.w;
        v.x = m00*x + m01*y + m02*z + m03*w;
        v.y = m10*x + m11*y + m12*z + m13*w;
        v.z = m20*x + m21*y + m22*z + m23*w;
        v.w = m30*x + m31*y + m32*z + m33*w;
    }

    public void transform(Vector4f src, Vector4f dst) {
        float x = src.x, y = src.y, z = src.z, w = src.w;
        dst.x = m00*x + m01*y + m02*z + m03*w;
        dst.y = m10*x + m11*y + m12*z + m13*w;
        dst.z = m20*x + m21*y + m22*z + m23*w;
        dst.w = m30*x + m31*y + m32*z + m33*w;
    }

    public void rotX(float angle) {
        float c = (float)Math.cos(angle);
        float s = (float)Math.sin(angle);
        setIdentity();
        m11 = c; m12 = -s;
        m21 = s; m22 =  c;
    }

    public void rotY(float angle) {
        float c = (float)Math.cos(angle);
        float s = (float)Math.sin(angle);
        setIdentity();
        m00 =  c; m02 = s;
        m20 = -s; m22 = c;
    }

    @Override public String toString() {
        return String.format("[[%f,%f,%f,%f],[%f,%f,%f,%f],[%f,%f,%f,%f],[%f,%f,%f,%f]]",
                m00,m01,m02,m03, m10,m11,m12,m13, m20,m21,m22,m23, m30,m31,m32,m33);
    }
}