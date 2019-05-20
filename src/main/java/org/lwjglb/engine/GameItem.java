package org.lwjglb.engine;

import org.joml.Vector3f;
import org.lwjglb.engine.graph.Mesh;

public class GameItem {

    private Mesh mesh;
    
    private final Vector3f position;
    
    private float scale;

    private final Vector3f rotation;


    public boolean equals(Object obj) {
        if (obj == null)    return false;
        if (obj instanceof  GameItem) {
            GameItem c = (GameItem)obj;
//            System.out.printf("%d %d|%d %d|%d %d\n",(int)c.position.x , (int)position.x, (int)c.position.y , (int)position.y, (int)c.position.z, (int)position.z);
            return ((int)c.position.x == (int)position.x && (int)c.position.y == (int)position.y && (int)c.position.z == (int)position.z);
        }
        return false;


    }
    public GameItem()
    {
        this.mesh = null;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    public GameItem(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }
    
    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}