package org.wensheng.juicyraspberrypie;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ApiHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	protected static final float TOO_SMALL = (float) 1e-9;
	private PrintWriter writer = null;
	private ServerWorld world = null;
	private PlayerEntity player = null;
	private ArrayDeque<String> inQueue = new ArrayDeque<String>();
	private final List<String> queuedCommands = Arrays.asList(new String[]{"world.setBlock", "world.setBlocks"});

	public ApiHandler() {
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


	protected void handleCommand(String cmd, String[] args)
			throws InputMismatchException, NoSuchElementException, IndexOutOfBoundsException {

		if(cmd.equals("chat.post")){
			sendLine("NOT YET");
		}
		else if (cmd.equals("world.getBlock")) {
			BlockPos pos = parseRelativeBlockLocation(args[0], args[1], args[2]);
			BlockState bs = world.getBlockState(pos);
			String name = bs.getBlock().toString();
			if(name.startsWith("Block{minecraft:")){
				// make it consistent with Bukkit reply
				name = name.substring(16, name.length()-1).toUpperCase();
			}
			sendLine(name);
		} else if (cmd.equals("world.getBlockWithData")) {
			sendLine("NOT YET");
		} else if (cmd.equals("world.setBlock")) {
			BlockPos pos = parseRelativeBlockLocation(args[0], args[1], args[2]);
			BlockState bs = blockStateFromName(args[3]);
			int facing = args.length > 4 ? Integer.parseInt(args[4]) : 0;
			world.setBlockState(pos, bs, 2);
			sendLine("OK");
		} else if (cmd.equals("world.setBlocks")) {
			BlockPos pos1 = parseRelativeBlockLocation(args[0], args[1], args[2]);
			BlockPos pos2 = parseRelativeBlockLocation(args[3], args[4], args[5]);
			BlockState bs = blockStateFromName(args[6]);
			int facing = args.length > 7 ? Integer.parseInt(args[4]) : 0;
			setCuboid(world, pos1, pos2, bs);
			sendLine("OK");
		} else if (cmd.equals("world.getPlayerIds")) {
			sendLine("NOT YET");
		} else if (cmd.equals("world.spawnEntity")) {
			BlockPos pos = parseRelativeBlockLocation(args[0], args[1], args[2]);
			EntityType et = entityTypeFromName(args[3]);
			Entity e = et.create(world);
			e.setPosition(pos.getX(), pos.getY(), pos.getZ());
			world.addEntity(e);
			sendLine(e.getEntityId());
		} else if (cmd.equals("world.removeEntity")){
			Entity e = world.getEntityByID(Integer.parseInt(args[0]));
			removeEntity(e);
		} else if (cmd.startsWith("player.")) {
			entityCommand(player, cmd.substring(7), args);
		} else if (cmd.startsWith("entity.")) {
		    if(args.length == 0){
		    	sendLine("No entity ID");
		    	return;
			}
		    Entity e = world.getEntityByID(Integer.parseInt(args[0]));
		    entityCommand(e, cmd.substring(7), args);
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

	protected void removeEntity(Entity e) {
		world.removeEntity(e);
		sendLine("removed");
	}

    protected void unknownCommand() {
        fail("unknown command");
    }

	protected void entityCommand(Entity entity, String cmd, String[] args) {
		if (cmd.equals("getPos")) {
			entityGetPos(entity);
		}
		else if (cmd.equals("getTile")) {
			entityGetTile(entity);
		}
		else if (cmd.equals("getRotation")) {
			entityGetRotation(entity);
		}
		else if (cmd.equals("setRotation")) {
			entitySetRotation(entity, Float.parseFloat(args[1]));
		}
		else if (cmd.equals("getPitch")) {
			entityGetPitch(entity);
		}
		else if (cmd.equals("setPitch")) {
			entitySetPitch(entity, Float.parseFloat(args[1]));
		}
		else if (cmd.equals("getDirection")) {
			entityGetDirection(entity);
		}
		else if (cmd.equals("setDirection")) {
			entitySetDirection(entity, args);
		}
		else if (cmd.equals("setPos")) {
			entitySetPos(entity, args);
		}
		else if (cmd.equals("getNameAndUUID")) {
			entityGetNameAndUUID(entity);
		}
        else {
            unknownCommand();
        }
	}

	protected void entitySetDirection(Entity e, String[] args) {
		double x = Double.parseDouble(args[0]);
		double y = Double.parseDouble(args[1]);
		double z = Double.parseDouble(args[2]);

		if (e != null)
			entitySetDirection(e, x, y, z);

		if (e != null)
			entitySetDirection(e, x, y, z);
	}

	protected void entityGetNameAndUUID(Entity e) {
        if (e != null)
            sendLine(e.getName()+","+e.getUniqueID());
	}

	protected void entitySetDirection(Entity e, double x, double y, double z) {
		double xz = Math.sqrt(x * x + z * z);

		if (xz >= TOO_SMALL) {
			float yaw = (float) (Math.atan2(-x, z) * 180 / Math.PI);
			e.setRotationYawHead(yaw);
			e.rotationYaw = yaw;
		}

		if (x * x + y * y + z * z >= TOO_SMALL * TOO_SMALL)
			e.rotationPitch = (float) (Math.atan2(-y, xz) * 180 / Math.PI);
	}

	protected void fail(String string) {
		System.err.println("Error: "+string);
		sendLine("Fail");
	}
	
	protected void entitySetPitch(Entity e, float angle) {
		if (e != null)
			e.rotationPitch = angle;
	}

	protected void entitySetRotation(Entity e, float angle) {
		if (e != null) {
			e.rotationYaw = angle;
			e.setRotationYawHead(angle);
		}
	}
	
	protected void entityGetRotation(Entity e) {
		if (e != null)
			sendLine(normalizeAngle(e.rotationYaw));
	}

	protected float normalizeAngle(float angle) {
		angle = angle % 360;
		if (angle <= -180)
			angle += 360;
		if (angle > 180)
			angle -= 360;
		return angle;
	}

	protected void entityGetPitch(Entity e) {
		if (e != null)
			sendLine(normalizeAngle(e.rotationPitch));
	}

	protected void entityGetDirection(Entity e) {
		if (e != null) {
			//sendLine(e.getLookVec());
			double pitch = e.rotationPitch * Math.PI / 180.;
			double yaw = e.rotationYaw * Math.PI / 180.;
			double x = Math.cos(-pitch) * Math.sin(-yaw);
			double z = Math.cos(-pitch) * Math.cos(-yaw);
			double y = Math.sin(-pitch);
			sendLine(new Vec3d(x,y,z));
		}
	}

	protected void entitySetPos(Entity e, String[] args) {
		int x = (int) Double.parseDouble(args[0]);
		int y = (int) Double.parseDouble(args[1]);
		int z = (int) Double.parseDouble(args[2]);
		BlockPos pos = decodeLocation(x, y, z);
		e.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
		sendLine("setPos");
	}


	protected static int trunc(double x) {
		return (int)Math.floor(x);
	}

	protected void entityGetTile(Entity e) {
		if (e != null) {
			World w = e.getEntityWorld();
			Vec3d pos0 = e.getPositionVector();

			while (w != e.getEntityWorld()) {
				// Rare concurrency issue: entity switched worlds between getting w and pos0.
				// To be somewhat safe, let's sleep for approximately a server tick and get
				// everything again. 
				try { Thread.sleep(50); } catch(Exception exc) {}
				w = e.getEntityWorld();
				pos0 = e.getPositionVector();
			}
			
			Vec3d pos = encodeVec3(e.getPositionVector());
			sendLine(""+trunc(pos.getX())+","+trunc(pos.getY())+","+trunc(pos.getZ()));
		}
	}

	protected void sendLine(BlockPos pos) {
		sendLine(""+pos.getX()+","+pos.getY()+","+pos.getZ());
	}

	protected void entityGetPos(Entity e) {
		if(e == null){
			fail("Could not get entity position");
			return;
		}
		World w = e.getEntityWorld();
		Vec3d pos0 = e.getPositionVector();
		Vec3d pos = encodeVec3(pos0);
		sendLine(pos);
	}

	protected void sendLine(double x) {
		sendLine(Double.toString(x));
	}

	protected void sendLine(int x) {
		sendLine(Integer.toString(x));
	}

	protected void sendLine(Vec3d v) {
		sendLine(""+v.getX()+","+v.getY()+","+v.getZ());
	}

	protected void sendLine(String string) {
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
