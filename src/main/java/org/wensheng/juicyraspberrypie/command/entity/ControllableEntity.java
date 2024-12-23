package org.wensheng.juicyraspberrypie.command.entity;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.destroystokyo.paper.entity.ai.MobGoals;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class to control an entity by enabling and disabling AI.
 */
public class ControllableEntity {
	/**
	 * The plugin associated with this entity.
	 */
	private final Plugin plugin;

	/**
	 * The entity to control.
	 */
	private final Entity entity;

	/**
	 * Create a new controllable entity.
	 *
	 * @param plugin The plugin to associate with this entity.
	 * @param entity The entity to control.
	 */
	public ControllableEntity(final Plugin plugin, final Entity entity) {
		this.plugin = plugin;
		this.entity = entity;
	}

	/**
	 * Enable control of the entity.
	 */
	public void enableControl() {
		if (entity instanceof final Mob mob) {
			final PersistentDataContainer persistentDataContainer = mob.getPersistentDataContainer();
			persistentDataContainer.set(new NamespacedKey(plugin, "controlled"), PersistentDataType.BOOLEAN, true);
			if (!mob.hasAI()) {
				persistentDataContainer.set(new NamespacedKey(plugin, "noAI"), PersistentDataType.BOOLEAN, true);
				mob.setAI(true);
			}
			final MobGoals mobGoals = Bukkit.getMobGoals();
			getGoalKeyNamespacedKeys().forEach((goalType, namespacedKey) -> mobGoals.addGoal(mob, Integer.MIN_VALUE, new EmptyGoal(namespacedKey, goalType)));
		}
	}

	/**
	 * Reactivate control of the entity.
	 */
	public void reactivateControl() {
		if (entity instanceof final Mob mob) {
			final PersistentDataContainer persistentDataContainer = mob.getPersistentDataContainer();
			final Boolean controlled = persistentDataContainer.getOrDefault(new NamespacedKey(plugin, "controlled"), PersistentDataType.BOOLEAN, false);
			if (controlled) {
				enableControl();
			}
		}
	}

	/**
	 * Disable control of the entity.
	 */
	public void disableControl() {
		if (entity instanceof final Mob mob) {
			final MobGoals mobGoals = Bukkit.getMobGoals();
			getGoalKeyNamespacedKeys().forEach((goalType, namespacedKey) -> mobGoals.removeGoal(mob, GoalKey.of(Mob.class, namespacedKey)));
			final PersistentDataContainer persistentDataContainer = mob.getPersistentDataContainer();
			persistentDataContainer.remove(new NamespacedKey(plugin, "controlled"));
			final boolean noAI = persistentDataContainer.has(new NamespacedKey(plugin, "noAI"), PersistentDataType.BOOLEAN);
			if (noAI) {
				persistentDataContainer.remove(new NamespacedKey(plugin, "noAI"));
				mob.setAI(false);
			}
		}
	}

	private Map<GoalType, NamespacedKey> getGoalKeyNamespacedKeys() {
		return Arrays.stream(GoalType.values())
				.map(goalType -> {
					final String key = "no_" + goalType.name() + "_AI";
					return Map.entry(goalType, new NamespacedKey(plugin, key));
				})
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private record EmptyGoal(NamespacedKey namespacedKey, GoalType type) implements Goal<Mob> {

		@Override
		public boolean shouldActivate() {
			return true;
		}

		@Override
		public @NotNull GoalKey<Mob> getKey() {
			return GoalKey.of(Mob.class, namespacedKey);
		}

		@Override
		public @NotNull EnumSet<GoalType> getTypes() {
			return EnumSet.of(type);
		}
	}
}
