package org.wtfTeam.engine;

import java.util.*;

import org.joml.Vector3f;
import org.wtfTeam.engine.graph.*;
import org.wtfTeam.engine.items.*;

public class Generator{
	public int Xsize;
	public int Ysize;
	public int[][] Heights;
	public Noise no;
	public List<Block> mapItems;
	public final int biasX;
	public final int biasY;
	public Set<Integer> pretty;
	public String mode;
	//default
	//superflat
	//mountain
	//desert
	//forest
	public Generator(int Xsize, int Ysize, String mode) throws Exception{
		this.Xsize = Xsize;
		this.Ysize = Ysize;
		this.mode = mode;
		biasX = (Xsize - 1)/ 2;
		biasY = (Ysize - 1)/ 2;
		Heights = new int[Xsize][Ysize];
		mapItems = new ArrayList<>();
		pretty = new HashSet<Integer>();
		gen_heights();
		genTerrain();
		if (mode.contentEquals("forest")) genMoreTrees();
		
        if (mode.contentEquals("desert")) genCactus();
        else if (!mode.contentEquals("superflat"))
        		genPlants();
        
	}
	public Generator(int Xsize, int Ysize) throws Exception{
		this(Xsize, Ysize, "default");
	}

	public void gen_heights() {
		if (mode.contentEquals("superflat")) {
			for (int i = 0; i < Xsize; i++)
				for (int j = 0; j < Ysize; j++) {
					Heights[i][j] = 2;
				}
			return;
		}
		int noiselevel = 8;
		double heightK = 0.14;
		int barrier = 16;
		double tb = 7.0;
		if (mode.contentEquals("mountain")) {
			noiselevel = 5;
			heightK = 1.1;
			barrier = 40;
			tb = 15;
		}
		no = new Noise(noiselevel);
		for (int i = 0; i < Xsize; i++)
			for (int j = 0; j < Ysize; j++) {
				int curH = (int)(no.getVal(i, j) * heightK + tb);
				if (curH > barrier) curH = barrier;
				if (curH < 5) curH = 5;
				Heights[i][j] = curH;
			}
	}
	public int getHeight(int x, int y) {
		return Heights[x][y];
	}
	public void wrapItem(int id, int i, int j, int k) {
		//save my time from repeating these operations
		mapItems.add(new Block(i, k, j, id));
	}
	public void wrapItem(String name, int i, int j, int k) {
		//save my time from repeating these operations
		wrapItem(Blocklist.toId(name), i, j, k);
	}
	public static boolean possible(double db) {
		return Math.random() < db;
	}
	public void growTree(String Log, String Leaf, int i, int j) {
		//if (mode.contentEquals("superflat")) return;
		//trunks
		int treeH = Math.random() < 0.3 ? 5 : 6;
		treeH = Math.random() < 0.2 ? 7: treeH;
		int curH = Heights[i + biasX][j + biasY];
		
		for (int h = curH + 1; h <= curH + treeH; h++)
			wrapItem(Log, i, j, h);
		//plant leaves
		int myH = curH+treeH + 1;
		wrapItem(Leaf, i, j, myH);
		
		for (int di = -1; di <= 1; di++)
			for (int dj = -1; dj <= 1; dj++) {
				if (di == 0 && dj == 0) continue;
				if (di != 0 && dj != 0) continue;
				wrapItem(Leaf, i + di, j + dj, myH - 1);
		}
		
		for (int di = -1; di <= 1; di++)
			for (int dj = -1; dj <= 1; dj++) {
				if (di == 0 && dj == 0) continue;
				wrapItem(Leaf, i + di, j + dj, myH - 2);
		}
		
		for (int di = -2; di <= 2; di++)
			for (int dj = -2; dj <= 2; dj++) {
				if (di == 0 && dj == 0) continue;
				wrapItem(Leaf, i + di, j + dj, myH - 3);
			}
		
		//optional leaves
		if (Math.random() < 0.5) {
		for (int di = -1; di <= 1; di++)
			for (int dj = -1; dj <= 1; dj++) {
				if (di == 0 && dj == 0) continue;
				if (di != 0 && dj != 0) continue;
				wrapItem(Leaf, i + di, j + dj, myH - 4);
		}}
		if (Math.random() < 0.5) {
			for (int di = -1; di <= 1; di++)
				for (int dj = -1; dj <= 1; dj++) {
					if (di == 0 || dj == 0) continue;
					//if (di != 0 && dj != 0) continue;
					wrapItem(Leaf, i + di, j + dj, myH - 1);
			}
			for (int di = -2; di <= 2; di++)
				for (int dj = -2; dj <= 2; dj++) {
					if (Math.abs(di) == 2 && Math.abs(dj) == 2 &&
							Math.random() < 0.6)continue;
					if (Math.abs(di) < 2 && Math.abs(dj) < 2) continue;
					wrapItem(Leaf, i + di, j + dj, myH - 2);
			}
		}
	}
	public void genTerrain() throws Exception {
		for (int i = -biasX; i < Xsize - biasX; i++)
			for (int j = -biasY; j < Ysize - biasY; j++) {
				wrapItem("bedrock", i, j, 0);
				if (mode.contentEquals("superflat")) {
					wrapItem("stone", i, j, 1);
	            	wrapItem("grass", i, j, 2);
	            	continue;
				}
                int curH = Heights[i + biasX][j + biasY];
                for (int k = 1; k < curH - 3; k++) {
                	if (Math.random() < 0.90)
                		wrapItem("stone", i, j, k);
                	else wrapItem(whateverore(), i, j, k);
                }
                
                for (int k = curH - 3; k <= curH - 1; k++) {
                	GameItem DTitem;
                	if (mode.contentEquals("desert"))
                		wrapItem("sand", i, j, k);
                	else wrapItem("dirt", i, j, k);
                }
                GameItem GSitem;
            	if (mode.contentEquals("desert"))
            		wrapItem("sand", i, j, curH);
            	else wrapItem("grass", i, j, curH);
            	
			}
	}
	public String whateverflower() {
		if (possible(0.3)) {
			return "rose";
		}
		else if (possible(0.33)) {
			return "tulip";
		}
		else if (possible(0.5)) {
			return "orchid";
		}
		else return "paeonia";
	}
	public String whatevermushroom() {
		if (possible(0.5)) return "mushroom";
		else return "mushbrown";
	}
	public String whateverore() {
		if (possible(0.5)) return "diamond";
		if (possible(0.5)) return "iron";
		return "coal";
	}
	public void genPlants() throws Exception {
		//we do not care if these blocks will crash, because in that
		//case they would not live together in hashset
		//Noise no_grass = new Noise(9);
		Noise no_rose = new Noise(5);
		Noise no_mush = new Noise(5);
		for (int i = -biasX; i < Xsize - biasX; i++)
			for (int j = -biasY; j < Ysize - biasY; j++) {
				int curH = Heights[i + biasX][j + biasY];
				
				if (possible(0.005)) {
					int bi = i + biasX;
					int bj = j + biasY;
					if (bi < 3 || bi > Xsize - 3) continue;
					if (bj < 3 || bj > Ysize - 3) continue;
					
					if (mode.contentEquals("mountain")) {
						growTree("spruce", "sleaf", i, j);
						continue;
					}
					boolean flagH = false;
					for (int sci = -1; sci <= 1; sci++)
						for (int scj = -1; scj <= 1; scj++) {
							if (Heights[sci + bi][scj + bj] > curH)
								flagH = true;
						}
					if (flagH) continue;
					//System.out.printf("perfect spot!\n");
					//so it seems alright to grow a oak tree here
					
					growTree("oak", "leaf", i, j);
					continue;
				}
				if (possible(0.008)) {
                	wrapItem("tallgrass", i, j, curH + 1);
                	continue;
				}
				if (Math.random() < Math.min(0.00004 * (curH + 100) * no_mush.getVal(i + biasX, j + biasY), 0.2)) {
                	wrapItem(whatevermushroom(), i, j, curH + 1);
                	continue;
				}
				if (Math.random() < Math.min(0.00004 * (curH + 100) * no_rose.getVal(i + biasX, j + biasY), 0.2)) {
                	wrapItem(whateverflower(), i, j, curH + 1);
                	continue;
				}
				
		}
	}
	public void genCactus() throws Exception{
		Noise no_deadtree = new Noise(8);
		for (int i = -biasX; i < Xsize - biasX; i++)
			for (int j = -biasY; j < Ysize - biasY; j++) {
				int curH = Heights[i + biasX][j + biasY];
				if (possible(Math.min(0.0003 * no_deadtree.getVal(i + biasX, j + biasY), 0.3))) {
                	wrapItem("deadtree", i, j, curH + 1);
                	continue;
				}
				if (possible(0.005)) {
					int rep = 2;
					if (possible(0.5)) rep = 3;
					for (int myh = 1; myh <= rep; myh++) {
                	wrapItem("cactus", i, j, curH + myh);
                	}
				}
		}
	}
	public void genMoreTrees() throws Exception{
		for (int i = -biasX; i < Xsize - biasX; i++)
			for (int j = -biasY; j < Ysize - biasY; j++) {
				int curH = Heights[i + biasX][j + biasY];
		if (Math.random() < 0.01) {
			int bi = i + biasX;
			int bj = j + biasY;
			if (bi < 3 || bi > Xsize - 3) continue;
			if (bj < 3 || bj > Ysize - 3) continue;
			boolean flagH = false;
			for (int sci = -1; sci <= 1; sci++)
				for (int scj = -1; scj <= 1; scj++) {
					if (Heights[sci + bi][scj + bj] > curH)
						flagH = true;
				}
			if (flagH) continue;
			//System.out.printf("perfect spot!\n");
			//so it seems alright to grow a oak tree here
			growTree("jungle", "jleaf", i, j);
		}}
	}
	public ArrayList<Block> getItems(){
		ArrayList<Block> ret = new ArrayList<>();
		for(Block item : mapItems) {
			int ix = item.getX();
			int iy = item.getY();
			int iz = item.getZ();
			int curp = iy*Xsize*Ysize+ix*Ysize+iz;
			if (!pretty.add(curp)) continue;
			ret.add(item);
		}
		return ret;
	}
}