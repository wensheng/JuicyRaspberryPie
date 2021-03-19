package org.wensheng.juicyraspberrypie;


//import net.minecraft.block.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
    private ArrayDeque<PlayerInteractEvent> interactEventQueue = new ArrayDeque<>();
    private ArrayDeque<ClientChatReceivedEvent> chatPostedQueue = new ArrayDeque<>();
    private ArrayDeque<ProjectileImpactEvent> projectileHitQueue = new ArrayDeque<>();
    private final List<String> queuedCommands = Arrays.asList("world.setBlock", "world.setBlocks");

    ApiHandler() {
        // Only worry about single player world for now
        final MinecraftServer ms = ServerLifecycleHooks.getCurrentServer();
        //this.world = DimensionManager.getWorld(ms, DimensionType.OVERWORLD, false,false);
        this.world = ms.overworld();
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

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event){
        ItemStack itemStack = event.getItemStack();
        if(itemStack.getItem() instanceof SwordItem){
            interactEventQueue.add(event);
        }
    }

    @SubscribeEvent
    public void onChatPosted(ClientChatReceivedEvent event) {
        chatPostedQueue.add(event);
    }

    @SubscribeEvent
    public void onProjectileHit(ProjectileImpactEvent.Arrow event) {
        // what about fireball?
        projectileHitQueue.add(event);
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
            throws NoSuchElementException, IndexOutOfBoundsException {

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
            Block block = blockFromName(args[3]);
            BlockState bs;
            if(args.length > 4){
                bs = block.defaultBlockState();
                bs = bs.setValue(DirectionalBlock.FACING, getFacing(Integer.parseInt(args[4])));
            }else{
                bs = block.defaultBlockState();
            }
            world.setBlock(pos, bs, 2);
        } else if (cmd.equals("world.getBlocks")) {
            BlockPos pos1 = parseRelativeBlockLocation(args[0], args[1], args[2]);
            BlockPos pos2 = parseRelativeBlockLocation(args[3], args[4], args[5]);
            sendLine(getBlocks(pos1, pos2));
        } else if (cmd.equals("world.setBlocks")) {
            BlockPos pos1 = parseRelativeBlockLocation(args[0], args[1], args[2]);
            BlockPos pos2 = parseRelativeBlockLocation(args[3], args[4], args[5]);

            BlockState bs = blockFromName(args[6]).defaultBlockState();
            //int facing = args.length > 7 ? Integer.parseInt(args[7]) : 0;
            setCuboid(world, pos1, pos2, bs);
        } else if (cmd.equals("world.getPlayerId")) {
            sendLine(player.getUUID().toString());
        } else if (cmd.equals("world.getPlayerIds")) {
            // single-player world, this command doesn't make sense
            sendLine(player.getUUID().toString());
        } else if (cmd.equals("world.spawnEntity")) {
            BlockPos pos = parseRelativeBlockLocation(args[0], args[1], args[2]);
            EntityType et = entityTypeFromName(args[3]);
            Entity e = et.create(world);
            if(e != null) {
                e.setPos(pos.getX(), pos.getY(), pos.getZ());
                world.addFreshEntity(e);
                sendLine(e.getStringUUID());
            }else{
                sendLine("Error spawning entity");
            }
        } else if (cmd.equals("world.spawnParticle")) {
            BlockPos pos = parseRelativeBlockLocation(args[0], args[1], args[2]);
            BasicParticleType pt = particleTypeFromName(args[3]);
            int count;
            if(args.length > 4) {
                count = Integer.parseInt(args[4]);
            }else{
                count = 10;
            }
            float speed;
            if(args.length > 5) {
                speed = Float.parseFloat(args[5]);
            }else{
                speed = 1.0f;
            }
            world.addParticle(pt, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
        } else if (cmd.equals("world.getNearbyEntities")) {
            BlockPos pos = parseRelativeBlockLocation(args[0], args[1], args[2]);
            int nearby_distance = 10;
            if(args.length > 3){
                nearby_distance = Integer.parseInt(args[3]);
            }
            BlockPos pos1 = new BlockPos(pos.getX() - nearby_distance, pos.getY() - 5, pos.getZ() - nearby_distance);
            BlockPos pos2 = new BlockPos(pos.getX() + nearby_distance, pos.getY() + 5, pos.getZ() + nearby_distance);
            AxisAlignedBB aabb = new AxisAlignedBB(pos1, pos2);
            //List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);
            List<LivingEntity> entities = world.getNearbyEntities(LivingEntity.class, EntityPredicate.DEFAULT, player, aabb);
            StringBuilder sb = new StringBuilder();
            for(LivingEntity entity:entities){
                sb.append(entity.getClass().toString()).append(":").append(entity.getStringUUID()).append(",");
            }
            sb.setLength(sb.length()-1);
            sendLine(sb.toString());
        } else if (cmd.equals("world.getHeight")) {
            int x = Integer.parseInt(args[0]);
            int z = Integer.parseInt(args[1]);
            int highest = -32;
            BlockPos pos = new BlockPos(x, 0, z);
            for(int y = 255; y > highest; y--){
                if(!world.getBlockState(pos.above(y)).getBlock().equals(Blocks.AIR)){
                    highest = y;
                    break;
                }
            }
            sendLine(highest);
        } else if (cmd.equals("world.setSign")) {
            BlockPos pos = parseRelativeBlockLocation(args[0], args[1], args[2]);
            Block block = blockFromName(args[3]);
            if(!block.toString().contains("_sign")){
                block = blockFromName("birch_sign");
            }
            int facing = Integer.parseInt(args[4]);
            BlockState bs;
            if(block.toString().contains("_wall_sign")){
                bs = block.defaultBlockState();
                bs = bs.setValue(WallSignBlock.FACING, getFacing(facing));
            }else {
                bs = block.defaultBlockState();
                bs = bs.setValue(StandingSignBlock.ROTATION, facing);
            }
            world.setBlock(pos, bs, 3);
            if(bs.hasTileEntity()) {
                SignTileEntity tile = (SignTileEntity) bs.createTileEntity(world);
                if(tile != null) {
                    for (int i = 0; i < 4 && i < (args.length - 5); i++) {
                        tile.setMessage(i, new StringTextComponent(args[i + 5]));
                    }
                    tile.setPosition(pos);
                    // TODO:
                    //tile.markDirty();
                    world.addBlockEntity(tile);
                    // this is beyond ridiculous
                    //IChunk chunk = world.getChunk(pos);
                    //chunk.addTileEntity(pos, tile);
                }
            }
        } else if(cmd.equals("chat.post")){
            StringBuilder sb = new StringBuilder();
            for(String arg: args){
                sb.append(arg).append(",");
            }
            // because single player, send to herself
            player.sendMessage(new StringTextComponent(sb.toString()), player.getUUID());
        } else if (cmd.startsWith("events.block.hits")) {
            StringBuilder sb = new StringBuilder();
            PlayerInteractEvent event;
            while((event = interactEventQueue.poll())!= null){
                BlockPos pos = event.getPos();
                BlockState bs = world.getBlockState(pos);
                sb.append(blockPosToRelative(pos));
                sb.append(",");
                sb.append(event.getFace().getName());
                sb.append(",");
                sb.append(event.getPlayer().getUUID());
                sb.append("|");
            }
            if(sb.length()>0) {
                sb.setLength(sb.length() - 1);
            }
            sendLine(sb.toString());
        } else if (cmd.startsWith("events.projectile.hists")) {
            StringBuilder sb = new StringBuilder();
            ProjectileImpactEvent event;
            while((event = projectileHitQueue.poll())!= null){
                ArrowEntity arrow = (ArrowEntity) event.getEntity();
                BlockPos pos = arrow.blockPosition();
                sb.append(blockPosToRelative(pos)).append(",");
                //Entity shooter = arrow.getShooter();
                Entity shooter = arrow.getOwner();
                if(shooter != null) {
                    sb.append(shooter.getStringUUID()).append(",");
                }else{
                    sb.append("Nobody");
                }
                sb.append(",");
                //TODO: hit what? entity or block
                sb.append(event.getRayTraceResult().getType().name());
                sb.append("|");
            }
            if(sb.length()>0) {
                sb.setLength(sb.length() - 1);
            }
            sendLine(sb.toString());
        } else if (cmd.startsWith("events.chat.posts")) {
            StringBuilder sb = new StringBuilder();
            ClientChatReceivedEvent event;
            while((event = chatPostedQueue.poll())!= null){
                sb.append("0").append(",");  // dummy player id for single player
                sb.append(event.getMessage().getString());
                sb.append("|");
            }
            if(sb.length()>0) {
                sb.setLength(sb.length() - 1);
            }
            sendLine(sb.toString());
        } else if (cmd.startsWith("events.clear")) {
            interactEventQueue.clear();
            chatPostedQueue.clear();
            projectileHitQueue.clear();
        } else if (cmd.startsWith("player.")) {
            entityCommand(player, cmd.substring(7), args);
        } else if (cmd.startsWith("entity.")) {
            if(args.length == 0){
                sendLine("No entity ID");
                return;
            }
            Entity e = world.getEntity(UUID.fromString(args[0]));
            String[] newargs = Arrays.copyOfRange(args, 1, args.length);
            entityCommand(e, cmd.substring(7), newargs);
        }
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

    private Block blockFromName(String blockName){
        String name;
        if(!blockName.contains(":")) {
            name = "minecraft:" + blockName.toLowerCase();
        }else{
            name = blockName.toLowerCase();
        }
        if(JuicyRaspberryPieMod.BLOCK_NAMES.contains(name)){
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
        }
        return Blocks.SANDSTONE;
    }

    private Direction getFacing(int facing) {
        // Note this is opposite of Bukkit facing
        Direction direction;
        switch (facing){
            case 1:
                direction = Direction.EAST;
                break;
            case 2:
                direction = Direction.SOUTH;
                break;
            case 3:
                direction = Direction.WEST;
                break;
            default:
                direction = Direction.NORTH;
        }
        return direction;

    }

    private EntityType entityTypeFromName(String entityName) {
        String name;
        if(!entityName.contains(":")) {
            name = "minecraft:" + entityName.toLowerCase();
        }else{
            name = entityName.toLowerCase();
        }
        if(JuicyRaspberryPieMod.ENTITY_NAMES.contains(name)){
            return ForgeRegistries.ENTITIES.getValue(new ResourceLocation(name));
        }
        return EntityType.ZOMBIE;
    }

    private BasicParticleType particleTypeFromName(String particleName) {
        String name;
        if(!particleName.contains(":")) {
            name = "minecraft:" + particleName.toLowerCase();
        }else{
            name = particleName.toLowerCase();
        }
        if(JuicyRaspberryPieMod.PARTICLE_NAMES.contains(name)){
            return (BasicParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(name));
        }
        return ParticleTypes.EXPLOSION;
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
                    world.setBlock(pos, bs, 2);
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
                sendLine(encodeVec3(entity.position()));
                break;
            case "setPos":
                entitySetPos(entity, args);
                break;
            case "getTile": {
                Vector3d pos = encodeVec3(entity.position());
                sendLine("" + trunc(pos.x()) + "," + trunc(pos.y()) + "," + trunc(pos.z()));
                break;
            }
            case "setTile":
                // TODO: actually set tile pos
                entitySetPos(entity, args);
                break;
            case "getRotation":
                Vector2f vec = entity.getRotationVector();
                sendLine(vec.toString());
                break;
            case "setRotation": {
                float angle = Float.parseFloat(args[0]);
                //entity.rotationYaw = angle;
                //entity.setRotationYawHead(angle);
                // TODO:
                break;
            }
            case "getPitch":
                //sendLine(normalizeAngle(entity.rotationPitch));
                // TODO:
                sendLine(entity.getRotationVector().toString());
                break;
            case "setPitch":
                //entity.rotationPitch = Float.parseFloat(args[0]);
                // TODO:
                break;
            case "getDirection":
                entityGetDirection(entity);
                break;
            case "setDirection":
                entitySetDirection(entity, args);
                break;
            case "getNameAndUUID":
                sendLine(entity.getName() + "," + entity.getStringUUID());
                break;
            case "remove": {
                if(!(entity instanceof PlayerEntity)){
                    world.removeEntity(entity, false);
                }
                break;
            }
            default:
                unknownCommand();
                break;
        }
    }

    private void entitySetPos(Entity e, String[] args) {
        BlockPos pos = parseRelativeBlockLocation(args[0], args[1], args[2]);
        //e.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
        //setPositionAndUpdate is gone, no one mentions it
        e.setPos(pos.getX(), pos.getY(), pos.getZ());
    }

    private void entitySetDirection(Entity e, String[] args) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        double z = Double.parseDouble(args[2]);
        //entitySetDirection(e, x, y, z);
        // TODO:
    }

    /*
    private void entitySetDirection(Entity e, double x, double y, double z) {
        double xz = Math.sqrt(x * x + z * z);

        if (xz >= TOO_SMALL) {
            float yaw = (float) (Math.atan2(-x, z) * 180 / Math.PI);
            e.setRotationYawHead(yaw);
            e.rotationYaw = yaw;
        }

        if (x * x + y * y + z * z >= TOO_SMALL * TOO_SMALL)
            e.rotationPitch = (float) (Math.atan2(-y, xz) * 180 / Math.PI);
    }*/

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
        /*
        double pitch = e.rotationPitch * Math.PI / 180.;
        double yaw = e.rotationYaw * Math.PI / 180.;
        double x = Math.cos(-pitch) * Math.sin(-yaw);
        double z = Math.cos(-pitch) * Math.cos(-yaw);
        double y = Math.sin(-pitch);
        sendLine(new Vector3d(x,y,z));
        */
        Direction d = e.getDirection();
        sendLine(new Vector3d(d.getStepX(), d.getStepY(), d.getStepZ()));
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

    private void sendLine(Vector3d v) {
        sendLine(""+v.x()+","+v.y()+","+v.z());
    }

    private void sendLine(String string) {
        writer.print(string+"\n");
        writer.flush();
    }

    private BlockPos parseRelativeBlockLocation(String xstr, String ystr, String zstr){
        int x = (int) Double.parseDouble(xstr);
        int y = (int) Double.parseDouble(ystr);
        int z = (int) Double.parseDouble(zstr);
        BlockPos spawnPos = world.getSharedSpawnPos();
        return new BlockPos(spawnPos.getX() + x, spawnPos.getY() + y, spawnPos.getZ() + z);
    }

    private Vector3d encodeVec3(Vector3d pos) {
        BlockPos spawnPos = world.getSharedSpawnPos();
        return new Vector3d(pos.x()-spawnPos.getX(), pos.y()-spawnPos.getY(),
                pos.z()-spawnPos.getZ());
    }
    private BlockPos decodeLocation(int x, int y, int z) {
        BlockPos spawnPos = world.getSharedSpawnPos();
        return new BlockPos(spawnPos.getX() + x, spawnPos.getY() + y, spawnPos.getZ() + z);
    }

    private String blockPosToRelative(BlockPos pos) {
        BlockPos spawnPos = world.getSharedSpawnPos();
        return (pos.getX() - spawnPos.getX()) + "," + (pos.getY() - spawnPos.getY()) + "," +
                (pos.getZ() - spawnPos.getZ());
    }
}
