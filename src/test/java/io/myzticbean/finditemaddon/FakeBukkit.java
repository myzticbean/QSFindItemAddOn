package io.myzticbean.finditemaddon;

import com.ghostchu.quickshop.api.QuickShopProvider;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.Field;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Installs a mocked Bukkit {@link Server} for tests. QuickShop-Hikari's API interfaces
 * (e.g. {@code Shop}) look up the plugin via {@code Bukkit.getServicesManager()} in their
 * static initializers, so they can't even be mocked until a server is present.
 */
public final class FakeBukkit {

    private FakeBukkit() {}

    /** Safe to call from every test class. */
    public static synchronized void install() {
        if (Bukkit.getServer() != null) {
            return;
        }
        // Bukkit.setServer() can't be used: Paper's version logging needs real build metadata
        try {
            Field server = Bukkit.class.getDeclaredField("server");
            server.setAccessible(true);
            Server fake = mock(Server.class, RETURNS_DEEP_STUBS);
            // Shop.SHOP_NAMESPACED_KEY is built from the registered QuickShop plugin's namespace
            when(fake.getServicesManager().getRegistration(QuickShopProvider.class).getPlugin().namespace())
                    .thenReturn("quickshop-hikari");
            server.set(null, fake);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not install fake Bukkit server", e);
        }
    }
}
