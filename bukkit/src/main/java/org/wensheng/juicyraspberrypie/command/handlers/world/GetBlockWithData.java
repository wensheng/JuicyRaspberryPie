package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.block.Block;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

public class GetBlockWithData implements Handler {

    @Override
    public String handle(final Instruction instruction) {
        final Block block = instruction.nextLocation().getBlock();
        return block.getType().name() + "," + block.getBlockData();
    }
}
