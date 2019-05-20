package org.lwjglb.engine.graph;

import org.joml.Vector3f;

public class Camera {

    private final Vector3f position;
    
    private final Vector3f rotation;

    private final Vector3f front = new Vector3f(0,0,1);

    public Camera() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
    }
    
    public Camera(Vector3f position, Vector3f rotation) {//构造函数
        this.position = position;
        this.rotation = rotation;
    }
    public Camera(Vector3f position) {//构造函数
        this.position = position;
        rotation = new Vector3f(0, 0, 0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setFront(float x, float y, float z){
        front.x = x;
        front.y = y;
        front.z = z;
    }

    public void setFrontFromRotation(){
        front.x = (float)Math.cos(Math.toRadians(rotation.y - 90)) * (float)Math.cos(Math.toRadians(-rotation.x));
        front.y = (float)Math.sin(Math.toRadians(-rotation.x));
        front.z = (float)Math.sin(Math.toRadians(rotation.y - 90)) * (float)Math.cos(Math.toRadians(-rotation.x));
//        System.out.printf("%f\n",rotation.x);//向上是负的，向下是正的
//        System.out.printf("%f\n",rotation.y);//右旋是正的，左旋是负的
    }

    public Vector3f getFront() {
        return front;
    }

    public Vector3f tempMovePosition(float offsetX, float offsetY, float offsetZ) {
        Vector3f tmpPosition = new Vector3f(position);
        if ( offsetZ != 0 ) {
            tmpPosition.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            tmpPosition.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if ( offsetX != 0) {
            tmpPosition.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            tmpPosition.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        tmpPosition.y += offsetY;
        return tmpPosition;
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if ( offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;//y是上下，不受旋转影响
    }

    public Vector3f getRotation() {
        return rotation;
    }
    
    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;

        setFrontFromRotation();
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;

        setFrontFromRotation();

    }
//    public Vector3f getEyePosition(float offsetZ) {
//        Vector3f eyePositon = new Vector3f(position);
//        if ( offsetZ != 0 ) {
//            eyePositon.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
//            eyePositon.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
//        }
//        return eyePositon;
//    }
}