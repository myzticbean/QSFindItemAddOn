# Shop Search AddOn For QuickShop
### Version: ${project.version}

An unofficial add-on for the QuickShop Reremake spigot plugin.
Adds a `/finditem` command in game for searching through all the shops on the server.

![MC](https://img.shields.io/badge/Minecraft-Java%20Edition:%201.16.5%20--%201.18.1-brightgreen)
![Ver](https://img.shields.io/spiget/version/95104?label=Current%20Spigot%20Version)

**Features:**
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
- Ignores shops that are out of stock
- Support for world blacklisting (Shops in blacklisted worlds are ignored in the search result)

Check out the sample config.yml [here](https://gitlab.com/ronsane/QSFindItemAddOn/-/wikis/Sample-config.yml).

**Integrations:**
- Supports [PlayerWarps](https://www.spigotmc.org/resources/66692/) (by Olzie-12) integration. It shows the nearest warp to each shop in the search result GUI.
- Supports EssentialsX Warps integration for fetching nearest warps.
    - Global warps list for essentials is updated in batches every 15 minutes due to technical limitations, which is then used in every search query.
    - If you added a new warp and want it to get updated immediately, run **/finditem updatelist**
    - Remember, this applies only to Essential Warps.
- WorldGuard region support for fetching the WorldGuard region the shop is in (if overlapping regions, highest priority will be chosen)

**Requires:**
- [QuickShop Reremake](https://www.spigotmc.org/resources/62575/) 5.0 or higher (Use add-on v1.7 for QuickShop version 4.0.*)

**Assumptions:**
- A compatible economy plugin is installed
- [Vault](https://www.spigotmc.org/resources/34315/) 1.7.3 or higher is installed

**Issue Tracking**

Please create a new issue [here](https://gitlab.com/ronsane/QSFindItemAddOn/-/issues) if you encounter any errors. Please try to explain in detail about your issue, and attach your console log if possible. Don't forget to mention the QuickShop version and Integrated plugin version (EssentialsX, PlayerWarps, WorldGuard).
