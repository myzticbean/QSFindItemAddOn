package uk.mangostudios.finditemaddon.cache;

import com.ghostchu.quickshop.api.shop.Shop;
import org.checkerframework.checker.nullness.qual.Nullable;
import uk.mangostudios.finditemaddon.FindItemAddOn;
import uk.mangostudios.finditemaddon.external.QuickShopHandler;
import uk.mangostudios.finditemaddon.storage.HiddenShopsStorage;
import uk.mangostudios.finditemaddon.storage.impl.FinePosition;
import uk.mangostudios.finditemaddon.util.Colourify;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HiddenShopsCache {

    private static HiddenShopsCache instance;

    private final Map<UUID, FinePosition> hiddenShops = new ConcurrentHashMap<>();
    private final HiddenShopsStorage hiddenShopsStorage;

    public HiddenShopsCache(FindItemAddOn plugin) {
        this.hiddenShopsStorage = new HiddenShopsStorage(plugin);
        hiddenShopsStorage.load().thenAccept(hiddenShops::putAll);
        instance = this;
    }

    public void shutdown() {
        hiddenShopsStorage.saveAll(hiddenShops);
    }

    public void hideShop(Player player, @Nullable Location shopLocation) {
        if (shopLocation == null) shopLocation = player.getTargetBlock(null, 5).getLocation();
        FinePosition finePosition = new FinePosition(shopLocation.getX(), shopLocation.getY(), shopLocation.getZ(), shopLocation.getWorld().getName());
        Shop shop = QuickShopHandler.getInstance().findShopAtLocation(shopLocation);

        if (shop == null) {
            player.sendMessage(Colourify.colour(FindItemAddOn.getConfigProvider().NOT_LOOKING_AT_SHOP_MSG));
            return;
        }

        if (!(shop.getOwner().getUniqueId().equals(player.getUniqueId()))) {
            player.sendMessage(Colourify.colour(FindItemAddOn.getConfigProvider().NOT_YOUR_SHOP_MSG));
            return;
        }

        hiddenShops.put(player.getUniqueId(), finePosition);
        player.sendMessage(Colourify.colour(FindItemAddOn.getConfigProvider().HIDDEN_SHOP_MSG));
    }

    public void unhideShop(Player player, @Nullable Location shopLocation) {
        if (shopLocation == null) shopLocation = player.getTargetBlock(null, 5).getLocation();
        FinePosition finePosition = new FinePosition(shopLocation.getX(), shopLocation.getY(), shopLocation.getZ(), shopLocation.getWorld().getName());
        Shop shop = QuickShopHandler.getInstance().findShopAtLocation(shopLocation);

        if (shop == null) {
            player.sendMessage(Colourify.colour(FindItemAddOn.getConfigProvider().NOT_LOOKING_AT_SHOP_MSG));
            return;
        }

        if (!(shop.getOwner().getUniqueId().equals(player.getUniqueId()))) {
            player.sendMessage(Colourify.colour(FindItemAddOn.getConfigProvider().NOT_YOUR_SHOP_MSG));
            return;
        }

        hiddenShops.remove(player.getUniqueId(), finePosition);
        player.sendMessage(Colourify.colour(FindItemAddOn.getConfigProvider().UNHIDDEN_SHOP_MSG));
    }

    public boolean isShopHidden(Player player, Location shopLocation) {
        FinePosition finePosition = hiddenShops.get(player.getUniqueId());
        return finePosition != null && finePosition.equals(new FinePosition(shopLocation.getX(), shopLocation.getY(), shopLocation.getZ(), shopLocation.getWorld().getName()));
    }

    public static HiddenShopsCache getInstance() {
        return instance;
    }

}
