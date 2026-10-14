package io.myzticbean.finditemaddon.handlers.command;

import com.ghostchu.quickshop.api.shop.Shop;
import io.myzticbean.finditemaddon.FakeBukkit;
import io.myzticbean.finditemaddon.FindItemAddOn;
import io.myzticbean.finditemaddon.config.ConfigProvider;
import io.myzticbean.finditemaddon.models.enums.PlayerPermsEnum;
import io.myzticbean.finditemaddon.quickshop.impl.QSHikariAPIHandler;
import io.myzticbean.finditemaddon.utils.json.HiddenShopStorageUtil;
import io.myzticbean.finditemaddon.utils.platform.PlatformBridge;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.UUID;

import static org.mockito.Mockito.*;

class CmdExecutorHandlerHideRevealTest {

    private static final UUID OWNER_ID = UUID.randomUUID();

    private MockedStatic<FindItemAddOn> plugin;
    private MockedStatic<HiddenShopStorageUtil> storage;
    private PlatformBridge bridge;
    private Shop shop;
    private final CmdExecutorHandler handler = new CmdExecutorHandler();

    @BeforeAll
    static void installServer() {
        FakeBukkit.install();
    }

    @BeforeEach
    void setUp() {
        shop = mock(Shop.class, RETURNS_DEEP_STUBS);
        when(shop.getOwner().getUniqueId()).thenReturn(OWNER_ID);

        QSHikariAPIHandler qsApi = mock(QSHikariAPIHandler.class);
        when(qsApi.findShopAtLocation(any())).thenReturn(shop);
        when(qsApi.isShopOwnerCommandRunner(any(), any())).thenCallRealMethod();

        bridge = mock(PlatformBridge.class);
        ConfigProvider config = mock(ConfigProvider.class);

        plugin = mockStatic(FindItemAddOn.class);
        plugin.when(FindItemAddOn::getQsApiInstance).thenAnswer(i -> qsApi);
        plugin.when(FindItemAddOn::getConfigProvider).thenReturn(config);
        plugin.when(FindItemAddOn::getPlatformBridge).thenReturn(bridge);

        storage = mockStatic(HiddenShopStorageUtil.class);
    }

    @AfterEach
    void tearDown() {
        storage.close();
        plugin.close();
    }

    private Player player(UUID id, PlayerPermsEnum... perms) {
        Player p = mock(Player.class);
        when(p.getUniqueId()).thenReturn(id);
        when(p.getTargetBlock(any(), anyInt())).thenReturn(mock(Block.class));
        for (PlayerPermsEnum perm : perms) {
            when(p.hasPermission(perm.value())).thenReturn(true);
        }
        return p;
    }

    private void shopHidden(boolean hidden) {
        storage.when(() -> HiddenShopStorageUtil.isShopHidden(any(Shop.class))).thenReturn(hidden);
    }

    private void verifyVisibilitySetTo(boolean hidden) {
        storage.verify(() -> HiddenShopStorageUtil.handleShopSearchVisibilityAsync(shop, hidden));
    }

    private void verifyVisibilityUntouched() {
        storage.verify(() -> HiddenShopStorageUtil.handleShopSearchVisibilityAsync(any(Shop.class), anyBoolean()), never());
    }

    @Test
    void ownerCanHideOwnShop() {
        shopHidden(false);
        handler.handleHideShop(player(OWNER_ID, PlayerPermsEnum.FINDITEM_HIDESHOP));
        verifyVisibilitySetTo(true);
    }

    @Test
    void nonOwnerCannotHideOthersShop() {
        shopHidden(false);
        handler.handleHideShop(player(UUID.randomUUID(), PlayerPermsEnum.FINDITEM_HIDESHOP));
        verifyVisibilityUntouched();
    }

    @Test
    void adminCanHideOthersShop() {
        shopHidden(false);
        handler.handleHideShop(player(UUID.randomUUID(), PlayerPermsEnum.FINDITEM_ADMIN));
        verifyVisibilitySetTo(true);
    }

    @Test
    void adminCanRevealOthersShop() {
        shopHidden(true);
        handler.handleRevealShop(player(UUID.randomUUID(), PlayerPermsEnum.FINDITEM_ADMIN));
        verifyVisibilitySetTo(false);
    }

    @Test
    void nonOwnerCannotRevealOthersShop() {
        shopHidden(true);
        handler.handleRevealShop(player(UUID.randomUUID(), PlayerPermsEnum.FINDITEM_HIDESHOP));
        verifyVisibilityUntouched();
    }

    @Test
    void playerWithoutPermissionIsRejected() {
        Player p = player(OWNER_ID);
        handler.handleHideShop(p);
        verifyVisibilityUntouched();
        verify(bridge).sendMessage(eq(p), contains(CmdExecutorHandler.NO_PERMISSION));
    }
}
