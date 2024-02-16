package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;

public class SetBlocks extends SetBlock implements HandlerVoid {
    @Override
    public void handleVoid(final Instruction instruction) {
        final Location loc1 = instruction.nextLocation();
        final Location loc2 = instruction.nextLocation();
        final Material mat = Material.matchMaterial(instruction.next());
        final Material material = mat == null ? Material.valueOf("SANDSTONE") : mat;
        final int facing = instruction.hasNext() ? Integer.parseInt(instruction.next()) : 0;
        final BlockFace blockFace = BlockFace.values()[facing];

        getLocationsBetween(loc1, loc2).forEach(loc -> updateBlock(loc, material, blockFace));
    }
}
