package com.tfar.dankstorage;

import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.block.DankStorageBlock;
import com.tfar.dankstorage.container.*;
import com.tfar.dankstorage.network.DankPacketHandler;
import com.tfar.dankstorage.screen.*;
import com.tfar.dankstorage.tile.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DankStorage.MODID)
public class DankStorage {
  // Directly reference a log4j logger.

  public static final String MODID = "dankstorage";

  private static final Logger LOGGER = LogManager.getLogger();

  public DankStorage() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
  }

  private void setup(final FMLCommonSetupEvent event) {
    DankPacketHandler.registerMessages(MODID);
  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {
    private static final Set<Block> MOD_BLOCKS = new HashSet<>();

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
      // register a new block here
      Block.Properties properties = Block.Properties.create(Material.IRON);
      for (int i = 1; i < 8; i++) {
        register(new DankStorageBlock(properties), "dank_" + i, event.getRegistry());
      }
    }

    @SubscribeEvent
    public static void item(final RegistryEvent.Register<Item> event) {
      Item.Properties properties = new Item.Properties().group(ItemGroup.DECORATIONS).maxStackSize(1);
      for (Block block : MOD_BLOCKS)
        register(new DankItemBlock(block, properties), block.getRegistryName().getPath(), event.getRegistry());
    }

    @SubscribeEvent
    public static void container(final RegistryEvent.Register<ContainerType<?>> event) {
      register(IForgeContainerType.create((windowId, inv, data)
              -> {
        BlockPos pos = data.readBlockPos();
        return new DankContainer1(windowId, inv.player.world, pos, inv, inv.player);
      }), "dank_1_container", event.getRegistry());
      register(IForgeContainerType.create((windowId, inv, data)
              -> new PortableDankContainer1(windowId, inv.player.world, inv, inv.player)), "portable_dank_1_container", event.getRegistry());

      register(IForgeContainerType.create((windowId, inv, data)
              -> {
        BlockPos pos = data.readBlockPos();
        return new DankContainer2(windowId, inv.player.world, pos, inv, inv.player);
      }), "dank_2_container", event.getRegistry());
      register(IForgeContainerType.create((windowId, inv, data)
              -> new PortableDankContainer2(windowId, inv.player.world, inv, inv.player)), "portable_dank_2_container", event.getRegistry());

      register(IForgeContainerType.create((windowId, inv, data)
              -> {
        BlockPos pos = data.readBlockPos();
        return new DankContainer3(windowId, inv.player.world, pos, inv, inv.player);
      }), "dank_3_container", event.getRegistry());
      register(IForgeContainerType.create((windowId, inv, data)
              -> new PortableDankContainer3(windowId, inv.player.world, inv, inv.player)), "portable_dank_3_container", event.getRegistry());

      register(IForgeContainerType.create((windowId, inv, data)
              -> {
        BlockPos pos = data.readBlockPos();
        return new DankContainer4(windowId, inv.player.world, pos, inv, inv.player);
      }), "dank_4_container", event.getRegistry());
      register(IForgeContainerType.create((windowId, inv, data)
              -> new PortableDankContainer4(windowId, inv.player.world, inv, inv.player)), "portable_dank_4_container", event.getRegistry());

      register(IForgeContainerType.create((windowId, inv, data)
              -> {
        BlockPos pos = data.readBlockPos();
        return new DankContainer5(windowId, inv.player.world, pos, inv, inv.player);
      }), "dank_5_container", event.getRegistry());
      register(IForgeContainerType.create((windowId, inv, data)
              -> new PortableDankContainer5(windowId, inv.player.world, inv, inv.player)), "portable_dank_5_container", event.getRegistry());

      register(IForgeContainerType.create((windowId, inv, data)
              -> {
        BlockPos pos = data.readBlockPos();
        return new DankContainer6(windowId, inv.player.world, pos, inv, inv.player);
      }), "dank_6_container", event.getRegistry());
      register(IForgeContainerType.create((windowId, inv, data)
              -> new PortableDankContainer6(windowId, inv.player.world, inv, inv.player)), "portable_dank_6_container", event.getRegistry());

      register(IForgeContainerType.create((windowId, inv, data)
              -> {
        BlockPos pos = data.readBlockPos();
        return new DankContainer7(windowId, inv.player.world, pos, inv, inv.player);
      }), "dank_7_container", event.getRegistry());
      register(IForgeContainerType.create((windowId, inv, data)
              -> new PortableDankContainer7(windowId, inv.player.world, inv, inv.player)), "portable_dank_7_container", event.getRegistry());
    }



    @SubscribeEvent
    public static void tile(final RegistryEvent.Register<TileEntityType<?>> event) {
      register(TileEntityType.Builder.create(DankStorageTile1::new, Objects.dank_1).build(null), "dank_1_tile", event.getRegistry());
      register(TileEntityType.Builder.create(DankStorageTile2::new, Objects.dank_2).build(null), "dank_2_tile", event.getRegistry());
      register(TileEntityType.Builder.create(DankStorageTile3::new, Objects.dank_3).build(null), "dank_3_tile", event.getRegistry());
      register(TileEntityType.Builder.create(DankStorageTile4::new, Objects.dank_4).build(null), "dank_4_tile", event.getRegistry());
      register(TileEntityType.Builder.create(DankStorageTile5::new, Objects.dank_5).build(null), "dank_5_tile", event.getRegistry());
      register(TileEntityType.Builder.create(DankStorageTile6::new, Objects.dank_6).build(null), "dank_6_tile", event.getRegistry());
      register(TileEntityType.Builder.create(DankStorageTile7::new, Objects.dank_7).build(null), "dank_7_tile", event.getRegistry());

    }

    private static <T extends IForgeRegistryEntry<T>> void register(T obj, String name, IForgeRegistry<T> registry) {
      registry.register(obj.setRegistryName(new ResourceLocation(MODID, name)));
      if (obj instanceof Block) MOD_BLOCKS.add((Block) obj);
    }
  }

  @ObjectHolder(MODID)
  public static class Objects {

    public static final Block dank_1 = null;
    public static final Block dank_2 = null;
    public static final Block dank_3 = null;
    public static final Block dank_4 = null;
    public static final Block dank_5 = null;
    public static final Block dank_6 = null;
    public static final Block dank_7 = null;

    public static final ContainerType<DankContainer1> dank_1_container = null;
    public static final ContainerType<DankContainer2> dank_2_container = null;
    public static final ContainerType<DankContainer3> dank_3_container = null;
    public static final ContainerType<DankContainer4> dank_4_container = null;
    public static final ContainerType<DankContainer5> dank_5_container = null;
    public static final ContainerType<DankContainer6> dank_6_container = null;
    public static final ContainerType<DankContainer7> dank_7_container = null;


    public static final ContainerType<PortableDankContainer1> portable_dank_1_container = null;
    public static final ContainerType<PortableDankContainer2> portable_dank_2_container = null;
    public static final ContainerType<PortableDankContainer3> portable_dank_3_container = null;
    public static final ContainerType<PortableDankContainer4> portable_dank_4_container = null;
    public static final ContainerType<PortableDankContainer5> portable_dank_5_container = null;
    public static final ContainerType<PortableDankContainer6> portable_dank_6_container = null;
    public static final ContainerType<PortableDankContainer7> portable_dank_7_container = null;


    public static final TileEntityType<DankStorageTile1> dank_1_tile = null;
    public static final TileEntityType<DankStorageTile2> dank_2_tile = null;
    public static final TileEntityType<DankStorageTile3> dank_3_tile = null;
    public static final TileEntityType<DankStorageTile4> dank_4_tile = null;
    public static final TileEntityType<DankStorageTile5> dank_5_tile = null;
    public static final TileEntityType<DankStorageTile6> dank_6_tile = null;
    public static final TileEntityType<DankStorageTile7> dank_7_tile = null;

  }
}
