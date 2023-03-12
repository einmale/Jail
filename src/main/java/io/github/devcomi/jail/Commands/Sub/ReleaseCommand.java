package io.github.devcomi.jail.Commands.Sub;

import io.github.devcomi.jail.Errors.ReleaseException;
import io.github.devcomi.jail.Main;
import io.github.devcomi.jail.Util.ColorEnum;
import io.github.devcomi.jail.Util.Jailer;
import io.github.devcomi.jail.Util.Location3;
import io.github.devcomi.jail.Util.SQLite;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ReleaseCommand implements SubCommand {

    private Main plugin;
    private SQLite lite;

    public ReleaseCommand(Main plugin) {
        this.plugin = plugin;
        this.lite = (SQLite) plugin.getDatabase();
    }

    @Override
    public void handle(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        if (!sender.hasPermission("jail.commands.release")) {
            sender.sendMessage(Component.text("이 명령어를 쓰기에 권한이 부족합니다!").color(ColorEnum.fail.getColor()));
            return;
        }

//        if (!(sender instanceof Player)) {
//            sender.sendMessage(Component.text("유저인 상태로 입력해주세요.").color(ColorEnum.fail.getColor()));
//            return;
//        }

        if (args.length < 1) {
            sender.sendMessage(Component.text("수감자 이름을 입력해주세요.").color(ColorEnum.fail.getColor()));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        String managerUUID = "SERVER";
        if (sender instanceof Player) {
            managerUUID = ((Player) sender).getUniqueId().toString();
        }

        String targetUUID = null;
        HashMap<String, Long> map = this.lite.getAllPrisoners("jail_players");

        for (String s : map.keySet()) {
            if (s.equals(args[0])) {
                targetUUID = s;
                break;
            }

            UUID convertedUUID = UUID.fromString(s);
            OfflinePlayer player = Bukkit.getOfflinePlayer(convertedUUID);
            if (!player.hasPlayedBefore()) {
                continue;
            }
            if (player.getName().equalsIgnoreCase(args[0])) {
                targetUUID = s;
                break;
            }
        }

        if (targetUUID == null) {
            sender.sendMessage(Component.text("수감자를 찾을 수 없습니다.").color(ColorEnum.fail.getColor()));
            return;
        }

        String locationString = (String) lite.getObject("jail_players", "SPAWN_POSITION", "UUID", targetUUID);
        Location location = new Location(Bukkit.getWorld("world"), 0, 0, 0);

        if (locationString != null) {
            location = Location3.convert(Bukkit.getWorld("world"), locationString.split(","));
        }

        int success = lite.releasePrisoner(targetUUID, managerUUID);

        ReleaseException exception = ReleaseException.getFromErrorCode(success);

        if (success <= 0) {
            sender.sendMessage(exception.getTextComponent());
            return;
        }

        if (!Jailer.release(target, location)) {
            String[] keys = {"UUID", "POSITION"};
            String[] values = {targetUUID, Location3.convert(location)};

            this.lite.setObject("wait_locations", keys, values);
        } else {
            target.sendMessage(Component.text("석방되었습니다!").color(ColorEnum.success.getColor()));
        }

        sender.sendMessage(Component.text(args[0])
                .color(ColorEnum.success.getColor())
                .append(exception.getTextComponent()));
    }
}
