package com.tfar.dankstorage.block;

import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.tile.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class DankStorageBlock extends Block {
  public DankStorageBlock(Properties p_i48440_1_) {
    super(p_i48440_1_);
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public boolean onBlockActivated(BlockState p_220051_1_, World world, BlockPos pos, PlayerEntity player, Hand p_220051_5_, BlockRayTraceResult p_220051_6_) {
    if (!world.isRemote) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity instanceof INamedContainerProvider) {
        NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());

      } else throw new IllegalStateException("Our named container provider is missing!");
    }
    return true;
  }

  @Override
  public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    final TileEntity tile = world.getTileEntity(pos);
    if (tile instanceof AbstractDankStorageTile && !world.isRemote){
      ItemStack dank = new ItemStack(((AbstractDankStorageTile) tile).getDank());
      CompoundNBT nbt = ((AbstractDankStorageTile) tile).itemHandler.serializeNBT();
      dank.setTag(nbt);
      ItemEntity itemEntity = new ItemEntity(world,pos.getX()+ .5,pos.getY() + .5,pos.getZ()+.5,dank);
      world.addEntity(itemEntity);
    }
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof AbstractDankStorageTile && !world.isRemote && entity != null) {
      if (stack.hasTag()){
        ((AbstractDankStorageTile) te).setContents(stack.getTag());
      }
    }
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    int type = Integer.parseInt(state.getBlock().getRegistryName().getPath().substring(5));
    switch (type) {
      case 1:
      default:
        return new DankStorageTile1();
      case 2:
        return new DankStorageTile2();
      case 3:
        return new DankStorageTile3();
      case 4:
        return new DankStorageTile4();
      case 5:
        return new DankStorageTile5();
      case 6:
        return new DankStorageTile6();
      case 7:
        return new DankStorageTile7();
    }
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack p_190948_1_, @Nullable IBlockReader p_190948_2_, List<ITextComponent> p_190948_3_, ITooltipFlag p_190948_4_) {
   // if (p_190948_1_.hasTag())p_190948_3_.add(new StringTextComponent(p_190948_1_.getTag().toString()));
    if (p_190948_1_.getOrCreateTag().getBoolean("pickup"))p_190948_3_.add(
            new TranslationTextComponent("dankstorage.autopickup_enabled"));
  }

  @SubscribeEvent
  public static void pickup(EntityItemPickupEvent e){
    PlayerEntity player = e.getPlayer();
    if (player.world.isRemote)return;
    ItemStack dank = findDank(player);
    if (dank.isEmpty())return;
    if (!dank.getOrCreateTag().getBoolean("pickup"))return;
    ItemStack stack = e.getItem().getItem();
    ItemStack remainder = add(dank,stack);
    if (remainder.isEmpty())e.getItem().getItem().setCount(0);
    e.setCanceled(true);
  }

  public static ItemStack findDank(PlayerEntity player){
    return player.inventory.mainInventory.stream().filter(stack -> stack.getItem() instanceof DankItemBlock).findFirst().orElse(ItemStack.EMPTY);
  }

  public static ItemStack add(ItemStack bag, ItemStack pickup){
    ItemStack remainder = ItemStack.EMPTY;
    PortableDankHandler handler;
    int type = Integer.parseInt(bag.getItem().getRegistryName().getPath().substring(5));
    switch (type){
      case 1:default:{handler = new PortableDankHandler(9,256,bag);
        break;
      }
      case 2:{handler = new PortableDankHandler(18,1024,bag);
        break;
      }
      case 3:{handler = new PortableDankHandler(27,4096,bag);
        break;
      }
      case 4:{handler = new PortableDankHandler(36,16384,bag);
        break;
      }
      case 5:{handler = new PortableDankHandler(45,65536,bag);
        break;
      }
      case 6: {
        handler = new PortableDankHandler(54, 262144, bag);
        break;
      }
      case 7:{handler = new PortableDankHandler(81,Integer.MAX_VALUE,bag);
        break;
      }
    }
    for (int i = 0; i < handler.getSlots(); i++){
      ItemStack remainder1 = handler.insertItem(i,pickup,false);
      if (remainder1.isEmpty()){
        handler.writeItemStack();
        return ItemStack.EMPTY;
      }
      remainder = remainder1;
    }
    handler.writeItemStack();
  return remainder;
  }
}
