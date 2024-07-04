package org.wensheng.juicyraspberrypie.command.handlers.events;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An event queue for handling events.
 *
 * @param <T> The type of event to queue.
 */
public abstract class EventQueue<T extends Event> implements Listener {
	/**
	 * The event queue.
	 */
	private final Deque<T> events = new ArrayDeque<>();

	/**
	 * Create a new event queue.
	 *
	 */
	public EventQueue() {
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
