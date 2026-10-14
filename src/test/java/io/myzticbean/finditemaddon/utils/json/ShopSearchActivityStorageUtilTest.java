package io.myzticbean.finditemaddon.utils.json;

import com.ghostchu.quickshop.api.shop.Shop;
import io.myzticbean.finditemaddon.FakeBukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The location index must answer exactly like the linear {@code compareWith} scans it replaced:
 * case-insensitive world name, exact coordinates, first entry wins, and gone once the shop is removed.
 */
class ShopSearchActivityStorageUtilTest {

    @BeforeAll
    static void installServer() {
        FakeBukkit.install();
    }

    /** Location only weakly references its World, so mocks must be held here or GC makes getWorld() throw "World unloaded". */
    private static final Map<String, World> WORLDS = new HashMap<>();

    private static Location location(String worldName, double x, double y, double z) {
        World world = WORLDS.computeIfAbsent(worldName, name -> {
            World w = mock(World.class);
            when(w.getName()).thenReturn(name);
            return w;
        });
        return new Location(world, x, y, z);
    }

    private static Shop shopAt(Location location) {
        Shop shop = mock(Shop.class, RETURNS_DEEP_STUBS);
        when(shop.bukkitLocation()).thenReturn(location);
        return shop;
    }

    @Test
    void indexMatchesCompareWithSemanticsAndTracksAddRemove() {
        Shop shop = shopAt(location("Survival", 10, 64, 0.0));
        ShopSearchActivityStorageUtil.addShop(shop);
        try {
            // Different world-name case and -0.0 vs 0.0 still match, as with compareWith()
            Location lookup = location("survival", 10, 64, -0.0);
            assertNotNull(ShopSearchActivityStorageUtil.findShop(lookup));
            assertNull(ShopSearchActivityStorageUtil.findShop(location("survival", 10, 65, 0)));
            assertNull(ShopSearchActivityStorageUtil.findShop(location("nether", 10, 64, 0)));

            // First entry at a location wins, like the old first-match scan
            var first = ShopSearchActivityStorageUtil.findShop(lookup);
            ShopSearchActivityStorageUtil.addShop(shopAt(location("SURVIVAL", 10, 64, 0)));
            assertSame(first, ShopSearchActivityStorageUtil.findShop(lookup));

            assertFalse(HiddenShopStorageUtil.isShopHidden(shop));
            first.setHiddenFromSearch(true);
            assertTrue(HiddenShopStorageUtil.isShopHidden(shop));
            assertTrue(ShopSearchActivityStorageUtil.getPlayerVisitCount(lookup) == 0);
        } finally {
            ShopSearchActivityStorageUtil.removeShop(shop);
        }
        assertNull(ShopSearchActivityStorageUtil.findShop(location("survival", 10, 64, 0)));
        assertTrue(ShopSearchActivityStorageUtil.getGlobalShopsList().isEmpty());
    }
}
