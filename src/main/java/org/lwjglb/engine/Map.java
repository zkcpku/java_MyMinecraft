package org.lwjglb.engine;

public class Map{
	public int Xsize;
	public int Ysize;
	public int[][] Heights;
	public Noise no;
	public Map(int Xsize, int Ysize) {
		this.Xsize = Xsize;
		this.Ysize = Ysize;
		Heights = new int[Xsize][Ysize];
		debug_gen_heights();
		//debug_print_heights();
	}
	public void debug_print_heights() {
		for (int i = 0; i < Xsize; i++) {
			for (int j = 0; j < Ysize; j++) System.out.printf("%.2f ", no.getVal(i, j));
			System.out.printf("\n");
		}
	}
	public void debug_gen_heights() {
		no = new Noise(8);
		for (int i = 0; i < 32; i++)
			for (int j = 0; j < 32; j++) {
				int curH = (int)(no.getVal(i, j) * 0.12 + 2.0);
				if (curH > 12) curH = 12;
				if (curH < 1) curH = 1;
				Heights[i][j] = curH;
			}
	}
	public int getHeight(int x, int y) {
		return Heights[x][y];
	}
}