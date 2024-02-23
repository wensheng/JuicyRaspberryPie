package org.wensheng.juicyraspberrypie.command.handlers.events.projectile;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.handlers.events.EventQueue;

public class Hits extends EventQueue<ProjectileHitEvent> {
    public Hits(final Plugin plugin) {
        super(plugin);
    }

    @Override
    public String handle(final Instruction instruction) {
        final StringBuilder b = new StringBuilder();
        ProjectileHitEvent event;
        while ((event = pollEvent()) != null) {
            final Arrow arrow = (Arrow) event.getEntity();
            final Player player = (Player) arrow.getShooter();
            if (player != null) {
                final Block block = arrow.getLocation().getBlock();
                final Location loc = block.getLocation();
                b.append(getBlockLocation(loc));
                b.append(",");
                b.append(player.getUniqueId());
                b.append(",");
                final Entity hitEntity = event.getHitEntity();
                if (hitEntity != null) {
                    b.append(hitEntity.getUniqueId());
                }
            } else {
                b.append("0,0,0,Fail,0");
            }
            if (!isQueueEmpty()) {
                b.append("|");
            }
        }
        return b.toString();
    }

    @EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {
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
