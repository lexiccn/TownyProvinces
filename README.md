# Towny Resources
- An add-on plugin for Towny, which makes town claiming more organized, reducing staff workload and server toxicity.

## Features:
- :world_map: **Automatically Divides The Map Into Provinces:**
  - Each Province can have just one town.
  - Province density can be configured to be vary by map location.
  - Province borders can be viewed on the "Borders" map layer: ![image](https://github.com/Goosius1/TownyProvinces/assets/50219223/9eb5849a-4540-49ba-b71f-26c128c3fc56)
  
- :moneybag: **Applies town costs depending on province location:**
  - Each province has a "Town Settlement" and "Town Upkeep" Cost.
  - These town costs can be the same for each province in the world, or they can be configured to be different for different regions.
  - Example: On an Earth map, town costs could be expensive in popular regions like Europe, but cheap in unfashionable regions like Antarctica.
  - Province prices can be viewed on the "Prices" map layer: ![image](https://github.com/Goosius1/TownyProvinces/assets/50219223/b5d6fdee-9625-4b2a-8edd-8a5b221e64e8)

## Installation Guide:
1. Ensure your server has *Towny 0.99.1.0* or newer.
2. If at all possible, ensure your server has *Dynmap*.
3. Download the *TownyProvinces* plugin jar file from [here](https://github.com/TownyAdvanced/TownyProvinces/releases), and drop it into your server plugins folder.
4. Restart your server.

## Admin Guide:
1. Configure some region definition files, and place them in the folder: /region_definitions.
   - Typically a server might have one region definition file for each continent.
   - The 1st region definition file should be the size of the entire map.
   - Region definition files are evaluated in ascending alpha-numeric order.
   - You can have as many region definition files as you want.
   - Two sample region definiton files are provided.
2. Run the command `/tpra region regenerate all` --- this will regenerate all regions.
3. Run the command `/tpra landvalidationjob start` --- The will convert any 'all sea' provinces to province type 'sea'.
    
## Admin Commands:
- `/tpra region [regenerate] [<Region Name>]`: Regenerate a region.
- `/tpra region [newtowncost] [<Region Name>] [amount]`: Set the new town cost for a region.
- `/tpra region [upkeeptowncost] [<Region Name>] [amount]`: Set the upkeep town cost for a region.
- `/tpra landvalidationjob [status|start|stop|restart|pause]`: Control the land validation job
- `/tpra province [sea|land] [<x>,<z>]`: Set a province to sea/land.
