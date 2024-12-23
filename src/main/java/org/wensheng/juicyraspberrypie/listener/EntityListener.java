package org.wensheng.juicyraspberrypie.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.plugin.Plugin;
import org.wensheng.juicyraspberrypie.command.entity.ControllableEntity;

/**
 * Listener for entity events.
 */
public class EntityListener implements Listener {
	/**
	 * The plugin associated with this listener.
	 */
	private final Plugin plugin;

	/**
	 * Create a new entity listener.
	 * @param plugin The plugin to associate with this listener.
	 */
	public EntityListener(final Plugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Handle the entities load event.
	 * @param event The event to handle.
	 */
	@EventHandler
	public void onEntitiesLoadEvent(final EntitiesLoadEvent event) {
		event.getEntities().forEach(entity -> {
			new ControllableEntity(plugin, entity).reactivateControl();
		});
	}
}
