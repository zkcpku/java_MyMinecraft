package org.wtfTeam.game;

import org.joml.Vector4f;
import org.wtfTeam.engine.GraphItem;
import org.wtfTeam.engine.graph.PicTexture;
import org.wtfTeam.engine.items.GameItem;
import org.wtfTeam.engine.IHud;
import org.wtfTeam.engine.items.TextItem;
import org.wtfTeam.engine.Window;
import org.wtfTeam.engine.graph.FontTexture;

import java.awt.*;

public class Hud implements IHud {
    private static final Font FONT = new Font("Consolas", Font.PLAIN, 18);

    private static final String CHARSET = "ISO-8859-1";

    private final GameItem[] gameItems;

    private final TextItem statusTextItem1, statusTextItem2, statusTextItem3, helpText1, helpText2, helpText3, helpText4, helpText5, helpText6, helpText7, helpText8, helpText9;

    private final GraphItem crosshair;

    public Hud(String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        PicTexture crsTexture = new PicTexture("/textures/crosshair.png", "0", 40, 40);

        this.statusTextItem1 = new TextItem(statusText, fontTexture);
        this.statusTextItem2 = new TextItem(statusText, fontTexture);
        this.statusTextItem3 = new TextItem(statusText, fontTexture);

        this.helpText1 = new TextItem("W/A/S/D - Move", fontTexture);
        this.helpText2 = new TextItem("L Click - Place", fontTexture);
        this.helpText3 = new TextItem("R Click - Destroy", fontTexture);
        this.helpText4 = new TextItem("      F - Toggle flying", fontTexture);
        this.helpText5 = new TextItem("  Space - Jump/Up", fontTexture);
        this.helpText6 = new TextItem("  Shift - Down", fontTexture);
        this.helpText7 = new TextItem("    1~9 - Switch item", fontTexture);
        this.helpText8 = new TextItem("      R - Switch toolbar", fontTexture);
        this.helpText9 = new TextItem("    ESC - Exit", fontTexture);

        this.crosshair = new GraphItem("0", crsTexture);

        gameItems = new GameItem[]{statusTextItem1, statusTextItem2, statusTextItem3, helpText1, helpText2, helpText3, helpText4, helpText5, helpText6, helpText7, helpText8, helpText9, crosshair};
    }

    public void setStatusText1(String statusText) {
        this.statusTextItem1.setText(statusText);
    }

    public void setStatusText2(String statusText) {
        this.statusTextItem2.setText(statusText);
    }

    public void setStatusText3(String statusText) {
        this.statusTextItem3.setText(statusText);
    }

    @Override
    public GameItem[] getGameItems() {
        return gameItems;
    }

    public void updateSize(Window window) {
        this.statusTextItem1.setPosition(10f, 10f, 0);
        this.statusTextItem2.setPosition(10f, 35f, 0);
        this.statusTextItem3.setPosition(10f, 60f, 0);
        this.helpText1.setPosition(window.getWidth() - 250f, 10f, 0);
        this.helpText2.setPosition(window.getWidth() - 250f, 35f, 0);
        this.helpText3.setPosition(window.getWidth() - 250f, 60f, 0);
        this.helpText4.setPosition(window.getWidth() - 250f, 85f, 0);
        this.helpText5.setPosition(window.getWidth() - 250f, 110f, 0);
        this.helpText6.setPosition(window.getWidth() - 250f, 135f, 0);
        this.helpText7.setPosition(window.getWidth() - 250f, 160f, 0);
        this.helpText8.setPosition(window.getWidth() - 250f, 185f, 0);
        this.helpText9.setPosition(window.getWidth() - 250f, 210f, 0);

        this.crosshair.setPosition(window.getWidth() / 2 - 20f, window.getHeight() / 2 - 20f, 0);
    }
}
