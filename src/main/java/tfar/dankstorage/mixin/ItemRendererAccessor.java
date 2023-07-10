package tfar.dankstorage.mixin;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {

   // @Invoker("fillRect")
  //  void $fillRect(BufferBuilder bufferBuilder, int i, int j, int k, int l, int m, int n, int o, int p);

    @Accessor
    BlockEntityWithoutLevelRenderer getBlockEntityRenderer();

}
