package kr.kro.mygaran.jail.Util;

import org.bukkit.Location;
import org.bukkit.World;

public class Location3 {

    public static Location convert(World world, String[] xyz) {
        if (xyz != null) {
            return new Location(
                    world,
                    Integer.parseInt(xyz[0]),
                    Integer.parseInt(xyz[1]),
                    Integer.parseInt(xyz[2])
            );
        }
        return new Location(world, 0,0,0);
    }

    public static String convert(Location location) {
        return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }
}
