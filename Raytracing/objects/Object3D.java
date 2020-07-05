/**
 * [1968] - [2020] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package Raytracing.objects;

import Raytracing.IIntersectable;
import Raytracing.Vector3D;

import java.awt.*;

/**
 * @author Carlos Daniel Avila Navarro
 * @author Jafet Rodríguez
 */
public abstract class Object3D implements IIntersectable {

    private Vector3D position;
    private Color color;
    private boolean isReflective;
    private boolean isTransparent;

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isReflective() { return isReflective; }

    public void setReflective(boolean reflective) { isReflective = reflective; }

    public boolean isTransparent() { return isTransparent; }

    public void setTransparent(boolean transparent) { isTransparent = transparent; }

    /**
     * @param position the Vector3D representing the position of the Object3D.
     * @param color the Color of the Object3D.
     * @param isReflective a flag to check if the Object3D can reflect Light.
     * @param isTransparent a flag to check if the Object3D is transparent.
     */
    public Object3D(Vector3D position, Color color, boolean isReflective, boolean isTransparent) {
        setPosition(position);
        setColor(color);
        setReflective(isReflective);
        setTransparent(isTransparent);
    }

}
