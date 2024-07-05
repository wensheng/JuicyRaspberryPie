package org.wensheng.juicyraspberrypie.command.handlers.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An event queue for handling events.
 *
 * @param <T> The type of event to queue.
 */
public abstract class EventQueue<T extends Event> implements AutoCloseable, Listener {
	/**
	 * The event queue.
	 */
	private final Deque<T> events = new ArrayDeque<>();

	/**
	 * Create a new event queue and register for events.
	 *
	 */
	public EventQueue(@NotNull final JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void close() throws Exception {
		HandlerList.unregisterAll(this);
	}

	/**
	 * Queue an event.
	 *
	 * @param event The event for queue.
	 */
	public void queueEvent(final T event) {
		events.add(event);
	}

	/**
	 * Get the next event from the queue.
	 *
	 * @return The next event from the queue.
	 */
	public T pollEvent() {
		return events.poll();
	}

	/**
	 * Check if the event queue is empty.
	 *
	 * @return True if the event queue is empty, false otherwise.
	 */
	public boolean isQueueEmpty() {
		return events.isEmpty();
	}

	/**
	 * Clear the event queue.
	 */
	public void clearQueue() {
		events.clear();
	}
}
