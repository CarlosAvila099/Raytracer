/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package Raytracing;

/**
 * @author Carlos Daniel Avila Navarro
 * @author Jafet Rodríguez
 */
public class Ray {

    private Vector3D origin;
    private Vector3D direction;

    /**
     * @param origin is the Vector3D representing the starting position of the Ray.
     * @param direction is the Vector3D representing the direction the Ray is traveling to.
     */
    public Ray(Vector3D origin, Vector3D direction) {
        setOrigin(origin);
        setDirection(direction);
    }

    public Vector3D getOrigin() {
        return origin;
    }

    public void setOrigin(Vector3D origin) {
        this.origin = origin;
    }

    public Vector3D getDirection() {
        return Vector3D.normalize(direction);
    }

    public void setDirection(Vector3D direction) {
        this.direction = direction;
    }
}
