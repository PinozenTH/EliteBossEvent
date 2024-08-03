package com.pinont.elitebossevent.Commands;

import com.pinont.elitebossevent.EliteBossEvent;
import com.pinont.elitebossevent.Tasks.SummonMobTask;
import com.pinont.elitebossevent.Utils.Message.Debug;
import com.pinont.elitebossevent.Utils.Message.Reply;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    HashMap<String, String> commandList = new HashMap<>();

    EliteBossEvent main = EliteBossEvent.getInstance();

    boolean bypass_perm = main.getConfig().getBoolean("debug.bypass-permission");

    public CommandHandler() {
        commandList.put("reload", "elitebossevent.reload");
        commandList.put("start", "elitebossevent.admin");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }
        if (command.getName().equalsIgnoreCase("eliteboss")) {
            if (label.isEmpty()) {
                return false;
            }
            switch (args[0]) {
                case "reload":
                    if (sender.hasPermission(commandList.get(args[0])) || bypass_perm) {
                        main.reloadConfig();
                        new Reply(Reply.SenderType.CONSOLE, "EliteBossEvent has been reloaded!");
                        new Reply("elitebossevent.notify", "EliteBossEvent has been reloaded!");
                    } else {
                        new Reply(sender, "You do not have permission to use this command!");
                    }
                    break;
                case "start":
                    if (sender.hasPermission(commandList.get(args[0])) || bypass_perm) {
                        int count = 30;
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("now")) {
                                count = 0;
                            }
                        }
                        for (int i = 0; i >= new SummonMobTask().maxTickedPlayers && i < Bukkit.getOnlinePlayers().size(); i++) {
                            Player p = (Player) Bukkit.getOnlinePlayers().toArray()[i];
                            new SummonMobTask().tickedPlayers.put(p, 0);
                        }
                        new Debug("Ticked players: " + new SummonMobTask().tickedPlayers, Debug.DebugType.INFO);
                        new SummonMobTask().summonMob(count);
                        new Reply(Reply.SenderType.ALLPLAYER, "EliteBossEvent has been started!");
                    } else {
                        new Reply(sender, "You do not have permission to use this command!");
                    }
                    break;
                default:
                    new Reply(sender, "Invalid command!");
                    break;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of(commandList.keySet().toArray(new String[0]));
    }
}
