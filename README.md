# Shop Search AddOn For QuickShop
### Version: 2.0.5.4-RELEASE

An unofficial add-on for the QuickShop Hikari and Reremake spigot plugin.
Adds a `/finditem` command in game for searching through all the shops on the server.

![MC](https://img.shields.io/badge/Minecraft-Java%20Edition:%201.16.5%20--%201.20.4-brightgreen)
![Ver](https://img.shields.io/spiget/version/95104?label=Current%20Spigot%20Version)

## Features
- Search items based on buying/selling shops
- Search items by item type
- Supports query based item search
- Supports item custom model data for custom items
- Configurable shop sorting methods
- Displays enchantments in the result for enchanted items
- Hides item enchants if item has hide_enchants flag
- Displays potion effects in the result for Potion items
- Hide certain shops from appearing in search lists
- Supports completely safe direct shop teleportation (configurable in config.yml)
- Ignores shops that are out of stock by default
- Support for world blacklisting (Shops in blacklisted worlds are ignored in the search result)


**QuickShop-Hikari Support**
- It supports both [QuickShop-Reremake](https://www.spigotmc.org/resources/62575/) and [QuickShop-Hikari](https://www.spigotmc.org/resources/100125/).

**Hexcode color support**
- All messages in the config.yml support hexcodes.

**View all shops on server**
- You can do `/finditem TO_BUY *` or `/finditem TO_SELL *` to view all shops on the server. As of this version, the sequence is always randomized. Sorting options are a work in progress.

**Shop visit count**
- You can choose to display shop visits count in the shop lore in Search GUI. Just add the placeholder `{SHOP_VISITS}` in the `shop-gui-item-lore` in config.yml. To prevent visit spamming, a new config property `shop-player-visit-cooldown-in-minutes` has been added. Please don't use decimals here. 😁

**Customizable command aliases**
- You can find a property in config.yml called `command-alias` where you can specify your own list of command aliases for /finditem command. If you don't wish to add any, just make it as:
```yaml
command-alias: []
```

## Integrations
- Supports [PlayerWarps](https://www.spigotmc.org/resources/66692/) (by Olzie-12) integration. It shows the nearest warp to each shop in the search result GUI.
- Supports EssentialsX Warps integration for fetching nearest warps.
    - Global warps list for essentials is updated in batches every 15 minutes due to technical limitations, which is then used in every search query.
    - If you added a new warp and want it to get updated immediately, run **/finditemadmin reload**
    - Remember, this applies only to Essential Warps.
- WorldGuard region support for fetching the WorldGuard region the shop is in (if overlapping regions, highest priority will be chosen)

>Check out the sample config.yml [here](https://github.com/myzticbean/QSFindItemAddOn/wiki/Sample-config.yml).

## Requires
- [QuickShop-Hikari](https://www.spigotmc.org/resources/100125/) v5.2 or higher

**OR**

- [QuickShop Reremake](https://www.spigotmc.org/resources/62575/) 5.1 or higher (Use add-on v1.7 for QuickShop version 4.0.*)

## Assumptions
- A compatible economy plugin is installed
- [Vault](https://www.spigotmc.org/resources/34315/) 1.7.3 or higher is installed

## Issue Tracking
Please create a new issue [here](https://github.com/myzticbean/QSFindItemAddOn/issues) if you encounter any errors. Please try to explain in detail about your issue, and attach your console log if possible.
You can also find the list of features currently being worked on (if any) in the same link.

## How to contribute?
See more details [here](https://github.com/myzticbean/QSFindItemAddOn/blob/master/CONTRIBUTING.md).

## BStats Metrics
[![BigImage](https://bstats.org/signatures/bukkit/QSFindItemAddOn.svg)](https://bstats.org/plugin/bukkit/QSFindItemAddOn/12382)

## Demo
### How to use `/finditem`?
![alt text](https://media.discordapp.net/attachments/1199729484518129775/1208709408658300938/upload_2021-8-22_15-46-29-png.png?ex=65e4455b&is=65d1d05b&hm=cb2b185313ce1c77603490523a5a51cadd588ad4cdbe476549116301b1dd070f&=&format=webp&quality=lossless&width=1000&height=652)
![alt text](https://media.discordapp.net/attachments/1199729484518129775/1208709440866357278/upload_2021-8-22_15-46-34-png.png?ex=65e44563&is=65d1d063&hm=7bd25579f371bf5c5c0b32228a8149dfa17efa0ee39197625e6470b9c5c28fd6&=&format=webp&quality=lossless&width=1000&height=652)
### Multiple search result pages:
![alt text](https://media.discordapp.net/attachments/1199729484518129775/1208709475633205308/upload_2021-8-22_15-50-28-png.png?ex=65e4456b&is=65d1d06b&hm=dc2a5a69bd6dd9ffe28075044075cd06c8ba85dfabca4dd6a2f53eda65380c45&=&format=webp&quality=lossless&width=1000&height=576)
### Shows item enchantments:
![alt text](https://media.discordapp.net/attachments/1199729484518129775/1208709518947524628/upload_2021-8-22_15-47-10-png.png?ex=65e44576&is=65d1d076&hm=7acb37cea96609ab3825bb64d37736aa9738067f9f98f9d391868e9e1276795a&=&format=webp&quality=lossless&width=1000&height=484)
### Shows Potion colors and effects:
![alt text](https://media.discordapp.net/attachments/1199729484518129775/1208709551872937995/upload_2021-8-22_15-52-40-png.png?ex=65e4457d&is=65d1d07d&hm=18331b5f4fc69c7ed8e8706e78dc0e700fb130970c55407a955f2cd9902e1c13&=&format=webp&quality=lossless&width=1000&height=432)
### Shows custom item names and lore:
![alt text](https://media.discordapp.net/attachments/1199729484518129775/1208709590997409802/upload_2021-8-22_15-47-1-png.png?ex=65e44587&is=65d1d087&hm=5a0254d97bb043197ecd1df5409d08d45d62fd117e635c22bca69c53f3b75519&=&format=webp&quality=lossless&width=1000&height=504)