package com.tfar.dankstorage.client.button;

import com.mojang.blaze3d.platform.GlStateManager;

public class TripleToggleButton extends SmallButton {

  protected int togglestate;

  public TripleToggleButton(int x, int y, int widthIn, int heightIn, IPressable callback, int togglestate) {
    super(x, y, widthIn, heightIn, "", callback);
    this.togglestate = togglestate;
  }

  @Override
  public void tint() {
    switch (togglestate){
      case 0:GlStateManager.color3f(1,1,1);
      case 1:GlStateManager.color3f(1,0,0);
      case 2:GlStateManager.color3f(1,1,0);
      case 3:GlStateManager.color3f(0,1,0);

    }
  }

  public void toggle() {
    togglestate++;
    if (togglestate > 3) togglestate = 0;
  }
}