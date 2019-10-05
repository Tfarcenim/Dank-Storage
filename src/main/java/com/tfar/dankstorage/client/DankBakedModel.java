package com.tfar.dankstorage.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
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

  public ItemCameraTransforms.TransformType transform;

  @Override
  public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType type) {
    RenderDankStorage.transform = type;
    //You can use a field on your TileEntityItemStackRenderer to store this TransformType for use in renderByItem, this method is always called before it.
    return Pair.of(this, internal.handlePerspective(type).getRight());
  }
}
