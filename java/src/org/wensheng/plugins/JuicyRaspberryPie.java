package org.wensheng.plugins;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class JuicyRaspberryPie extends JavaPlugin implements Listener{
    public final Logger logger = Logger.getLogger("Minecraft");
    public static final Set<Material> blockBreakDetectionTools = EnumSet.of(
            Material.DIAMOND_SWORD,
            Material.GOLD_SWORD, 
            Material.IRON_SWORD, 
            Material.STONE_SWORD, 
            Material.WOOD_SWORD);

    public ServerListenerThread serverThread;

    public List<RemoteSession> sessions;

    public Player hostPlayer = null;
    
    private void save_resources(){
        File py_init_file = new File(getDataFolder(), "__init__.py");
        if(!py_init_file.exists()){
            this.saveResource("__init__.py", false);
            this.saveResource("mcpi/__init__.py", false);
            this.saveResource("mcpi/block.py", false);
            this.saveResource("mcpi/connection.py", false);
            this.saveResource("mcpi/event.py", false);
            this.saveResource("mcpi/minecraft.py", false);
            this.saveResource("mcpi/pycmdsvr.py", false);
            this.saveResource("mcpi/util.py", false);
            this.saveResource("mcpi/vec3.py", false);
            this.saveResource("pplugins/__init__.py", false);
            this.saveResource("pplugins/README.txt", false);
        }
    }
    
    public void onEnable(){
        this.saveDefaultConfig();
        int port = this.getConfig().getInt("port");
        
        //setup session array
        sessions = new ArrayList<RemoteSession>();
        
        //create new tcp listener thread
        try {
            serverThread = new ServerListenerThread(this, new InetSocketAddress(port));
            new Thread(serverThread).start();
            getLogger().info("ThreadListener Started");
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().warning("Failed to start ThreadListener");
            return;
        }
        //register the events
        getServer().getPluginManager().registerEvents(this, this);
        //setup the schedule to called the tick handler
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new TickHandler(), 1, 1);

        this.save_resources();
        logger.info("Starting Python command server...");
        ProcessBuilder pb = new ProcessBuilder(this.getConfig().getString("pyexe", "python3.exe"), "mcpi/pycmdsvr.py");
        Map<String, String> envs = pb.environment();
        envs.put("Path", this.getConfig().getString("pypath", "C:\\Python34"));
        try {
            pb.redirectErrorStream(true);
            pb.directory(this.getDataFolder());
            pb.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    public void onDisable(){
        int port = this.getConfig().getInt("pysvr_port");
        if(port==0){
            port = 32123;
        }

        try {
            Socket socket = new Socket("localhost", port);
            DataOutputStream toPyServer = new DataOutputStream(socket.getOutputStream());
            toPyServer.writeUTF("BYE");
            logger.info("ask py server to shut itself down");
            toPyServer.close();
            socket.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        getServer().getScheduler().cancelTasks(this);
        for (RemoteSession session: sessions) {
            try {
                session.close();
            } catch (Exception e) {
                getLogger().warning("Failed to close RemoteSession");
                e.printStackTrace();
            }
        }
        serverThread.running = false;
        try {
            serverThread.serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        sessions = null;
        serverThread = null;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        String cmdString;
        int port = this.getConfig().getInt("pysvr_port");
        
        if(args.length<1){
            return false;
        }
        
        if(port==0){
            port = 32123;
        }
        
        try {
            Socket socket = new Socket("localhost", port);
            DataOutputStream toPyServer = new DataOutputStream(socket.getOutputStream());
            BufferedReader fromPyServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String cmdLine = String.join(" ", args);
            toPyServer.writeUTF(cmdLine);
            //if(player instanceof Player){
            //    logger.info(player.getName() + ": send to py server: " + args[0]);
            //}
            cmdString = fromPyServer.readLine();
            logger.info("the py server sent back " + cmdString);
            toPyServer.close();
            fromPyServer.close();
            socket.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }
    
    private class TickHandler implements Runnable {
        public void run() {
            Iterator<RemoteSession> sI = sessions.iterator();
            while(sI.hasNext()) {
                RemoteSession s = sI.next();
                if (s.pendingRemoval) {
                    s.close();
                    sI.remove();
                } else {
                    s.tick();
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack currentTool = event.getPlayer().getItemInHand();
        if (currentTool == null || !blockBreakDetectionTools.contains(currentTool.getType())) {
            return;
        }
        for (RemoteSession session: sessions) {
            session.queuePlayerInteractEvent(event);
        }
    }
    
    @EventHandler(ignoreCancelled=true)
    public void onChatPosted(AsyncPlayerChatEvent event) {
        //debug
        //getLogger().info("Chat event fired");
        for (RemoteSession session: sessions) {
            session.queueChatPostedEvent(event);
        }
    }

    /** called when a new session is established. */
    public void handleConnection(RemoteSession newSession) {
        if (checkBanned(newSession)) {
            getLogger().warning("Kicking " + newSession.getSocket().getRemoteSocketAddress() + " because the IP address has been banned.");
            newSession.kick("You've been banned from this server!");
            return;
        }
        synchronized(sessions) {
            sessions.add(newSession);
        }
    }

    public Player getNamedPlayer(String name) {
        if (name == null) return null;
        for(Player p: Bukkit.getOnlinePlayers()){
            if(name.equalsIgnoreCase(p.getName())){
                return p;
            }
        }
        return null;
    }

    public Player getHostPlayer() {
        if (hostPlayer != null) return hostPlayer;
        Collection<? extends Player> allPlayers = Bukkit.getOnlinePlayers();
        if(allPlayers.size()>=1){
            return allPlayers.iterator().next();
        }
        return null;
    }
    
    //get entity by id - TODO to be compatible with the pi it should be changed to return an entity not a player...
    public Player getEntity(int id) {
        for (Player p: getServer().getOnlinePlayers()) {
            if (p.getEntityId() == id) {
                return p;
            }
        }
        return null;
    }

    public boolean checkBanned(RemoteSession session) {
        Set<String> ipBans = getServer().getIPBans();
        String sessionIp = session.getSocket().getInetAddress().getHostAddress();
        return ipBans.contains(sessionIp);
    }

}
