package org.wtfTeam.engine;

import org.joml.Vector3f;

import java.util.ArrayList;

public class Map {

    private ArrayList<Block> blocks;

    private Vector3f playerPos;

    public Map() { blocks = new ArrayList<>(); }

    public Map(ArrayList<Block> blocks, Vector3f playerPos) {
        this.blocks = blocks;
        this.playerPos = playerPos;
    }

    public Map(ArrayList<Block> list) {
        this.blocks = list;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public Block getBlock(int x, int y, int z) {
        for (Block block : blocks) {
            if (block.getX() == x && block.getY() == y && block.getZ() == z)
                return block;
        }
        return null;
    }

    public Block getBlock(Block block) {
        return getBlock(block.getX(), block.getY(), block.getZ());
    }

    public void addBlock(int x, int y, int z, int id) {
        blocks.add(new Block(x, y, z, id));
    }

    public void removeBlock(int x, int y, int z) {
        blocks.remove(getBlock(x, y, z));
    }

    public void removeBlock(Block block) {
        removeBlock(block.getX(), block.getY(), block.getZ());
    }
}
