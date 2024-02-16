package org.wensheng.juicyraspberrypie.command.handlers.entity;

import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.MobGoals;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.entity.EntityByUUIDProvider;

import java.io.IOException;

public class DisableControl implements HandlerVoid {
    private final Plugin plugin;

    private final EntityByUUIDProvider entityProvider;

    public DisableControl(final Plugin plugin, final EntityByUUIDProvider entityProvider) {
        this.plugin = plugin;
        this.entityProvider = entityProvider;
    }

    @Override
    public void handleVoid(final Instruction instruction) throws IOException {
        final Entity entity = entityProvider.getEntity(instruction);
        if (entity instanceof final Mob mob) {
            final MobGoals mobGoals = Bukkit.getMobGoals();
            mobGoals.removeGoal(mob, GoalKey.of(Mob.class, new NamespacedKey(plugin, "noMoveAI")));
            mobGoals.removeGoal(mob, GoalKey.of(Mob.class, new NamespacedKey(plugin, "noTargetAI")));
            mobGoals.removeGoal(mob, GoalKey.of(Mob.class, new NamespacedKey(plugin, "noJumpAI")));
            mobGoals.removeGoal(mob, GoalKey.of(Mob.class, new NamespacedKey(plugin, "noLookAI")));
            mobGoals.removeGoal(mob, GoalKey.of(Mob.class, new NamespacedKey(plugin, "noUnknownBehaviorAI")));
            final PersistentDataContainer persistentDataContainer = mob.getPersistentDataContainer();
            final boolean noAI = persistentDataContainer.has(new NamespacedKey(plugin, "noAI"), PersistentDataType.BOOLEAN);
            if (noAI) {
                persistentDataContainer.remove(new NamespacedKey(plugin, "noAI"));
                mob.setAI(false);
            }
        }
    }
}
