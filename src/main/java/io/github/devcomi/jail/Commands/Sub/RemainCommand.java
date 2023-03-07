package io.github.devcomi.jail.Commands.Sub;

import io.github.devcomi.jail.Main;
import io.github.devcomi.jail.Util.ColorEnum;
import io.github.devcomi.jail.Util.SQLite;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RemainCommand implements SubCommand {

    private Main plugin;
    private SQLite lite;

    public RemainCommand(Main plugin) {
        this.plugin = plugin;
        this.lite = (SQLite) plugin.getDatabase();
    }

    @Override
    public void handle(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        if (!sender.hasPermission("jail.commands.remain")) {
            sender.sendMessage(Component.text("이 명령어를 쓰기에 권한이 부족합니다!").color(ColorEnum.fail.getColor()));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("유저인 상태로 입력해주세요.").color(ColorEnum.fail.getColor()));
            return;
        }

        Player player = (Player) sender;

        Object time = lite.getObject("jail_players", "TIME", "UUID", player.getUniqueId().toString());
        if (time == null) {
            player.sendMessage(Component.text("수감 중이 아닙니다!").color(ColorEnum.fail.getColor()));
            return;
        }

        long lt = ((Number) time).longValue();

        if (lt != 0) {
            player.sendMessage(Component.text(lt - System.currentTimeMillis()/1000)
                    .append(Component.text(" 초 남았습니다.").color(ColorEnum.blue.getColor())));
        } else {
            player.sendMessage(Component.text("당신은 무기수입니다.").color(ColorEnum.blue.getColor()));
        }
    }
}
