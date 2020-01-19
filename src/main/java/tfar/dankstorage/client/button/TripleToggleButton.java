package tfar.dankstorage.client.button;

import com.mojang.blaze3d.systems.RenderSystem;

public class TripleToggleButton extends SmallButton {

  protected int togglestate;

  public TripleToggleButton(int x, int y, int widthIn, int heightIn, IPressable callback, int togglestate) {
    super(x, y, widthIn, heightIn, "", callback);
    this.togglestate = togglestate;
  }

  @Override
  public void tint() {
    switch (togglestate){
      case 0:RenderSystem.color3f(1,1,1);break;
      case 1:RenderSystem.color3f(1,0,0);break;
      case 2:RenderSystem.color3f(1,1,0);break;
      case 3:RenderSystem.color3f(0,1,0);break;
    }
  }

  public void toggle() {
    togglestate++;
    if (togglestate > 3) togglestate = 0;
  }
}