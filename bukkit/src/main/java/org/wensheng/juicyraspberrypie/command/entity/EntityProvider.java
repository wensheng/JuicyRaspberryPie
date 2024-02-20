package org.wensheng.juicyraspberrypie.command.entity;

import org.bukkit.entity.Entity;
import org.wensheng.juicyraspberrypie.command.Instruction;

public interface EntityProvider {
    Entity getEntity(Instruction instruction);
}
