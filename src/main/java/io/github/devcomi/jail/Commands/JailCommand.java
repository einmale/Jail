package io.github.devcomi.jail.Commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.devcomi.jail.Classes.Jail;
import io.github.devcomi.jail.Commands.Sub.*;
import io.github.devcomi.jail.Main;
import io.github.devcomi.jail.Util.ColorEnum;
import io.github.devcomi.jail.Util.SQLite;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileReader;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public class JailCommand implements CommandExecutor, TabExecutor {

    private Main plugin;
    private SQLite lite;

    private Map<String, SubCommand> subCommands = new HashMap<>();

    public JailCommand(Main plugin) {
        this.plugin = plugin;

        PluginCommand command = plugin.getCommand("jail");

        command.setExecutor(this);
        command.setTabCompleter(this);

        if (CommodoreProvider.isSupported()) {
            Commodore commodore = CommodoreProvider.getCommodore(plugin);

            try {
                LiteralCommandNode<?> commandNode = CommodoreFileReader.INSTANCE.parse(plugin.getResource("jail.commodore"));
                commodore.register(command, commandNode, player -> player.hasPermission("jail.commands.use"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.lite = (SQLite) plugin.getDatabase();

        subCommands.put("help", new HelpCommand(plugin));
        subCommands.put("remain", new RemainCommand(plugin));
        subCommands.put("create", new CreateCommand(plugin));
        subCommands.put("remove", new RemoveCommand(plugin));
        subCommands.put("send", new SendCommand(plugin));
        subCommands.put("release", new ReleaseCommand(plugin));
        subCommands.put("list", new ListCommand(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("jail.commands.use")) {
            sender.sendMessage(Component.text("이 명령어를 쓰기에 권한이 부족합니다!").color(ColorEnum.fail.getColor()));
            return true;
        }

        if (args.length <= 0) {
            sender.sendMessage(Component.text("/jail help").color(ColorEnum.fail.getColor()));
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0]);

        if (subCommand == null) {
            sender.sendMessage(Component.text("명령어를 제대로 입력해주세요.").color(ColorEnum.fail.getColor()));
            return true;
        }

        String[] newArguments = args;
        List<String> list = new ArrayList<>(Arrays.asList(newArguments));
        list.remove(0);
        newArguments = list.toArray(new String[0]);

        subCommand.handle(sender, command, newArguments);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length <= 0) {
            return new ArrayList<>();
        }

        // 첫번째 sub command 를 입력하지 않았을때
//        if (args.length < 2) {
//            List<String> completions = new ArrayList<>();
//
//            StringUtil.copyPartialMatches(args[0], subCommands.keySet(), completions);
//
//            sender.sendMessage("tab");
//
//            for (String s : completions) {
//                sender.sendMessage(s);
//                if (!sender.hasPermission("jail.commands." + s)) {
//                    completions.remove(s);
//                }
//            }
//
//            Collections.sort(completions);
//
//            return completions;
//        }
        // 두번째 argument 를 입력하지 않았을때
        if (args.length < 3) {
            switch (args[0]) {
                case "create":
                case "list":
                case "help":
                case "remain": {
                    return new ArrayList<>();
                }
                case "remove": {
                    List<String> completions = new ArrayList<>();

                    if (!sender.hasPermission("jail.commands.list")) {
                        return completions;
                    }

                    for (Jail j : plugin.getJails()) {
                        completions.add(j.getId());
                    }

                    return completions;
                }
                case "release": {
                    HashMap<String, Long> map = this.lite.getAllPrisoners("jail_players");

                    List<String> completions = new ArrayList<>();

                    if (!sender.hasPermission("jail.commands.release")) {
                        return completions;
                    }

                    for (String s : map.keySet()) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(s));
                        String form = player.getName();

                        if (!player.hasPlayedBefore()) {
                            form = s;
                        }
                        completions.add(form);
                    }

                    return completions;
                }
                default: {
                    List<String> completions = new ArrayList<>();

                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        completions.add(p.getName());
                    }

                    return completions;
                }
            }
        }

        if (args[0].equalsIgnoreCase("send")) {
            if (args.length < 4) {
                List<String> completions = new ArrayList<>();

                if (!sender.hasPermission("jail.commands.list")) {
                    return completions;
                }

                for (Jail j : plugin.getJails()) {
                    completions.add(j.getId());
                }

                return completions;
            }
        }

        return new ArrayList<>();
    }
}
