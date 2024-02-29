# Shop Search AddOn For QuickShop
### Version: ${project.version}

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
![alt text](https://media.discordapp.net/attachments/875008602706956368/1195618006512959518/upload_2021-8-22_15-46-29-png.png?ex=65b4a50a&is=65a2300a&hm=470a64e63d7346eee58faaf0ab41502e5ffb10d6e9a35c113c5d310237e7d820&=&format=webp&quality=lossless&width=1000&height=652)
![alt text](https://media.discordapp.net/attachments/875008602706956368/1195618804751941672/upload_2021-8-22_15-46-34-png.png?ex=65b4a5c8&is=65a230c8&hm=0855e8be534da6e88501a14ec49f07ee74f8f70a7020055ab1999347bea32aad&=&format=webp&quality=lossless&width=1000&height=652)
### Multiple search result pages:
![alt text](https://media.discordapp.net/attachments/875008602706956368/1195618837601718272/upload_2021-8-22_15-50-28-png.png?ex=65b4a5d0&is=65a230d0&hm=40bf7698ef4d6a6847587d39fffcd032a021406052995228a6ae5b1a67ed00f8&=&format=webp&quality=lossless&width=1000&height=576)
### Shows item enchantments:
![alt text](https://media.discordapp.net/attachments/875008602706956368/1195618864944394291/upload_2021-8-22_15-47-10-png.png?ex=65b4a5d6&is=65a230d6&hm=d2bac9ac052b20cccd826b245c4ec36b1d7b4086a225117cb99368629d79a931&=&format=webp&quality=lossless&width=1000&height=484)
### Shows Potion colors and effects:
![alt text](https://media.discordapp.net/attachments/875008602706956368/1195618954136268840/upload_2021-8-22_15-52-40-png.png?ex=65b4a5ec&is=65a230ec&hm=415f00b45b850d5f6c976871da56fa36709dd2a1c35c63f2ffa0b779cc7436c9&=&format=webp&quality=lossless&width=1000&height=432)
### Shows custom item names and lore:
![alt text](https://media.discordapp.net/attachments/875008602706956368/1195618980610723841/upload_2021-8-22_15-47-1-png.png?ex=65b4a5f2&is=65a230f2&hm=734dae6fa9715411aab9ad34a5a0c358b1d1c828f677e126c88ff505fd0724e3&=&format=webp&quality=lossless&width=1000&height=504)