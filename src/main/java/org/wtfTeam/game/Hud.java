package org.wtfTeam.game;

import org.joml.Vector4f;
import org.wtfTeam.engine.items.GameItem;
import org.wtfTeam.engine.IHud;
import org.wtfTeam.engine.items.TextItem;
import org.wtfTeam.engine.Window;
import org.wtfTeam.engine.graph.FontTexture;

import java.awt.*;

public class Hud implements IHud {
    private static final Font FONT = new Font("Arial", Font.PLAIN, 18);

    private static final String CHARSET = "ISO-8859-1";

    private final GameItem[] gameItems;

    private final TextItem statusTextItem1, statusTextItem2;

    public Hud(String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem1 = new TextItem(statusText, fontTexture);
        this.statusTextItem1.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));
        this.statusTextItem2 = new TextItem(statusText, fontTexture);
        this.statusTextItem2.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));
        gameItems = new GameItem[]{statusTextItem1, statusTextItem2};
    }

    public void setStatusText1(String statusText) {
        this.statusTextItem1.setText(statusText);
    }

    public void setStatusText2(String statusText) {
        this.statusTextItem2.setText(statusText);
    }

    @Override
    public GameItem[] getGameItems() {
        return gameItems;
    }
    
    public void updateSize(Window window) {
        this.statusTextItem1.setPosition(10f, 10f, 0);
        this.statusTextItem2.setPosition(10f, 35f, 0);
    }
}
