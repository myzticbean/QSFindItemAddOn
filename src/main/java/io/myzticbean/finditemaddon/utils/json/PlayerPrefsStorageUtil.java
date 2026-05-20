package io.myzticbean.finditemaddon.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.models.PlayerSortFilterPrefs;
import io.myzticbean.finditemaddon.utils.async.VirtualThreadScheduler;
import io.myzticbean.finditemaddon.utils.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages per-player sort/filter preferences.
 * Backed by player_prefs.json in the plugin data folder.
 * Thread-safe: the in-memory map is a ConcurrentHashMap;
 * all file I/O is dispatched to a virtual thread via VirtualThreadScheduler.
 */
public class PlayerPrefsStorageUtil {

    private static final String FILE_NAME = "player_prefs.json";

    /** In-memory store. UUID → prefs. Never contains null values. */
    private static final ConcurrentHashMap<UUID, PlayerSortFilterPrefs> prefsMap = new ConcurrentHashMap<>();

    /**
     * Returns the preferences for the given UUID.
     * If no entry exists, inserts and returns a fresh default.
     * Never returns null. Safe to call from any thread.
     */
    @NotNull
    public static PlayerSortFilterPrefs getPrefs(@NotNull UUID uuid) {
        return prefsMap.computeIfAbsent(uuid, k -> new PlayerSortFilterPrefs());
    }

    /**
     * Persists the full prefs map to disk asynchronously.
     * Called after every in-GUI state change so preferences survive reloads.
     * The write is non-blocking; the in-memory map is the source of truth.
     */
    public static void saveAsync() {
        VirtualThreadScheduler.runTaskAsync(PlayerPrefsStorageUtil::saveToFile);
    }

    /**
     * Synchronous save — called from onDisable() to guarantee a final flush
     * before the JVM exits, in case the last async save hasn't completed yet.
     */
    public static void save() {
        saveToFile();
    }

    /**
     * Loads preferences from player_prefs.json into the in-memory map.
     * Called once during plugin startup (runPluginStartupTasks).
     * No-op if the file does not exist (first run).
     */
    public static void load() {
        File file = new File(getFilePath());
        if (!file.exists()) {
            Logger.logInfo("player_prefs.json not found — starting fresh (first run).");
            return;
        }
        Gson gson = new GsonBuilder().create();
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<HashMap<String, PlayerSortFilterPrefs>>() {}.getType();
            Map<String, PlayerSortFilterPrefs> rawMap = gson.fromJson(reader, type);
            if (rawMap != null) {
                rawMap.forEach((uuidStr, prefs) -> {
                    if (uuidStr != null && prefs != null) {
                        try {
                            prefsMap.put(UUID.fromString(uuidStr), prefs);
                        } catch (IllegalArgumentException ignored) {
                            Logger.logWarning("Skipping malformed UUID in player_prefs.json: " + uuidStr);
                        }
                    }
                });
                Logger.logInfo("Loaded player preferences for " + prefsMap.size() + " player(s).");
            }
        } catch (IOException e) {
            Logger.logError("Failed to load player_prefs.json", e);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static void saveToFile() {
        Gson gson = new GsonBuilder().create();
        File file = new File(getFilePath());
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
            // Serialise as Map<String, PlayerSortFilterPrefs> — Gson handles UUID keys poorly,
            // so convert to String keys first.
            Map<String, PlayerSortFilterPrefs> serialisable = new HashMap<>(prefsMap.size());
            prefsMap.forEach((uuid, prefs) -> serialisable.put(uuid.toString(), prefs));
            try (Writer writer = new FileWriter(file, false)) {
                gson.toJson(serialisable, writer);
                writer.flush();
            }
            Logger.logDebugInfo("Saved player preferences (" + serialisable.size() + " entries).");
        } catch (IOException e) {
            Logger.logError("Failed to save player_prefs.json", e);
        }
    }

    private static @NotNull String getFilePath() {
        return FindItemAddOn.getInstance().getDataFolder().getAbsolutePath()
                + File.separator + FILE_NAME;
    }
}
