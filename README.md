# FindItem AddOn For QuickShop

An unofficial add-on for the QuickShop Reremake spigot plugin.
Adds a `/finditem` command in game for searching through all the shops on the server.

**Features:**
- Search items based on buying/selling shops
- Search items by item type
- Supports query based item search
- Supports item custom model data for custom items
- Sorts search result automatically based on ascending prices
- Displays enchantments in the result for enchanted items
- Hides item enchants if item has *hide_enchants* flag
- Displays potion effects in the result for Potion items
- Supports direct shop teleportation (configurable in config.yml)
- Ignores shops that are out of stock

Check out the sample config.yml [here](https://github.com/ronsanecodes/QSFindItemAddOn/blob/master/src/main/resources/config.yml).

**Requires:**
- [QuickShop Reremake](https://www.spigotmc.org/resources/quickshop-reremake-1-17-ready-multi-currency.62575/) 4.0.4 or higher
- [EssentialsX](https://www.spigotmc.org/resources/essentialsx.9089/) 2.18.2 or higher
- [Vault](https://www.spigotmc.org/resources/vault.34315/) 1.7.3 or higher

**Tested for minecraft versions:** 1.16, 1.17

Note that you need Java 16 to build this project. Make sure you have Java 16 installed on your server machine if you are using Spigot/Paper 1.17 or higher versions.

**Permissions:**
- `finditem.use` Enables players to use the /finditem command
- `finditem.reload` For OP. Enables use of **/finditem reload** to reload the config

**Issue Tracking**

Please create a new issue [here](https://github.com/ronsanecodes/QSFindItemAddOn/issues) if you encounter any errors. Please try to explain in detail about your issue, and attach your console log if possible.
