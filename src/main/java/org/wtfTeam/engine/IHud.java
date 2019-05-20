package org.wtfTeam.engine;

import org.wtfTeam.engine.items.GameItem;

import java.util.List;

public interface IHud {

    GameItem[] getGameItems();

    default void cleanup() {
        GameItem[] gameItems = getGameItems();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
}
