package kr.kro.mygaran.jail.Commands.Sub;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface SubCommand {

    void handle(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args);

}
