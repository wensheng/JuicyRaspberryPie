package org.wensheng.juicyraspberrypie.command;

import org.bukkit.Location;

import java.io.IOException;

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
}
