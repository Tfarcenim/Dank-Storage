package tfar.dankstorage;

import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.dankstorage.command.DankCommands;
import tfar.dankstorage.init.ModBlockEntityTypes;
import tfar.dankstorage.init.ModItems;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.item.DankItemCapability;
import tfar.dankstorage.datagen.ModDatagen;
import tfar.dankstorage.network.DankPacketHandlerNeoForge;
import tfar.dankstorage.platform.TomlConfigs;

@Mod(DankStorage.MODID)
public class DankStorageNeoForge {

    public static final Logger LOGGER = LogManager.getLogger(DankStorage.MODID);

    public DankStorageNeoForge(IEventBus bus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
        NeoForge.EVENT_BUS.addListener(this::onServerStopped);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        NeoForge.EVENT_BUS.addListener(this::entityInvulnerabilityCheck);
        ModDatagen.setup(bus);
        bus.addListener(this::registerObjs);
        bus.addListener(this::onInitialize);
        bus.addListener(DankPacketHandlerNeoForge::register);
        bus.addListener(this::capabilities);

        DankStorage.init();
    }


    public void registerObjs(RegisterEvent event) {
        DankStorage.register();
    }

    private void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntityTypes.DOCK,(object, context) -> (ResourceHandler<ItemResource>) object.getInventory());
        event.registerItem(Capabilities.Item.ITEM,(stack,context) -> DankItemCapability.lookup(stack), ModItems.DANKS.values().toArray(DankItem[]::new));
    }

    public void onInitialize(FMLCommonSetupEvent e) {
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

    private void entityInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            if (itemEntity.getItem().getItem() instanceof DankItem && !event.getSource().is(Tags.DamageTypes.IS_TECHNICAL)) {
                event.setInvulnerable(true);
            }
        }
    }


  public static final TomlConfigs.ClientConfig CLIENT;
  public static final ModConfigSpec CLIENT_SPEC;

  public static final TomlConfigs.ServerConfig SERVER;
  public static final ModConfigSpec SERVER_SPEC;

  static {
    final Pair<TomlConfigs.ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(TomlConfigs.ClientConfig::new);
    CLIENT_SPEC = specPair.getRight();
    CLIENT = specPair.getLeft();
    final Pair<TomlConfigs.ServerConfig, ModConfigSpec> specPair2 = new ModConfigSpec.Builder().configure(TomlConfigs.ServerConfig::new);
    SERVER_SPEC = specPair2.getRight();
    SERVER = specPair2.getLeft();
  }

}
