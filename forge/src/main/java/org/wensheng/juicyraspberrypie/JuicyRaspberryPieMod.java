package org.wensheng.juicyraspberrypie;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("juicyraspberrypie")
public class JuicyRaspberryPieMod
{
    private static final int API_PORT = 4712;
    private ServerListenerThread serverThread = null;
    static final Set<String> BLOCK_NAMES = new HashSet<>();
    static final Set<String> ENTITY_NAMES = new HashSet<>();
    static final Set<String> PARTICLE_NAMES = new HashSet<>();
    private ServerSocket serverSocket;

    // Directly reference a log4j logger.
    static final Logger LOGGER = LogManager.getLogger();

    public JuicyRaspberryPieMod() {
        final FMLJavaModLoadingContext ctx = FMLJavaModLoadingContext.get();
        // Register the setup method for modloading
        ctx.getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        ctx.getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        ctx.getModEventBus().addListener(this::processIMC);
        ctx.getModEventBus().addListener(this::doServerStuff);
        ctx.getModEventBus().addListener(this::doClientStuff);
        JRPModConfig.register(ModLoadingContext.get());
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        for(ResourceLocation rloc: ForgeRegistries.BLOCKS.getKeys()){
            BLOCK_NAMES.add(rloc.toString());
        }
        for(ResourceLocation rloc: ForgeRegistries.ENTITIES.getKeys()){
            ENTITY_NAMES.add(rloc.toString());
        }
        for(ResourceLocation rloc: ForgeRegistries.PARTICLE_TYPES.getKeys()){
            PARTICLE_NAMES.add(rloc.toString());
        }
    }

    private void doServerStuff(final FMLDedicatedServerSetupEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        // TODO:
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("juicyraspberrypie", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        //PyCommand.register(event.getCommandDispatcher());
        ApiHandler apiHandler = new ApiHandler();
        MinecraftForge.EVENT_BUS.register(apiHandler);
        try{
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(API_PORT));
            serverThread = new ServerListenerThread(apiHandler, serverSocket);
            new Thread(serverThread).start();
            LOGGER.info("Socket server thread started");
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.warn("Could not start socket server thread");
        }
        // mods location, should be %appdata%/.minecraft/mods
        //LOGGER.info("MODSDIR=" + FMLPaths.MODSDIR.get().resolve(""));
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        // close socket in main thread instead of inside serverListenerThread
        // otherwise socket not closed
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }catch (IOException ignored){
        }
        if(serverThread != null){
            LOGGER.info("Stopping socket serverListenerThread...");
            serverThread.running.set(false);
        }
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
