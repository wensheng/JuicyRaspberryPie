package org.wensheng.juicyraspberrypie.command.handlers.events.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.handlers.events.EventQueue;

import java.util.EnumSet;
import java.util.Set;

public class Hits extends EventQueue<PlayerInteractEvent> {

    public Hits(final Plugin plugin) {
        super(plugin);
    }

    private static final Set<Material> blockBreakDetectionTools = EnumSet.of(
            Material.DIAMOND_SWORD,
            Material.GOLDEN_SWORD,
            Material.IRON_SWORD,
            Material.STONE_SWORD,
            Material.WOODEN_SWORD);

    @Override
    public String handle(final Instruction instruction) {
        final StringBuilder b = new StringBuilder();
        PlayerInteractEvent event;
        while ((event = pollEvent()) != null) {
            final Block block = event.getClickedBlock();
            if (block != null) {
                final Location loc = block.getLocation();
                b.append(getBlockLocation(loc));
                b.append(",");
                b.append(event.getBlockFace().name());
                b.append(",");
                b.append(event.getPlayer().getUniqueId());
            } else {
                b.append("0,0,0,Fail,0");
            }
            if (!isQueueEmpty()) {
                b.append("|");
            }
        }
        return b.toString();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final ItemStack currentTool = event.getItem();
        if (currentTool == null || !blockBreakDetectionTools.contains(currentTool.getType())) {
            return;
        }
        queueEvent(event);
    }

    @Override
    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }
}
