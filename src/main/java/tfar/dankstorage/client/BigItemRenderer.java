package tfar.dankstorage.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class BigItemRenderer extends ItemRenderer {

  public static final BigItemRenderer INSTANCE = new BigItemRenderer(Minecraft.getInstance().textureManager,Minecraft.getInstance().getModelManager(),Minecraft.getInstance().getItemColors());

  protected BigItemRenderer(TextureManager textureManagerIn, ModelManager modelManagerIn, ItemColors itemColorsIn) {
    super(textureManagerIn, modelManagerIn, itemColorsIn);
  }

  @Override
  public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
    if (!stack.isEmpty()) {
      ItemStack fakeStack = ItemHandlerHelper.copyStackWithSize(stack,1);
     super.renderItemOverlayIntoGUI(fr, fakeStack, xPosition, yPosition, text);
      MatrixStack matrixstack = new MatrixStack();
      if (stack.getCount() != 1 || text != null) {
        String s = text == null ? getStringFromInt(stack.getCount()) : text;
        matrixstack.translate(0.0D, 0.0D, this.zLevel + 200.0F);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());

        RenderSystem.pushMatrix();
        float scale = .75f;
        RenderSystem.scalef(scale, scale, 1.0F);
        fr.renderString(s, (xPosition + 19 - 2 - (fr.getStringWidth(s)*scale)) /scale,
                (yPosition + 6 + 3 + (1 / (scale * scale) - 1) ) /scale, 16777215,true, matrixstack.getLast().getMatrix(), irendertypebuffer$impl, false, 0, 15728880);
        //true, matrixstack.getLast().getNormal(), irendertypebuffer$impl, false, 0, 15728880);
        irendertypebuffer$impl.finish();
        RenderSystem.popMatrix();
      }
    }
  }

  private static final DecimalFormat decimalFormat = new DecimalFormat("0.#");

  public String getStringFromInt(int number){

    if (number >= 1000000000) return decimalFormat.format(number / 1000000000f) + "b";
    if (number >= 1000000) return decimalFormat.format(number / 1000000f) + "m";
    if (number >= 1000) return decimalFormat.format(number / 1000f) + "k";

    return Float.toString(number).replaceAll("\\.?0*$", "");
  }

}