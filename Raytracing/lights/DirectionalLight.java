/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package Raytracing.lights;

import Raytracing.Intersection;
import Raytracing.Vector3D;

import java.awt.*;

/**
 * @author Carlos Daniel Avila Navarro
 * @author Jafet Rodríguez
 */
public class DirectionalLight extends Light {
    private Vector3D direction;

    /**
     * @param position the Vector3D representing the position of the Light.
     * @param direction the Vector3D representing the direction the Light is pointing.
     * @param color the Color of the Light.
     * @param intensity the intensity of the Light.
     */
    public DirectionalLight(Vector3D position, Vector3D direction, Color color, double intensity){
        super(position, color, intensity);
        setDirection(Vector3D.normalize(direction));
    }

    public Vector3D getDirection() {
        return direction;
    }

    public void setDirection(Vector3D direction) {
        this.direction = direction;
    }

    /**
     * @param intersection the Intersection with the part of the object the Light hits.
     * @return the value of the dot product.
     */
    @Override
    public float getNDotL(Intersection intersection) {
        return (float)Math.max(Vector3D.dotProduct(intersection.getNormal(), Vector3D.scalarMultiplication(getDirection(), -1.0)), 0.0);
    }
}
