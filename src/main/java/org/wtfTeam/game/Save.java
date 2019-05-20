package org.wtfTeam.game;

import java.io.*;
import org.wtfTeam.game.*;
import org.joml.Vector3f;
import org.wtfTeam.engine.*;
import org.wtfTeam.engine.graph.*;

public class Save {
	public static void SaveMap(Camera camera, Map mp) {
		File wfile1, wfile2;
		BufferedWriter writer1 = null, writer2 = null;
		wfile1 = new File("record\\camera_record.log");
		wfile2 = new File("record\\map_record.log");
		Vector3f front = new Vector3f(0, 0, 0);
    	front = camera.getPosition();
    	try {
        	writer1 = new BufferedWriter(new FileWriter(wfile1));
        	writer2 = new BufferedWriter(new FileWriter(wfile2));
    		writer1.write(front.x+" "+front.y+" "+front.z+"\n");
            for (Block block : mp.getBlocks())
        		writer2.write(block.getX()+" "+block.getY()+" "+block.getZ()+ " " + block.getId()+"\n");
    		writer1.close();
    		writer2.close();
    	} catch (IOException e) {
            e.printStackTrace();
        }
	}
}
