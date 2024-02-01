package org.wensheng.juicyraspberrypie;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class JuicyRaspberryPie extends JavaPlugin implements Listener{
    final Logger logger = Logger.getLogger("Minecraft");
    private static final Set<Material> blockBreakDetectionTools = EnumSet.of(
            Material.DIAMOND_SWORD,
            Material.GOLDEN_SWORD,
            Material.IRON_SWORD, 
            Material.STONE_SWORD, 
            Material.WOODEN_SWORD);

    private ServerListenerThread serverThread;
    private final List<RemoteSession> sessions = new ArrayList<>();

    private void save_resources(){
        File py_init_file = new File(getDataFolder(), "config.yml");
        if(!py_init_file.exists()){
            this.saveResource("config.yml", false);
        }

        File svrFolder = new File(getDataFolder(), "cmdsvr");
        if (svrFolder.exists() || svrFolder.mkdir()) {
                this.saveResource("cmdsvr/pycmdsvr.py", true);
        } else {
            logger.warning("Could not create cmdsvr directory in plugin.");
        }

        File mcpiFolder = new File(getDataFolder(), "mcpi");
        if (mcpiFolder.exists() || mcpiFolder.mkdir()) {
            this.saveResource("mcpi/__init__.py", true);
            this.saveResource("mcpi/connection.py", true);
            this.saveResource("mcpi/event.py", true);
            this.saveResource("mcpi/minecraft.py", true);
            this.saveResource("mcpi/util.py", true);
            this.saveResource("mcpi/vec3.py", true);
        } else {
            logger.warning("Could not create mcpi directory in plugin.");
        }

        File ppluginsFolder = new File(getDataFolder(), "pplugins");
        if (ppluginsFolder.exists() || ppluginsFolder.mkdir()){
            this.saveResource("pplugins/README.txt", true);
            this.saveResource("pplugins/examples.py", true);
        } else {
            logger.warning("Could not create pplugins directory in plugin.");
        }
    }
    
    public void onEnable(){
        this.saveDefaultConfig();
        int port = this.getConfig().getInt("api_port");
        boolean start_pyserver = this.getConfig().getBoolean("start_cmdsvr");
        
        //create new tcp listener thread
        try {
            serverThread = new ServerListenerThread(this, new InetSocketAddress(port));
            new Thread(serverThread).start();
            logger.info("ThreadListener Started");
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning("Failed to start ThreadListener");
            return;
        }
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new TickHandler(), 1, 1);

        this.save_resources();

        if(start_pyserver){
            String pyexe = getConfig().getString("pyexe", "python.exe");
            //String pypath = this.getConfig().getString("pypath", "C:\\Python37");

            //logger.info("Starting Python command server using " + pyexe + " in " + pypath);
            logger.info("Starting Python command server using " + pyexe);
            ProcessBuilder pb = new ProcessBuilder(pyexe, "cmdsvr/pycmdsvr.py");
            //Map<String, String> envs = pb.environment();
            //envs.put("Path", pypath);
            try {
                pb.redirectErrorStream(true);
                pb.directory(this.getDataFolder());
                pb.start();
            } catch (IOException e) {
                logger.warning("******************************************************************************");
                logger.warning("Could not start python command server! Please check your `pyexe` in config.yml");
                logger.warning("******************************************************************************");
            }
        }

    }
    
    public void onDisable(){
        int port = this.getConfig().getInt("cmdsvr_port");
        // cmdsvr_host is not used, always "localhost" for now
        boolean start_pyserver = this.getConfig().getBoolean("start_cmdsvr");
        if(port==0){
            port = 32123;
        }

        if(start_pyserver){
            try {
                Socket socket = new Socket("localhost", port);
                DataOutputStream toPyServer = new DataOutputStream(socket.getOutputStream());
                toPyServer.writeUTF("BYE");
                logger.info("ask py server to shut itself down");
                toPyServer.close();
                socket.close();
            } catch (Exception e) {
                logger.warning("Could not send shutdown signal to python command server, please shutdown manually.");
            }
        }

        getServer().getScheduler().cancelTasks(this);
        for (RemoteSession session: sessions) {
            try {
                session.close();
            } catch (Exception e) {
                logger.warning("Failed to close RemoteSession");
                e.printStackTrace();
            }
        }
        serverThread.running = false;
        try {
            serverThread.serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        serverThread = null;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        String cmdString;
        int port = this.getConfig().getInt("cmdsvr_port");
        
        if(args.length<1){
            return false;
        }
        
        if(port==0){
            port = 4731;
        }
        
        try {
            Socket socket = new Socket("localhost", port);
            DataOutputStream toPyServer = new DataOutputStream(socket.getOutputStream());
            BufferedReader fromPyServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String cmdLine = String.join(" ", args);
            toPyServer.writeUTF(cmdLine);
            cmdString = fromPyServer.readLine();
            logger.info("the py server send back:|" + cmdString + "|");
            if(!cmdString.equals("ok")){
                sender.sendMessage(cmdString);
            }
            toPyServer.close();
            fromPyServer.close();
            socket.close();
        } catch (Exception e) {
            sender.sendMessage("command server not available.");
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

        //ItemStack currentTool = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack currentTool = event.getItem();
        if (currentTool == null || !blockBreakDetectionTools.contains(currentTool.getType())) {
            return;
        }
        for (RemoteSession session: sessions) {
            session.queuePlayerInteractEvent(event);
        }
    }
    
    @EventHandler
    public void onChatPosted(AsyncPlayerChatEvent event) {
        for (RemoteSession session: sessions) {
            session.queueChatPostedEvent(event);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        for (RemoteSession session: sessions) {
            session.queueProjectileHitEvent(event);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        for (RemoteSession session: sessions) {
            session.handlePlayerQuitEvent();
        }
    }

    /** called when a new session is established. */
    void handleConnection(RemoteSession newSession) {
        if (checkBanned(newSession)) {
            logger.warning("Kicking " + newSession.getSocket().getRemoteSocketAddress() + " because the IP address has been banned.");
            newSession.kick("You've been banned from this server!");
            return;
        }
        synchronized(sessions) {
            sessions.add(newSession);
        }
    }



    private boolean checkBanned(RemoteSession session) {
        Set<String> ipBans = getServer().getIPBans();
        String sessionIp = session.getSocket().getInetAddress().getHostAddress();
        return ipBans.contains(sessionIp);
    }

}
