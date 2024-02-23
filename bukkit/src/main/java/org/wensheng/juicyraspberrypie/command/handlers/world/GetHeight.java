package org.wensheng.juicyraspberrypie.command.handlers.world;

import org.bukkit.Location;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;

public class GetHeight implements Handler {
    @Override
    public String handle(final Instruction instruction) {
        final Location loc = instruction.nextLocation();
        return String.valueOf(loc.getWorld().getHighestBlockYAt(loc));
    }
}
