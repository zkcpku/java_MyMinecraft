package org.wtfTeam.engine.items;

import org.wtfTeam.engine.Utils;
import org.wtfTeam.engine.graph.Material;
import org.wtfTeam.engine.graph.Mesh;
import org.wtfTeam.engine.graph.PicTexture;

import java.util.ArrayList;
import java.util.List;

public class GraphItem extends GameItem {

    private static final float ZPOS = 0.0f;

    private static final int VERTICES_PER_QUAD = 4;

    private final PicTexture picTexture;

    private String text;

    public GraphItem(String text, PicTexture picTexture) throws Exception {
        super();
        this.text = text;
        this.picTexture = picTexture;
        setMesh(buildMesh());
    }

    private Mesh buildMesh() {
        List<Float> positions = new ArrayList();
        List<Float> textCoords = new ArrayList();
        float[] normals = new float[0];
        List<Integer> indices = new ArrayList();
        char[] characters = text.toCharArray();
        int numChars = characters.length;

        float startx = 0;
        for (int i = 0; i < numChars; i++) {
            PicTexture.CharInfo charInfo = picTexture.getCharInfo(characters[i]);

            // Build a character tile composed by two triangles

            // Left Top vertex
            positions.add(startx); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float) charInfo.getStartX() / (float) picTexture.getWidth());
            textCoords.add(0.0f);
            indices.add(i * VERTICES_PER_QUAD);

            // Left Bottom vertex
            positions.add(startx); // x
            positions.add((float) picTexture.getHeight()); //y
            positions.add(ZPOS); //z
            textCoords.add((float) charInfo.getStartX() / (float) picTexture.getWidth());
            textCoords.add(1.0f);
            indices.add(i * VERTICES_PER_QUAD + 1);

            // Right Bottom vertex
            positions.add(startx + charInfo.getWidth()); // x
            positions.add((float) picTexture.getHeight()); //y
            positions.add(ZPOS); //z
            textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) picTexture.getWidth());
            textCoords.add(1.0f);
            indices.add(i * VERTICES_PER_QUAD + 2);

            // Right Top vertex
            positions.add(startx + charInfo.getWidth()); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) picTexture.getWidth());
            textCoords.add(0.0f);
            indices.add(i * VERTICES_PER_QUAD + 3);

            // Add indices por left top and bottom right vertices
            indices.add(i * VERTICES_PER_QUAD);
            indices.add(i * VERTICES_PER_QUAD + 2);

            startx += charInfo.getWidth();
        }

        float[] posArr = Utils.listToArray(positions);
        float[] textCoordsArr = Utils.listToArray(textCoords);
        int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
        Mesh mesh = new Mesh(posArr, textCoordsArr, normals, indicesArr);
        mesh.setMaterial(new Material(picTexture.getTexture()));
        return mesh;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.getMesh().deleteBuffers();
        this.setMesh(buildMesh());
    }
}