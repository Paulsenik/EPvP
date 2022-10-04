# EPvP
Extends the Vanilla-Minecraft PvP expirience in a new way

## Features:
***Only apply when a player is killed by another one!!***
***Not when dying "normal"***
- Playerhead of victim is dropped.
- Victim keeps most of the items after death.
- Some of the "valueables" (e.g. diamonds, ingots, ...) are dropped according to a **table**, which is set with commands.
- The drop-**rate** specifies how much of the specified items a player loses to his killer. (e.g. 1.0 -> a player looses all his diamonds and ingots to his killer)
- Player keeps all other personal items (e.g. Armor, Weapons, ...)

## Commands (OP):
> /epvp : Helpmenu

> /epvp enable

> /epvp disable

> /epvp rate : shows amount of an Itemtype that is dropped

> /epvp rate [0.0-1.0] : sets the rate

> /epvp table : lists the valuable-table

> /epvp table add [MATERIAL] : adds a material to the table

> /epvp table remove [MATERIAL] : removes a material from the table

> /epvp table clear : clears the table
