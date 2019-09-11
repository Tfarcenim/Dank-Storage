package com.tfar.dankstorage.network;

import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
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
      if (bag.getItem() instanceof DankItemBlock) {
        PortableDankHandler handler = Utils.getHandler(bag,false);
        ItemStack pickblock = onPickBlock(Minecraft.getInstance().objectMouseOver,player,player.world);
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
    boolean isCreative = player.abilities.isCreativeMode;

    if (target.getType() == RayTraceResult.Type.BLOCK) {
      BlockPos pos = ((BlockRayTraceResult) target).getPos();
      BlockState state = world.getBlockState(pos);

      if (state.isAir(world, pos))
        return ItemStack.EMPTY;

    //  if (isCreative && Screen.hasControlDown() && state.hasTileEntity())

        result = state.getBlock().getPickBlock(state, target, world, pos, player);

      if (result.isEmpty())
        LOGGER.warn("Picking on: [{}] {} gave null item", target.getType(), state.getBlock().getRegistryName());
    }
    return result;
  }
}
