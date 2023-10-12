package tfar.dankstorage;

import com.google.common.collect.Lists;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.dankstorage.client.Client;
import tfar.dankstorage.command.DankCommands;
import tfar.dankstorage.container.AbstractDankMenu;
import tfar.dankstorage.container.CustomSync;
import tfar.dankstorage.datagen.DataGenerators;
import tfar.dankstorage.event.ForgeClientEvents;
import tfar.dankstorage.init.*;
import tfar.dankstorage.mixin.MinecraftServerAccess;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.Utils;
import tfar.dankstorage.world.DankSavedData;
import tfar.dankstorage.world.MaxId;

import java.io.File;
import java.util.List;

import static tfar.dankstorage.init.ModMenuTypes.portable_dank_7_container;

@Mod(DankStorage.MODID)
public class DankStorageForge {

    public static final Logger LOGGER = LogManager.getLogger(DankStorage.MODID);

    public static DankStorageForge instance;
    public MaxId maxId;

    public DankStorageForge() {
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, SERVER_SPEC);
        instance = this;
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopped);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.addListener(this::containerEvent);
        bus.addListener(DataGenerators::setupDataGenerator);
        bus.addListener(this::registerObjs);
        bus.addListener(ModItems::registerB);
        bus.addListener(this::onInitialize);
        if (FMLEnvironment.dist.isClient()) {
            bus.addListener(this::onInitializeClient);
            bus.addListener(Client::keybinds);
            bus.addListener(Client::clientTool);
            bus.addListener(ForgeClientEvents::renderStack);
        }
        DankStorage.init();
    }

    public void registerObjs(RegisterEvent event) {

        DankStorageForge.register(event, Registries.BLOCK, "dock", ModBlocks.dock);

        DankStorageForge.register(event, Registries.BLOCK_ENTITY_TYPE, "dank_tile", ModBlockEntityTypes.dank_tile);
        DankStorageForge.register(event, Registries.RECIPE_SERIALIZER,"upgrade", ModRecipeSerializers.upgrade);

        register(event, Registries.MENU, "dank_1", ModMenuTypes.dank_1_container);
        register(event,Registries.MENU, "portable_dank_1", ModMenuTypes.portable_dank_1_container);

        register(event,Registries.MENU, "dank_2", ModMenuTypes.dank_2_container);
        register(event,Registries.MENU,"portable_dank_2", ModMenuTypes.portable_dank_2_container);

        register(event,Registries.MENU, "dank_3", ModMenuTypes.dank_3_container);
        register(event,Registries.MENU, "portable_dank_3", ModMenuTypes.portable_dank_3_container);

        register(event,Registries.MENU, "dank_4", ModMenuTypes.dank_4_container);
        register(event,Registries.MENU, "portable_dank_4", ModMenuTypes.portable_dank_4_container);

        register(event,Registries.MENU, "dank_5", ModMenuTypes.dank_5_container);
        register(event,Registries.MENU, "portable_dank_5", ModMenuTypes.portable_dank_5_container);

        register(event,Registries.MENU, "dank_6", ModMenuTypes.dank_6_container);
        register(event,Registries.MENU, "portable_dank_6", ModMenuTypes.portable_dank_6_container);

        register(event,Registries.MENU, "dank_7", ModMenuTypes.dank_7_container);
        register(event,Registries.MENU, "portable_dank_7", portable_dank_7_container);
        register(event,Registries.CREATIVE_MODE_TAB, DankStorage.MODID,ModItems.tab);
    }

    public static <T>void register(RegisterEvent event, ResourceKey<? extends Registry<T>> registry, String name, T type) {
        event.register(registry,new ResourceLocation(DankStorage.MODID,name),() -> type);
    }

    public void onInitialize(FMLCommonSetupEvent e) {
        DankPacketHandler.registerMessages();
    }

    public void onInitializeClient(FMLClientSetupEvent e) {
        Client.client();
    }

    public void onServerStarted(ServerStartedEvent e) {
        MinecraftServer server = e.getServer();
        LevelStorageSource.LevelStorageAccess storageSource = ((MinecraftServerAccess)server).getStorageSource();
        File file = storageSource.getDimensionPath(server.getLevel(Level.OVERWORLD).dimension())
                .resolve("data/"+ DankStorage.MODID).toFile();
        file.mkdirs();

        instance.maxId = getMaxId(server);
    }

    public DankSavedData getData(int id,MinecraftServer server) {
        if (id == Utils.INVALID) throw new RuntimeException("Invalid frequency");
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        return overworld.getDataStorage()
                .computeIfAbsent(compoundTag -> DankSavedData.loadStatic(compoundTag,overworld), () -> new DankSavedData(overworld),
                        DankStorage.MODID+"/"+id);
    }

    public MaxId getMaxId(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage()
                .computeIfAbsent(MaxId::loadStatic,MaxId::new,
                        DankStorage.MODID+":max_id");
    }

    public void onServerStopped(ServerStoppedEvent e) {
        instance.maxId = null;
    }

    public void registerCommands(RegisterCommandsEvent e) {
        DankCommands.register(e.getDispatcher());
    }

    private void containerEvent(PlayerContainerEvent.Open e) {
        AbstractContainerMenu abstractContainerMenu = e.getContainer();
        if (abstractContainerMenu instanceof AbstractDankMenu dankMenu) {
            dankMenu.setSynchronizer(new CustomSync((ServerPlayer) e.getEntity()));
        }
    }




  public static final ClientConfig CLIENT;
  public static final ForgeConfigSpec CLIENT_SPEC;

  public static final ServerConfig SERVER;
  public static final ForgeConfigSpec SERVER_SPEC;

  static {
    final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
    CLIENT_SPEC = specPair.getRight();
    CLIENT = specPair.getLeft();
    final Pair<ServerConfig, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
    SERVER_SPEC = specPair2.getRight();
    SERVER = specPair2.getLeft();
  }


  public static class ClientConfig {
    public static ForgeConfigSpec.BooleanValue preview;
      public static ForgeConfigSpec.IntValue preview_x;
      public static ForgeConfigSpec.IntValue preview_y;
    public ClientConfig(ForgeConfigSpec.Builder builder) {
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
    public static ForgeConfigSpec.IntValue stacklimit1;
    public static ForgeConfigSpec.IntValue stacklimit2;
    public static ForgeConfigSpec.IntValue stacklimit3;
    public static ForgeConfigSpec.IntValue stacklimit4;
    public static ForgeConfigSpec.IntValue stacklimit5;
    public static ForgeConfigSpec.IntValue stacklimit6;
    public static ForgeConfigSpec.IntValue stacklimit7;
      public static ForgeConfigSpec.ConfigValue<List<String>> convertible_tags;

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

    public ServerConfig(ForgeConfigSpec.Builder builder) {
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
