package me.ronsane.finditemaddon.finditemaddon.Utils;

import com.earth2me.essentials.utils.LocationUtil;
import me.ronsane.finditemaddon.finditemaddon.QuickShopHandler.QuickShopAPIHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.maxgamer.quickshop.QuickShop;

import java.util.*;

public class LocationUtils {

    private static final List<Material> damagingBlocks = new ArrayList<>();
    private static final List<Material> nonSuffocatingBlocks = new ArrayList<>();
    private static final int BELOW_SAFE_BLOCK_CHECK_LIMIT = 20;

    static {
        // Initializing Damaging blocks
        damagingBlocks.add(Material.LAVA);
        damagingBlocks.add(Material.CACTUS);
        damagingBlocks.add(Material.CAMPFIRE);
        damagingBlocks.add(Material.SOUL_CAMPFIRE);
        damagingBlocks.add(Material.MAGMA_BLOCK);
        damagingBlocks.add(Material.FIRE);
        damagingBlocks.add(Material.SOUL_FIRE);
        damagingBlocks.add(Material.SWEET_BERRY_BUSH);
        damagingBlocks.add(Material.WITHER_ROSE);
        damagingBlocks.add(Material.END_PORTAL);
        damagingBlocks.add(Material.AIR);
        damagingBlocks.add(QuickShopAPIHandler.getShopSignMaterial());

        // Initializing Non Suffocating blocks
        nonSuffocatingBlocks.add(Material.AIR);
        // Glass
        nonSuffocatingBlocks.add(Material.GLASS);
        nonSuffocatingBlocks.add(Material.GRAY_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.GREEN_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.BLACK_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.BLUE_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.BROWN_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.CYAN_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.LIGHT_BLUE_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.LIGHT_GRAY_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.LIME_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.MAGENTA_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.ORANGE_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.PINK_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.PURPLE_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.RED_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.WHITE_STAINED_GLASS);
        nonSuffocatingBlocks.add(Material.YELLOW_STAINED_GLASS);
        // Glass Panes
        nonSuffocatingBlocks.add(Material.GLASS_PANE);
        nonSuffocatingBlocks.add(Material.GRAY_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.GREEN_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.BLACK_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.BLUE_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.BROWN_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.CYAN_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.LIME_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.MAGENTA_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.ORANGE_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.PINK_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.PURPLE_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.RED_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.WHITE_STAINED_GLASS_PANE);
        nonSuffocatingBlocks.add(Material.YELLOW_STAINED_GLASS_PANE);
        // Leaves
        nonSuffocatingBlocks.add(Material.ACACIA_LEAVES);
        nonSuffocatingBlocks.add(Material.BIRCH_LEAVES);
        nonSuffocatingBlocks.add(Material.DARK_OAK_LEAVES);
        nonSuffocatingBlocks.add(Material.JUNGLE_LEAVES);
        nonSuffocatingBlocks.add(Material.OAK_LEAVES);
        nonSuffocatingBlocks.add(Material.SPRUCE_LEAVES);
        if(Bukkit.getServer().getVersion().contains("1.17")) {
            nonSuffocatingBlocks.add(Material.AZALEA_LEAVES);
            nonSuffocatingBlocks.add(Material.FLOWERING_AZALEA_LEAVES);
        }
        // Slabs
        nonSuffocatingBlocks.add(Material.SANDSTONE_SLAB);
        nonSuffocatingBlocks.add(Material.SMOOTH_SANDSTONE_SLAB);
        nonSuffocatingBlocks.add(Material.QUARTZ_SLAB);
        nonSuffocatingBlocks.add(Material.SMOOTH_QUARTZ_SLAB);
        nonSuffocatingBlocks.add(Material.ACACIA_SLAB);
        nonSuffocatingBlocks.add(Material.RED_SANDSTONE_SLAB);
        nonSuffocatingBlocks.add(Material.SMOOTH_RED_SANDSTONE_SLAB);
        nonSuffocatingBlocks.add(Material.CUT_RED_SANDSTONE_SLAB);
        nonSuffocatingBlocks.add(Material.SPRUCE_SLAB);
        nonSuffocatingBlocks.add(Material.ACACIA_SLAB);
        nonSuffocatingBlocks.add(Material.BIRCH_SLAB);
        nonSuffocatingBlocks.add(Material.OAK_SLAB);
        nonSuffocatingBlocks.add(Material.DARK_OAK_SLAB);
        nonSuffocatingBlocks.add(Material.JUNGLE_SLAB);
        nonSuffocatingBlocks.add(Material.CRIMSON_SLAB);
        nonSuffocatingBlocks.add(Material.WARPED_SLAB);
        nonSuffocatingBlocks.add(Material.STONE_SLAB);
        nonSuffocatingBlocks.add(Material.COBBLESTONE_SLAB);
        nonSuffocatingBlocks.add(Material.MOSSY_COBBLESTONE_SLAB);
        nonSuffocatingBlocks.add(Material.SMOOTH_STONE_SLAB);
        // Walled Signs
        nonSuffocatingBlocks.add(Material.ACACIA_WALL_SIGN);
        nonSuffocatingBlocks.add(Material.SPRUCE_WALL_SIGN);
        nonSuffocatingBlocks.add(Material.BIRCH_WALL_SIGN);
        nonSuffocatingBlocks.add(Material.CRIMSON_WALL_SIGN);
        nonSuffocatingBlocks.add(Material.WARPED_WALL_SIGN);
        nonSuffocatingBlocks.add(Material.DARK_OAK_WALL_SIGN);
        nonSuffocatingBlocks.add(Material.JUNGLE_WALL_SIGN);
        nonSuffocatingBlocks.add(Material.OAK_WALL_SIGN);
        // Stairs
        nonSuffocatingBlocks.add(Material.SANDSTONE_STAIRS);
//        Collections.addAll(nonSuffocatingBlocks,
//                Arrays.asList(Arrays.stream(Material.values()).filter(material -> {
//            return material.toString().toLowerCase().contains("_stairs");
//        })));
        // MISC
        nonSuffocatingBlocks.add(Material.HONEY_BLOCK);
        nonSuffocatingBlocks.add(Material.BELL);
        nonSuffocatingBlocks.add(Material.CHEST);
        nonSuffocatingBlocks.add(Material.TRAPPED_CHEST);
        nonSuffocatingBlocks.add(Material.HOPPER);
        nonSuffocatingBlocks.add(Material.COMPOSTER);
        nonSuffocatingBlocks.add(Material.GRINDSTONE);
        nonSuffocatingBlocks.add(Material.STONECUTTER);
        nonSuffocatingBlocks.add(Material.IRON_BARS);
        nonSuffocatingBlocks.add(Material.END_PORTAL_FRAME);
        nonSuffocatingBlocks.add(Material.PISTON_HEAD);
    }

    @Nullable @Deprecated
    public static Location findSafeLocationAroundShop(Location shopLocation, Player player) throws Exception {
        Location possibleSafeLoc = LocationUtil.getSafeDestination(shopLocation);
        Location blockBelow = new Location(
                possibleSafeLoc.getWorld(),
                possibleSafeLoc.getX(),
                possibleSafeLoc.getY() - 1,
                possibleSafeLoc.getZ());
        Location blockBelowIfAir = new Location(
                possibleSafeLoc.getWorld(),
                possibleSafeLoc.getX(),
                possibleSafeLoc.getY() - 2,
                possibleSafeLoc.getZ());
        if(blockBelow.getBlock().getType().equals(
                Material.getMaterial(
                        Objects.requireNonNull(
                                QuickShop.getInstance().getConfig().getString("shop.sign-material"))))) {
            Location blockBelowShopSign = LocationUtil.getRoundedDestination(new Location(
                    blockBelow.getWorld(),
                    blockBelow.getBlockX(),
                    blockBelow.getBlockY() - 1,
                    blockBelow.getBlockZ()));
            if(!LocationUtil.isBlockDamaging(
                    blockBelowShopSign.getWorld(),
                    blockBelowShopSign.getBlockX(),
                    blockBelowShopSign.getBlockY(),
                    blockBelowShopSign.getBlockZ()
            )) {
                Location locToTeleport = new Location(
                        possibleSafeLoc.getWorld(),
                        possibleSafeLoc.getBlockX(),
                        possibleSafeLoc.getBlockY(),
                        possibleSafeLoc.getBlockZ(),
                        lookAt(player.getLocation(), shopLocation).getYaw(),
                        lookAt(player.getLocation(), shopLocation).getPitch()
                );
                return locToTeleport;
            }
            else {
                return null;
            }
        }
        else if(!blockBelowIfAir.getBlock().getType().equals(Material.AIR)){
            Location locToTeleport = new Location(
                    possibleSafeLoc.getWorld(),
                    possibleSafeLoc.getBlockX(),
                    possibleSafeLoc.getBlockY(),
                    possibleSafeLoc.getBlockZ(),
                    lookAt(player.getLocation(), shopLocation).getYaw(),
                    lookAt(player.getLocation(), shopLocation).getPitch()
            );
            return locToTeleport;
        }
        else {
            return null;
        }
    }

    @Nullable
    public static Location findSafeLocationAroundShop(Location shopLocation) {

        Location roundedShopLoc = LocationUtil.getRoundedDestination(shopLocation);
        LoggerUtils.logDebugInfo("Rounded location: " + roundedShopLoc.getX() + ", " + roundedShopLoc.getY() + ", " + roundedShopLoc.getZ());
        // Creating a list of four block locations in 4 sides of the shop
        List<Location> possibleSafeLocList = new ArrayList<>();
        possibleSafeLocList.add(new Location(
                roundedShopLoc.getWorld(),
                roundedShopLoc.getX() + 1,
                roundedShopLoc.getY(),
                roundedShopLoc.getZ()
        ));
        possibleSafeLocList.add(new Location(
                roundedShopLoc.getWorld(),
                roundedShopLoc.getX() - 1,
                roundedShopLoc.getY(),
                roundedShopLoc.getZ()
        ));
        possibleSafeLocList.add(new Location(
                roundedShopLoc.getWorld(),
                roundedShopLoc.getX(),
                roundedShopLoc.getY(),
                roundedShopLoc.getZ() + 1
        ));
        possibleSafeLocList.add(new Location(
                roundedShopLoc.getWorld(),
                roundedShopLoc.getX(),
                roundedShopLoc.getY(),
                roundedShopLoc.getZ() - 1
        ));
        for(Location loc_i : possibleSafeLocList) {
            LoggerUtils.logDebugInfo("Possible safe location: " + loc_i.getX() + ", " + loc_i.getY() + ", " + loc_i.getZ());
            if(loc_i.getBlock().getType().equals(QuickShopAPIHandler.getShopSignMaterial())) {
                LoggerUtils.logDebugInfo("Shop sign block found at " + loc_i.getX() + ", " + loc_i.getY() + ", " + loc_i.getZ());
                // check if the block above is suffocating
                Location blockAbove = new Location(
                        loc_i.getWorld(),
                        loc_i.getBlockX(),
                        loc_i.getBlockY() + 1,
                        loc_i.getBlockZ());
                LoggerUtils.logDebugInfo("Block above shop sign: " + blockAbove.getX() + ", " + blockAbove.getY() + ", " + blockAbove.getZ());
                if(!isBlockSuffocating(blockAbove)) {
                    Location blockBelow = null;
                    boolean safeLocFound = false;
                    for(int i = 1; i <= BELOW_SAFE_BLOCK_CHECK_LIMIT; i++) {
                        blockBelow = new Location(
                                loc_i.getWorld(),
                                loc_i.getBlockX(),
                                loc_i.getBlockY() - i,
                                loc_i.getBlockZ()
                        );
                        LoggerUtils.logDebugInfo("Block below shop sign: " + blockBelow.getX() + ", " + blockBelow.getY() + ", " + blockBelow.getZ());
                        if(!isBlockDamaging(blockBelow)) {
                            LoggerUtils.logDebugInfo("Safe block found!");
                            safeLocFound = true;
                            break;
                        }
                    }
                    if(safeLocFound) {
                        loc_i = lookAt(LocationUtil.getRoundedDestination(new Location(
                                blockBelow.getWorld(),
                                blockBelow.getX(),
                                blockBelow.getY() + 1,
                                blockBelow.getZ()
                        )), roundedShopLoc);
                        return loc_i;
                    }
                    else {
                        return null;
                    }
//                    Location blockBelow = new Location(
//                            loc_i.getWorld(),
//                            loc_i.getBlockX(),
//                            loc_i.getBlockY() - 1,
//                            loc_i.getBlockZ()
//                    );
//                    LoggerUtils.logDebugInfo("Block below shop sign: " + blockBelow.getX() + ", " + blockBelow.getY() + ", " + blockBelow.getZ());
                    // check if the block below is not damaging
//                    if(!isBlockDamaging(blockBelow)) {
//                        loc_i = lookAt(loc_i, roundedShopLoc);
//                        return loc_i;
//                    }
//                    else {
//                        LoggerUtils.logDebugInfo("Block below shop sign found damaging. Block type: " + blockBelow.getBlock().getType());
//                        return null;
//                    }
                }
                else {
                    LoggerUtils.logDebugInfo("Block above shop sign found not air. Block type: " + blockAbove.getBlock().getType());
                    return null;
                }
            }
            else {
                LoggerUtils.logDebugInfo("Block not shop sign. Block type: " + loc_i.getBlock().getType());
            }
        }
        LoggerUtils.logDebugInfo("No safe block found near shop");
        return null;
    }

    // Found the below function from this thread: https://bukkit.org/threads/lookat-and-move-functions.26768/
    private static Location lookAt(Location loc, Location lookat) {
        //Clone the loc to prevent applied changes to the input loc
        loc = loc.clone();

        // Values of change in distance (make it relative)
        double dx = lookat.getX() - loc.getX();
        double dy = lookat.getY() - loc.getY();
        double dz = lookat.getZ() - loc.getZ();

        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                loc.setYaw((float) (1.5 * Math.PI));
            } else {
                loc.setYaw((float) (0.5 * Math.PI));
            }
            loc.setYaw((float) loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }

        // Get the distance from dx/dz
        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

        // Set pitch
        loc.setPitch((float) -Math.atan(dy / dxz));

        // Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
        loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

        return loc;
    }

    private static boolean isBlockDamaging(Location loc) {
        return damagingBlocks.contains(loc.getBlock().getType());
    }

    private static boolean isBlockSuffocating(Location loc) {
        return !nonSuffocatingBlocks.contains(loc.getBlock().getType());
    }
}
