package org.wensheng.juicyraspberrypie.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class Instruction implements Iterator<String> {
    private final String[] args;

    private final LocationParser locationParser;

    private int courser;

    public Instruction(final String[] args, final LocationParser locationParser) {
        this.args = args;
        this.locationParser = locationParser;
        this.courser = 0;
    }

    public boolean hasNext() {
        return hasNext(1);
    }

    public boolean hasNext(final int n) {
        return courser + n <= args.length;
    }

    public String next() {
        return hasNext() ? args[courser++] : null;
    }

    public String peek() {
        return hasNext() ? args[courser] : null;
    }

    public Location nextLocation() {
        return locationParser.parse(next(), next(), next());
    }

    public Location nextBlockLocation() {
        return nextLocation().getBlock().getLocation();
    }


    public Player nextNamedPlayer() {
        final String name = next();
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if (name.equalsIgnoreCase(p.getName())) {
                return p;
            }
        }
        return null;
    }
}
