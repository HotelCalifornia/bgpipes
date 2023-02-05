/*
 * @author "Hannah Brooke <hannah@mail.yttrium.io>" a.k.a hotel, HotelCalifornia, hotel_california
 *
 * Copyright (c) 2023.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.yttrium.bgpipes.gui.node

import io.yttrium.bgpipes.BGPipes
import net.minecraft.core.Direction
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

/**
 * primary constructor: server side
 */
class MenuNode(
    containerId: Int, playerInventory: Inventory, private var containerAccess: ContainerLevelAccess
) : AbstractContainerMenu(BGPipes.Menus[BGPipes.MenuTypes.Node]!!.get(), containerId) {

    private val player = playerInventory.player
    private val inputSlots: Map<Direction, Container> = Direction.values()
        .asIterable()
        .associateBy({ it }, {
            object : SimpleContainer(9) {
                override fun setChanged() {
                    super.setChanged()
                    this@MenuNode.slotsChanged(this)
                }
            }
        })

    init {
        TODO("actually figure out the layout of this gui and fix the slot positions")
        TODO("create class that handles items as filter inputs only and not real itemstacks")
        inputSlots.forEach { (k, v) -> addSlot(Slot(v, k.ordinal, 0, 0)) }

        // bind player inventory
        for (row in (0..2)) {
            for (column in (0..8)) {
                addSlot(
                    Slot(
                        playerInventory,
                        // some magic shit i could probably work out if i cared
                        // to but since i don't, this is copied from minecraft
                        // source
                        column + row * 9 + 9, 8 + column * 18, 84 + row * 18
                    )
                )
            }
        }
        // bind player hotbar
        for (column in (0..8)) {
            addSlot(Slot(playerInventory, column, 8 + column * 18, 142))
        }
    }

    /**
     * secondary constructor: client side
     */
    constructor(containerId: Int, playerInventory: Inventory) : this(
        containerId, playerInventory, ContainerLevelAccess.NULL
    )

    override fun quickMoveStack(pPlayer: Player, pIndex: Int): ItemStack {
        // doesn't really make sense to quick move items into this menu
        return ItemStack.EMPTY
    }

    override fun stillValid(pPlayer: Player): Boolean {
        return stillValid(
            containerAccess, pPlayer, BGPipes.Blocks[BGPipes.BlockTypes.Node]!!.get()
        )
    }
}