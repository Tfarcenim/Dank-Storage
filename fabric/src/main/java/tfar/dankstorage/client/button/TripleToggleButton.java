package tfar.dankstorage.client.button;

import net.minecraft.network.chat.Component;
import tfar.dankstorage.client.screens.DankStorageScreen;

public class TripleToggleButton extends SmallButton {

    protected DankStorageScreen screen;

    public TripleToggleButton(int x, int y, int widthIn, int heightIn, Component component, OnPress callback, DankStorageScreen screen) {
        super(x, y, widthIn, heightIn,component, callback);
        this.screen = screen;
    }
}