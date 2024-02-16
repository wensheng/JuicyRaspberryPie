package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;

public class SetBlock implements HandlerVoid {

    @Override
    public void handleVoid(final Instruction instruction) {
        final Location loc = instruction.nextLocation();
        Material material = Material.matchMaterial(instruction.next());
        if (material == null) {
            material = Material.valueOf("SANDSTONE");
        }
        final int facing = instruction.hasNext() ? Integer.parseInt(instruction.next()) : 0;
        final BlockFace blockFace = BlockFace.values()[facing];
        updateBlock(loc, material, blockFace);
    }

    protected void updateBlock(final Location loc, final Material blockType, final BlockFace blockFace) {
        final BlockData blockData = blockType.createBlockData();
        if (blockData instanceof Directional) {
            ((Directional) blockData).setFacing(blockFace);
        }
        loc.getBlock().setBlockData(blockData);
    }
}
