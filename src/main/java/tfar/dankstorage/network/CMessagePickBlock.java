package tfar.dankstorage.network;

import tfar.dankstorage.DankItem;
import tfar.dankstorage.inventory.PortableDankHandler;
import tfar.dankstorage.utils.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;


public class CMessagePickBlock {

  public CMessagePickBlock() {
  }

  private static final Logger LOGGER = LogManager.getLogger();

  public void handle(Supplier<NetworkEvent.Context> ctx) {
    PlayerEntity player = ctx.get().getSender();

    if (player == null) return;

    ctx.get().enqueueWork(() -> {
      ItemStack bag = player.getHeldItemMainhand();
      if (bag.getItem() instanceof DankItem) {
        PortableDankHandler handler = Utils.getHandler(bag);
        ItemStack pickblock = onPickBlock(player.pick(20.0D,0,false),player,player.world);
        int slot = -1;
        if (!pickblock.isEmpty())
        for (int i = 0; i < handler.getSlots(); i++) {
          if (pickblock.getItem() == handler.getStackInSlot(i).getItem()){
            slot = i;
            break;
          }
        }
        if (slot != -1)
        Utils.setSelectedSlot(bag,slot);
      }
    });
    ctx.get().setPacketHandled(true);
  }

  public static ItemStack onPickBlock(RayTraceResult target, PlayerEntity player, World world) {
    ItemStack result = ItemStack.EMPTY;

    if (target.getType() == RayTraceResult.Type.BLOCK) {
      BlockPos pos = ((BlockRayTraceResult) target).getPos();
      BlockState state = world.getBlockState(pos);

      if (state.isAir(world, pos)) return ItemStack.EMPTY;
        result = state.getBlock().getPickBlock(state, target, world, pos, player);

      if (result.isEmpty())
        LOGGER.warn("Picking on: [{}] {} gave null item", target.getType(), state.getBlock().getRegistryName());
    }
    return result;
  }
}
