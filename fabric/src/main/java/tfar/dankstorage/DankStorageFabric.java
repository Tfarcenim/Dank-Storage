package tfar.dankstorage;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.config.ModConfig;
import tfar.dankstorage.blockentity.DockBlockEntity;
import tfar.dankstorage.command.DankCommands;
import tfar.dankstorage.init.ModBlockEntityTypes;
import tfar.dankstorage.inventory.DankInventory;
import tfar.dankstorage.inventory.api.DankInventorySlotWrapper;
import tfar.dankstorage.network.DankPacketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DankStorageFabric implements ModInitializer,
        ServerLifecycleEvents.ServerStarted, ServerLifecycleEvents.ServerStopped, CommandRegistrationCallback {


    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this);
        ServerLifecycleEvents.SERVER_STOPPED.register(this);
        CommandRegistrationCallback.EVENT.register(this);

        ItemStorage.SIDED.registerForBlockEntity(DankStorageFabric::getStorage,ModBlockEntityTypes.DOCK);
        DankStorage.init();
        DankStorage.register();
        DankPacketHandler.registerPackets();
        ConfigRegistry.INSTANCE.register(DankStorage.MODID, ModConfig.Type.SERVER,DankConfig.SERVER_SPEC);
        ConfigRegistry.INSTANCE.register(DankStorage.MODID, ModConfig.Type.CLIENT,DankConfig.CLIENT_SPEC);
    }


    @Override
    public void onServerStarted(MinecraftServer server) {
        DankStorage.onServerStart(server);
    }

    @Override
    public void onServerStopped(MinecraftServer server) {
        DankStorage.onServerShutDown(server);
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandbuildcontext, CommandSelection commandselection) {
        DankCommands.register(dispatcher);
    }

    static Map<BlockEntity, CombinedStorage<ItemVariant,DankInventorySlotWrapper>> MAP = new HashMap<>();

    //item api

    public static CombinedStorage<ItemVariant,DankInventorySlotWrapper> getStorage(DockBlockEntity dockBlockEntity, Direction direction) {

        DankInventory dankInventory = dockBlockEntity.getInventory();

        CombinedStorage<ItemVariant,DankInventorySlotWrapper> storage = MAP.get(dockBlockEntity);

        if (storage != null && storage.parts.size() != dankInventory.slotCount()) {
            storage = null;
        }
        if (storage == null) {
            storage = create(dankInventory);
            MAP.put(dockBlockEntity,storage);
        }
        return storage;
    }


    public static CombinedStorage<ItemVariant,DankInventorySlotWrapper> create(DankInventory dankInventoryFabric) {
        int slots = dankInventoryFabric.slotCount();

        List<DankInventorySlotWrapper> storages = new ArrayList<>();

        for (int i = 0 ;i < slots;i++) {
            DankInventorySlotWrapper storage = new DankInventorySlotWrapper(dankInventoryFabric,i);
            storages.add(storage);
        }

        return new CombinedStorage<>(storages);
    }
}
