package tfar.dankstorage;

import com.google.common.collect.Lists;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.dankstorage.client.ModClientForge;
import tfar.dankstorage.command.DankCommands;
import tfar.dankstorage.init.ModBlockEntityTypes;
import tfar.dankstorage.init.ModItems;
import tfar.dankstorage.item.CDankItem;
import tfar.dankstorage.item.DankItemCapability;
import tfar.dankstorage.menu.AbstractDankMenu;
import tfar.dankstorage.menu.CustomSync;
import tfar.dankstorage.datagen.ModDatagen;
import tfar.dankstorage.event.ForgeClientEvents;
import tfar.dankstorage.network.DankPacketHandlerNeoForge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod(DankStorage.MODID)
public class DankStorageNeoForge {

    public static final Logger LOGGER = LogManager.getLogger(DankStorage.MODID);
    public static Map<Registry<?>, List<Pair<ResourceLocation, Supplier<?>>>> registerLater = new HashMap<>();

    public DankStorageNeoForge(IEventBus bus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
        NeoForge.EVENT_BUS.addListener(this::onServerStopped);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        bus.addListener(ModDatagen::setupDataGenerator);
        bus.addListener(this::registerObjs);
        bus.addListener(this::onInitialize);
        bus.addListener(DankPacketHandlerNeoForge::register);
        bus.addListener(this::capabilities);
        if (FMLEnvironment.dist.isClient()) {
            bus.addListener(this::onInitializeClient);
            bus.addListener(ModClientForge::keybinds);
            bus.addListener(ModClientForge::clientTool);
            bus.addListener(ForgeClientEvents::renderStack);
        }
        DankStorage.init();
    }

    public void registerObjs(RegisterEvent event) {
        for (Map.Entry<Registry<?>,List<Pair<ResourceLocation, Supplier<?>>>> entry : registerLater.entrySet()) {
            Registry<?> registry = entry.getKey();
            List<Pair<ResourceLocation, Supplier<?>>> toRegister = entry.getValue();
            for (Pair<ResourceLocation,Supplier<?>> pair : toRegister) {
                event.register((ResourceKey<? extends Registry<Object>>)registry.key(),pair.getLeft(),(Supplier<Object>)pair.getValue());
            }
        }
    }

    private void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntityTypes.dank_tile,(object, context) -> (IItemHandler) object.getInventory());
        event.registerItem(Capabilities.ItemHandler.ITEM,(stack,context) -> DankItemCapability.lookup(stack), ModItems.DANKS.values().toArray(CDankItem[]::new));
    }

    public void onInitialize(FMLCommonSetupEvent e) {
        registerLater.clear();
    }

    public void onInitializeClient(FMLClientSetupEvent e) {
        ModClientForge.client();
    }

    public void onServerStarted(ServerStartedEvent e) {
        DankStorage.onServerStart(e.getServer());
    }

    public void onServerStopped(ServerStoppedEvent e) {
        DankStorage.onServerShutDown(e.getServer());
    }

    public void registerCommands(RegisterCommandsEvent e) {
        DankCommands.register(e.getDispatcher());
    }




  public static final ClientConfig CLIENT;
  public static final ModConfigSpec CLIENT_SPEC;

  public static final ServerConfig SERVER;
  public static final ModConfigSpec SERVER_SPEC;

  static {
    final Pair<ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
    CLIENT_SPEC = specPair.getRight();
    CLIENT = specPair.getLeft();
    final Pair<ServerConfig, ModConfigSpec> specPair2 = new ModConfigSpec.Builder().configure(ServerConfig::new);
    SERVER_SPEC = specPair2.getRight();
    SERVER = specPair2.getLeft();
  }


  public static class ClientConfig {
    public static ModConfigSpec.BooleanValue preview;
      public static ModConfigSpec.IntValue preview_x;
      public static ModConfigSpec.IntValue preview_y;
    public ClientConfig(ModConfigSpec.Builder builder) {
      builder.push("client");
      preview = builder
              .comment("Whether to display the preview of the item in the dank, disable if you have optifine")
              .define("preview", true);
        preview_x = builder
                .comment("X position of preview")
                .defineInRange("preview_x", -140,-10000,10000);
        preview_y = builder
                .comment("Y position of preview")
                .defineInRange("preview_y",-25,-10000,10000);
      builder.pop();
    }
  }

  public static class ServerConfig {
    public static ModConfigSpec.IntValue stacklimit1;
    public static ModConfigSpec.IntValue stacklimit2;
    public static ModConfigSpec.IntValue stacklimit3;
    public static ModConfigSpec.IntValue stacklimit4;
    public static ModConfigSpec.IntValue stacklimit5;
    public static ModConfigSpec.IntValue stacklimit6;
    public static ModConfigSpec.IntValue stacklimit7;
      public static ModConfigSpec.ConfigValue<List<String>> convertible_tags;

    public static final List<String> defaults = Lists.newArrayList(
            "forge:ingots/iron",
            "forge:ingots/gold",
            "forge:ores/coal",
            "forge:ores/diamond",
            "forge:ores/emerald",
            "forge:ores/gold",
            "forge:ores/iron",
            "forge:ores/lapis",
            "forge:ores/redstone",

            "forge:gems/amethyst",
            "forge:gems/peridot",
            "forge:gems/ruby",

            "forge:ingots/copper",
            "forge:ingots/lead",
            "forge:ingots/nickel",
            "forge:ingots/silver",
            "forge:ingots/tin",

            "forge:ores/copper",
            "forge:ores/lead",
            "forge:ores/ruby",
            "forge:ores/silver",
            "forge:ores/tin");

    public ServerConfig(ModConfigSpec.Builder builder) {
      builder.push("server");
      stacklimit1 = builder.
              comment("Stack limit of first dank storage")
              .defineInRange("stacklimit1", 256, 1, Integer.MAX_VALUE);
      stacklimit2 = builder.
              comment("Stack limit of second dank storage")
              .defineInRange("stacklimit2", 1024, 1, Integer.MAX_VALUE);
      stacklimit3 = builder.
              comment("Stack limit of third dank storage")
              .defineInRange("stacklimit3", 4096, 1, Integer.MAX_VALUE);
      stacklimit4 = builder.
              comment("Stack limit of fourth dank storage")
              .defineInRange("stacklimit4", 16384, 1, Integer.MAX_VALUE);
      stacklimit5 = builder.
              comment("Stack limit of fifth dank storage")
              .defineInRange("stacklimit5", 65536, 1, Integer.MAX_VALUE);
      stacklimit6 = builder.
              comment("Stack limit of sixth dank storage")
              .defineInRange("stacklimit6", 262144, 1, Integer.MAX_VALUE);
      stacklimit7 = builder.
              comment("Stack limit of seventh dank storage")
              .defineInRange("stacklimit7", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

      convertible_tags = builder.
              comment("Tags that are eligible for conversion, input as a list of resourcelocation, eg 'forge:ingots/iron'")
              .define("convertible tags", defaults);
      builder.pop();
    }
  }
}
