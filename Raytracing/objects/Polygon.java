/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package Raytracing.objects;

import Raytracing.Intersection;
import Raytracing.Ray;
import Raytracing.Vector3D;
import Raytracing.tools.Barycentric;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Carlos Daniel Avila Navarro
 * @author Jafet Rodríguez
 */
public class Polygon extends Object3D {

    public List<Triangle> triangles;

    public List<Triangle> getTriangles() {
        return triangles;
    }

    /**
     * @param position the Vector3D representing the position of the Polygon.
     * @param triangles is the Triangle array with all the triangles that conform the Polygon.
     * @param color the Color of the Polygon.
     * @param isReflective a flag to check if the Polygon can reflect Light.
     * @param isTransparent a flag to check if the Polygon is transparent.
     */
    public Polygon(Vector3D position, Triangle[] triangles, Color color, boolean isReflective, boolean isTransparent){
        super(position, color, isReflective, isTransparent);
        setTriangles(triangles);
    }

    public void setTriangles(Triangle[] triangles) {
        Vector3D position = getPosition();
        Set<Vector3D> uniqueVertices = new HashSet<Vector3D>();
        for(Triangle triangle : triangles){
            uniqueVertices.addAll(Arrays.asList(triangle.getVertices()));
        }

        for(Vector3D vertex : uniqueVertices){
            vertex.setX(vertex.getX() + position.getX());
            vertex.setY(vertex.getY() + position.getY());
            vertex.setZ(vertex.getZ() + position.getZ());
        }

        this.triangles = Arrays.asList(triangles);
    }

    /**
     * @param ray is the Ray we are going to check if the Polygon intersects with.
     * @return the Intersection between the Polygon and a Raay.
     */
    @Override
    public Intersection getIntersection(Ray ray) {
        double distance = -1;
        Vector3D normal = Vector3D.ZERO();
        Vector3D position = Vector3D.ZERO();

        for(Triangle triangle : getTriangles()){
            Intersection intersection = triangle.getIntersection(ray);
            double intersectionDistance = intersection.getDistance();
            if(intersection != null && intersectionDistance > 0 && (intersectionDistance < distance ||distance < 0)){
                distance = intersectionDistance;
                position = Vector3D.add(ray.getOrigin(), Vector3D.scalarMultiplication(ray.getDirection(), distance));

                normal = Vector3D.ZERO();
                double[] uVw = Barycentric.CalculateBarycentricCoordinates(position, triangle);
                Vector3D[] normals = triangle.getNormals();
                for(int i = 0; i < uVw.length; i++) {
                    normal = Vector3D.add(normal, Vector3D.scalarMultiplication(normals[i], uVw[i]));
                }
            }
        }

        if(distance == -1){
            return null;
        }

        return new Intersection(position, distance, normal, this);
    }
}
