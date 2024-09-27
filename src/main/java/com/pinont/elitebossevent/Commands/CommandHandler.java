package com.pinont.elitebossevent.Commands;

import com.pinont.elitebossevent.Config.Lang;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    HashMap<String, String> commandList = new HashMap<>();

    EliteBossEvent main = EliteBossEvent.getInstance();

    SummonMobTask summonMobTask = main.summonMobTask;

    boolean bypass_perm = main.getConfig().getBoolean("debug.bypass-permission");

    public CommandHandler() {
        commandList.put("reload", "elitebossevent.admin");
        commandList.put("start", "elitebossevent.admin");
        commandList.put("stop", "elitebossevent.admin");
        commandList.put("delay-status", "elitebossevent.admin");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (main.debug) {
            new Debug("Command: " + command.getName() + " Label: " + label + " Args: " + Arrays.toString(args), Debug.DebugType.BOTH);
            new Debug("Bypass permission: " + bypass_perm, Debug.DebugType.BOTH);
        }
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
                        if (summonMobTask.scheduleTask != null) {
                            summonMobTask.scheduleTask.cancel();
                        } else {
                            new Debug("Task is null or not scheduling", Debug.DebugType.WARNING);
                        }
                        main.reloadConfig();
                        new Reply(Reply.SenderType.CONSOLE, Lang.RELOAD.toString());
                        new Reply("elitebossevent.notify", Lang.RELOAD.toString());
                        main.executeTask();
                    } else {
                        new Reply(sender, Lang.NO_PERMISSION.toString());
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
                        summonMobTask.summonMob(count);
                    } else {
                        new Reply(sender, Lang.NO_PERMISSION.toString());
                    }
                    break;
                case "stop":
                    if (sender.hasPermission(commandList.get(args[0])) || bypass_perm) {
                        summonMobTask.stop();
                    } else {
                        new Reply(sender, Lang.NO_PERMISSION.toString());
                    }
                    break;
                case "delay-status":
                    if (sender.hasPermission(commandList.get(args[0])) || bypass_perm) {
                        new Reply(sender, "Delay status: " + summonMobTask.getDelayStatus());
                    } else {
                        new Reply(sender, Lang.NO_PERMISSION.toString());
                    }
                    break;
                default:
                    new Reply(sender, Lang.INVALID_COMMAND.toString());
                    break;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if ((commandSender.hasPermission("elitebossevent.admin") || bypass_perm) && strings.length == 0) {
            return List.of(commandList.keySet().toArray(new String[0]));
        }
        return null;
    }
}
