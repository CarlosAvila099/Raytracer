/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package Raytracing.objects;

import Raytracing.Intersection;
import Raytracing.Ray;
import Raytracing.Vector3D;

import java.awt.*;

/**
 * @author Carlos Daniel Avila Navarro
 * @author Jafet Rodríguez
 */
public class Sphere extends Object3D {

    private float radius;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * @param position the Vector3D representing the position of the Sphere.
     * @param radius is the float value of the Sphere's radius.
     * @param color the Color of the Sphere.
     * @param isReflective a flag to check if the Sphere can reflect Light.
     * @param isTransparent a flag to check if the Sphere is transparent.
     */
    public Sphere(Vector3D position, float radius, Color color, boolean isReflective, boolean isTransparent) {
        super(position, color, isReflective, isTransparent);
        setRadius(radius);
    }

    /**
     * @param ray a Ray we are going to check if the Sphere intersects with.
     * @return the Intersection between the Sphere and a Ray.
     * @see <a href="https://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection">Rendering Simple Shapes</a>
     */
    @Override
    public Intersection getIntersection(Ray ray) {
        double distance = -1;
        Vector3D normal = Vector3D.ZERO();
        Vector3D position = Vector3D.ZERO();

        Vector3D directionSphereRay = Vector3D.substract(ray.getOrigin(), getPosition());
        double firstP = Vector3D.dotProduct(ray.getDirection(), directionSphereRay);
        double secondP = Math.pow(Vector3D.magnitude(directionSphereRay), 2);
        double intersection = Math.pow(firstP, 2) - secondP + Math.pow(getRadius(), 2);

        if(intersection >= 0){
            double sqrtIntersection = Math.sqrt(intersection);
            double part1 = -firstP + sqrtIntersection;
            double part2 = -firstP - sqrtIntersection;

            distance = Math.min(part1, part2);
            position = Vector3D.add(ray.getOrigin(), Vector3D.scalarMultiplication(ray.getDirection(), distance));
            normal = Vector3D.normalize(Vector3D.substract(position, getPosition()));
        } else {
            return null;
        }

        return new Intersection(position, distance, normal, this);
    }
}
