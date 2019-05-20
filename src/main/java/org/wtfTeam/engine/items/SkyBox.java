package org.wtfTeam.engine.items;

import org.wtfTeam.engine.graph.Material;
import org.wtfTeam.engine.graph.Mesh;
import org.wtfTeam.engine.graph.OBJLoader;
import org.wtfTeam.engine.graph.Texture;

public class SkyBox extends GameItem {

    public SkyBox(String objModel, String textureFile) throws Exception {
        super();
        Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
        Texture skyBoxtexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxtexture, 0.0f));
        setMesh(skyBoxMesh);
        setPosition(0, 0, 0);
    }
}
