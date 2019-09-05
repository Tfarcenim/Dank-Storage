package com.tfar.dankstorage.network;

import com.tfar.dankstorage.DankStorage;
import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CMessagePickBlock implements IMessage {

  public CMessagePickBlock() {
  }


  /**
   * Convert from the supplied buffer into your specific message type
   *
   * @param buf
   */
  @Override
  public void fromBytes(ByteBuf buf) {
  }

  /**
   * Deconstruct your message into the supplied byte buffer
   *
   * @param buf
   */
  @Override
  public void toBytes(ByteBuf buf) {
  }

  public static class Handler implements IMessageHandler<CMessagePickBlock, IMessage> {
    @Override
    public IMessage onMessage(CMessagePickBlock message, MessageContext ctx) {
      FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(ctx));
      return null;
    }

    private void handle(MessageContext ctx) {

      EntityPlayer player = ctx.getServerHandler().player;
      ItemStack bag = player.getHeldItemMainhand();
      if (bag.getItem() instanceof DankItemBlock) {
        PortableDankHandler handler = Utils.getHandler(bag);
        ItemStack pickblock = onPickBlock(Minecraft.getMinecraft().objectMouseOver, player, player.world);
        int slot = -1;
        if (!pickblock.isEmpty())
          for (int i = 0; i < handler.getSlots(); i++) {
            if (pickblock.getItem() == handler.getStackInSlot(i).getItem()) {
              slot = i;
              break;
            }
          }
        if (slot != -1)
          Utils.setSelectedSlot(bag, slot);
      }
    }


    public static ItemStack onPickBlock(RayTraceResult target, EntityPlayer player, World world) {
      ItemStack result = ItemStack.EMPTY;
      //boolean isCreative = player.capabilities.isCreativeMode;

      if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
        BlockPos pos = target.getBlockPos();
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock().isAir(state, world, pos))
          return ItemStack.EMPTY;

        //  if (isCreative && Screen.hasControlDown() && state.hasTileEntity())

        result = state.getBlock().getPickBlock(state, target, world, pos, player);

        if (result.isEmpty())
          DankStorage.LOGGER.warn("Picking on: [{}] {} gave null item", target.hitInfo, state.getBlock().getRegistryName());
      }
      return result;
    }
  }
}
