package io.github.devcomi.jail.Commands.Sub;

import io.github.devcomi.jail.Errors.CreateException;
import io.github.devcomi.jail.Main;
import io.github.devcomi.jail.Util.ColorEnum;
import io.github.devcomi.jail.Util.SQLite;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreateCommand implements SubCommand {

    private Main plugin;
    private SQLite lite;

    public CreateCommand(Main plugin) {
        this.plugin = plugin;
        this.lite = (SQLite) plugin.getDatabase();
    }

    @Override
    public void handle(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        if (!sender.hasPermission("jail.commands.create")) {
            sender.sendMessage(Component.text("이 명령어를 쓰기에 권한이 부족합니다!").color(ColorEnum.fail.getColor()));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("유저인 상태로 입력해주세요.").color(ColorEnum.fail.getColor()));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(Component.text("감옥 이름을 입력해주세요.").color(ColorEnum.fail.getColor()));
            return;
        }

        Player player = (Player) sender;

        int success = lite.createJail(player, args[0]);

        CreateException exception = CreateException.getFromErrorCode(success);

        if (success <= 0) {
            player.sendMessage(exception.getTextComponent());
            return;
        }

        player.sendMessage(Component.text(args[0])
                .color(ColorEnum.success.getColor())
                .append(exception.getTextComponent()));
    }
}
