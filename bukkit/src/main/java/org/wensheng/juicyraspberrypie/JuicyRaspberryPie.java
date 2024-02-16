package org.wensheng.juicyraspberrypie;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class JuicyRaspberryPie extends JavaPlugin implements Listener {
    final Logger logger = Logger.getLogger("Minecraft");

    private ServerListenerThread serverThread;

    private final List<RemoteSession> sessions = new ArrayList<>();

    private void save_resources() {
        final File py_init_file = new File(getDataFolder(), "config.yml");
        if (!py_init_file.exists()) {
            this.saveResource("config.yml", false);
        }

        final File svrFolder = new File(getDataFolder(), "cmdsvr");
        if (svrFolder.exists() || svrFolder.mkdir()) {
            this.saveResource("cmdsvr/pycmdsvr.py", true);
        } else {
            logger.warning("Could not create cmdsvr directory in plugin.");
        }

        final File mcpiFolder = new File(getDataFolder(), "mcpi");
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

        final File ppluginsFolder = new File(getDataFolder(), "pplugins");
        if (ppluginsFolder.exists() || ppluginsFolder.mkdir()) {
            this.saveResource("pplugins/README.txt", true);
            this.saveResource("pplugins/examples.py", true);
        } else {
            logger.warning("Could not create pplugins directory in plugin.");
        }
    }

    public void onEnable() {
        this.saveDefaultConfig();
        final int port = this.getConfig().getInt("api_port");
        final boolean start_pyserver = this.getConfig().getBoolean("start_cmdsvr");

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
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new TickHandler(), 1, 1);

        this.save_resources();

        if (start_pyserver) {
            final String pyexe = getConfig().getString("pyexe", "python.exe");
            //String pypath = this.getConfig().getString("pypath", "C:\\Python37");

            //logger.info("Starting Python command server using " + pyexe + " in " + pypath);
            logger.info("Starting Python command server using " + pyexe);
            final ProcessBuilder pb = new ProcessBuilder(pyexe, "cmdsvr/pycmdsvr.py");
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

    public void onDisable() {
        int port = this.getConfig().getInt("cmdsvr_port");
        // cmdsvr_host is not used, always "localhost" for now
        final boolean start_pyserver = this.getConfig().getBoolean("start_cmdsvr");
        if (port == 0) {
            port = 32123;
        }

        if (start_pyserver) {
            try {
                final Socket socket = new Socket("localhost", port);
                final DataOutputStream toPyServer = new DataOutputStream(socket.getOutputStream());
                toPyServer.writeUTF("BYE");
                logger.info("ask py server to shut itself down");
                toPyServer.close();
                socket.close();
            } catch (Exception e) {
                logger.warning("Could not send shutdown signal to python command server, please shutdown manually.");
            }
        }

        getServer().getScheduler().cancelTasks(this);
        for (final RemoteSession session : sessions) {
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

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final String cmdString;
        int port = this.getConfig().getInt("cmdsvr_port");

        if (args.length < 1) {
            return false;
        }

        if (port == 0) {
            port = 4731;
        }

        try {
            final Socket socket = new Socket("localhost", port);
            final DataOutputStream toPyServer = new DataOutputStream(socket.getOutputStream());
            final BufferedReader fromPyServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            final String cmdLine = String.join(" ", args);
            toPyServer.writeUTF(cmdLine);
            cmdString = fromPyServer.readLine();
            logger.info("the py server send back:|" + cmdString + "|");
            if (!cmdString.equals("ok")) {
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
            final Iterator<RemoteSession> sI = sessions.iterator();
            while (sI.hasNext()) {
                final RemoteSession s = sI.next();
                if (s.pendingRemoval) {
                    s.close();
                    sI.remove();
                } else {
                    s.tick();
                }
            }
        }
    }

    /**
     * called when a new session is established.
     */
    void handleConnection(final RemoteSession newSession) {
        if (checkBanned(newSession)) {
            logger.warning("Kicking " + newSession.getSocket().getRemoteSocketAddress() + " because the IP address has been banned.");
            newSession.kick("You've been banned from this server!");
            return;
        }
        synchronized (sessions) {
            sessions.add(newSession);
        }
    }


    private boolean checkBanned(final RemoteSession session) {
        final Set<String> ipBans = getServer().getIPBans();
        final String sessionIp = session.getSocket().getInetAddress().getHostAddress();
        return ipBans.contains(sessionIp);
    }

}
