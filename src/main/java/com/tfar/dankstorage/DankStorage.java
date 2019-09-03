package com.tfar.dankstorage;

import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.block.DankBlock;
import com.tfar.dankstorage.capability.CapabilityDankStorage;
import com.tfar.dankstorage.network.DankPacketHandler;
import com.tfar.dankstorage.tile.AbstractDankStorageTile;
import com.tfar.dankstorage.tile.DankTiles;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashSet;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(modid = DankStorage.MODID, name = DankStorage.NAME, version = DankStorage.VERSION)
public class DankStorage {
  public static final String MODID = "dankstorage";
  public static final String NAME = "Dank Storage";
  public static final String VERSION = "@VERSION@";

  @Mod.Instance
  public static DankStorage instance;

  public DankStorage() {
    // Register the setup method for modloading
  }

  @Mod.EventHandler
  public void setup(final FMLPreInitializationEvent event) {
    DankPacketHandler.registerMessages(MODID);
    CapabilityDankStorage.register();
    NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber
  public static class RegistryEvents {
    public static final Set<Block> MOD_BLOCKS = new HashSet<>();

    @SubscribeEvent
    public static void blocks(final RegistryEvent.Register<Block> event) {
      // register a new block here
      for (int i = 1; i < 8; i++) {
        register(new DankBlock(Material.IRON).setCreativeTab(CreativeTabs.DECORATIONS), "dank_" + i, event.getRegistry());
      }
    }

    @SubscribeEvent
    public static void items(final RegistryEvent.Register<Item> event) {
      for (Block block : MOD_BLOCKS)
        register(new DankItemBlock(block).setCreativeTab(CreativeTabs.DECORATIONS), block.getRegistryName().getPath(), event.getRegistry());

      GameRegistry.registerTileEntity(DankTiles.DankStorageTile1.class, new ResourceLocation(MODID,"dank_1"));
      GameRegistry.registerTileEntity(DankTiles.DankStorageTile2.class, new ResourceLocation(MODID,"dank_2"));
      GameRegistry.registerTileEntity(DankTiles.DankStorageTile3.class, new ResourceLocation(MODID,"dank_3"));
      GameRegistry.registerTileEntity(DankTiles.DankStorageTile4.class, new ResourceLocation(MODID,"dank_4"));
      GameRegistry.registerTileEntity(DankTiles.DankStorageTile5.class, new ResourceLocation(MODID,"dank_5"));
      GameRegistry.registerTileEntity(DankTiles.DankStorageTile6.class, new ResourceLocation(MODID,"dank_6"));
      GameRegistry.registerTileEntity(DankTiles.DankStorageTile7.class, new ResourceLocation(MODID,"dank_7"));
    }

    private static <T extends IForgeRegistryEntry<T>> void register(T obj, String name, IForgeRegistry<T> registry) {
      obj = obj.setRegistryName(new ResourceLocation(MODID, name));
      if (obj instanceof Block) {
        ((Block) obj).setTranslationKey(obj.getRegistryName().toString());
        MOD_BLOCKS.add((Block) obj);
      }
      registry.register(obj);
    }
  }

  @GameRegistry.ObjectHolder(MODID)
  public static class Objects {
    public static final Block dank_1 = null;
    public static final Block dank_2 = null;
    public static final Block dank_3 = null;
    public static final Block dank_4 = null;
    public static final Block dank_5 = null;
    public static final Block dank_6 = null;
    public static final Block dank_7 = null;
  }
}
