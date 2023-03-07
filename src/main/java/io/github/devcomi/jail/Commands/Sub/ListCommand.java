package io.github.devcomi.jail.Commands.Sub;

import io.github.devcomi.jail.Classes.Jail;
import io.github.devcomi.jail.Main;
import io.github.devcomi.jail.Util.ColorEnum;
import io.github.devcomi.jail.Util.Location3;
import io.github.devcomi.jail.Util.SQLite;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListCommand implements SubCommand {

    private Main plugin;

    public ListCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        if (!sender.hasPermission("jail.commands.list")) {
            sender.sendMessage(Component.text("이 명령어를 쓰기에 권한이 부족합니다!").color(ColorEnum.fail.getColor()));
            return;
        }

        List<String> list = new ArrayList<>();

        for (Jail j : this.plugin.getJails()) {
            list.add(j.getId());
        }

        TextComponent component = Component.text("감옥 리스트 :").color(ColorEnum.blue.getColor()).append(Component.newline())
                .append(Component.text(String.join("\n", list)));

        sender.sendMessage(component);
    }
}
