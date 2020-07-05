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
public class PointLight extends Light {
    /**
     * @param position the Vector3D representing the position of the Light.
     * @param color the Color of the Light.
     * @param intensity the intensity of the Light.
     */
    public PointLight(Vector3D position, Color color, double intensity) {
        super(position, color, intensity);
    }

    /**
     * @param intersection the Intersection with the part of the object the Light hits.
     * @return the value of the dot product.
     */
    @Override
    public float getNDotL(Intersection intersection) {
        return (float) Math.max(Vector3D.dotProduct(intersection.getNormal(), Vector3D.normalize(Vector3D.substract(getPosition(), intersection.getPosition()))), 0.0);
    }
}
