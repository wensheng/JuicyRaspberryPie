package org.wensheng.juicyraspberrypie.command.entity;

import org.bukkit.entity.Entity;
import org.wensheng.juicyraspberrypie.command.Instruction;

import java.io.IOException;

public interface EntityProvider {
    Entity getEntity(Instruction instruction) throws IOException;
}
