package tfar.dankstorage;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class DankStorageConfig {
    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(DankStorageConfig.ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
        final Pair<DankStorageConfig.ServerConfig, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(DankStorageConfig.ServerConfig::new);
        SERVER_SPEC = specPair2.getRight();
        SERVER = specPair2.getLeft();
    }

    public static class ClientConfig {
      public final ForgeConfigSpec.BooleanValue preview;
        public final ForgeConfigSpec.IntValue preview_x;
        public final ForgeConfigSpec.IntValue preview_y;
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
    public final ForgeConfigSpec.IntValue stacklimit1;
    public final ForgeConfigSpec.IntValue stacklimit2;
    public final ForgeConfigSpec.IntValue stacklimit3;
    public final ForgeConfigSpec.IntValue stacklimit4;
    public final ForgeConfigSpec.IntValue stacklimit5;
    public final ForgeConfigSpec.IntValue stacklimit6;
    public final ForgeConfigSpec.IntValue stacklimit7;
    public final ForgeConfigSpec.ConfigValue<List<String>> convertible_tags;

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
