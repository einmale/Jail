package io.github.devcomi.jail.Commands;

import io.github.devcomi.jail.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {

    private Main main;

    public TestCommand(Main main) {
        this.main = main;

        this.main.getCommand("test").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(this.main.getJails().get(0).getId() + "good");

        return true;
    }
}
