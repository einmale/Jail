package io.github.devcomi.jail.Commands.Sub;

import io.github.devcomi.jail.Main;
import io.github.devcomi.jail.Util.ColorEnum;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements SubCommand {

    public HelpCommand(Main plugin) {

    }

    @Override
    public void handle(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        if (!sender.hasPermission("jail.commands.help")) {
            sender.sendMessage(Component.text("이 명령어를 쓰기에 권한이 부족합니다!").color(ColorEnum.fail.getColor()));
            return;
        }

        TextComponent component = Component.text("- 명령어의 도움말을 보여줍니다.").color(TextColor.color(162, 238, 255))
                .append(Component.newline())
                .append(Component.text("/jail help"))
                .append(Component.newline())
                .append(Component.text("- 남은 수감 시간을 보여줍니다."))
                .append(Component.newline())
                .append(Component.text("/jail remain"))
                .append(Component.newline())
                .append(Component.text("- 감옥을 생성합니다."))
                .append(Component.newline())
                .append(Component.text("/jail create <감옥 아이디>"))
                .append(Component.newline())
                .append(Component.text("- 감옥을 삭제합니다."))
                .append(Component.newline())
                .append(Component.text("/jail remove <감옥 아이디>"))
                .append(Component.newline())
                .append(Component.text("- 유저를 감옥에 보냅니다. (물음표가 붙은 것은 필수가 아닙니다.)"))
                .append(Component.newline())
                .append(Component.text("/jail send <유저> <감옥 아이디> <시간?> <사유?>"))
                .append(Component.newline())
                .append(Component.text("- 수감자를 풀어줍니다."))
                .append(Component.newline())
                .append(Component.text("/jail release <유저>"));
        sender.sendMessage(component);
    }
}
