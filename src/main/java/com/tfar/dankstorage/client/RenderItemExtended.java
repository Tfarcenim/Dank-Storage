package com.tfar.dankstorage.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class RenderItemExtended extends ItemRenderer {

  public static final RenderItemExtended INSTANCE = new RenderItemExtended(Minecraft.getInstance().textureManager,Minecraft.getInstance().getModelManager(),Minecraft.getInstance().getItemColors());

  protected RenderItemExtended(TextureManager textureManagerIn, ModelManager modelManagerIn, ItemColors itemColorsIn) {
    super(textureManagerIn, modelManagerIn, itemColorsIn);
  }

  public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition,
                                       @Nullable String text) {
    if (!stack.isEmpty()) {
      MatrixStack matrixstack = new MatrixStack();

      if (stack.getCount() != 1 || text != null) {
        String s = text == null ? getStringFromInt(stack.getCount()) : text;
        matrixstack.func_227861_a_(0.0D, 0.0D, (double)(this.zLevel + 200.0F));
        IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.func_228455_a_(Tessellator.getInstance().getBuffer());

        RenderSystem.pushMatrix();
        float scale = .75f;
        RenderSystem.scalef(scale, scale, 1.0F);
        fr.func_228079_a_(s, (xPosition + 19 - 2 - (fr.getStringWidth(s)*scale)) /scale,
                (yPosition + 6 + 3 + (1 / (scale * scale) - 1) ) /scale, 16777215, true, matrixstack.func_227866_c_().func_227870_a_(), irendertypebuffer$impl, false, 0, 15728880);
        irendertypebuffer$impl.func_228461_a_();
        RenderSystem.popMatrix();
      }


      if (stack.getItem().showDurabilityBar(stack)) {
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        double health = stack.getItem().getDurabilityForDisplay(stack);
        int rgbfordisplay = stack.getItem().getRGBDurabilityForDisplay(stack);
        int i = Math.round(13.0F - (float) health * 13.0F);
        this.draw(vertexbuffer, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
        this.draw(vertexbuffer, xPosition + 2, yPosition + 13, i, 1, rgbfordisplay >> 16 & 255, rgbfordisplay >> 8 & 255, rgbfordisplay & 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
        RenderSystem.enableLighting();
        RenderSystem.enableDepthTest();
      }

      ClientPlayerEntity entityplayersp = Minecraft.getInstance().player;
      float f3 = entityplayersp == null ? 0.0F
              : entityplayersp.getCooldownTracker().getCooldown(stack.getItem(),
              Minecraft.getInstance().getRenderPartialTicks());

      if (f3 > 0.0F) {
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        Tessellator tessellator1 = Tessellator.getInstance();
        BufferBuilder vertexbuffer1 = tessellator1.getBuffer();
        this.draw(vertexbuffer1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16,
                MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
        RenderSystem.enableTexture();
        RenderSystem.enableLighting();
        RenderSystem.enableDepthTest();
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

  private void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue,
                    int alpha) {
    renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    renderer.func_225582_a_(x, y, 0.0D).func_225586_a_(red, green, blue, alpha).endVertex();
    renderer.func_225582_a_(x, y + height, 0.0D).func_225586_a_(red, green, blue, alpha).endVertex();
    renderer.func_225582_a_(x + width, y + height, 0.0D).func_225586_a_(red, green, blue, alpha).endVertex();
    renderer.func_225582_a_(x + width, y, 0.0D).func_225586_a_(red, green, blue, alpha).endVertex();
    Tessellator.getInstance().draw();
  }

}