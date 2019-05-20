package org.wtfTeam.engine;

import org.wtfTeam.engine.graph.Material;
import org.wtfTeam.engine.graph.Mesh;
import org.wtfTeam.engine.graph.OBJLoader;
import org.wtfTeam.engine.graph.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Blocklist {
    public static List<Blocklist> Bdic = new ArrayList<>();
    public static boolean initialized = false;
    public static HashMap<String, Integer> Bmap = new HashMap<>();
    public boolean is3D = true;
    public int numero;
    public boolean no_place = false;
    public boolean no_crash = false;
    public boolean transp = false;
    public String name = null;
    //public static final int blocknum = 19;
    public static ArrayList<Mesh> meshes;

    public Blocklist(boolean is3D, boolean nop, boolean noc, boolean transp, String name) {
        this.is3D = is3D;
        this.numero = Bmap.size();
        System.out.println(this.numero);
        this.no_place = nop;
        this.no_crash = noc;
        this.transp = transp;
        this.name = name;
        Bmap.put(name, this.numero);
    }

    public static int switchplace(int num, int dir) {
        num = (num + dir + Bdic.size()) % Bdic.size();
        System.out.println(num);
        if (Bdic.get(num).no_place) {
            return switchplace(num, dir);
        }
        System.out.println("ok!");
        return num;
    }

    public static Mesh getmesh(int num, float reflect) throws Exception {
        if (Bdic.get(num).is3D) {
            Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
            Texture texture = new Texture("/textures/blocks/" + Bdic.get(num).name + "block.png");
            Material material = new Material(texture, reflect);
            mesh.setMaterial(material);
            mesh.setTransp(Bdic.get(num).transp);
            return mesh;
        } else {
            Mesh mesh = OBJLoader.loadMesh("/models/improvedcross.obj");
            Texture texture = new Texture("/textures/blocks/" + Bdic.get(num).name + ".png");
            Material material = new Material(texture, reflect);
            mesh.setMaterial(material);
            mesh.setTransp(Bdic.get(num).transp);
            return mesh;
        }
    }

    public static int toId(String name) {
        return Bmap.get(name);
    }

    public static Mesh getmesh(String name, float reflect) throws Exception {
        return getmesh(Bmap.get(name), reflect);
    }

    public static ArrayList<Mesh> getmeshes() {
        meshes = new ArrayList<>();
        for (Blocklist bl : Blocklist.Bdic) {
            try {
                meshes.add(getmesh(bl.numero, 0.1f));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return meshes;
    }

    public static void initblocks() {
        if (initialized) return;
        initialized = true;
        /*--------------------is3d-no--place--crash--trans-name---*/
        /* these we can hold */
        Bdic.add(new Blocklist(true, false, false, false, "grass"));
        Bdic.add(new Blocklist(true, false, false, false, "stone"));
        Bdic.add(new Blocklist(true, false, false, false, "tnt"));
        Bdic.add(new Blocklist(true, false, false, false, "sand"));
        Bdic.add(new Blocklist(true, false, false, false, "oak"));
        Bdic.add(new Blocklist(true, false, false, false, "plank"));
        Bdic.add(new Blocklist(true, false, false, false, "dirt"));
        Bdic.add(new Blocklist(true, false, false, false, "brick"));
        Bdic.add(new Blocklist(true, false, false, false, "diamondblock"));
        /* miserable blocks */
        Bdic.add(new Blocklist(true, true, true, false, "bedrock"));

        /* ores*/
        Bdic.add(new Blocklist(true, true, false, false, "diamond"));
        Bdic.add(new Blocklist(true, true, false, false, "coal"));
        Bdic.add(new Blocklist(true, true, false, false, "iron"));


        /* tree and leaf */
        Bdic.add(new Blocklist(true, true, false, true, "leaf"));
        Bdic.add(new Blocklist(false, true, false, true, "deadtree"));
        Bdic.add(new Blocklist(true, true, false, false, "spruce"));
        Bdic.add(new Blocklist(true, true, false, true, "sleaf"));
        Bdic.add(new Blocklist(true, true, false, false, "jungle"));
        Bdic.add(new Blocklist(true, true, false, true, "jleaf"));

        /* ??? */
        Bdic.add(new Blocklist(true, false, false, true, "cactus"));
        Bdic.add(new Blocklist(false, true, false, true, "tallgrass"));
        /* flowers */
        Bdic.add(new Blocklist(false, false, false, true, "rose"));
        Bdic.add(new Blocklist(false, true, false, true, "orchid"));
        Bdic.add(new Blocklist(false, true, false, true, "tulip"));
        Bdic.add(new Blocklist(false, true, false, true, "paeonia"));
        Bdic.add(new Blocklist(false, false, false, true, "mushroom"));
        Bdic.add(new Blocklist(false, true, false, true, "mushbrown"));


    }
}