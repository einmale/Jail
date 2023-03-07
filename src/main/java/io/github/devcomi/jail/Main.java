package io.github.devcomi.jail;

import io.github.devcomi.jail.Classes.Jail;
import io.github.devcomi.jail.Commands.JailCommand;
import io.github.devcomi.jail.Commands.TestCommand;
import io.github.devcomi.jail.Events.PlayerJoined;
import io.github.devcomi.jail.Runnables.JailTimer;
import io.github.devcomi.jail.Util.Database;
import io.github.devcomi.jail.Util.SQLite;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    private Logger logger;

    private List<Jail> jails;
    private Database db;

    @Override
    public void onEnable() {
        this.logger = getLogger();

        this.db = new SQLite(this);
        this.db.load();

        this.jails = ((SQLite) this.db).getAllJails("jails");

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new JailTimer(this), 0, 1*20);

        this.getServer().getPluginManager().registerEvents(new PlayerJoined(this), this);

        new JailCommand(this);
    }

    @Override
    public void onDisable() {

    }

    public Database getDatabase() {
        return this.db;
    }

    public List<Jail> getJails() {
        return jails;
    }

    public Jail getJail(String id) {
        for (Jail jail : this.jails) {
            if (!jail.getId().equalsIgnoreCase(id)) {
                continue;
            }
            return jail;
        }
        return null;
    }

    public void reloadJails() {
        this.jails = ((SQLite) this.db).getAllJails("jails");
    }
}
