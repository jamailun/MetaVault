package fr.jamailun.metaVault.commands;

import fr.jamailun.metaVault.storage.MetaDataStore;
import fr.jamailun.metaVault.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Command handling admin actions.
 */
public class MetaVaultCommand implements TabExecutor, CommandExecutor {

    private static final List<String> ARGS = List.of("status", "store.get", "store.has", "store.set");

    private final Storage storage;
    private final String pluginError;

    public MetaVaultCommand(@Nullable Storage storage, @Nullable String pluginError) {
        this.storage = storage;
        this.pluginError = pluginError;
        PluginCommand pc = Objects.requireNonNull(Bukkit.getPluginCommand("meta-vault"), "Command not found.");
        pc.setTabCompleter(this);
        pc.setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if(args.length == 0)
            return error(sender, "Missing argument. Expected one of " + ARGS + ".");
        String arg0 = args[0].toLowerCase();
        if( ! ARGS.contains(arg0))
            return error(sender, "Invalid argument '" + args[0] + "'. Expected one of " + ARGS + ".");

        if("status".equals(arg0)) {
            if(storage == null) {
                error(sender, "Could not initialize storage.");
                error(sender, "- Error: " + pluginError);
                return error(sender, "- Check the console for more informations.");
            }
            info(sender, "§aStorage enabled.");
            return info(sender, "- Storage type: " + storage.getType());
        }

        if(storage == null) {
            return error(sender, "Could not initialize MetaVault. Error was: " + pluginError);
        }

        // store.X command. syntax = /label store.X <player> (key) (val)
        if(args.length < 2)
            return error(sender, "Specify the player to select the store");
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        UUID uuid = player.getUniqueId();
        MetaDataStore store = storage.getStore(uuid);


        if(args.length < 3)
            return error(sender, "Specify the key to use.");

        if("store.has".equalsIgnoreCase(args[2])) {
            store.hasValue(args[2]).handle((result,err) -> {
                if(err != null) {
                    error(sender, "Error during query: " + err.getMessage());
                } else {
                    info(sender, "store["+args[1]+"§7].has(§e" + args[2] + "§7) = " + (result?"§atrue":"§cfalse"));
                }
                return null;
            });
            return true;
        }

        if("store.get".equalsIgnoreCase(args[2])) {
            store.getValue(args[2]).handle((result,err) -> {
                if(err != null) {
                    error(sender, "Error during query: " + err.getMessage());
                } else {
                    info(sender, "store["+args[1]+"§7].get(§e" + args[2] + "§7) = " + (result==null?"§c§onull":"§f"+result));
                }
                return null;
            });
            return true;
        }

        if("store.set".equalsIgnoreCase(args[2])) {
            if(args.length < 4)
                return error(sender, "Missing new value to set.");
            String newValue = String.join(" ", List.of(args).subList(3, args.length));
            store.newTransaction().setKeyValue(args[2], newValue).apply().handle((x,err) -> {
                if(err != null) {
                    error(sender, "Error during transaction: " + err.getMessage());
                } else {
                    info(sender, "store["+args[1]+"§7].get(§e" + args[2] + "§7) <- \"§f" + newValue + "§7\".");
                }
                return null;
            });
            return true;
        }

        return error(sender, "Not supposed to happen. Args = " + Arrays.toString(args));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if(args.length == 1) {
            String arg = args[0].toLowerCase();
            return ARGS.stream()
                    .filter(s -> s.contains(arg))
                    .toList();
        }

        if(args.length == 2) {
            if(args[0].toLowerCase().startsWith("store.")) {
                return Arrays.stream(Bukkit.getOfflinePlayers())
                        .map(OfflinePlayer::getName)
                        .filter(n -> n != null && n.startsWith(args[1]))
                        .toList();
            }
        }

        return List.of();
    }

    private boolean error(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage("§c" + message);
        return true;
    }
    private boolean info(@NotNull CommandSender sender, @NotNull String message) {
        sender.sendMessage("§7" + message);
        return true;
    }
}
