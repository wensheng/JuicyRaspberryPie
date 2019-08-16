package org.wensheng.juicyraspberrypie;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class JRPModConfig {
    private static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static void register(final ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, serverSpec);
    }

    public static class Server {
        public final ForgeConfigSpec.ConfigValue pysvr_addr;
        public final ForgeConfigSpec.IntValue pysvr_port;
        public final ForgeConfigSpec.ConfigValue pyexe;
        public final ForgeConfigSpec.ConfigValue pypath;

        Server(final ForgeConfigSpec.Builder builder) {
            builder.push("general");
            pysvr_addr = builder
                    .comment("Python Command Server Address")
                    .translation("org.wensheng.juicyraspberrypie.config.pysvr_addr")
                    .define("pysvr_addr", "127.0.0.1");
            pysvr_port = builder
                    .comment("Python Command Server Port")
                    .translation("org.wensheng.juicyraspberrypie.config.pysvr_port")
                    .defineInRange("pysvr_port", 4732, 4731, 4732);
            pyexe = builder
                    .comment("Python executable file name, in Linux/mac: python or python3, in Windows: python.exe")
                    .translation("org.wensheng.juicyraspberrypie.config.pyexe")
                    .define("pyexe", "python.exe");
            pypath = builder
                    .comment("Python Command Server Port")
                    .translation("org.wensheng.juicyraspberrypie.config.pypath")
                    .define("pypath", "C:\\python3.7");
            builder.pop();
        }
    }
}
