package org.wtfTeam.engine;

import java.util.*;

/* Perlin noise 
 * Implementation based on javascript code at classic.minecraft.net
 * Create a Noise object with levels to get your noise graph! 
 */

class PrimitiveNoise{
	public List<Integer> Shp;
	public int[] p;
	
	public static double fadeCurve(double d0) {// 5x^5-15x^4+10x^3 ease function
        return d0 * d0 * d0 * (d0 * (d0 * 6.0 - 15.0) + 10.0);
    }

    public static double lerp(double d0, double d1, double d2) {// linear interpolation
        return d1 + d0 * (d2 - d1);
    }

    public static double grad(int i, double d0, double d1, double d2) {
    	/* (quasi-)random gradient (dot vector)
    	 * (1,1,0),(-1,1,0),(1,-1,0),(-1,-1,0), d0 and d1, 
    	 * (1,0,1),(-1,0,1),(1,0,-1),(-1,0,-1), d0 not d1
    	 * (0,1,1),(0,-1,1),(0,1,-1),(0,-1,-1), d1 not d0
    	 * modified original code to comply with [Perlin02]
    	 */
    	i &= 12;
        double d3 = i < 8 ? d0 : d1;
        double d4 = i < 4 ? d1 : d2;
        return ((i & 1) == 0 ? d3 : -d3) + ((i & 2) == 0 ? d4 : -d4);
    }
    public PrimitiveNoise() { // shuffle p
    	Shp = new ArrayList<Integer>();
    	for (int i = 0; i < 256; i++)
    		Shp.add(i);
    	Collections.shuffle(Shp);
    	p = new int[256];
    	for (int i = 0; i < 256; i++) p[i] = Shp.get(i);
    }

	public double getXY(double d0, double d1) {
		
		/* point (d0, d1) is surrounded by 4 integer points.
		 * Assign random gradients and perform linear interpolation on d0 and d1.
		 * changed original code from 3d to 2d
		 */
	        double d2 = 0.0;
	        double d3 = d1;
	        double d4 = d0;
	        int i = ((int)d0) & 255;
	        int j = ((int)d1) & 255;

	        d4 -= ((int)d4);
	        d3 -= ((int)d3);
	        
	        double d5 = fadeCurve(d4);
	        double d6 = fadeCurve(d3);
	        //鬼畜shuffle
	        int l = (p[i] + j) & 255;
	        int i1 = p[l];

	        l = p[(l + 1) & 255];
	        i = (p[(i + 1) & 255] + j) & 255;
	        j = p[i];
	        i = p[(i + 1) & 255];
	        return lerp(d6, 
	        				lerp(d5, 
	        						grad(p[i1], d4, d3, d2), 
	        						grad(p[j], d4 - 1.0, d3, d2)), 
	        				lerp(d5, grad(p[l], d4, d3 - 1.0, d2), 
	        						grad(p[i], d4 - 1.0, d3 - 1.0, d2)));
	    }
}

public class Noise{
	public int level;
	public PrimitiveNoise[] pn;
	public Noise(int level){
		this.level = level;
		pn = new PrimitiveNoise[level];
		for (int i = 0; i < level; i++) pn[i] = new PrimitiveNoise();
	}
	public double getVal(double x, double y) {
		double value = 0;
		double pow = 1;
		for (int i = 0; i < level; i++) {
			value += pn[i].getXY(x * pow, y * pow) / pow;
			pow /= 2;
		}
		return value;
	}
}
