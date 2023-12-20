package tfar.dankstorage;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import tfar.dankstorage.block.CDockBlock;
import tfar.dankstorage.block.DankDispenserBehavior;
import tfar.dankstorage.blockentity.DockBlockEntity;
import tfar.dankstorage.client.Client;
import tfar.dankstorage.command.DankCommands;
import tfar.dankstorage.init.ModCreativeTabs;
import tfar.dankstorage.init.ModMenuTypes;
import tfar.dankstorage.init.ModRecipeSerializers;
import tfar.dankstorage.item.DankItem;
import tfar.dankstorage.item.RedprintItem;
import tfar.dankstorage.utils.UpgradeInfo;
import tfar.dankstorage.item.UpgradeItem;
import tfar.dankstorage.network.DankPacketHandler;
import tfar.dankstorage.utils.DankStats;

import java.util.stream.IntStream;

import static tfar.dankstorage.DankStorage.MODID;

public class DankStorageFabric implements ModInitializer, ClientModInitializer,
        ServerLifecycleEvents.ServerStarted, ServerLifecycleEvents.ServerStopped, CommandRegistrationCallback {

    public static Block dock;
    public static Item dock_item;
    public static Item red_print;
    public static BlockEntityType<DockBlockEntity> dank_tile;

    @Override
    public void onInitialize() {
        Item.Properties properties = new Item.Properties();

        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(MODID, "dock"),
                dock = new CDockBlock(BlockBehaviour.Properties.of().strength(1, 30), DockBlockEntity::new));
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "dock"), dock_item = new BlockItem(dock, properties));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> entries.accept(dock));


        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "red_print"), red_print = new RedprintItem(properties));
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(MODID, "dank_tile"), dank_tile = BlockEntityType.Builder.of(DockBlockEntity::new, dock).build(null));



        IntStream.range(1, 7).forEach(i -> Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, i + "_to_" + (i + 1)), new UpgradeItem(properties, new UpgradeInfo(i, i + 1))));
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(MODID, "upgrade"), ModRecipeSerializers.upgrade);

        properties.stacksTo(1);

        IntStream.range(1, 8).forEach(i -> {
            DankItem dankItem = new DankItem(properties, DankStats.values()[i]);
            DispenserBlock.registerBehavior(dankItem, new DankDispenserBehavior());
            Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MODID, "dank_" + i), dankItem);
        });

        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "dank_1"), ModMenuTypes.dank_1_container);
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "portable_dank_1"), ModMenuTypes.portable_dank_1_container);

        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "dank_2"), ModMenuTypes.dank_2_container);
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "portable_dank_2"), ModMenuTypes.portable_dank_2_container);

        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "dank_3"), ModMenuTypes.dank_3_container);
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "portable_dank_3"), ModMenuTypes.portable_dank_3_container);

        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "dank_4"), ModMenuTypes.dank_4_container);
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "portable_dank_4"), ModMenuTypes.portable_dank_4_container);

        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "dank_5"), ModMenuTypes.dank_5_container);
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "portable_dank_5"), ModMenuTypes.portable_dank_5_container);

        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "dank_6"), ModMenuTypes.dank_6_container );
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "portable_dank_6"), ModMenuTypes.portable_dank_6_container);

        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "dank_7"), ModMenuTypes.dank_7_container);
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(MODID, "portable_dank_7"), ModMenuTypes.portable_dank_7_container);


        ModCreativeTabs.tab = CreativeModeTab.builder(null,-1)
                .icon(() -> new ItemStack(dock_item))
                .title(Component.translatable("itemGroup."+ DankStorage.MODID))
                .displayItems((features, output) -> BuiltInRegistries.ITEM.stream().filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(MODID)).forEach(output::accept)).build();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,new ResourceLocation(MODID,"creative_tab"), ModCreativeTabs.tab);

        DankPacketHandler.registerMessages();

        ServerLifecycleEvents.SERVER_STARTED.register(this);
        ServerLifecycleEvents.SERVER_STOPPED.register(this);
        CommandRegistrationCallback.EVENT.register(this);

        ItemStorage.SIDED.registerForBlockEntity(DockBlockEntity::getStorage, dank_tile);
    }

    @Override
    public void onInitializeClient() {
        Client.client();
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


    /*

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

    public ClientConfig(ForgeConfigSpec.Builder builder) {
      builder.push("client");
      preview = builder
              .comment("Whether to display the preview of the item in the dank, disable if you have optifine")
              .define("preview", true);
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
    public static ForgeConfigSpec.BooleanValue useShareTag;
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
  }*/
}
