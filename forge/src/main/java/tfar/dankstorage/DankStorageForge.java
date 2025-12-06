package tfar.dankstorage;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import tfar.dankstorage.client.ModClientForge;
import tfar.dankstorage.command.DankCommands;
import tfar.dankstorage.menu.AbstractDankMenu;
import tfar.dankstorage.menu.CustomSync;
import tfar.dankstorage.datagen.ModDatagen;
import tfar.dankstorage.event.ForgeClientEvents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod(DankStorage.MODID)
public class DankStorageForge {

    public static final Logger LOGGER = LogManager.getLogger(DankStorage.MODID);
    public static Map<Registry<?>, List<Pair<ResourceLocation, Supplier<?>>>> registerLater = new HashMap<>();

    public DankStorageForge() {
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, DankStorageConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, DankStorageConfig.SERVER_SPEC);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopped);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.addListener(this::containerEvent);
        bus.addListener(ModDatagen::setupDataGenerator);
        bus.addListener(this::registerObjs);
        bus.addListener(this::onInitialize);
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

    private void containerEvent(PlayerContainerEvent.Open e) {
        AbstractContainerMenu abstractContainerMenu = e.getContainer();
        if (abstractContainerMenu instanceof AbstractDankMenu dankMenu) {
            dankMenu.setSynchronizer(new CustomSync((ServerPlayer) e.getEntity()));
        }
    }





}
