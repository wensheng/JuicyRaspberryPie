package org.wensheng.juicyraspberrypie;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.util.*;

public class ApiHandler {
    private static final float TOO_SMALL = (float) 1e-9;
    private static Logger logger = JuicyRaspberryPieMod.LOGGER;
    private PrintWriter writer = null;
    private ServerWorld world;
    private PlayerEntity player = null;
    private ArrayDeque<String> inQueue = new ArrayDeque<>();
    private final List<String> queuedCommands = Arrays.asList("world.setBlock", "world.setBlocks");

    ApiHandler() {
        // TODO: actually get the world which the current player is in (overworld, nether, the_end)
        final MinecraftServer ms = ServerLifecycleHooks.getCurrentServer();
        this.world = DimensionManager.getWorld(ms, DimensionType.OVERWORLD, false,false);
    }

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof PlayerEntity){
            player = (PlayerEntity) event.getEntity();
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        String line;
        while((line = inQueue.poll()) != null){
            String methodName = line.substring(0, line.indexOf("("));
            String[] args = line.substring(line.indexOf("(") + 1, line.length() - 1).split(",");
            handleCommand(methodName, args);
        }
    }

    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {
    }

    void process(PrintWriter writer, String line) {
        this.writer = writer;
        if(player == null) {
            fail("no Player");
            return;
        }
        try {
            String methodName = line.substring(0, line.indexOf("("));
            if(queuedCommands.contains(methodName)){
                inQueue.add(line);
            }else {
                String[] args = line.substring(line.indexOf("(") + 1, line.length() - 1).split(",");
                handleCommand(methodName, args);
            }
        }
        catch(Exception e) {
            System.out.println(""+e);
            e.printStackTrace();
        }
    }


    private void handleCommand(String cmd, String[] args)
            throws InputMismatchException, NoSuchElementException, IndexOutOfBoundsException {

        if (cmd.equals("world.getBlock")) {
            BlockPos pos = parseRelativeBlockLocation(args[0], args[1], args[2]);
            BlockState bs = world.getBlockState(pos);
            String name = bs.getBlock().toString();
            if(name.startsWith("Block{minecraft:")){
                // make it consistent with Bukkit reply
                name = name.substring(16, name.length()-1).toUpperCase();
            }
            sendLine(name);
        } else if (cmd.equals("world.getBlockWithData")) {
            StringBuilder sb = new StringBuilder();
            BlockPos pos = parseRelativeBlockLocation(args[0], args[1], args[2]);
            BlockState bs = world.getBlockState(pos);
            String name = bs.getBlock().toString();
            if(name.startsWith("Block{minecraft:")){
                name = name.substring(16, name.length()-1).toUpperCase();
            }
            // TODO: not sure what data to send to user
            sb.append(name).append(",").append(bs.getValues().toString());
            sendLine(sb.toString());
        } else if (cmd.equals("world.setBlock")) {
            BlockPos pos = parseRelativeBlockLocation(args[0], args[1], args[2]);
            BlockState bs = blockStateFromName(args[3]);
            int facing = args.length > 4 ? Integer.parseInt(args[4]) : 0;
            world.setBlockState(pos, bs, 2);
        } else if (cmd.equals("world.getBlocks")) {
            BlockPos pos1 = parseRelativeBlockLocation(args[0], args[1], args[2]);
            BlockPos pos2 = parseRelativeBlockLocation(args[3], args[4], args[5]);
            sendLine(getBlocks(pos1, pos2));
        } else if (cmd.equals("world.setBlocks")) {
            BlockPos pos1 = parseRelativeBlockLocation(args[0], args[1], args[2]);
            BlockPos pos2 = parseRelativeBlockLocation(args[3], args[4], args[5]);
            BlockState bs = blockStateFromName(args[6]);
            int facing = args.length > 7 ? Integer.parseInt(args[7]) : 0;
            setCuboid(world, pos1, pos2, bs);
        } else if (cmd.equals("world.getPlayerIds")) {
            sendLine("NOT YET implemented");
        } else if (cmd.equals("world.spawnEntity")) {
            BlockPos pos = parseRelativeBlockLocation(args[0], args[1], args[2]);
            EntityType et = entityTypeFromName(args[3]);
            Entity e = et.create(world);
            e.setPosition(pos.getX(), pos.getY(), pos.getZ());
            world.addEntity(e);
            sendLine(e.getEntityId());
        } else if (cmd.equals("world.removeEntity")) {
            Entity e = world.getEntityByID(Integer.parseInt(args[0]));
            world.removeEntity(e);
            sendLine("removed");
        } else if(cmd.equals("chat.post")){
            sendLine("NOT YET");
        } else if (cmd.startsWith("player.")) {
            entityCommand(player, cmd.substring(7), args);
        } else if (cmd.startsWith("entity.")) {
            if(args.length == 0){
                sendLine("No entity ID");
                return;
            }
            Entity e = world.getEntityByID(Integer.parseInt(args[0]));
            String[] newargs = Arrays.copyOfRange(args, 1, args.length);
            entityCommand(e, cmd.substring(7), newargs);
        }
        /* TODO: to be implemented
        "world.getHeight"
        "world.spawnParticle"
        "world.getPlayerId"
        "world.setting"
        "events.block.hits"
        "events.chat.posts"
        "events.clear"
        "world.setSign"
         */
        else {
            unknownCommand();
        }
    }

    private String getBlocks(BlockPos pos1, BlockPos pos2){
        StringBuilder sb = new StringBuilder();
        int minX = Math.min(pos1.getX(), pos2.getX());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());
        for (int y = minY; y <= maxY; y++){
            for(int x = minX; x <= maxX; x++){
                for(int z = minZ; z <= maxZ; z++){
                    String name = world.getBlockState(new BlockPos(x,y,z)).getBlock().toString();
                    if(name.startsWith("Block{minecraft:")){
                        name = name.substring(16, name.length()-1).toUpperCase();
                    }
                    sb.append(name).append(",");
                }
            }
        }
        sb.setLength(sb.length()-1);
        return sb.toString();
    }

    private BlockState blockStateFromName(String blockName){
        // from ID works, but it's not the way from 1.13 on
        /*
            int itemId = Integer.parseInt(args[3]);
            if (null != world.getTileEntity(pos)) {
                world.removeTileEntity(pos);
            }
            Item item = Item.getItemById(itemId);
            if(item != null) {
                ItemStack stack = new ItemStack(item, 1);
                bs = Block.getBlockFromItem(stack.getItem()).getDefaultState();
            }else{
                bs = Blocks.SANDSTONE.getDefaultState();
            }*/
        BlockState bs;
        // This doesn't work
        /*try {
            ByteArrayInputStream bi = new ByteArrayInputStream(blockName.getBytes());
            ObjectInputStream oi = new ObjectInputStream(bi);
            ItemStack stack = (ItemStack) oi.readObject();
            bs = Block.getBlockFromItem(stack.getItem()).getDefaultState();
        } catch (IOException | ClassNotFoundException  e) {
            bs = Blocks.SANDSTONE.getDefaultState();
        }*/
        // TODO: resource from other mod namespaces, not just "minecraft:"
        ResourceLocation found = null;
        String name = "minecraft:" + blockName.toLowerCase();
        for(ResourceLocation bn: JuicyRaspberryPieMod.BLOCKNAMES){
            if(bn.toString().equals(name)){
                found = bn;
                break;
            }
        }
        if(found != null){
            //Item item = ForgeRegistries.ITEMS.getValue(found);
            //bs = Block.getBlockFromItem(item).getDefaultState();
            Block block = ForgeRegistries.BLOCKS.getValue(found);
            bs = block.getDefaultState();
        }else{
            bs = Blocks.SANDSTONE.getDefaultState();
        }
        return bs;
    }

    private EntityType entityTypeFromName(String entityName) {
        ResourceLocation found = null;
        String name = "minecraft:" + entityName.toLowerCase();
        for(ResourceLocation bn: JuicyRaspberryPieMod.ENTITYNAMES){
            if(bn.toString().equals(name)){
                found = bn;
                break;
            }
        }
        EntityType et;
        if(found != null){
            et = ForgeRegistries.ENTITIES.getValue(found);
        }else{
            et = EntityType.ZOMBIE;
        }
        return et;
    }

    private void setCuboid(World world, BlockPos pos1, BlockPos pos2, BlockState bs) {
        int minX, maxX, minY, maxY, minZ, maxZ;
        minX = Math.min(pos1.getX(), pos2.getX());
        minY = Math.min(pos1.getY(), pos2.getY());
        minZ = Math.min(pos1.getZ(), pos2.getZ());
        maxX = Math.max(pos1.getX(), pos2.getX());
        maxY = Math.max(pos1.getY(), pos2.getY());
        maxZ = Math.max(pos1.getZ(), pos2.getZ());

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int y = minY; y <= maxY; ++y) {
                    BlockPos pos = new BlockPos(x, y, z);
                    //world.removeTileEntity(pos);
                    world.setBlockState(pos, bs, 2);
                }
            }
        }
    }

    private void unknownCommand() {
        fail("unknown command");
    }

    private void entityCommand(Entity entity, String cmd, String[] args) {
        if(entity == null){
            fail("entity not found");
            return;
        }
        switch (cmd) {
            case "getPos":
                sendLine(encodeVec3(entity.getPositionVector()));
                break;
            case "setPos":
                entitySetPos(entity, args);
                break;
            case "getTile": {
                Vec3d pos = encodeVec3(entity.getPositionVector());
                sendLine("" + trunc(pos.getX()) + "," + trunc(pos.getY()) + "," + trunc(pos.getZ()));
                break;
            }
            case "setTile":
                // same as SetPos
                entitySetPos(entity, args);
                break;
            case "getRotation":
                sendLine(normalizeAngle(entity.rotationYaw));
                break;
            case "setRotation": {
                float angle = Float.parseFloat(args[0]);
                entity.rotationYaw = angle;
                entity.setRotationYawHead(angle);
                break;
            }
            case "getPitch":
                sendLine(normalizeAngle(entity.rotationPitch));
                break;
            case "setPitch":
                entity.rotationPitch = Float.parseFloat(args[0]);
                break;
            case "getDirection":
                entityGetDirection(entity);
                break;
            case "setDirection":
                entitySetDirection(entity, args);
                break;
            case "getNameAndUUID":
                sendLine(entity.getName() + "," + entity.getUniqueID());
                break;
            default:
                unknownCommand();
                break;
        }
    }

    private void entitySetPos(Entity e, String[] args) {
        BlockPos pos = parseRelativeBlockLocation(args[0], args[1], args[2]);
        //e.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
        //setPositionAndUpdate is gone, no one mentions it
        e.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    private void entitySetDirection(Entity e, String[] args) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        double z = Double.parseDouble(args[2]);
        entitySetDirection(e, x, y, z);
    }

    private void entitySetDirection(Entity e, double x, double y, double z) {
        double xz = Math.sqrt(x * x + z * z);

        if (xz >= TOO_SMALL) {
            float yaw = (float) (Math.atan2(-x, z) * 180 / Math.PI);
            e.setRotationYawHead(yaw);
            e.rotationYaw = yaw;
        }

        if (x * x + y * y + z * z >= TOO_SMALL * TOO_SMALL)
            e.rotationPitch = (float) (Math.atan2(-y, xz) * 180 / Math.PI);
    }

    private void fail(String string) {
        System.err.println("Error: "+string);
        sendLine("Fail");
    }

    private float normalizeAngle(float angle) {
        angle = angle % 360;
        if (angle <= -180)
            angle += 360;
        if (angle > 180)
            angle -= 360;
        return angle;
    }

    private void entityGetDirection(Entity e) {
        //sendLine(e.getLookVec());
        double pitch = e.rotationPitch * Math.PI / 180.;
        double yaw = e.rotationYaw * Math.PI / 180.;
        double x = Math.cos(-pitch) * Math.sin(-yaw);
        double z = Math.cos(-pitch) * Math.cos(-yaw);
        double y = Math.sin(-pitch);
        sendLine(new Vec3d(x,y,z));
    }

    private static int trunc(double x) {
        return (int)Math.floor(x);
    }

    protected void sendLine(BlockPos pos) {
        sendLine(""+pos.getX()+","+pos.getY()+","+pos.getZ());
    }

    private void sendLine(double x) {
        sendLine(Double.toString(x));
    }

    private void sendLine(int x) {
        sendLine(Integer.toString(x));
    }

    private void sendLine(Vec3d v) {
        sendLine(""+v.getX()+","+v.getY()+","+v.getZ());
    }

    private void sendLine(String string) {
        writer.print(string+"\n");
        writer.flush();
    }

    private BlockPos parseRelativeBlockLocation(String xstr, String ystr, String zstr){
        int x = (int) Double.parseDouble(xstr);
        int y = (int) Double.parseDouble(ystr);
        int z = (int) Double.parseDouble(zstr);
        BlockPos spawnPos = world.getSpawnPoint();
        return new BlockPos(spawnPos.getX() + x, spawnPos.getY() + y, spawnPos.getZ() + z);
    }

    private Vec3d encodeVec3(Vec3d pos) {
        BlockPos spawnPos = world.getSpawnPoint();
        return new Vec3d(pos.getX()-spawnPos.getX(), pos.getY()-spawnPos.getY(),
                pos.getZ()-spawnPos.getZ());
    }
    private BlockPos decodeLocation(int x, int y, int z) {
        BlockPos spawnPos = world.getSpawnPoint();
        return new BlockPos(spawnPos.getX() + x, spawnPos.getY() + y, spawnPos.getZ() + z);
    }
}
