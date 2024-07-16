package com.flyerzrule.mc.customtags;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.flyerzrule.mc.customtags.Database.TagsDatabase;
import com.flyerzrule.mc.customtags.commands.AddTagCommand;
import com.flyerzrule.mc.customtags.commands.RemoveTagCommand;
import com.flyerzrule.mc.customtags.commands.TagsCommand;
import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.listeners.ChatListener;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class CustomTags extends JavaPlugin {
    private File configFile = new File(getDataFolder(), "tags.json");
    private static Permission perms = null;
    private static Chat chat = null;
    private static JavaPlugin plugin = null;

    @Override
    public void onEnable() {
        plugin = this;

        ensureDataFolderExists();
        ensureTagsConfigExists();

        TagsConfig tagConfig = TagsConfig.getInstance();
        tagConfig.setFile(configFile);
        tagConfig.parseFile();

        TagsDatabase.getInstance();

        setupPermissions();
        setupChat();

        registerGlobalIngredients();
        registerCommands();
        registerListeners();

        getLogger().info("CustomTags has been enabled!");
    }

    @Override
    public void onDisable() {
        TagsDatabase db = TagsDatabase.getInstance();
        if (db != null) {
            db.closeConnection();
        }
        getLogger().info("CustomTags has been disabled!");
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static Chat getChat() {
        return chat;
    }

    public static Permission getPermission() {
        return perms;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    public static void setPlayerPrefix(Player player, String oldTag, String newTag) {
        if (chat != null) {
            String oldPrefix = chat.getPlayerPrefix(player);

            // Remove the old tag from the prefix
            String rankPrefix = oldPrefix.replace(oldTag, "").trim();

            String newPrefix = String.format("%s %s", rankPrefix, newTag);

            chat.setPlayerPrefix(player, newPrefix);
        }
    }

    private void registerGlobalIngredients() {
        Structure.addGlobalIngredient('#', new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§r"));
    }

    private void ensureDataFolderExists() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

    }

    private void ensureTagsConfigExists() {
        if (!configFile.exists()) {
            try (InputStream in = getResource("tags.json")) {
                if (in != null) {
                    Files.copy(in, configFile.toPath());
                } else {
                    getLogger().severe("Default tags.json not found in resources!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerCommands() {
        this.getCommand("tags").setExecutor(new TagsCommand());
        this.getCommand("addtag").setExecutor(new AddTagCommand());
        this.getCommand("removetag").setExecutor(new RemoveTagCommand());
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }
}