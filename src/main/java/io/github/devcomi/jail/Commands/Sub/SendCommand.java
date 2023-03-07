package io.github.devcomi.jail.Commands.Sub;

import io.github.devcomi.jail.Classes.Jail;
import io.github.devcomi.jail.Errors.SendException;
import io.github.devcomi.jail.Main;
import io.github.devcomi.jail.Util.ColorEnum;
import io.github.devcomi.jail.Util.Number3;
import io.github.devcomi.jail.Util.SQLite;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SendCommand implements SubCommand {

    private Main plugin;
    private SQLite lite;

    public SendCommand(Main plugin) {
        this.plugin = plugin;
        this.lite = (SQLite) plugin.getDatabase();
    }

    @Override
    public void handle(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        if (!sender.hasPermission("jail.commands.send")) {
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
        if (args.length < 2) {
            sender.sendMessage(Component.text("감옥 아이디를 입력해주세요.").color(ColorEnum.fail.getColor()));
            return;
        }

        String managerUUID = "SERVER";
        if (sender instanceof Player) {
            managerUUID = ((Player) sender).getUniqueId().toString();
        }

        Player target = Bukkit.getPlayer(args[0]);
        Jail jail = this.plugin.getJail(args[1]);

        if (target == null) {
            sender.sendMessage(Component.text("수감자를 찾을 수 없습니다.").color(ColorEnum.fail.getColor()));
            return;
        }
        if (jail == null) {
            sender.sendMessage(Component.text("감옥을 찾을 수 없습니다.").color(ColorEnum.fail.getColor()));
            return;
        }

        if (target.hasPermission("jail.immune")) {
            sender.sendMessage(Component.text("해당 플레이어를 감옥에 보낼 수 없습니다.").color(ColorEnum.fail.getColor()));
            return;
        }

        long time = args.length >= 3 && Number3.isStringInt(args[2]) ? System.currentTimeMillis()/1000 + Long.parseLong(args[2]) : 0;
        String reason = "없음";
        if (args.length >= 4) {
            List<String> reasons = new ArrayList<>();
            for (int i = 3; i < args.length; i++) {
                reasons.add(args[i]);
            }
            reason = String.join(" ", reasons);
        }

        int success = lite.sendPrisoner(target.getUniqueId().toString(), managerUUID, jail, time, reason);

        SendException exception = SendException.getFromErrorCode(success);

        if (success <= 0) {
            sender.sendMessage(exception.getTextComponent());
            return;
        }

        target.sendMessage(Component.text("당신은 수감되었습니다!").color(ColorEnum.fail.getColor())
                .append(Component.newline())
                .append(Component.text("사유: "))
                .append(Component.text(reason)));
        target.setBedSpawnLocation(jail.getLocation(), true);
        target.teleport(jail.getLocation());
        target.setGameMode(GameMode.ADVENTURE);

        sender.sendMessage(Component.text(args[0])
                .color(ColorEnum.success.getColor())
                .append(exception.getTextComponent())
                .append(Component.newline())
                .append(Component.text("사유: "))
                .append(Component.text(reason)));
    }
}
