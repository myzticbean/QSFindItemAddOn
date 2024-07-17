package io.myzticbean.finditemaddon.Utils.WarpUtils;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import io.myzticbean.finditemaddon.ConfigUtil.ConfigProvider;
import io.myzticbean.finditemaddon.Dependencies.ResidencePlugin;
import io.myzticbean.finditemaddon.FindItemAddOn;
import org.bukkit.Location;

import javax.annotation.Nullable;

/**
 * @author SengyEU
 */
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

}
