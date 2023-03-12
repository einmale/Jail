package kr.kro.mygaran.jail.Events;

import kr.kro.mygaran.jail.Main;
import kr.kro.mygaran.jail.Util.ColorEnum;
import kr.kro.mygaran.jail.Util.Location3;
import kr.kro.mygaran.jail.Util.SQLite;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoined implements Listener {

    private Main plugin;
    private SQLite lite;

    public PlayerJoined(Main plugin) {
        this.plugin = plugin;
        this.lite = (SQLite) plugin.getDatabase();
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String locationString = (String) this.lite.getObject("wait_locations", "POSITION", "UUID", player.getUniqueId().toString());

        if (locationString != null) {
            Location location = Location3.convert(Bukkit.getWorld("world"), locationString.split(","));

            player.setBedSpawnLocation(location, true);
            player.teleport(location);
            player.setGameMode(player.getPreviousGameMode() == null ? GameMode.ADVENTURE : player.getPreviousGameMode());

            this.lite.removeObject("wait_locations", "UUID", player.getUniqueId().toString());

            player.sendMessage(Component.text("석방되었습니다!").color(ColorEnum.success.getColor()));
        }
    }

}
