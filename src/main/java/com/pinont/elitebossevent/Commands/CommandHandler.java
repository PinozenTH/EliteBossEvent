package com.pinont.elitebossevent.Commands;

import com.pinont.elitebossevent.EliteBossEvent;
import com.pinont.elitebossevent.Utils.Message.Reply;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CommandHandler implements CommandExecutor {

    HashMap<String, String> commandList = new HashMap<>();

    EliteBossEvent main = EliteBossEvent.getInstance();

    boolean bypass_perm = main.getConfig().getBoolean("debug.bypass-permission");

    public CommandHandler() {
        commandList.put("reload", "elitebossevent.reload");
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
                        new Reply("elitebossevent.notify", "EliteBossEvent has been reloaded!");
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
}
