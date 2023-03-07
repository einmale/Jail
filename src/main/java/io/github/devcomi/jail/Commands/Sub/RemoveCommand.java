package io.github.devcomi.jail.Commands.Sub;

import io.github.devcomi.jail.Classes.Jail;
import io.github.devcomi.jail.Errors.RemoveException;
import io.github.devcomi.jail.Main;
import io.github.devcomi.jail.Util.ColorEnum;
import io.github.devcomi.jail.Util.Location3;
import io.github.devcomi.jail.Util.SQLite;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RemoveCommand implements SubCommand {

    private Main plugin;
    private SQLite lite;

    public RemoveCommand(Main plugin) {
        this.plugin = plugin;
        this.lite = (SQLite) plugin.getDatabase();
    }

    @Override
    public void handle(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        if (!sender.hasPermission("jail.commands.remove")) {
            sender.sendMessage(Component.text("이 명령어를 쓰기에 권한이 부족합니다!").color(ColorEnum.fail.getColor()));
            return;
        }

//        if (!(sender instanceof Player)) {
//            sender.sendMessage(Component.text("유저인 상태로 입력해주세요.").color(ColorEnum.fail.getColor()));
//            return;
//        }

        if (args.length < 1) {
            sender.sendMessage(Component.text("감옥 이름을 입력해주세요.").color(ColorEnum.fail.getColor()));
            return;
        }

        String managerUUID = "SERVER";
        if (sender instanceof Player) {
            managerUUID = ((Player) sender).getUniqueId().toString();
        }

        Jail jail = this.plugin.getJail(args[0]);

        if (jail == null) {
            sender.sendMessage(Component.text("감옥을 찾을 수 없습니다.").color(ColorEnum.fail.getColor()));
            return;
        }

        int success = lite.removeJail(jail.getId(), managerUUID);

        RemoveException exception = RemoveException.getFromErrorCode(success);

        if (success <= 0) {
            sender.sendMessage(exception.getTextComponent());
            return;
        }

        sender.sendMessage(Component.text(args[0])
                .color(ColorEnum.success.getColor())
                .append(exception.getTextComponent()));
    }
}
