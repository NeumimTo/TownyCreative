# About

- Allows admins to create block palletes for each towny nation
- Players then may enter a creative mode and build from blocks from that pallete

### Commands

`/townycreative`
- permission: `townycreative.player.toggle`
- Allows players enter or exit creative mode
- While in this managed creative mode:
    - Players are unable to open inventories, damage other entities, interact with villagers, drop items from inventory, etc
    - Players are allowed to build and destroy blocks only within their own town
    - Players may place and destroy only blocks that are defined in their nation block pallete
    - When exiting the creative mode player inventory is cleared
    - The creative mode is automatically removed when a player crosses town border


`/townycreative set-build-blocks <nation> <SET/ADD>`
- permission: `townycreative.admin`
- Allows admins/moderators to specify block palletes for each nation
    - Place a chest
    - Put desired blocks inside the chest
    - Stand directly on top of the chest
    - Run the command


### Requirements

- Towny https://github.com/TownyAdvanced/Towny
- Paper (or its forks) 1.19.x (should work on older versions as well as long as the server itself is running on Java 17)

### Support

Ping me at towny discord