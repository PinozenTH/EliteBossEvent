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

import java.util.HashMap;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    HashMap<String, String> commandList = new HashMap<>();

    EliteBossEvent main = EliteBossEvent.getInstance();

    SummonMobTask summonMobTask = new SummonMobTask();

    boolean bypass_perm = main.getConfig().getBoolean("debug.bypass-permission");

    public CommandHandler() {
        commandList.put("reload", "elitebossevent.reload");
        commandList.put("start", "elitebossevent.admin");
        commandList.put("stop", "elitebossevent.admin");
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
                        new Reply(Reply.SenderType.CONSOLE, Lang.RELOAD.toString());
                        new Reply("elitebossevent.notify", Lang.RELOAD.toString());
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
                        new Debug("Max Ticked player count: " + summonMobTask.getMaxTickedPlayers(), Debug.DebugType.INFO);
                        for (int i = 0; i >= summonMobTask.getMaxTickedPlayers() && i < Bukkit.getOnlinePlayers().size(); i++) {
                            Player p = (Player) Bukkit.getOnlinePlayers().toArray()[i];
                            summonMobTask.addTickPlayer(p);
                        }
                        summonMobTask.summonMob(count);
                        new Reply(Reply.SenderType.ALLPLAYER, Lang.ELITE_EVENT_STARTED.toString());
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
        return List.of(commandList.keySet().toArray(new String[0]));
    }
}
