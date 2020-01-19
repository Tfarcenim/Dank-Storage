package tfar.dankstorage.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static tfar.dankstorage.client.Client.mc;

public class RenderDankStorage extends ItemStackTileEntityRenderer {

  public DankBakedModel model;
  public static ItemCameraTransforms.TransformType transform;
  public static List<RenderDankStorage> teisrs = new ArrayList<>();
  private DankItemRenderer itemRenderer;
  public RenderDankStorage() {
    teisrs.add(this);
  }

  public RenderDankStorage setModel(DankBakedModel model){
    this.model = model;
    return this;
  }


  @Override
  public void func_228364_a_(ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int p_228364_4_, int p_228364_5_) {
    RenderSystem.pushMatrix();
    if (itemRenderer == null)itemRenderer = new DankItemRenderer(mc.textureManager,mc.getModelManager(),mc.getItemColors(),mc.getItemRenderer().getItemModelMesher());
    model.handlePerspective(transform,matrixStack);
    this.itemRenderer.func_229111_a_(stack,transform,false,matrixStack,iRenderTypeBuffer,p_228364_4_,p_228364_5_, model.internal);
    RenderSystem.popMatrix();
  }
}

