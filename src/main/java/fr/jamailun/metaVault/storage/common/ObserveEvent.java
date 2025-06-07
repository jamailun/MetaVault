package fr.jamailun.metaVault.storage.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ObserveEvent {

    void apply(@NotNull UUID uuid, @NotNull String key, @Nullable String value);

}
