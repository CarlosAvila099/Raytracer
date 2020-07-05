/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package Raytracing;

import Raytracing.lights.Light;
import Raytracing.lights.PointLight;
import Raytracing.objects.Camera;
import Raytracing.objects.Object3D;
import Raytracing.objects.Sphere;
import Raytracing.tools.OBJReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Carlos Daniel Avila Navarro
 * @author Jafet Rodríguez
 * @version 1.0
 */
public class Raytracer {
    public static final int MAX_DEPTH = 3; //Number of recursive cycles permitted for reflection and refraction.
    public static final int TIME_LIMIT = 20; //Number of minutes the program is allowed to run.
    public static final float SHININESS = 50f; //Bias value of shininess, used in Specular.
    public static final float SPECULAR = 1f; //Bias value of specular.
    public static final float AMBIENT = .1f; //Bias value of ambient.
    public static final float DIFFUSE = .4f; //Bias value of diffuse.
    public static final float REFRACTION = 0.0015f; //Bias value of refraction color
    public static final double MOVEMENT = 0.00001; //Bias value used to move a Ray origin to make sure it doesn't intersects with itself.
    public static final float IOR_NORMAL = 1.0f; //Index Of Refraction of the air.
    public static final float IOR_OBJECT = 1.5f; //Index Of Refraction of the glass.
    public static final int THREADS = 8; //Number of logic processors of the computer.
    public static final float[] INFINITY = new float[]{Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY};


    public static void main(String[] args) {
        System.out.println(new Date());
        Scene scene01 = new Scene();
        scene01.setCamera(new Camera(new Vector3D(0, 0, -8), 160, 160, 1200, 1200, 8.2f, 50f));
        scene01.addLight(new PointLight(new Vector3D(0f, 1.5f, 6f), Color.YELLOW, 10));
        scene01.addObject(OBJReader.GetPolygon("Plane.obj", new Vector3D(0f, -3.5f, 12f), Color.GRAY, true, false));
        scene01.addObject(OBJReader.GetPolygon("Plane.obj", new Vector3D(0f, -3.5f, 31.5f), Color.GRAY, true, false));
        scene01.addObject(OBJReader.GetPolygon("ring.obj", new Vector3D(0f, -2f, 3f), Color.BLUE, true, false));
        scene01.addObject(OBJReader.GetPolygon("Plate.obj", new Vector3D(0f, -2f, 3f), Color.RED, true, false));
        scene01.addObject(new Sphere(new Vector3D(0f, 0f, 3f), 1f, Color.PINK, true, false));
        scene01.addObject(OBJReader.GetPolygon("Wood_Table.obj", new Vector3D(-4.3f, -3.5f, 4f), Color.YELLOW, false, true));
        scene01.addObject(OBJReader.GetPolygon("smallTeapot.obj", new Vector3D(-3f, -2.25f, 4f), Color.GREEN, true, false));
        scene01.addObject(OBJReader.GetPolygon("Wood_Table.obj", new Vector3D(1.5f, -3.5f, 4f), Color.GREEN, false, true));
        scene01.addObject(OBJReader.GetPolygon("smallTeapot.obj", new Vector3D(2.8f, -2.25f, 4f), Color.YELLOW, true, false));

        BufferedImage image = raytrace(scene01);
        File outputImage = new File("image.png");
        try {
            ImageIO.write(image, "png", outputImage);
        } catch (IOException ioe) {
            System.out.println("Something failed");
        }
        System.out.println(new Date());
    }

    /**
     * @param scene is the Scene to be raytraced.
     * @return an image made from the Scene.
     */
    public static BufferedImage raytrace(Scene scene) {
        ExecutorService executorService  = Executors.newFixedThreadPool(THREADS);
        Camera mainCamera = scene.getCamera();
        ArrayList<Light> lights = scene.getLights();
        float[] nearFarPlanes = mainCamera.getNearFarPlanes();
        BufferedImage image = new BufferedImage(mainCamera.getResolutionWidth(), mainCamera.getResolutionHeight(), BufferedImage.TYPE_INT_RGB);
        ArrayList<Object3D> objects = scene.getObjects();
        Vector3D[][] positionsToRaytrace = mainCamera.calculatePositionsToRay();
        for (int i = 0; i < positionsToRaytrace.length; i++) {
            for (int j = 0; j < positionsToRaytrace[i].length; j++) {
                Runnable runnable = color(positionsToRaytrace, mainCamera, objects, nearFarPlanes, image, lights, i, j);
                executorService.execute(runnable);
            }
        }
        executorService.shutdown();
        try {
            if(!executorService.awaitTermination(TIME_LIMIT, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
                System.out.println("The time allowed by the program has ended, thus it has been terminated");
            }
        }
        catch (InterruptedException e) { e.printStackTrace(); }
        finally {
            if(!executorService.isTerminated()) {
                System.err.println("Cancel non-finished");
            }
        }

        return image;
    }

    /**
     * @param value is the value used to see if it is in the certain range.
     * @param min is the smallest value of the range.
     * @param max is the greatest value of the range.
     * @return If the value is in the range, it returns the value, else, it returns the closest limit.
     */
    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * @param original the original Color.
     * @param otherColor the Color to be added to original.
     * @return the resultant Color.
     */
    public static Color addColor(Color original, Color otherColor){
        float red = clamp((original.getRed() / 255.0f) + (otherColor.getRed() / 255.0f), 0, 1);
        float green = clamp((original.getGreen() / 255.0f) + (otherColor.getGreen() / 255.0f), 0, 1);
        float blue = clamp((original.getBlue() / 255.0f) + (otherColor.getBlue() / 255.0f), 0, 1);
        return new Color(red, green, blue);
    }

    /**
     * @param ray is the Ray we are going to check its intersections with all the objects.
     * @param objects all the Object3D of the scene.
     * @param clippingPlanes the near and far planes, these are what limits what objects can be intersected.
     * @return the closest Intersection of the Ray and an Object3D.
     */
    public static Intersection raycast(Ray ray, ArrayList<Object3D> objects, float[] clippingPlanes) {
        Intersection closestIntersection = null;
        for (int k = 0; k < objects.size(); k++) {
            Object3D currentObj = objects.get(k);
            Intersection intersection = currentObj.getIntersection(ray);
            if (intersection != null) {
                double distance = intersection.getDistance();
                if (distance >= 0 &&
                        (closestIntersection == null || distance < closestIntersection.getDistance()) &&
                        (clippingPlanes == null || (intersection.getPosition().getZ() >= clippingPlanes[0] &&
                                intersection.getPosition().getZ() <= clippingPlanes[1]))) {
                    closestIntersection = intersection;
                }
            }
        }
        return closestIntersection;
    }

    /**
     * @param mainCamera is the Camera used in the Scene.
     * @param object the Object3D we are going to get the specular Color.
     * @param light the Light that will affect the specular Color.
     * @param objColors the Colors of the object.
     * @param normal the normal Vector3D of the Intersection between the Light and the Object3D.
     * @return the specular Color of the Object3D.
     * @see <a href="https://www.scratchapixel.com/lessons/3d-basic-rendering/phong-shader-BRDF">Phong-Shader</a>
     */
    public static Color specular(Camera mainCamera, Object3D object, Light light, float[] objColors, Vector3D normal) {
        Vector3D viewer = Vector3D.normalize(Vector3D.substract(mainCamera.getPosition(), object.getPosition()));
        Vector3D lightVector = Vector3D.normalize(Vector3D.substract(light.getPosition(), object.getPosition()));
        Vector3D halfVector = Vector3D.normalize(Vector3D.add(lightVector, viewer));
        float specularValue = (float) Math.pow(Math.max(Vector3D.dotProduct(normal, halfVector), 0.0), SHININESS);
        for(int colorIndex = 0; colorIndex < 3; colorIndex++) {
            objColors[colorIndex] *= specularValue * SPECULAR / DIFFUSE;
        }
        return new Color(clamp(objColors[0],0, 1), clamp(objColors[1], 0, 1), clamp(objColors[2], 0, 1));
    }

    /**
     * @param origin the Vector3D representing the Intersection position.
     * @param light the Vector3D representing the Light position.
     * @param normal the normal Vector3D of the Intersection.
     * @return the reflection Ray of the Intersection.
     * @see <a href="https://www.scratchapixel.com/lessons/3d-basic-rendering/introduction-to-shading/reflection-refraction-fresnel">Reflection, Refraction and Fresnel</a>
     */
    public static Ray reflection(Vector3D origin, Vector3D light, Vector3D normal) {
        Vector3D reflection = Vector3D.scalarMultiplication(normal, (2* Vector3D.dotProduct(normal, light)));
        reflection = Vector3D.substract(light, reflection);
        origin = Vector3D.add(origin, Vector3D.scalarMultiplication(normal, MOVEMENT));
        return new Ray(origin, reflection);
    }

    /**
     * @param origin the Vector3D representing the Intersection position.
     * @param viewer is the Vector3D with the difference between the Intersection and the Camera.
     * @param normal the normal Vector3D of the Intersection.
     * @return the refraction Ray of the Intersection.
     * @see <a href="https://www.scratchapixel.com/lessons/3d-basic-rendering/introduction-to-shading/reflection-refraction-fresnel">Reflection, Refraction and Fresnel</a>
     */
    public static Ray refraction(Vector3D origin, Vector3D viewer, Vector3D normal) {
        double enterConstant = clamp((float) Vector3D.dotProduct(normal, Vector3D.normalize(viewer)), -1, 1);
        float ior1 = IOR_NORMAL;
        float ior2 = IOR_OBJECT;
        if(enterConstant < 0) { enterConstant = -enterConstant; }
        else {
            ior1 = IOR_OBJECT;
            ior2 = IOR_NORMAL;
            normal = Vector3D.scalarMultiplication(normal, -1);
        }
        float ior = ior1 / ior2;
        double outConstant = 1 - ior * ior * (1 - enterConstant * enterConstant);
        if(outConstant < 0) { return null; }
        Vector3D refraction = Vector3D.add( Vector3D.scalarMultiplication(viewer, ior),
                Vector3D.scalarMultiplication(normal, (ior * enterConstant - Math.sqrt(outConstant))) );
        origin = Vector3D.add(origin, Vector3D.scalarMultiplication(refraction, MOVEMENT));
        return new Ray(origin, refraction);
    }

    /**
     * @param intersection the Intersection between an Object3D and a Light.
     * @param mainCamera the Camera used in the Scene.
     * @param light the Light intersected.
     * @param objects all the objects of the Scene.
     * @param reflectionDepth is the number of times the reflection Ray has been calculated.
     * @param refractionDepth is the number of times the refraction Ray has been calculated.
     * @return the Color of an Object3D with Blinn-Phong, shadows, reflection and refraction.
     * @see <a href="https://www.scratchapixel.com/lessons/3d-basic-rendering/introduction-to-shading/ligth-and-shadows">Lights and Shadows</a>
     */
    public static Color calculateColor(Intersection intersection, Camera mainCamera, Light light, ArrayList<Object3D> objects, int reflectionDepth, int refractionDepth) {
        Color pixelColor = Color.BLACK;

        Object3D object = intersection.getObject();
        float nDotL = light.getNDotL(intersection);
        float intensity = (float) light.getIntensity() * nDotL;
        Color lightColor = light.getColor();
        Color objColor = intersection.getObject().getColor();
        float[] lightColors = new float[]{lightColor.getRed() / 255.0f, lightColor.getGreen() / 255.0f, lightColor.getBlue() / 255.0f};
        float[] objColors = new float[]{objColor.getRed() / 255.0f, objColor.getGreen() / 255.0f, objColor.getBlue() / 255.0f};
        float[] ambientColors = new float[]{objColors[0]*AMBIENT, objColors[1]*AMBIENT, objColors[2]*AMBIENT};
        float attenuation = (float) Vector3D.magnitude(Vector3D.substract(light.getPosition(), object.getPosition()));

        for (int colorIndex = 0; colorIndex < objColors.length; colorIndex++) {
            objColors[colorIndex] *= intensity * lightColors[colorIndex] * DIFFUSE / attenuation;
        }
        Color ambient = new Color(clamp(ambientColors[0], 0, 1), clamp(ambientColors[1], 0,  1), clamp(ambientColors[2], 0, 1));
        Color diffuse = new Color(clamp(objColors[0], 0, 1),clamp(objColors[1], 0, 1),clamp(objColors[2], 0, 1));
        Color specular = specular(mainCamera, object, light, objColors, intersection.getNormal());

        //Shadows
        boolean inShadow = false;
        Vector3D shadowVector = Vector3D.add(intersection.getPosition(), Vector3D.scalarMultiplication(intersection.getNormal(), MOVEMENT));
        Ray rayObject = new Ray(shadowVector, light.getPosition());
        if(raycast(rayObject, objects, INFINITY) != null) {
            inShadow = true;
        }
        if(!object.isTransparent()) {
            pixelColor = addColor(pixelColor, ambient);
            if(!inShadow) {
                pixelColor = addColor(pixelColor, diffuse);
                pixelColor = addColor(pixelColor, specular);
            }
        }
        else {
            diffuse = new Color(clamp((diffuse.getRed() * REFRACTION), 0, 1),
                    clamp((diffuse.getGreen() * REFRACTION), 0, 1),
                    clamp((diffuse.getBlue() * REFRACTION), 0, 1));
            pixelColor = addColor(pixelColor, diffuse);
        }

        //Reflection
        Intersection reflectionIntersection = null;
        Ray reflectionRay;
        if(object.isReflective()) {
            reflectionRay = reflection(intersection.getPosition(), light.getPosition(), intersection.getNormal());
            reflectionIntersection = raycast(reflectionRay, objects, INFINITY);
        }
        if(reflectionIntersection != null && reflectionDepth < MAX_DEPTH) {
            reflectionDepth++;
            pixelColor = addColor(pixelColor, calculateColor(reflectionIntersection, mainCamera, light, objects, reflectionDepth, refractionDepth));
        }

        //Refraction
        Intersection refractionIntersection = null;
        Ray refractionRay;
        if(object.isTransparent()) {
            Vector3D viewer = Vector3D.substract(intersection.getPosition(), mainCamera.getPosition());
            refractionRay = refraction(intersection.getPosition(), viewer, intersection.getNormal());
            if(refractionRay != null) {
                refractionIntersection = raycast(refractionRay, objects, INFINITY);
            }
            if(refractionIntersection != null && refractionDepth < MAX_DEPTH) {
                refractionDepth++;
                pixelColor = addColor(pixelColor, calculateColor(refractionIntersection, mainCamera, light, objects, reflectionDepth, refractionDepth));
            }
        }

        return pixelColor;
    }

    /**
     *
     * @param positionsToRaytrace all the Object3D positions in the Scene that are seen by the Camera.
     * @param mainCamera the Camera used in the Scene.
     * @param objects all the objects in the Scene.
     * @param nearFarPlanes the clipping planes of the Scene.
     * @param image the BufferedImage that will save the raytraced image.
     * @param lights all the lights in the Scene.
     * @param i the file of the positionsToRaytrace array.
     * @param j the column of the positionsToRaytrace array.
     * @return a Runnable that will raytrace the objects to create an image.
     */
    public static Runnable color(Vector3D[][] positionsToRaytrace, Camera mainCamera, ArrayList<Object3D> objects,
                                 float[] nearFarPlanes, BufferedImage image, ArrayList<Light> lights, int i, int j) {
        Runnable aRunnable = new Runnable() {
            @Override
            public void run() {
                double x = positionsToRaytrace[i][j].getX() + mainCamera.getPosition().getX();
                double y = positionsToRaytrace[i][j].getY() + mainCamera.getPosition().getY();
                double z = positionsToRaytrace[i][j].getZ() + mainCamera.getPosition().getZ();
                Ray ray = new Ray(mainCamera.getPosition(), new Vector3D(x, y, z));
                float cameraZ = (float) mainCamera.getPosition().getZ();
                Intersection closestIntersection = raycast(ray, objects, new float[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});
                //Background color
                Color pixelColor = Color.BLACK;
                if (closestIntersection != null) {
                    for (Light light : lights) {

                        //Color
                        pixelColor = addColor(pixelColor, calculateColor(closestIntersection, mainCamera, light, objects, 0, 0));
                    }
                }
                setRGB(image, i, j, pixelColor);
            }
        };
        return aRunnable;
    }

    /**
     *
     * @param image the image to be modified
     * @param i the file of the image to be modified.
     * @param j the column of the image to be modified.
     * @param pixelColor the Color of the pixel to be modified.
     */
    public static synchronized void setRGB(BufferedImage image, int i, int j, Color pixelColor) {
        image.setRGB(i, j, pixelColor.getRGB());
    }
}