package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;

public class SetBlocks extends SetBlock implements HandlerVoid {
    @Override
    public void handleVoid(final Instruction instruction) {
        final Location loc1 = instruction.nextLocation();
        final Location loc2 = instruction.nextLocation();
        Material material = Material.matchMaterial(instruction.next());
        if (material == null) {
            material = Material.valueOf("SANDSTONE");
        }
        final int facing = instruction.hasNext() ? Integer.parseInt(instruction.next()) : 0;
        final BlockFace blockFace = BlockFace.values()[facing];
        setCuboid(loc1, loc2, material, blockFace);
    }

    private void setCuboid(final Location pos1, final Location pos2, final Material blockType, final BlockFace blockFace) {
        final World world = pos1.getWorld();
        final int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        final int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        final int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        final int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        final int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        final int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                for (int y = minY; y <= maxY; ++y) {
                    updateBlock(new Location(world, x, y, z), blockType, blockFace);
                }
            }
        }
    }
}
