package tfar.dankstorage.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class DankBakedModel implements IBakedModel {

  public final IBakedModel internal;

  public DankBakedModel(IBakedModel internal) {
    this.internal = internal;
  }

  /**
   * @param state
   * @param side
   * @param rand
   * @deprecated Forge: Use {@link IForgeBakedModel#getQuads(BlockState, Direction, Random, IModelData)}
   */
  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
    return internal.getQuads(state,side,rand);
  }

  @Override
  public boolean isAmbientOcclusion() {
    return internal.isAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return internal.isGui3d();
  }

  @Override
  public boolean func_230044_c_() {
    return false;
  }

  @Override
  public boolean isBuiltInRenderer() {
    return true;
  }

  /**
   * @deprecated Forge: Use {@link IForgeBakedModel#getParticleTexture(IModelData)}
   */
  @Override
  public TextureAtlasSprite getParticleTexture() {
    return internal.getParticleTexture();
  }

  @Override
  public ItemOverrideList getOverrides() {
    return internal.getOverrides();
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return internal.getItemCameraTransforms();
  }
}
