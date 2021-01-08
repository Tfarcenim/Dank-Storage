package tfar.dankstorage.client.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.text.StringTextComponent;
import tfar.dankstorage.utils.Mode;

public class TripleToggleButton extends SmallButton {

  public Mode mode;

  public TripleToggleButton(int x, int y, int widthIn, int heightIn, IPressable callback, Mode mode) {
    super(x, y, widthIn, heightIn, new StringTextComponent(""), callback);
    this.mode = mode;
  }

  @Override
  public void tint() {
      RenderSystem.color4f(mode.r(),mode.g(),mode.b(),1);
  }

  public void toggle() {
    mode = mode.cycle();
  }
}