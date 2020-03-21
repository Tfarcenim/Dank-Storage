package tfar.dankstorage.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.Objects;
import java.util.Random;

public class DankItemRenderer extends ItemRenderer {

  private final ItemModelMesher mesher;
  public DankItemRenderer(TextureManager p_i46552_1_, ModelManager p_i46552_2_, ItemColors p_i46552_3_, ItemModelMesher mesher) {
    super(p_i46552_1_, p_i46552_2_, p_i46552_3_);
    this.mesher = mesher;
  }

  public void func_229111_a_(ItemStack stack, ItemCameraTransforms.TransformType p_229111_2_, boolean p_229111_3_, MatrixStack p_229111_4_, IRenderTypeBuffer p_229111_5_, int p_229111_6_, int p_229111_7_, IBakedModel model) {
    if (!stack.isEmpty()) {
      p_229111_4_.func_227860_a_();
      boolean flag = p_229111_2_ == ItemCameraTransforms.TransformType.GUI;
      boolean flag1 = flag || p_229111_2_ == ItemCameraTransforms.TransformType.GROUND || p_229111_2_ == ItemCameraTransforms.TransformType.FIXED;
      if (stack.getItem() == Items.TRIDENT && flag1) {
        model = this.getItemModelMesher().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
      }

      ForgeHooksClient.handleCameraTransforms(p_229111_4_, model, p_229111_2_, p_229111_3_);
      p_229111_4_.func_227861_a_(-0.5D, -0.5D, -0.5D);
      if (!model.isBuiltInRenderer() && (stack.getItem() != Items.TRIDENT || flag1)) {
        RenderType rendertype = RenderTypeLookup.func_228389_a_(stack);
        RenderType rendertype1;
        if (flag && Objects.equals(rendertype, Atlases.func_228784_i_())) {
          rendertype1 = Atlases.func_228785_j_();
        } else {
          rendertype1 = rendertype;
        }

        IVertexBuilder ivertexbuilder = func_229113_a_(p_229111_5_, rendertype1, true, stack.hasEffect());
        this.func_229114_a_(model, stack, p_229111_6_, p_229111_7_, p_229111_4_, ivertexbuilder);
      } else {
        stack.getItem().getItemStackTileEntityRenderer().func_228364_a_(stack, p_229111_4_, p_229111_5_, p_229111_6_, p_229111_7_);
      }

      p_229111_4_.func_227865_b_();
    }
  }

  private void func_229114_a_(IBakedModel p_229114_1_, ItemStack p_229114_2_, int p_229114_3_, int p_229114_4_, MatrixStack p_229114_5_, IVertexBuilder p_229114_6_) {
    Random random = new Random();
    long i = 42L;

    for(Direction direction : Direction.values()) {
      random.setSeed(42L);
      this.func_229112_a_(p_229114_5_, p_229114_6_, p_229114_1_.getQuads(null, direction, random), p_229114_2_, p_229114_3_, p_229114_4_);
    }

    random.setSeed(42L);
    this.func_229112_a_(p_229114_5_, p_229114_6_, p_229114_1_.getQuads(null, null, random), p_229114_2_, p_229114_3_, p_229114_4_);
  }

  @Override
  public ItemModelMesher getItemModelMesher() {
    return mesher;
  }
}
