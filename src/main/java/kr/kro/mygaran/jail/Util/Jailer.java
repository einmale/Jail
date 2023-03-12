package kr.kro.mygaran.jail.Util;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Jailer {

    public static boolean release(Player target, Location location) {
        if (target == null) {
            return false;
        }
        if (location != null) {
            target.setBedSpawnLocation(location, true);
            target.teleport(location);
            target.setGameMode(target.getPreviousGameMode() == null ? GameMode.ADVENTURE : target.getPreviousGameMode());
        } else {
            target.setBedSpawnLocation(Bukkit.getWorld("world").getSpawnLocation(), true);
            target.teleport(Bukkit.getWorld("world").getSpawnLocation());
            target.setGameMode(target.getPreviousGameMode() == null ? GameMode.ADVENTURE : target.getPreviousGameMode());
        }
        return true;
    }
}
