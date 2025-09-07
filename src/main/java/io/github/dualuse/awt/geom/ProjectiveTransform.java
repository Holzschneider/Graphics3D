package io.github.dualuse.awt.geom;


import io.github.dualuse.awt.TransformedShape;

import io.github.dualuse.awt.math.Matrix4d;
import io.github.dualuse.awt.math.Matrix4f;
import io.github.dualuse.awt.math.Vector4d;
import io.github.dualuse.awt.math.Vector4f;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;


public class ProjectiveTransform extends AffineTransform {
	Matrix4d wrapped = new Matrix4d();
	
	public ProjectiveTransform(Matrix4d m) {
		wrapped.set(m);
		super.setTransform(m.m00, m.m10, m.m01, m.m11, m.m03, m.m13);
	}
	
	public ProjectiveTransform(Matrix4f m) {
		wrapped.set(m);
		super.setTransform(m.m00, m.m10, m.m01, m.m11, m.m03, m.m13);
	}
	
	
	public int getType() {
		return super.getType();
	}

	public double getDeterminant() {
		return wrapped.determinant();
	}

	public Matrix4d getMatrix() {
		return new Matrix4d(wrapped);
	}
	
	public Matrix4f getMatrix(Matrix4f target) {
		target.set(wrapped);
		return target;
	}

	public Matrix4d getMatrix(Matrix4d target) {
		target.set(wrapped);
		return target;
	}

	public void getMatrix(double[] flatmatrix) {
		int i=0;
		flatmatrix[i++] = wrapped.m00;
		flatmatrix[i++] = wrapped.m10;
		flatmatrix[i++] = wrapped.m20;
		flatmatrix[i++] = wrapped.m30;

		flatmatrix[i++] = wrapped.m01;
		flatmatrix[i++] = wrapped.m11;
		flatmatrix[i++] = wrapped.m21;
		flatmatrix[i++] = wrapped.m31;

		flatmatrix[i++] = wrapped.m02;
		flatmatrix[i++] = wrapped.m12;
		flatmatrix[i++] = wrapped.m22;
		flatmatrix[i++] = wrapped.m32;

		flatmatrix[i++] = wrapped.m03;
		flatmatrix[i++] = wrapped.m13;
		flatmatrix[i++] = wrapped.m23;
		flatmatrix[i++] = wrapped.m33;
	}

	public double getScaleX() { return wrapped.m00; }
	public double getScaleY() { return wrapped.m11; }
	public double getScaleZ() { return wrapped.m22; }

	public double getShearX() { return wrapped.m01; }
	public double getShearY() { return wrapped.m10; }

	public double getShearXY() { return wrapped.m01; }
	public double getShearXZ() { return wrapped.m02; }

	public double getShearYX() { return wrapped.m10; }
	public double getShearYZ() { return wrapped.m12; }

	public double getShearZX() { return wrapped.m20; }
	public double getShearZY() { return wrapped.m21; }
	
	public double getTranslateX() { return wrapped.m03; }
	public double getTranslateY() { return wrapped.m13; };
	public double getTranslateZ() { return wrapped.m23; };

	public double getProjectiveX() { return wrapped.m30; }
	public double getProjectiveY() { return wrapped.m31; };
	public double getProjectiveZ() { return wrapped.m32; };
	public double getProjectiveW() { return wrapped.m33; };

	
	static private Matrix4d createMatrixWithTransform(AffineTransform at) {
		Matrix4d m = new Matrix4d();
		m.setIdentity();
		
		m.m00 = (float)at.getScaleX();
		m.m01 = (float)at.getShearX();
		
		m.m10 = (float)at.getShearY();
		m.m11 = (float)at.getScaleY();
		
		m.m03 = (float)at.getTranslateX();
		m.m13 = (float)at.getTranslateY();
		
		return m;
	}
	
	public void translate(double tx, double ty) {
		wrapped.mul(createMatrixWithTransform(AffineTransform.getTranslateInstance(tx, ty)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void rotate(double theta) {
		wrapped.mul(createMatrixWithTransform(AffineTransform.getRotateInstance(theta)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void rotate(double theta, double anchorx, double anchory) {
		wrapped.mul(createMatrixWithTransform(AffineTransform.getRotateInstance(theta, anchorx, anchory)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void rotate(double vecx, double vecy) {
		wrapped.mul(createMatrixWithTransform(AffineTransform.getRotateInstance(vecx, vecy)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void rotate(double vecx, double vecy, double anchorx, double anchory) {
		wrapped.mul(createMatrixWithTransform(AffineTransform.getRotateInstance(vecx, vecy, anchorx, anchory)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void quadrantRotate(int numquadrants) {
		wrapped.mul(createMatrixWithTransform(AffineTransform.getQuadrantRotateInstance(numquadrants)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void quadrantRotate(int numquadrants, double anchorx, double anchory) {
		wrapped.mul(createMatrixWithTransform(AffineTransform.getQuadrantRotateInstance(numquadrants,anchorx,anchory)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void scale(double sx, double sy) {
		wrapped.mul(createMatrixWithTransform(AffineTransform.getScaleInstance(sx, sy)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void shear(double shx, double shy) {
		wrapped.mul(createMatrixWithTransform(AffineTransform.getShearInstance(shx, shy)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void setToIdentity() {
		wrapped.setIdentity();
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void setToTranslation(double tx, double ty) {
		wrapped.set(createMatrixWithTransform(AffineTransform.getTranslateInstance(tx, ty)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void setToRotation(double theta) {
		wrapped.set(createMatrixWithTransform(AffineTransform.getRotateInstance(theta)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void setToRotation(double theta, double anchorx, double anchory) {
		wrapped.set(createMatrixWithTransform(AffineTransform.getRotateInstance(theta,anchorx, anchory)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void setToRotation(double vecx, double vecy) {
		wrapped.set(createMatrixWithTransform(AffineTransform.getRotateInstance(vecx,vecy)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void setToRotation(double vecx, double vecy, double anchorx, double anchory) {
		wrapped.set(createMatrixWithTransform(AffineTransform.getRotateInstance(vecx,vecy,anchorx,anchory)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void setToQuadrantRotation(int numquadrants) {
		wrapped.set(createMatrixWithTransform(AffineTransform.getQuadrantRotateInstance(numquadrants)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void setToQuadrantRotation(int numquadrants, double anchorx, double anchory) {
		wrapped.set(createMatrixWithTransform(AffineTransform.getQuadrantRotateInstance(numquadrants,anchorx,anchory)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void setToScale(double sx, double sy) {
		wrapped.set(createMatrixWithTransform(AffineTransform.getScaleInstance(sx, sy)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void setToShear(double shx, double shy) {
		wrapped.set(createMatrixWithTransform(AffineTransform.getShearInstance(shx,shy)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void setTransform(ProjectiveTransform Px) {
		wrapped.set(Px.wrapped);
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}
	public void setTransform(AffineTransform Tx) {
		wrapped.set(createMatrixWithTransform(Tx));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void setTransform(double m00, double m10, double m01, double m11, double m03, double m13) {
		wrapped.set(createMatrixWithTransform(new AffineTransform(m00, m10, m01, m11, m03, m13)));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void setTransform(
			double m00, double m10, double m20, double m30,
			double m01, double m11, double m21, double m31,
			double m02, double m12, double m22, double m32,
			double m03, double m13, double m23, double m33) {
		
		wrapped.m00 = m00; wrapped.m10 = m10; wrapped.m20 = m20; wrapped.m30 = m30;
		wrapped.m01 = m01; wrapped.m11 = m11; wrapped.m21 = m21; wrapped.m31 = m31;
		wrapped.m02 = m02; wrapped.m12 = m12; wrapped.m22 = m22; wrapped.m32 = m32;
		wrapped.m03 = m03; wrapped.m13 = m13; wrapped.m23 = m23; wrapped.m33 = m33;
		
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);	
	}
	
	public void concatenate(ProjectiveTransform Tx) {
		wrapped.mul(Tx.wrapped);
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}
	
	public void concatenate(AffineTransform Tx) {
		if (Tx instanceof ProjectiveTransform)
			wrapped.mul(((ProjectiveTransform)Tx).wrapped);
		else
			wrapped.mul(createMatrixWithTransform(Tx));
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public void preConcatenate(AffineTransform Tx) {
		Matrix4d m = createMatrixWithTransform(Tx);
		m.mul(wrapped);
		wrapped.set(m);
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	public ProjectiveTransform createInverse() {
		Matrix4d m = new Matrix4d(wrapped);
		m.invert();
		return new ProjectiveTransform(m);
	}

	public void invert() {
		wrapped.invert();
		super.setTransform(wrapped.m00, wrapped.m10, wrapped.m01, wrapped.m11, wrapped.m03, wrapped.m13);
	}

	
	public Vector4d transform(Vector4d ptSrc, Vector4d ptDst) { wrapped.transform(ptSrc, ptDst); return ptDst; }
	public Vector4f transform(Vector4f ptSrc, Vector4f ptDst) { wrapped.transform(ptSrc, ptDst); return ptDst; }
	public Point2D transform(Point2D ptSrc, Point2D ptDst) { return super.transform(ptSrc, ptDst); }

	private void assertAffineTransform() {
		if (wrapped.m20==0 && wrapped.m30==0 && wrapped.m21==0 && wrapped.m31==0 && wrapped.m22== 1 && wrapped.m32 ==0 && wrapped.m23==0  && wrapped.m33==1 )
			return;
		
		throw new RuntimeException("Non-Affine transform is not applicable on 2D coordinates");
	}
	
	public void transform(Point2D[] ptSrc, int srcOff, Point2D[] ptDst, int dstOff, int numPts) { assertAffineTransform(); super.transform(ptSrc, srcOff, ptDst, dstOff, numPts); }
	public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) { assertAffineTransform(); super.transform(srcPts, srcOff, dstPts, dstOff, numPts); }
	public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) { assertAffineTransform(); super.transform(srcPts, srcOff, dstPts, dstOff, numPts); }
	public void transform(float[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) { assertAffineTransform(); super.transform(srcPts, srcOff, dstPts, dstOff, numPts); }
	public void transform(double[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) { assertAffineTransform(); super.transform(srcPts, srcOff, dstPts, dstOff, numPts); }

	public Point2D deltaTransform(Point2D ptSrc, Point2D ptDst) { assertAffineTransform(); return deltaTransform(ptSrc, ptDst); }
	public void deltaTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) { assertAffineTransform(); super.deltaTransform(srcPts, srcOff, dstPts, dstOff, numPts); }
	public Point2D inverseTransform(Point2D ptSrc, Point2D ptDst) throws NoninvertibleTransformException { assertAffineTransform(); return super.inverseTransform(ptSrc, ptDst); }
	public void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) throws NoninvertibleTransformException { assertAffineTransform(); super.inverseTransform(srcPts, srcOff, dstPts, dstOff, numPts); }
	
	public Shape createTransformedShape(Shape pSrc) { return new TransformedShape(wrapped, pSrc); }

	public String toString() { return wrapped.toString(); }
	public boolean isIdentity() { 
		return 
		wrapped.m00==1 && wrapped.m11==1 && wrapped.m22==1 && wrapped.m33==1 
		&&    
		(wrapped.m01==0 && wrapped.m02==0 && wrapped.m03==0)
		&&    
		(wrapped.m10==0 && wrapped.m12==0 && wrapped.m13==0)
		&&    
		(wrapped.m20==0 && wrapped.m21==0 && wrapped.m23==0)
		&&    
		(wrapped.m30==0 && wrapped.m31==0 && wrapped.m32==0);
	}

	public ProjectiveTransform clone() {
		return new ProjectiveTransform(wrapped);
	}

	public int hashCode() { return wrapped.hashCode(); }

	public boolean equals(Object obj) {
		if (obj==this)
			return true;
		else
		if (obj instanceof ProjectiveTransform)
			return wrapped.equals(((ProjectiveTransform)obj).wrapped);
		else
			return false;
	}

	private static final long serialVersionUID = 1L;

}
