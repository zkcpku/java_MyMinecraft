package org.wtfTeam.engine;

public class Block {
    int id, x, y, z;

    public Block(int x, int y, int z, int id) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getX() { return x; }

    public int getY() { return y; }

    public int getZ() { return z; }

    public boolean getTransp() {
        return Blocklist.Bdic.get(id).transp;
    }
}