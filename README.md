# MetaVault

Made by [jamailun](https://github.com/jamailun).

This plugin allows to **propagate and persist** key/value data across plugin.

## API usage

### Dependency

Dependencies in you pom :

```xml
<repositories>
    <!-- Your other repositories ... -->
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <!-- Your other repositories ... -->
    <dependency>
        <groupId>com.github.jamailun.MetaVault</groupId>
        <artifactId>MetaVault</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

### How to use : basics I/O

You must obtain the `Storage` instance, provided by the plugin.

```java
import fr.jamailun.metaVault.observer.Observer;
import fr.jamailun.metaVault.storage.MetaDataStore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import fr.jamailun.metaVault.storage.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FooPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        RegisteredServiceProvider<Storage> storageProvider = Bukkit.getServicesManager().getRegistration(Storage.class);
        if (storageProvider == null) {
            getLogger().severe("Could not get storage provider!");
            return;
        }
        // You have the storage, you can do transactions and queries.
        Storage storage = storageProvider.getProvider();

        UUID uuid = Bukkit.getOfflinePlayer("jamailun").getUniqueId();
        MetaDataStore store = storage.getStore(uuid);

        // Get a value
        store.getValue("test-key").handle((val, err) -> {
            if (err != null) {
                getLogger().severe("Could not fetch data: " + err.getMessage());
            } else {
                getLogger().severe("Data value is : " + val);
            }
            return null;
        });

        // Set some values
        store.newTransaction()
                .setKeyValue("key_a", "a")
                .setKeyValue("key_b", "b") // null in the 2nd argument remove the entry.
                .apply() // All changes will be applied on the #apply() call.
                .handle((x, err) -> {
                    if (err != null) {
                        getLogger().severe("Could not set data: " + err.getMessage());
                    }
                    return null;
                });
    }
}
```

### How to use : observe changes

You need to register an `Observer` instance to the Storage.

```java
public class FooPlugin extends JavaPlugin {
    // ...
    private void registerObserver(Storage storage) {
        JavaPlugin that = this;
        storage.observe(new Observer() {
            @Override
            public @NotNull Plugin getPlugin() {
                return that;
            }

            @Override
            public void onValueChanged(@NotNull UUID uuid, @NotNull String key, @Nullable String value) {
                that.getLogger().info("Value changed: " + uuid + "/" + key + " = " + value);
            }
        });
    }
}
```

