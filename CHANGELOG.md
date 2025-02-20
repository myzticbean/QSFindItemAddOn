## Release 2.0.7.4
### Bug fixes
- Fix for /finditem shows only 'searching...' but no GUI (PR #69)

## Release 2.0.7.3
### Changes
- JDK version upgraded from 17 to 21
- Added check to exclude shops of `buying` shops for owners who don't have enough money.
- Bentobox integration to ignore shops in locked islands

### Bug fixes
- Hideshop/revealshop should work with double chests also

## Release 2.0.7.2
### Bug fixes
- Fixed item meta not showing correctly in Search GUI

## Release 2.0.7.1
### Changes
- Added config option to suppress update notifications in chat
  ```yaml
  suppress-update-notifications: false
  ```
- Added config option to configure blacklisted materials
  ```yaml
  blacklisted-materials:
  - "BARRIER"
  - "STRUCTURE_BLOCK"
  - "COMMAND_BLOCK"
  ```

## Release 2.0.7.0
### Changes
- Added runnable custom commands on GUI item click. New related config options added.
- Added a new permission to bypass safety check for shop tp.
- MAJOR code refactoring in FoundShopsMenu.java to make it human readable.. finally!
- Added publish-release.yml github action 

## Release 2.0.6.4
### Changes
- Residence support for fetching the residence the shop is in (including subzones) ([#45](https://github.com/myzticbean/QSFindItemAddOn/pull/45)) [Contribution by SengyEU]
- Configurable "No ... near shop" messages ([#45](https://github.com/myzticbean/QSFindItemAddOn/pull/45)) [Contribution by SengyEU]
- Spigot API updated to 1.21.

### Bug fixes
- Fixed NPE when putting AIR as a filler item ([#46](https://github.com/myzticbean/QSFindItemAddOn/pull/46)) [Contribution by SengyEU]
- Fixed bug where exception was thrown when trying to render player heads in GUI buttons (Broke in 1.21 update)

## Release 2.0.6.3
### Changes
- Added stack size placeholder to shop lore ([#37](https://github.com/myzticbean/QSFindItemAddOn/issues/37))
- Added a 500 block limit for searching nearby PlayerWarp (@TODO: to be made configurable in a future version)

## Release 2.0.6.2
### Changes
- Added option to show only player owned warps in GUI ([#38](https://github.com/myzticbean/QSFindItemAddOn/issues/38)) [Contribution by lukemango]

## Release 2.0.6.1
### Bug fixes
- Changed scheduled task loggers to debug-mode only ([#29](https://github.com/myzticbean/QSFindItemAddOn/issues/29))
- Fix for players not getting teleported ([#30](https://github.com/myzticbean/QSFindItemAddOn/issues/30))

## Release 2.0.6.0
### Changes (Feature request [#23](https://github.com/myzticbean/QSFindItemAddOn/issues/23))
- Plugin updated for Minecraft version 1.20.5.
- Changed 15Min Scheduled Task to run in async thread (should help with lag).
- Deprecated QSReremakeAPIHandler. Reremake implementation will no longer be updated. Last support is for QS-Reremake v5.1.2.5.
- Changed logger statements to show Main/Async thread state.
- Structural changes in the config.yml, specifically for shop-gui related options.
- Fixed an issue with showing large numbers in currency.
- Added new feature to format currency format. For example: $10,210,100 will be shown as $10.21M. New config option to toggle it:
```yaml
shop-gui:
  use-shorter-currency-format: true
```
- New config option added as below to toggle if players should be allowed to warp to locked warps (Only for PlayerWarps).
```yaml
player-shop-teleportation:
  nearest-warp-tp-mode:
    do-not-tp-if-warp-locked: true
```
- Config version is now 15.

## Release 2.0.5.7
### Bug fixes
- Fixed a bug where disabling hideshop/revealshop will not disable it for `/qs finditem` ([#13](https://github.com/myzticbean/QSFindItemAddOn/issues/13))
- Fixed stock/space not updating properly from QS cache ([#17](https://github.com/myzticbean/QSFindItemAddOn/issues/17))

## Release 2.0.5.6
### Bug fixes
- Fixed a code refactoring bug where search all shops would return no shops for Quickshop Reremake
- Added a INFO level logger to log time taken for a search process
- Fix the lag issue caused due to new QS-Hikari cache feature impl ([#12](https://github.com/myzticbean/QSFindItemAddOn/issues/12))

## Release 2.0.5.5
### Bug fixes
- Fixed /finditem command not working in some cases ([#9](https://github.com/myzticbean/QSFindItemAddOn/issues/9))
- Added a config option to disable the `/finditem {TO_BUY | TO_SELL} *`

## Release 2.0.5.4
### Features
- Added Go to First and Last page GUI buttons ([#3](https://github.com/myzticbean/QSFindItemAddOn/issues/3))
- Added new config option to include empty chests in the search result ([#3](https://github.com/myzticbean/QSFindItemAddOn/issues/3))
- Added compatibility with QS Hikari 6.* shop stock/space fetch from cache feature. This should help prevent massive lag spikes.
### Bug fixes
- Full/out of space chests will be excluded from `/finditem TO-SELL <item_name>` ([#3](https://github.com/myzticbean/QSFindItemAddOn/issues/3))
- Bug fix to prevent lag when during `/finditem TO_BUY *` on large servers (Only for QS Hikari)
- Fix for issue [#5](https://github.com/myzticbean/QSFindItemAddOn/issues/5)

## Release 2.0.5.3
### Features
- Added shop TP delay ([#28](https://gitlab.com/ronsane/QSFindItemAddOn/-/issues/28))
### Bug fixes
- Fix for issue [#31](https://gitlab.com/ronsane/QSFindItemAddOn/-/issues/31)

## Release 2.0.5.2
### Plugin updates
- Updated the addon to make it compatible with the recent QuickShop and other dependency updates.

## Release 2.0.5.1
### Plugin updates
- Updated QuickShop-Hikari dependency to v4.0.0.0
- Updated PlayerWarps dependency to v6.18.1
- Updated QuickShop-Reremake dependency to v5.1.1.2
### Bug fixes
- Fixed issue [#25](https://gitlab.com/ronsane/QSFindItemAddOn/-/issues/25)

## Release 2.0.5.0
### Bug fixes
- Fix for Issue [#24](https://gitlab.com/ronsane/QSFindItemAddOn/-/issues/24)
- Fixed a bug related to nearest warp teleportation algorithm
- Other minor bug fixes

## Snapshot 2.0
### Changes
- **QuickShop-Hikari Support:** It now supports both QuickShop-Reremake and QuickShop-Hikari.
- **Hexcode color support:** All messages in the config.yml now support hexcodes so you can get a lot more creative with colors.
- **Different commands for players and admins**
  - The /finditem command is now split into two:
    - `/finditem` - This will support the usual shop searches and hiding
    - `/finditemadmin` or `/fiadmin` - This will support plugin reloads. For example: `/fiadmin reload`.
- **View all shops on server:** Now you can do `/finditem TO_BUY *` or `/finditem TO_SELL *` to view all shops on the server. As of this version, the sequence is always randomized. Sorting options are a work in progress.
- **New shop visit count:** Now you can choose to display shop visits count in the shop lore in Search GUI. Just add the placeholder {SHOP_VISITS} in the "shop-gui-item-lore" in config.yml. To prevent visit spamming, a new config property "shop-player-visit-cooldown-in-minutes" has been added. Please don't use decimals here LOL.
- **New storage format:** The "hiddenShops.json" will automatically be converted to a new file called "shops.json" which will store data of all shops along with their player visit count and whether they are hidden from searches.
- **New Next/Back buttons:** The default next/back buttons are now fancy playerheads to make more sense. If you don't wish to use them, just specify the material in the config.yml.
- **Customizable command aliases:** Added a new property in config.yml called "command-alias" where you can specify your own list of command aliases for /finditem command. If you don't wish to add any, just make it as:
```yaml
command-alias: []
```
- **Compatibility with QuickShop-Hikari v2.0.0.0:** Added support for Per Shop permission management for "quickshop-hikari.search". If for a shop, the shop owner Steve configures Alex to be in a group that does not have "quickshop-hikari.search" permission, then Alex won't be able to see that shop in GUI when searching.
- **Added finditem sub-command for /qs:** Now you can try the below commands, same functionality. This feature removes the original /qs find sub-command to avoid confusion among players.
- Added a new sample-config.yml file which will be copied over to the plugin folder. This file is just for your reference when editing the fields in the actual config.yml file.

### Bug fixes
- Removed an erroneous config field "warp-player-to-nearest-warp".
- Fixed an issue where it will load up the plugin before QuickShop is done loading causing it to disable itself. Thanks Baran for reporting :)
- Added support for EssentialsX /back.
- Fixed a bug that broke autocompletes for all commands in-game.
- Made minor bug fixes when enabling plugin.
- Made some fixes in the search system for shops that are buying items.
- Some other minor stability fixes.
- Fixed bug in which the new `/qs finditem` sub-command was not showing up in `/qs help`.
- Fixed a NullPointerException bug for PlayerWarps plugin integration.
- Updated PlayerWarps API to latest version and fixed a NPE console error