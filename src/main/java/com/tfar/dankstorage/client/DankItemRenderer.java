package com.tfar.dankstorage.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.tfar.dankstorage.block.DankItemBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class DankItemRenderer extends ItemRenderer {

  private final ItemModelMesher mesher;
  public DankItemRenderer(TextureManager p_i46552_1_, ModelManager p_i46552_2_, ItemColors p_i46552_3_, ItemModelMesher mesher) {
    super(p_i46552_1_, p_i46552_2_, p_i46552_3_);
    this.mesher = mesher;
  }

  public void renderItem(ItemStack stack, IBakedModel model) {
    if (!stack.isEmpty()) {
      GlStateManager.pushMatrix();
//      GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
 {
        this.renderModel(model, stack);
        if (stack.hasEffect()) {
          final int color = ((DankItemBlock)stack.getItem()).getGlintColor(stack);
          renderEffect(Minecraft.getInstance().textureManager, () -> {
            this.renderModel(model, color);
          }, 8);
        }
      }

      GlStateManager.popMatrix();
    }
  }

  public IBakedModel getModelWithOverrides(ItemStack stack, World worldIn, LivingEntity entityIn) {
    Item item = stack.getItem();
    IBakedModel ibakedmodel;
      ibakedmodel = this.mesher.getItemModel(stack);

    return !item.hasCustomProperties() ? ibakedmodel : this.getModelWithOverrides(ibakedmodel, stack, worldIn, entityIn);
  }

  private IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
    IBakedModel ibakedmodel = model.getOverrides().getModelWithOverrides(model, stack, worldIn, entityIn);
    return ibakedmodel == null ? this.mesher.getModelManager().getMissingModel() : ibakedmodel;
  }

  private void renderModel(IBakedModel model, ItemStack stack) {
    this.renderModel(model, -1, stack);
  }

  private void renderModel(IBakedModel model, int color) {
    this.renderModel(model, color, ItemStack.EMPTY);
  }

  private void renderModel(IBakedModel model, int color, ItemStack stack) {
    if (net.minecraftforge.common.ForgeConfig.CLIENT.allowEmissiveItems.get()) {
      net.minecraftforge.client.ForgeHooksClient.renderLitItem(this, model, color, stack);
      return;
    }
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferbuilder = tessellator.getBuffer();
    bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
    Random random = new Random();
    long i = 42L;

    for(Direction direction : Direction.values()) {
      random.setSeed(42L);
      this.renderQuads(bufferbuilder, model.getQuads(null, direction, random), color, stack);
    }

    random.setSeed(42L);
    this.renderQuads(bufferbuilder, model.getQuads(null, null, random), color, stack);
    tessellator.draw();
  }

  @Override
  public ItemModelMesher getItemModelMesher() {
    return mesher;
  }
}
