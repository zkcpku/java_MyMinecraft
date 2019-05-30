package org.wtfTeam.game;

import org.wtfTeam.engine.items.GraphItem;
import org.wtfTeam.engine.IHud;
import org.wtfTeam.engine.Window;
import org.wtfTeam.engine.graph.PicTexture;
import org.wtfTeam.engine.items.GameItem;

public class Toolbar implements IHud {
    public static final int itemsPerGroup = 9;
    public static final int groupCount = 3;

    private int selGroup, selPos = 0;

    private GraphItem graphItems[] = new GraphItem[itemsPerGroup + 2];

    private PicTexture iconsTexture;

    private String getTextureId(int idx) {
        return (idx < 10)? String.format("%d", idx): String.format("%c", 'a' + idx - 10);
    }

    Toolbar() {
        try {
            PicTexture selboxTexture = new PicTexture("/textures/selbox.png", "0", 86, 80);
            PicTexture bgTexture = new PicTexture("/textures/toolbarBg.png", "012345678", 80, 80);
            iconsTexture = new PicTexture("/textures/icons.png", "0123456789abcdefghijklmnopq", 54, 54);

            graphItems[0] = new GraphItem("012345678", bgTexture);
            graphItems[itemsPerGroup + 1] = new GraphItem("0", selboxTexture);
        } catch (Exception e) {

        }
    }

    public GameItem[] getGameItems() {
        return graphItems;
    }

    public int getSelPos() {
        return selPos;
    }

    public void setSelPos(int selPos) {
        this.selPos = selPos;
    }

    public int getSelGroup() {
        return selGroup;
    }

    public void setSelGroup(int selGroup) {
        try {
            this.selGroup = selGroup;
            for (int i = 0; i < itemsPerGroup; i++) {
                graphItems[i + 1] = new GraphItem(getTextureId(i + selGroup * itemsPerGroup), iconsTexture);
            }
        } catch (Exception e) {

        }
    }

    public int getSelId() {
        return selGroup * itemsPerGroup + selPos;
    }

    public void updateSize(Window window) {
        graphItems[0].setPosition(window.getWidth() / 2 - 360, window.getHeight() - 100f, 0);
        for (int i = 0; i < itemsPerGroup; i++) {
            graphItems[i + 1].setPosition(window.getWidth() / 2 - 343 + (int) Math.rint(79.2 * i), window.getHeight() - 86f, 0.01f);
        }
        graphItems[itemsPerGroup + 1].setPosition(window.getWidth() / 2 - 359 + (int) Math.rint(79.2 * selPos), window.getHeight() - 101f, 0.02f);
    }
}
