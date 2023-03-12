package kr.kro.mygaran.jail.Commands.Sub;

import kr.kro.mygaran.jail.Main;
import kr.kro.mygaran.jail.Util.ColorEnum;
import kr.kro.mygaran.jail.Util.SQLite;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LogCommand implements SubCommand {

    private Main plugin;
    private SQLite lite;

    public LogCommand(Main plugin) {
        this.plugin = plugin;
        this.lite = (SQLite) plugin.getDatabase();
    }

    @Override
    public void handle(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {
        if (!sender.isOp() && sender.hasPermission("jail.commands.log")) {
            sender.sendMessage(Component.text("이 명령어를 쓰기에 권한이 부족합니다!").color(ColorEnum.fail.getColor()));
            return;
        }


    }
}
