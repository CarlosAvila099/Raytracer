/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package Raytracing;

/**
 * @author Carlos Daniel Avila Navarro
 * @author Jafet Rodríguez
 */
public class Vector3D {

    private static final Vector3D ZERO = new Vector3D(0.0, 0.0, 0.0);
    private double x, y, z;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    /**
     * @param x is the value of the horizontal positions of Vector3D.
     * @param y is the value of the vertical positions of Vector3D.
     * @param z is the value of the depth positions of Vector3D.
     */
    public Vector3D(double x, double y, double z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    /**
     * @param vectorA first Vector3D
     * @param vectorB second Vector3D
     * @return the dot product between the two vectors.
     */
    public static double dotProduct(Vector3D vectorA, Vector3D vectorB){
        return (vectorA.getX() * vectorB.getX()) + (vectorA.getY() * vectorB.getY()) + (vectorA.getZ() * vectorB.getZ());
    }

    /**
     * @param vectorA first Vector3D
     * @param vectorB second Vector3D
     * @return the cross product between the two vectors.
     */
    public static Vector3D crossProduct(Vector3D vectorA, Vector3D vectorB){
        return new Vector3D((vectorA.getY() * vectorB.getZ()) - (vectorA.getZ() * vectorB.getY()),
                (vectorA.getZ() * vectorB.getX()) - (vectorA.getX() * vectorB.getZ()),
                (vectorA.getX() * vectorB.getY()) - (vectorA.getY() * vectorB.getX()));
    }

    /**
     * @param vectorA the Vector3D we are going to get the magnitude from.
     * @return the magnitude of the Vector3D
     */
    public static double magnitude(Vector3D vectorA){
        return Math.sqrt(dotProduct(vectorA, vectorA));
    }

    /**
     * @param vectorA first Vector3D
     * @param vectorB second Vector3D
     * @return a Vector3D of the addition of two vectors.
     */
    public static Vector3D add(Vector3D vectorA, Vector3D vectorB){
        return new Vector3D(vectorA.getX() + vectorB.getX(), vectorA.getY() + vectorB.getY(), vectorA.getZ() + vectorB.getZ());
    }

    /**
     * @param vectorA original Vector3D
     * @param vectorB vector to subtract.
     * @return a Vector3D of the subtraction of two vectors.
     */
    public static Vector3D substract(Vector3D vectorA, Vector3D vectorB){
        return new Vector3D(vectorA.getX() - vectorB.getX(), vectorA.getY() - vectorB.getY(), vectorA.getZ() - vectorB.getZ());
    }

    /**
     * @param vectorA Vector3D to normalize.
     * @return a normalized Vector3D.
     */
    public static Vector3D normalize(Vector3D vectorA){
        double mag = Vector3D.magnitude(vectorA);
        return new Vector3D(vectorA.getX() / mag, vectorA.getY() / mag, vectorA.getZ() / mag);
    }

    /**
     * @param vectorA Vector3D to multiply.
     * @param scalar is the double value the Vector3D will be multiplied.
     * @return a multiplied Vector3D.
     */
    public static Vector3D scalarMultiplication(Vector3D vectorA, double scalar){
        return new Vector3D(vectorA.getX() * scalar, vectorA.getY() * scalar, vectorA.getZ() * scalar);
    }

    /**
     * @return a String with the x, y and z values of the Vector3D.
     */
    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ", " + getZ() + ")";
    }

    /**
     * @return a new Vector3D with the x, y, and z values of the original.
     */
    public Vector3D clone(){
        return new Vector3D(getX(), getY(), getZ());
    }

    /**
     * @return a clone of the Vector3D.ZERO
     */
    public static Vector3D ZERO(){
        return ZERO.clone();
    }
}
