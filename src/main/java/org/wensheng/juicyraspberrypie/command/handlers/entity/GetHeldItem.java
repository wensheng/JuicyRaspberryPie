package org.wensheng.juicyraspberrypie.command.handlers.entity;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.wensheng.juicyraspberrypie.command.Handler;
import org.wensheng.juicyraspberrypie.command.Instruction;
import org.wensheng.juicyraspberrypie.command.SessionAttachment;
import org.wensheng.juicyraspberrypie.command.entity.EntityProvider;

/**
 * Get the item held by an entity.
 */
public class GetHeldItem implements Handler {
	/**
	 * The entity provider associated with this handler.
	 */
	private final EntityProvider entityProvider;

	/**
	 * Create a new GetHeldItem event handler.
	 *
	 * @param entityProvider The entity provider to associate with this handler.
	 */
	public GetHeldItem(final EntityProvider entityProvider) {
		this.entityProvider = entityProvider;
	}

	@Override
	public String handle(@NotNull final SessionAttachment sessionAttachment, @NotNull final Instruction instruction) {
		final Entity entity = entityProvider.getEntity(sessionAttachment, instruction);
		if (entity instanceof final LivingEntity living) {
			final EntityEquipment equipment = living.getEquipment();
			if (equipment != null) {
				final ItemStack itemInMainHand = equipment.getItemInMainHand();
				return itemInMainHand.getType().name() + getCustomModelData(itemInMainHand);
			}
		}
		return Material.AIR.name();
	}

	private static String getCustomModelData(final ItemStack itemInMainHand) {
		final ItemMeta itemMeta = itemInMainHand.getItemMeta();
		if (itemMeta == null || !itemMeta.hasCustomModelData()) {
			return "";
		}
		return "," + itemMeta.getCustomModelData();
	}
}
