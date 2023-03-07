package io.github.devcomi.jail.Classes;

import org.bukkit.Location;
import org.bukkit.World;

public class Jail {

    private String id;
    private World world;
    private Location location;

    public Jail(String id, World world, Location location) {
        this.id = id;
        this.world = world;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public World getWorld() {
        return world;
    }

    public Location getLocation() {
        return location;
    }
}
