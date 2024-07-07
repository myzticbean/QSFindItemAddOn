/**
 * QSFindItemAddOn: An Minecraft add-on plugin for the QuickShop Hikari
 * and Reremake Shop plugins for Spigot server platform.
 * Copyright (C) 2021  myzticbean
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */
package uk.mangostudios.finditemaddon.external;

import com.olziedev.playerwarps.api.PlayerWarpsAPI;
import com.olziedev.playerwarps.api.warp.Warp;

import java.util.List;

public class PlayerWarpsHandler {

    private static List<Warp> allWarpsList = null;
    private static PlayerWarpsAPI playerWarpsAPI = null;
    private static final String ALL_WARPS_LIST_CLASSPATH = PlayerWarpsHandler.class.getCanonicalName() + ".allWarpsList";

    private PlayerWarpsHandler() {
    }

    public static void setup() {
        PlayerWarpsAPI.getInstance(api -> {
            playerWarpsAPI = api;
        });
    }

    /**
     * Issue #24 Fix: Changing all api get references to callback, making this method deprecated
     */
    @Deprecated
    public static PlayerWarpsAPI getAPI() {
        return playerWarpsAPI;
    }

    public static List<Warp> getAllWarps() {
        PlayerWarpsAPI.getInstance(api -> {
            allWarpsList = api.getPlayerWarps(false);
        });
        return allWarpsList;
    }

    public static void updateAllWarpsFromAPI() {
        // Issue #24 Fix: Changing api instance to callback
        PlayerWarpsAPI.getInstance(api -> {
            allWarpsList = api.getPlayerWarps(false);
        });
    }

    public static void updateWarpsOnEventCall(Warp warp, boolean isRemoved) {
        tryUpdateWarps(warp, isRemoved, 1);
    }

    private static void tryUpdateWarps(Warp warp, boolean isRemoved, int updateTrialSequence) {
        // Issue #21 Fix: Adding a NPE check
        if (allWarpsList != null) {
            if (isRemoved) {
                if (allWarpsList.contains(warp)) {
                    allWarpsList.remove(warp);
                }
            } else {
                allWarpsList.add(warp);
            }
        } else {
            // Issue #21 Fix: forcing update of allWarpsList
            if (updateTrialSequence == 1) {
                updateAllWarpsFromAPI();
                tryUpdateWarps(warp, isRemoved, 2);
            } else {
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("Error occurred while updating '").append(ALL_WARPS_LIST_CLASSPATH).append("' as it is null! ")
                        .append("Please install PlayerWarps by Olzie-12 if you would like to use 'nearest-warp-mode' as 2. ")
                        .append("If PlayerWarps plugin is installed and issue persists, please contact the developer!");
            }
        }
    }

}
