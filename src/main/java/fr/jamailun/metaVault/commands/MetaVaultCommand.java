package fr.jamailun.metaVault.commands;

import fr.jamailun.metaVault.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Command handling admin actions.
 */
public class MetaVaultCommand implements TabExecutor, CommandExecutor {

    private static final List<String> ARGS = List.of("status", "store.get", "store.has", "store.set");

    private final Storage storage;
    private final String error;

    public MetaVaultCommand(@Nullable Storage storage, @Nullable String error) {
        this.storage = storage;
        this.error = error;
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
                error(sender, "- Error: " + error);
                return error(sender, "- Check the console for more informations.");
            }
            info(sender, "§aStorage enabled.");
            return info(sender, "- Storage type: " + storage.getType());
        }

        if(error != null) {
            return error(sender, "Could not initialize MetaVault. Error was: " + error);
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull[] args) {
        if(args.length == 1) {
            String arg = args[0].toLowerCase();
            return ARGS.stream()
                    .filter(s -> s.contains(arg))
                    .toList();
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
