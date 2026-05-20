package io.myzticbean.finditemaddon.models;

import io.myzticbean.finditemaddon.models.enums.SortMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Holds a player's persistent sort and filter preferences for the shop search GUI.
 * Serialized to player_prefs.json via Gson. All fields must have defaults so that
 * a fresh instance (for first-time players) behaves identically to the old plugin behaviour.
 */
@Getter
@Setter
@NoArgsConstructor
public class PlayerSortFilterPrefs {
    private SortMode sortMode = SortMode.DEFAULT;
    private boolean filterHasStock = false;
    private boolean filterSameWorld = false;
    private boolean filterExcludeOwnShops = false;
}
