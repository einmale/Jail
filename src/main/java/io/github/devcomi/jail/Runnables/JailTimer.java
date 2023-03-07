package io.github.devcomi.jail.Runnables;

import io.github.devcomi.jail.Main;
import io.github.devcomi.jail.Util.ColorEnum;
import io.github.devcomi.jail.Util.Jailer;
import io.github.devcomi.jail.Util.Location3;
import io.github.devcomi.jail.Util.SQLite;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JailTimer implements Runnable {

    private Main plugin;
    private SQLite lite;

    public JailTimer(Main plugin) {
        this.plugin = plugin;
        this.lite = (SQLite) plugin.getDatabase();
    }

    @Override
    public void run() {
        HashMap<String, Long> map = this.lite.getAllPrisoners("jail_players");

        for(Map.Entry<String, Long> entry : map.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();

            if (value - (System.currentTimeMillis()/1000) <= 0 && value != 0) {
                Player player = Bukkit.getPlayer(UUID.fromString(key));
                String locationString = (String) lite.getObject("jail_players", "SPAWN_POSITION", "UUID", key);
                Location location = new Location(Bukkit.getWorld("world"), 0, 0, 0);

                this.lite.releasePrisoner(key, "SERVER");
                
                if (locationString != null) {
                    location = Location3.convert(Bukkit.getWorld("world"), locationString.split(","));
                }
                
                if (!Jailer.release(player, location)) {
                    String[] keys = {"UUID", "POSITION"};
                    String[] values = {key, Location3.convert(location)};

                    this.lite.setObject("wait_locations", keys, values);
                }
                
                if (player != null) {
                    player.sendMessage(Component.text("석방되었습니다!").color(ColorEnum.success.getColor()));
                }
            }
        }
    }
}
