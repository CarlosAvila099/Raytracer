/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package Raytracing;

/**
 * @author Carlos Daniel Avila Navarro
 * @author Jafet Rodríguez
 */
public interface IIntersectable {

    /**
     * @param ray a Ray we are going to check if the object intersects with.
     * @return the Intersection between the object and a Ray.
     */
    public abstract Intersection getIntersection(Ray ray);
}
