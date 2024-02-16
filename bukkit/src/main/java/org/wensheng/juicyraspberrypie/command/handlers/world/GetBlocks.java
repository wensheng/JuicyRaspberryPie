package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

public class GetBlocks implements Handler {

    @Override
    public String handle(final Instruction instruction) {
        return getBlocks(instruction.nextLocation(), instruction.nextLocation());
    }

    private String getBlocks(final Location pos1, final Location pos2) {
        final World world = pos1.getWorld();
        if (world == null) {
            return "Fail";
        }
        final StringBuilder blockData = new StringBuilder();
        final int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        final int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        final int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        final int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        final int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        final int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int y = minY; y <= maxY; ++y) {
            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    blockData.append(world.getBlockAt(x, y, z).getType()).append(",");
                }
            }
        }

        return blockData.substring(0, !blockData.isEmpty() ? blockData.length() - 1 : 0);    // We don't want last comma
    }
}
