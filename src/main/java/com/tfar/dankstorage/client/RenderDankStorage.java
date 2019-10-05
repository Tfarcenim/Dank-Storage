package com.tfar.dankstorage.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.tfar.dankstorage.client.Client.mc;

public class RenderDankStorage extends ItemStackTileEntityRenderer {

  public DankBakedModel model;
  public static ItemCameraTransforms.TransformType transform;
  public static List<RenderDankStorage> teisrs = new ArrayList<>();
  private ItemRenderer itemRenderer;
  public RenderDankStorage() {
    teisrs.add(this);
  }

  public RenderDankStorage setModel(DankBakedModel model){
    this.model = model;
    return this;
  }


  @Override
  public void renderByItem(ItemStack stack) {
    GlStateManager.pushMatrix();
    if (itemRenderer == null)itemRenderer = new DankItemRenderer(mc.textureManager,mc.getModelManager(),mc.getItemColors(),mc.getItemRenderer().getItemModelMesher());
    model.handlePerspective(transform);

    this.itemRenderer.renderItem(stack, model.internal);

    GlStateManager.popMatrix();
  }
}

