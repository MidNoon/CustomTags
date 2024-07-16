package com.flyerzrule.mc.customtags.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.Database.TagsDatabase;

public class AddTagCommand implements CommandExecutor {

    public AddTagCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("addtag")) {
            if (args.length == 2) {
                String username = args[0];
                String tagId = args[1];
                Player player = Bukkit.getPlayer(username);

                if (player != null) {
                    String uuid = player.getUniqueId().toString();
                    TagsDatabase db = TagsDatabase.getInstance();
                    db.giveUserTag(uuid, tagId);
                    CustomTags.getPlugin().getLogger()
                            .info(String.format("%s added tag %s to user %s(%s)", sender.getName(), tagId,
                                    player.getName(), uuid));
                    return true;
                }
            }
        }
        return false;
    }
}