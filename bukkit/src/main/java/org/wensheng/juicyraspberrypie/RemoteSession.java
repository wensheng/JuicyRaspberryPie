package org.wensheng.juicyraspberrypie;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.UUID;

class RemoteSession {
    boolean pendingRemoval = false;
    private Location origin;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Thread inThread;
    private Thread outThread;
    private ArrayDeque<String> inQueue = new ArrayDeque<>();
    private final ArrayDeque<String> outQueue = new ArrayDeque<>();
    private boolean running = true;
    private JuicyRaspberryPie plugin;
    private ArrayDeque<PlayerInteractEvent> interactEventQueue = new ArrayDeque<>();
    private ArrayDeque<AsyncPlayerChatEvent> chatPostedQueue = new ArrayDeque<>();
    private ArrayDeque<ProjectileHitEvent> projectileHitQueue = new ArrayDeque<>();
    private int maxCommandsPerTick = 9000;
    private Player attachedPlayer = null;

    RemoteSession(JuicyRaspberryPie plugin, Socket socket) throws IOException {
        this.socket = socket;
        this.plugin = plugin;
        init();
        setPlayerAndOrigin();
    }

    private void init() throws IOException {
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
        socket.setTrafficClass(0x10);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        startThreads();
        plugin.logger.info("Opened connection to" + socket.getRemoteSocketAddress() + ".");

    }

    private void startThreads() {
        inThread = new Thread(new InputThread());
        inThread.start();
        outThread = new Thread(new OutputThread());
        outThread.start();
    }

    Socket getSocket() {
        return socket;
    }

    void queuePlayerInteractEvent(PlayerInteractEvent event) {
        interactEventQueue.add(event);
    }

    void queueChatPostedEvent(AsyncPlayerChatEvent event) {
        chatPostedQueue.add(event);
    }

    void handlePlayerQuitEvent(){
        // one player quit should not affect other sessions, so no
        //setPlayerAndOrigin();
    }

    private void setPlayerAndOrigin(){
        int world_dimension = 0;
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if(players.size() > 0) {
            /*
            Player firstPlayer, opPlayer = null;
            firstPlayer = players.iterator().next();
            for (Player player : players) {
                if (player.isOp()) {
                    opPlayer = player;
                }
            }
            if(opPlayer != null){
                attachedPlayer = opPlayer;
            }else{
                attachedPlayer = firstPlayer;
            }
            */
            attachedPlayer = players.iterator().next();
            world_dimension = attachedPlayer.getWorld().getEnvironment().ordinal();
        }
        origin = plugin.getServer().getWorlds().get(world_dimension).getSpawnLocation();
    }

    private boolean setPlayerAndOrigin(String playerName){
        for(Player p: Bukkit.getOnlinePlayers())
            if(playerName.equalsIgnoreCase(p.getName())){
                attachedPlayer = p;
                int d = attachedPlayer.getWorld().getEnvironment().ordinal();
                origin = plugin.getServer().getWorlds().get(d).getSpawnLocation();
                return true;
        }
        return false;
    }

    void queueProjectileHitEvent(ProjectileHitEvent event){
        Arrow arrow = (Arrow) event.getEntity();
        if(arrow.getShooter() instanceof Player){
            projectileHitQueue.add(event);
        }
    }

    /** called from the server main thread */
    void tick() {
        int processedCount = 0;
        String message;
        while ((message = inQueue.poll()) != null) {
            handleLine(message);
            processedCount++;
            if (processedCount >= maxCommandsPerTick) {
                plugin.logger.warning("Over " + maxCommandsPerTick +
                    " commands were queued - deferring " + inQueue.size() + " to next tick");
                break;
            }
        }

        if (!running && inQueue.size() <= 0) {
            pendingRemoval = true;
        }
    }

    private void handleLine(String line) {
        if(!line.contains("(") || !line.contains(")")){
            send("Wrong format");
            return;
        }
        line = line.trim();
        String methodName = line.substring(0, line.indexOf("("));
        String[] args = line.substring(line.indexOf("(") + 1, line.length() - 1).split(",\\s*");
        if(args.length == 1 && args[0].equals("")){
            args = new String[0];
        }
        handleCommand(methodName, args);
    }

    private void handleCommand(String c, String[] args) {
        
        try {
            World world = origin.getWorld();
            if(world == null){
                send("Could not get world");
                return;
            }
            
            if (c.equals("world.getBlock")) {
                Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
                send(world.getBlockAt(loc).getType().name());
            } else if (c.equals("world.getBlocks")) {
                Location loc1 = parseRelativeBlockLocation(args[0], args[1], args[2]);
                Location loc2 = parseRelativeBlockLocation(args[3], args[4], args[5]);
                send(getBlocks(loc1, loc2));
            } else if (c.equals("world.getBlockWithData")) {
                Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
                Block block = world.getBlockAt(loc);
                send(block.getType().name() + "," + block.getBlockData());
            } else if (c.equals("world.setBlock")) {
                Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
                Material material = Material.matchMaterial(args[3]);
                if(material == null){
                    material = Material.valueOf("SANDSTONE");
                }
                int facing = args.length > 4? Integer.parseInt(args[4]): 0;
                BlockFace blockFace = BlockFace.values()[facing];
                updateBlock(world, loc, material, blockFace);
            } else if (c.equals("world.setBlocks")) {
                Location loc1 = parseRelativeBlockLocation(args[0], args[1], args[2]);
                Location loc2 = parseRelativeBlockLocation(args[3], args[4], args[5]);
                Material material = Material.matchMaterial(args[6]);
                if(material == null){
                    material = Material.valueOf("SANDSTONE");
                }
                int facing = args.length > 7? Integer.parseInt(args[7]): 0;
                BlockFace blockFace = BlockFace.values()[facing];
                setCuboid(loc1, loc2, material, blockFace);
            } else if (c.equals("world.getPlayerIds")) {
                StringBuilder bdr = new StringBuilder();
                for (Player p: plugin.getServer().getOnlinePlayers()) {
                    bdr.append(p.getName());
                    bdr.append(":");
                    bdr.append(p.getUniqueId());
                    bdr.append("|");
                }
                bdr.deleteCharAt(bdr.length()-1);
                send(bdr.toString());
            } else if (c.equals("world.getPlayerId")) {
                Player p = getNamedPlayer(args[0]);
                if (p != null) {
                    send(p.getUniqueId());
                } else {
                    plugin.logger.info("Player [" + args[0] + "] not found.");
                    send("Fail");
                }
            } else if (c.equals("world.setSign")) {
                // in 1.14
                //ACACIA BIRCH OAK DARK_OAK JUNGLE SPRUCE -LEGACY- +_SIGN +_WALL_SIGN
                // no ACACIA_WALL_SIGN
                // in 1.13
                // SIGN WALL_SIGN, LEGACY
                // note in 1.14.4 LEGACY is deprecated
                Material material = Material.matchMaterial(args[3]);
                if(material == null){
                    material = Material.BIRCH_SIGN;
                }
                if (!material.toString().contains("_SIGN")){
                    send("material must be sign");
                    return;
                }

                Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
                Block thisBlock = world.getBlockAt(loc);
                thisBlock.setType(material);
                plugin.logger.info(material.toString());

                int facing = args.length > 4? Integer.parseInt(args[4]): 0;
                if(facing >= 4 || facing < 0){
                    facing = 0;
                }
                BlockFace blockFace = BlockFace.values()[facing];
                BlockData blockData = thisBlock.getBlockData();
                if(blockData instanceof org.bukkit.block.data.type.WallSign){
                    org.bukkit.block.data.type.WallSign s = (org.bukkit.block.data.type.WallSign) blockData;
                    s.setFacing(blockFace);
                    thisBlock.setBlockData(s);
                }else{
                    org.bukkit.block.data.type.Sign s = (org.bukkit.block.data.type.Sign) blockData;
                    s.setRotation(blockFace);
                    thisBlock.setBlockData(s);
                }

                Sign sign = (Sign) thisBlock.getState();
                for (int i = 5; i - 5 < 4 && i < args.length; i++) {
                    sign.setLine(i - 5, args[i]);
                }
                sign.update();

            } else if(c.equals("world.getNearbyEntities")) {
                Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
                double nearby_distance = 10.0;
                if(args.length > 3){
                    nearby_distance = Double.parseDouble(args[3]);
                }
                Collection<Entity> nearbyEntities = world.getNearbyEntities(loc, nearby_distance, 5.0, nearby_distance);
                StringBuilder sb = new StringBuilder();
                for(Entity e: nearbyEntities){
                    sb.append(e.getName()).append(":").append(e.getUniqueId()).append(",");
                }
                if(sb.length()>1) {
                    sb.setLength(sb.length() - 1);
                }
                send(sb.toString());
            } else if (c.equals("world.spawnEntity")) {
                 Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
                 EntityType entityType;
                 try{
                     entityType = EntityType.valueOf(args[3].toUpperCase());
                 }catch(Exception exc){
                     entityType = EntityType.valueOf("COW");
                 }
                 Entity entity = world.spawnEntity(loc, entityType);
                 send(entity.getUniqueId());
            } else if (c.equals("world.spawnParticle")) {
                Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
                Particle particle;
                try{
                    particle = Particle.valueOf(args[3].toUpperCase());
                }catch(Exception exc){
                    particle = Particle.valueOf("EXPLOSION_NORMAL");
                }
                int count;
                if(args.length > 4){
                    count = Integer.parseInt(args[4]);
                }else{
                    count = 10;
                }
                double speed;
                if(args.length > 5){
                    speed = Double.parseDouble(args[5]);
                }else{
                    speed = 1.0;
                }
                world.spawnParticle(particle, loc, count, 0, 0, 0, speed);
            } else if (c.equals("world.getHeight")) {
                send(world.getHighestBlockYAt(parseRelativeBlockLocation(args[0], "0", args[1])) - origin.getBlockY());
            } else if (c.equals("chat.post")) {
                StringBuilder sb = new StringBuilder();
                for (String arg : args) {
                    sb.append(arg).append(",");
                }
                sb.setLength(sb.length() - 1);
                plugin.getServer().broadcastMessage(sb.toString());
            } else if (c.equals("events.clear")) {
                interactEventQueue.clear();
                chatPostedQueue.clear();
                projectileHitQueue.clear();
            } else if (c.equals("events.block.hits")) {
                StringBuilder b = new StringBuilder();
                PlayerInteractEvent event;
                while ((event = interactEventQueue.poll()) != null) {
                    Block block = event.getClickedBlock();
                    if(block != null) {
                        Location loc = block.getLocation();
                        b.append(blockLocationToRelative(loc));
                        b.append(",");
                        b.append(event.getBlockFace().name());
                        b.append(",");
                        b.append(event.getPlayer().getUniqueId());
                    }else{
                        b.append("0,0,0,Fail,0");
                    }
                    if (interactEventQueue.size() > 0) {
                        b.append("|");
                    }
                }
                send(b.toString());
            } else if (c.equals("events.projectile.hits")) {
                StringBuilder b = new StringBuilder();
                 ProjectileHitEvent event;
                while ((event = projectileHitQueue.poll()) != null) {
                    Arrow arrow = (Arrow) event.getEntity();
                    Player player = (Player) arrow.getShooter();
                    if(player != null) {
                        Block block = arrow.getLocation().getBlock();
                        Location loc = block.getLocation();
                        b.append(blockLocationToRelative(loc));
                        b.append(",");
                        b.append(player.getUniqueId());
                        b.append(",");
                        Entity hitEntity = event.getHitEntity();
                        if (hitEntity != null) {
                            b.append(hitEntity.getUniqueId());
                        }
                    }else{
                        b.append("0,0,0,Fail,0");
                    }
                    if (projectileHitQueue.size() > 0) {
                        b.append("|");
                    }
                }
                send(b.toString());
            } else if (c.equals("events.chat.posts")) {
                StringBuilder b = new StringBuilder();
                AsyncPlayerChatEvent event;
                while ((event = chatPostedQueue.poll()) != null) {
                    b.append(event.getPlayer().getEntityId());
                    b.append(",");
                    b.append(event.getMessage());
                    if (chatPostedQueue.size() > 0) {
                        b.append("|");
                    }
                }
                //DEBUG
                //System.out.println(b.toString());
                send(b.toString());
            } else if(c.startsWith("player.")){
                handleEntityCommand(c.substring(7), args, true);
            } else if(c.startsWith("entity.")){
                handleEntityCommand(c.substring(7), args, false);
            } else if(c.startsWith("getPlayer")){
                send(attachedPlayer == null ? "(none)" : attachedPlayer.getName());
            } else if(c.startsWith("setPlayer")){
                if(setPlayerAndOrigin(args[0])){
                    send("true");
                }else{
                    send("false");
                }
            } else {
                plugin.getLogger().warning(c + " is not supported.");
                send("Fail");
            }
        } catch (Exception e) {
            
            plugin.logger.warning("Error occured handling command");
            e.printStackTrace();
            send("Fail");
            
        }
    }


    private void handleEntityCommand(String c, String[] args, boolean entityIsPlayer) {
        Entity entity;
        if(entityIsPlayer) {
            if(args.length > 3 || args.length == 2 || (c.startsWith("get") && args.length == 1)) {
                //getX(pid)
                //setX(pid,a1)
                //setX(pid,a1,a2,a3)
                entity = getNamedPlayer(args[0]);
            }else {
                //getX()
                //setX(a1)
                //setX(a1,a2,a3)
                entity = getCurrentPlayer();
            }
        }else{
            entity = plugin.getServer().getEntity(UUID.fromString(args[0]));
        }

        if(entity == null){
            send("No such player or entity");
            return;
        }

        switch (c) {
            case "getTile":
                send(blockLocationToRelative(entity.getLocation()));
                break;
            case "setTile": {
                String x = args[0], y = args[1], z = args[2];
                if (args.length > 3) {
                    x = args[1];
                    y = args[2];
                    z = args[3];
                }
                Location loc = entity.getLocation();
                entity.teleport(parseRelativeBlockLocation(x, y, z, loc.getPitch(), loc.getYaw()));
                break;
            }
            case "getPos":
                send(locationToRelative(entity.getLocation()));
                break;
            case "setPos": {
                String x = args[0], y = args[1], z = args[2];
                if (args.length > 3) {
                    x = args[1];
                    y = args[2];
                    z = args[3];
                }
                Location loc = entity.getLocation();
                entity.teleport(parseRelativeLocation(x, y, z, loc.getPitch(), loc.getYaw()));
                break;
            }
            case "getDirection":
                send(entity.getLocation().getDirection().toString());
                break;
            case "getRotation":
                send(entity.getLocation().getYaw());
                break;
            case "setRotation": {
                String angle = args[0];
                if (args.length > 1) {
                    angle = args[1];
                }
                float yaw = Float.parseFloat(angle);
                Location loc = entity.getLocation();
                loc.setYaw(yaw);
                entity.teleport(loc);
                send("ok");
                break;
            }
            case "getPitch":
                send(entity.getLocation().getPitch());
                break;
            case "setPitch": {
                String angle = args[0];
                if (args.length > 1) {
                    angle = args[1];
                }
                float pitch = Float.parseFloat(angle);
                Location loc = entity.getLocation();
                loc.setPitch(pitch);
                entity.teleport(loc);
                send("ok");
                break;
            }
            case "remove":
                if(!(entity instanceof Player)){
                    entity.remove();
                }
                break;
            default:
                send("No such entity/player command");
        }
    }

    // create a cuboid of lots of blocks
    private void setCuboid(Location pos1, Location pos2, Material blockType, BlockFace blockFace) {
        int minX, maxX, minY, maxY, minZ, maxZ;
        World world = pos1.getWorld();
        minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int y = minY; y <= maxY; ++y) {
                    updateBlock(world, x, y, z, blockType, blockFace);
                }
            }
        }
    }

    // get a cuboid of lots of blocks
    private String getBlocks(Location pos1, Location pos2) {
        World world = pos1.getWorld();
        if(world == null){
            return "Fail";
        }
        StringBuilder blockData = new StringBuilder();
        int minX, maxX, minY, maxY, minZ, maxZ;
        minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int y = minY; y <= maxY; ++y) {
             for (int x = minX; x <= maxX; ++x) {
                 for (int z = minZ; z <= maxZ; ++z) {
                     blockData.append(world.getBlockAt(x, y, z).getType()).append(",");
                }
            }
        }

        return blockData.substring(0, blockData.length() > 0 ? blockData.length() - 1 : 0);    // We don't want last comma
    }

    // updates a block
    private void updateBlock(World world, Location loc, Material blockType, BlockFace blockFace) {
        Block block = world.getBlockAt(loc);
        block.setType(blockType);
        BlockData blockData = block.getBlockData();
        if(blockData instanceof Directional){
            ((Directional) blockData).setFacing(blockFace);
        }
        block.setBlockData(blockData);
    }
    
    private void updateBlock(World world, int x, int y, int z, Material blockType, BlockFace blockFace) {
        Location loc = new Location(world, x, y, z);
        updateBlock(world, loc, blockType, blockFace);
    }

    private Player getNamedPlayer(String name) {
        if (name == null) return null;
        for(Player p: Bukkit.getOnlinePlayers()){
            if(name.equalsIgnoreCase(p.getName())){
                return p;
            }
        }
        return null;
    }

    // gets the current player
    private Player getCurrentPlayer() {
        if(attachedPlayer != null){
            return attachedPlayer;
        }
        setPlayerAndOrigin();
        return attachedPlayer;
    }

    private Location parseRelativeBlockLocation(String xstr, String ystr, String zstr) {
        int x = (int) Double.parseDouble(xstr);
        int y = (int) Double.parseDouble(ystr);
        int z = (int) Double.parseDouble(zstr);
        return new Location(origin.getWorld(), origin.getBlockX() + x, origin.getBlockY() + y, origin.getBlockZ() + z);
    }

    private Location parseRelativeLocation(String xstr, String ystr, String zstr) {
        double x = Double.parseDouble(xstr);
        double y = Double.parseDouble(ystr);
        double z = Double.parseDouble(zstr);
        return new Location(origin.getWorld(), origin.getX() + x, origin.getY() + y, origin.getZ() + z);
    }

    private Location parseRelativeBlockLocation(String xstr, String ystr, String zstr, float pitch, float yaw) {
        Location loc = parseRelativeBlockLocation(xstr, ystr, zstr);
        loc.setPitch(pitch);
        loc.setYaw(yaw);
        return loc;
    }

    private Location parseRelativeLocation(String xstr, String ystr, String zstr, float pitch, float yaw) {
        Location loc = parseRelativeLocation(xstr, ystr, zstr);
        loc.setPitch(pitch);
        loc.setYaw(yaw);
        return loc;
    }
    
    private String blockLocationToRelative(Location loc) {
        return (loc.getBlockX() - origin.getBlockX()) + "," + (loc.getBlockY() - origin.getBlockY()) + "," +
            (loc.getBlockZ() - origin.getBlockZ());
    }

    private String locationToRelative(Location loc) {
        return (loc.getX() - origin.getX()) + "," + (loc.getY() - origin.getY()) + "," +
            (loc.getZ() - origin.getZ());
    }

    private void send(Object a) {
        send(a.toString());
    }

    private void send(String a) {
        if (pendingRemoval) return;
        synchronized(outQueue) {
            outQueue.add(a);
        }
    }

    void close() {
        running = false;
        pendingRemoval = true;

        //wait for threads to stop
        try {
            inThread.join(2000);
            outThread.join(2000);
        }
        catch (InterruptedException e) {
            plugin.logger.warning("Failed to stop in/out thread");
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        plugin.logger.info("Closed connection to" + socket.getRemoteSocketAddress() + ".");
    }

    void kick(String reason) {
        try {
            out.write(reason);
            out.flush();
        } catch (Exception ignored) {
        }
        close();
    }

    /** socket listening thread */
    private class InputThread implements Runnable {
        public void run() {
            plugin.logger.info("Starting input thread");
            while (running) {
                try {
                    String newLine = in.readLine();
                    if (newLine == null) {
                        running = false;
                    } else {
                        inQueue.add(newLine);
                    }
                } catch (Exception e) {
                    if (running) {
                        e.printStackTrace();
                        running = false;
                    }
                }
            }
            //close in buffer
            try {
                in.close();
            } catch (Exception e) {
                plugin.logger.warning("Failed to close in buffer");
                e.printStackTrace();
            }
        }
    }

    private class OutputThread implements Runnable {
        public void run() {
            plugin.logger.info("Starting output thread!");
            while (running) {
                try {
                    String line;
                    while((line = outQueue.poll()) != null) {
                        out.write(line);
                        out.write('\n');
                    }
                    out.flush();
                    Thread.yield();
                    Thread.sleep(1L);
                } catch (Exception e) {
                    // if its running raise an error
                    if (running) {
                        e.printStackTrace();
                        running = false;
                    }
                }
            }
            //close out buffer
            try {
                out.close();
            } catch (Exception e) {
                plugin.logger.warning("Failed to close out buffer");
                e.printStackTrace();
            }
        }
    }
}
