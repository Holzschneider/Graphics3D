package io.github.dualuse.awt.math;

public class Matrix4d {
    public double m00, m01, m02, m03;
    public double m10, m11, m12, m13;
    public double m20, m21, m22, m23;
    public double m30, m31, m32, m33;

    public Matrix4d() { setIdentity(); }

    public Matrix4d(double m00, double m01, double m02, double m03,
                    double m10, double m11, double m12, double m13,
                    double m20, double m21, double m22, double m23,
                    double m30, double m31, double m32, double m33) {
        this.m00=m00; this.m01=m01; this.m02=m02; this.m03=m03;
        this.m10=m10; this.m11=m11; this.m12=m12; this.m13=m13;
        this.m20=m20; this.m21=m21; this.m22=m22; this.m23=m23;
        this.m30=m30; this.m31=m31; this.m32=m32; this.m33=m33;
    }

    public Matrix4d(Matrix4d o) { set(o); }

    public void setIdentity() {
        m00=1; m01=0; m02=0; m03=0;
        m10=0; m11=1; m12=0; m13=0;
        m20=0; m21=0; m22=1; m23=0;
        m30=0; m31=0; m32=0; m33=1;
    }

    public void set(Matrix4d o) {
        this.m00=o.m00; this.m01=o.m01; this.m02=o.m02; this.m03=o.m03;
        this.m10=o.m10; this.m11=o.m11; this.m12=o.m12; this.m13=o.m13;
        this.m20=o.m20; this.m21=o.m21; this.m22=o.m22; this.m23=o.m23;
        this.m30=o.m30; this.m31=o.m31; this.m32=o.m32; this.m33=o.m33;
    }

    public void set(Matrix4f o) {
        this.m00=o.m00; this.m01=o.m01; this.m02=o.m02; this.m03=o.m03;
        this.m10=o.m10; this.m11=o.m11; this.m12=o.m12; this.m13=o.m13;
        this.m20=o.m20; this.m21=o.m21; this.m22=o.m22; this.m23=o.m23;
        this.m30=o.m30; this.m31=o.m31; this.m32=o.m32; this.m33=o.m33;
    }

    public void mul(Matrix4d r) { // this = this * r
        mul(this, r, this);
    }

    public void mul(Matrix4d a, Matrix4d b) { // this = a * b
        mul(a, b, this);
    }

    private static void mul(Matrix4d a, Matrix4d b, Matrix4d dst) {
        double nm00 = a.m00*b.m00 + a.m01*b.m10 + a.m02*b.m20 + a.m03*b.m30;
        double nm01 = a.m00*b.m01 + a.m01*b.m11 + a.m02*b.m21 + a.m03*b.m31;
        double nm02 = a.m00*b.m02 + a.m01*b.m12 + a.m02*b.m22 + a.m03*b.m32;
        double nm03 = a.m00*b.m03 + a.m01*b.m13 + a.m02*b.m23 + a.m03*b.m33;

        double nm10 = a.m10*b.m00 + a.m11*b.m10 + a.m12*b.m20 + a.m13*b.m30;
        double nm11 = a.m10*b.m01 + a.m11*b.m11 + a.m12*b.m21 + a.m13*b.m31;
        double nm12 = a.m10*b.m02 + a.m11*b.m12 + a.m12*b.m22 + a.m13*b.m32;
        double nm13 = a.m10*b.m03 + a.m11*b.m13 + a.m12*b.m23 + a.m13*b.m33;

        double nm20 = a.m20*b.m00 + a.m21*b.m10 + a.m22*b.m20 + a.m23*b.m30;
        double nm21 = a.m20*b.m01 + a.m21*b.m11 + a.m22*b.m21 + a.m23*b.m31;
        double nm22 = a.m20*b.m02 + a.m21*b.m12 + a.m22*b.m22 + a.m23*b.m32;
        double nm23 = a.m20*b.m03 + a.m21*b.m13 + a.m22*b.m23 + a.m23*b.m33;

        double nm30 = a.m30*b.m00 + a.m31*b.m10 + a.m32*b.m20 + a.m33*b.m30;
        double nm31 = a.m30*b.m01 + a.m31*b.m11 + a.m32*b.m21 + a.m33*b.m31;
        double nm32 = a.m30*b.m02 + a.m31*b.m12 + a.m32*b.m22 + a.m33*b.m32;
        double nm33 = a.m30*b.m03 + a.m31*b.m13 + a.m32*b.m23 + a.m33*b.m33;

        dst.m00=nm00; dst.m01=nm01; dst.m02=nm02; dst.m03=nm03;
        dst.m10=nm10; dst.m11=nm11; dst.m12=nm12; dst.m13=nm13;
        dst.m20=nm20; dst.m21=nm21; dst.m22=nm22; dst.m23=nm23;
        dst.m30=nm30; dst.m31=nm31; dst.m32=nm32; dst.m33=nm33;
    }

    public double determinant() {
        // Compute determinant of 4x4 matrix
        double a0 = m00*m11 - m01*m10;
        double a1 = m00*m12 - m02*m10;
        double a2 = m00*m13 - m03*m10;
        double a3 = m01*m12 - m02*m11;
        double a4 = m01*m13 - m03*m11;
        double a5 = m02*m13 - m03*m12;

        double b0 = m20*m31 - m21*m30;
        double b1 = m20*m32 - m22*m30;
        double b2 = m20*m33 - m23*m30;
        double b3 = m21*m32 - m22*m31;
        double b4 = m21*m33 - m23*m31;
        double b5 = m22*m33 - m23*m32;

        return a0*b5 - a1*b4 + a2*b3 + a3*b2 - a4*b1 + a5*b0;
    }

    public void invert() {
        // Invert using adjugate/determinant method
        double det = determinant();
        if (Math.abs(det) < 1e-12) throw new IllegalStateException("Matrix not invertible");
        double invDet = 1.0/det;

        double a00=m00,a01=m01,a02=m02,a03=m03;
        double a10=m10,a11=m11,a12=m12,a13=m13;
        double a20=m20,a21=m21,a22=m22,a23=m23;
        double a30=m30,a31=m31,a32=m32,a33=m33;

        double c00 =  a11*(a22*a33 - a23*a32) - a12*(a21*a33 - a23*a31) + a13*(a21*a32 - a22*a31);
        double c01 = -a10*(a22*a33 - a23*a32) + a12*(a20*a33 - a23*a30) - a13*(a20*a32 - a22*a30);
        double c02 =  a10*(a21*a33 - a23*a31) - a11*(a20*a33 - a23*a30) + a13*(a20*a31 - a21*a30);
        double c03 = -a10*(a21*a32 - a22*a31) + a11*(a20*a32 - a22*a30) - a12*(a20*a31 - a21*a30);

        double c10 = -a01*(a22*a33 - a23*a32) + a02*(a21*a33 - a23*a31) - a03*(a21*a32 - a22*a31);
        double c11 =  a00*(a22*a33 - a23*a32) - a02*(a20*a33 - a23*a30) + a03*(a20*a32 - a22*a30);
        double c12 = -a00*(a21*a33 - a23*a31) + a01*(a20*a33 - a23*a30) - a03*(a20*a31 - a21*a30);
        double c13 =  a00*(a21*a32 - a22*a31) - a01*(a20*a32 - a22*a30) + a02*(a20*a31 - a21*a30);

        double c20 =  a01*(a12*a33 - a13*a32) - a02*(a11*a33 - a13*a31) + a03*(a11*a32 - a12*a31);
        double c21 = -a00*(a12*a33 - a13*a32) + a02*(a10*a33 - a13*a30) - a03*(a10*a32 - a12*a30);
        double c22 =  a00*(a11*a33 - a13*a31) - a01*(a10*a33 - a13*a30) + a03*(a10*a31 - a11*a30);
        double c23 = -a00*(a11*a32 - a12*a31) + a01*(a10*a32 - a12*a30) - a02*(a10*a31 - a11*a30);

        double c30 = -a01*(a12*a23 - a13*a22) + a02*(a11*a23 - a13*a21) - a03*(a11*a22 - a12*a21);
        double c31 =  a00*(a12*a23 - a13*a22) - a02*(a10*a23 - a13*a20) + a03*(a10*a22 - a12*a20);
        double c32 = -a00*(a11*a23 - a13*a21) + a01*(a10*a23 - a13*a20) - a03*(a10*a21 - a11*a20);
        double c33 =  a00*(a11*a22 - a12*a21) - a01*(a10*a22 - a12*a20) + a02*(a10*a21 - a11*a20);

        // adjugate is transpose of cofactor matrix
        m00 = c00*invDet; m01 = c10*invDet; m02 = c20*invDet; m03 = c30*invDet;
        m10 = c01*invDet; m11 = c11*invDet; m12 = c21*invDet; m13 = c31*invDet;
        m20 = c02*invDet; m21 = c12*invDet; m22 = c22*invDet; m23 = c32*invDet;
        m30 = c03*invDet; m31 = c13*invDet; m32 = c23*invDet; m33 = c33*invDet;
    }

    public void transform(Vector4d src, Vector4d dst) {
        double x = src.x, y = src.y, z = src.z, w = src.w;
        dst.x = m00*x + m01*y + m02*z + m03*w;
        dst.y = m10*x + m11*y + m12*z + m13*w;
        dst.z = m20*x + m21*y + m22*z + m23*w;
        dst.w = m30*x + m31*y + m32*z + m33*w;
    }

    public void transform(Vector4f src, Vector4f dst) {
        float x = src.x, y = src.y, z = src.z, w = src.w;
        dst.x = (float)(m00*x + m01*y + m02*z + m03*w);
        dst.y = (float)(m10*x + m11*y + m12*z + m13*w);
        dst.z = (float)(m20*x + m21*y + m22*z + m23*w);
        dst.w = (float)(m30*x + m31*y + m32*z + m33*w);
    }

    @Override public String toString() {
        return String.format("[[%f,%f,%f,%f],[%f,%f,%f,%f],[%f,%f,%f,%f],[%f,%f,%f,%f]]",
                m00,m01,m02,m03, m10,m11,m12,m13, m20,m21,m22,m23, m30,m31,m32,m33);
    }
}