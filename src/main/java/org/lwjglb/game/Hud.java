package org.lwjglb.game;

import org.joml.Vector4f;
import org.lwjglb.engine.*;
import org.lwjglb.engine.Window;
import org.lwjglb.engine.graph.FontTexture;
import org.lwjglb.engine.GraphItem;
import org.lwjglb.engine.graph.PicTexture;

import java.awt.*;

public class Hud implements IHud {
    private static final Font FONT = new Font("Arial", Font.PLAIN, 18);

    private static final String CHARSET = "ISO-8859-1";

    private final GameItem[] gameItems;

    private final TextItem statusTextItem1, statusTextItem2;

    private final GraphItem testGraphItem;


    public Hud(String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        PicTexture picTexture = new PicTexture("E:\\programming\\java\\item_rst.png","012345678",80,80);
        this.statusTextItem1 = new TextItem(statusText, fontTexture);
        this.statusTextItem1.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));
        this.statusTextItem2 = new TextItem(statusText, fontTexture);
        this.statusTextItem2.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));

        this.testGraphItem = new GraphItem("012345678",picTexture);
        this.testGraphItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1,1,1,1));
        gameItems = new GameItem[]{statusTextItem1, statusTextItem2,testGraphItem};
    }

    public void setStatusText1(String statusText) {
        this.statusTextItem1.setText(statusText);
    }

    public void setStatusText2(String statusText) {
        this.statusTextItem2.setText(statusText);
    }

    public void setTestGraphItem(String statusText) { this.testGraphItem.setText(statusText);}

    @Override
    public GameItem[] getGameItems() {
        return gameItems;
    }
    public String getTestGraphItem() { return this.testGraphItem.getText();}
    
    public void updateSize(Window window) {
        this.statusTextItem1.setPosition(10f, 10f, 0);
        this.statusTextItem2.setPosition(10f, 35f, 0);
        this.testGraphItem.setPosition(600f,800f,0);
    }
}
