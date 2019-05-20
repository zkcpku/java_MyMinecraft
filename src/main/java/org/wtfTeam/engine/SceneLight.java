package org.wtfTeam.engine;

import org.joml.Vector3f;
import org.wtfTeam.engine.graph.lights.DirectionalLight;
import org.wtfTeam.engine.graph.lights.PointLight;

public class SceneLight {

    private Vector3f ambientLight;
    
    private Vector3f skyBoxLight;

    private PointLight[] pointLightList;
    
    private DirectionalLight directionalLight;

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public PointLight[] getPointLightList() {
        return pointLightList;
    }

    public void setPointLightList(PointLight[] pointLightList) {
        this.pointLightList = pointLightList;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public Vector3f getSkyBoxLight() {
        return skyBoxLight;
    }

    public void setSkyBoxLight(Vector3f skyBoxLight) {
        this.skyBoxLight = skyBoxLight;
    }
    
}