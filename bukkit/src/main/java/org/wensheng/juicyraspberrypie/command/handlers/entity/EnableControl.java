package org.wensheng.juicyraspberrypie.command.handlers.entity;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.destroystokyo.paper.entity.ai.MobGoals;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.HandlerVoid;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.entity.EntityByUUIDProvider;

import java.io.IOException;
import java.util.EnumSet;

public class EnableControl implements HandlerVoid {
    private final Plugin plugin;

    private final EntityByUUIDProvider entityProvider;

    public EnableControl(final Plugin plugin, final EntityByUUIDProvider entityProvider) {
        this.plugin = plugin;
        this.entityProvider = entityProvider;
    }

    @Override
    public void handleVoid(final Instruction instruction) throws IOException {
        final Entity entity = entityProvider.getEntity(instruction);
        if (entity instanceof final Mob mob) {
            if (!mob.hasAI()) {
                mob.getPersistentDataContainer().set(new NamespacedKey(plugin, "noAI"), PersistentDataType.BOOLEAN, true);
                mob.setAI(true);
            }
            final MobGoals mobGoals = Bukkit.getMobGoals();
            mobGoals.addGoal(mob, Integer.MIN_VALUE, new EmptyGoal(plugin, "noMoveAI", GoalType.MOVE));
            mobGoals.addGoal(mob, Integer.MIN_VALUE, new EmptyGoal(plugin, "noTargetAI", GoalType.TARGET));
            mobGoals.addGoal(mob, Integer.MIN_VALUE, new EmptyGoal(plugin, "noJumpAI", GoalType.JUMP));
            mobGoals.addGoal(mob, Integer.MIN_VALUE, new EmptyGoal(plugin, "noLookAI", GoalType.LOOK));
            mobGoals.addGoal(mob, Integer.MIN_VALUE, new EmptyGoal(plugin, "noUnknownBehaviorAI", GoalType.UNKNOWN_BEHAVIOR));
        }
    }

    private record EmptyGoal(Plugin plugin, String namespacedKey, GoalType type) implements Goal<Mob> {

        @Override
        public boolean shouldActivate() {
            return true;
        }

        @Override
        public @NotNull GoalKey<Mob> getKey() {
            return GoalKey.of(Mob.class, new NamespacedKey(plugin, namespacedKey));
        }

        @Override
        public @NotNull EnumSet<GoalType> getTypes() {
            return EnumSet.of(type);
        }
    }
}
