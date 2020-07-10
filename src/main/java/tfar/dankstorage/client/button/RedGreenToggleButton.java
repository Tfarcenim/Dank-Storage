package tfar.dankstorage.client.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.text.StringTextComponent;

public class RedGreenToggleButton extends SmallButton {

  protected boolean toggled;

  public RedGreenToggleButton(int x, int y, int widthIn, int heightIn, IPressable callback, boolean toggled) {
    super(x, y, widthIn, heightIn,new StringTextComponent(""), callback);
    this.toggled = toggled;
  }

  public void toggle(){
    this.toggled = !this.toggled;
  }

  @Override
  public void tint() {
    if (toggled) RenderSystem.color3f(0,1,0);
    else RenderSystem.color3f(1,0,0);
  }
}