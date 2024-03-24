## Snapshot 2.0.5.6
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
