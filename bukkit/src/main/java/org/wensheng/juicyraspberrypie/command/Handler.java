package org.wensheng.juicyraspberrypie.command;

import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface Handler {
    default String get(final Instruction instruction) {
        try {
            return handle(instruction);
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    String handle(Instruction instruction) throws IOException;

    default String getLocation(final Location loc) {
        return loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    default String getBlockLocation(final Location loc) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    default List<Location> getLocationsBetween(final Location loc1, final Location loc2) {
        final List<Location> locations = new ArrayList<>();
        final World world = loc1.getWorld();
        final int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        final int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        final int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        final int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        final int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        final int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int y = minY; y <= maxY; ++y) {
                    locations.add(new Location(world, x, y, z));
                }
            }
        }
        return locations;
    }
}
