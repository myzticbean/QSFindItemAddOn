package io.myzticbean.finditemaddon.utils.warp;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import io.myzticbean.finditemaddon.dependencies.ResidencePlugin;
import io.myzticbean.finditemaddon.FindItemAddOn;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * @author SengyEU
 */
@UtilityClass
public class ResidenceUtils {

    @Nullable
    public static ClaimedResidence findNearestResidence(Location shopLocation) {
        return ResidencePlugin.getResidenceManager().getByLoc(shopLocation);
    }

    public static String getResidenceName(ClaimedResidence residence){
        if(!FindItemAddOn.getConfigProvider().USE_RESIDENCE_SUBZONES) {
            return residence.getParent() == null ? residence.getResidenceName() : residence.getTopParentName();
        }
        return residence.getName();
    }

    public static void residenceTp(Player player, String warpName) {
        Bukkit.dispatchCommand(player, "residence tp " + warpName);
    }

}
