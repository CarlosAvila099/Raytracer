/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package Raytracing.lights;

import Raytracing.Intersection;
import Raytracing.Ray;
import Raytracing.Vector3D;
import Raytracing.objects.Object3D;

import java.awt.*;

/**
 * @author Carlos Daniel Avila Navarro
 * @author Jafet Rodríguez
 */
public abstract class Light extends Object3D {
    private double intensity;

    /**
     * @param position the Vector3D representing the position of the Light.
     * @param color the Color of the Light.
     * @param intensity the intensity of the Light.
     */
    public Light(Vector3D position, Color color, double intensity){
        super(position, color, false, false);
        setIntensity(intensity);
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    /**
     * @param intersection the Intersection with the part of the object the Light hits.
     * @return the value of the dot product.
     */
    public abstract float getNDotL(Intersection intersection);

    /**
     * @param ray the Ray we are going to check if the Light intersects with.
     * @return the Intersection between the light and the Ray.
     */
    public Intersection getIntersection(Ray ray){
        return new Intersection(Vector3D.ZERO(), -1, Vector3D.ZERO(), null);
    }
}
