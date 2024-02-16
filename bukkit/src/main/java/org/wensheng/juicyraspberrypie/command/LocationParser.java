package org.wensheng.juicyraspberrypie.command;

import org.bukkit.Location;

public class LocationParser {

    private final SessionAttachment attachment;

    public LocationParser(final SessionAttachment attachment) {
        this.attachment = attachment;
    }

    public Location parse(final String stringX, final String stringY, final String stringZ) {
        final double x = Double.parseDouble(stringX);
        final double y = Double.parseDouble(stringY);
        final double z = Double.parseDouble(stringZ);
        return parse(x, y, z);
    }

    public Location parse(final double doubleX, final double doubleY, final double doubleZ) {
        return new Location(attachment.getWorld(), doubleX, doubleY, doubleZ);
    }

    public Location parse(final String stringX, final String stringY, final String stringZ, final float yaw, final float pitch) {
        final Location loc = parse(stringX, stringY, stringZ);
        loc.setYaw(yaw);
        loc.setPitch(pitch);
        return loc;
    }
}
