package com.tfar.dankstorage;

import com.tfar.dankstorage.block.DankItemBlock;
import com.tfar.dankstorage.block.DankBlock;
import com.tfar.dankstorage.capability.CapabilityDankStorage;
import com.tfar.dankstorage.container.*;
import com.tfar.dankstorage.inventory.DankHandler;
import com.tfar.dankstorage.inventory.PortableDankHandler;
import com.tfar.dankstorage.network.DankPacketHandler;
import com.tfar.dankstorage.network.Utils;
import com.tfar.dankstorage.recipe.DankUpgradeRecipes;
import com.tfar.dankstorage.tile.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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

  public DankStorage() {
    // Register the setup method for modloading
  }

  @Mod.EventHandler
  public void setup(final FMLPreInitializationEvent event) {
    DankPacketHandler.registerMessages(MODID);
    CapabilityDankStorage.register();
  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {
    private static final Set<Block> MOD_BLOCKS = new HashSet<>();

    @SubscribeEvent
    public static void blocks(final RegistryEvent.Register<Block> event) {
      // register a new block here
      Block.Properties properties = Block.Properties.create(Material.IRON).hardnessAndResistance(1,30);
      for (int i = 1; i < 8; i++) {
        register(new DankBlock(properties), "dank_" + i, event.getRegistry());
      }
    }

    @SubscribeEvent
    public static void items(final RegistryEvent.Register<Item> event) {
      Item.Properties properties = new Item.Properties().group(ItemGroup.DECORATIONS).maxStackSize(1);
      for (Block block : MOD_BLOCKS)
        register(new DankItemBlock(block, properties), block.getRegistryName().getPath(), event.getRegistry());
    }

    @SubscribeEvent
    public static void recipes(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
      register(new SpecialRecipeSerializer<>(DankUpgradeRecipes.DankUpgradeRecipe1::new),"upgrade1",event.getRegistry());
      register(new SpecialRecipeSerializer<>(DankUpgradeRecipes.DankUpgradeRecipe2::new),"upgrade2",event.getRegistry());
      register(new SpecialRecipeSerializer<>(DankUpgradeRecipes.DankUpgradeRecipe3::new),"upgrade3",event.getRegistry());
      register(new SpecialRecipeSerializer<>(DankUpgradeRecipes.DankUpgradeRecipe4::new),"upgrade4",event.getRegistry());
      register(new SpecialRecipeSerializer<>(DankUpgradeRecipes.DankUpgradeRecipe5::new),"upgrade5",event.getRegistry());
      register(new SpecialRecipeSerializer<>(DankUpgradeRecipes.DankUpgradeRecipe6::new),"upgrade6",event.getRegistry());
    }

    private static <T extends IForgeRegistryEntry<T>> void register(T obj, String name, IForgeRegistry<T> registry) {
      registry.register(obj.setRegistryName(new ResourceLocation(MODID, name)));
      if (obj instanceof Block) MOD_BLOCKS.add((Block) obj);
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

    public static final IRecipeSerializer<?> upgrade1 = null;
    public static final IRecipeSerializer<?> upgrade2 = null;
    public static final IRecipeSerializer<?> upgrade3 = null;
    public static final IRecipeSerializer<?> upgrade4 = null;
    public static final IRecipeSerializer<?> upgrade5 = null;
    public static final IRecipeSerializer<?> upgrade6 = null;
  }
}
