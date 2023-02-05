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

    enum class GuiElement {
        PlayerInventory, PlayerHotbar, NodeFiltersUp, NodeFiltersNorth, NodeFiltersWest, NodeFiltersEast, NodeFiltersSouth, NodeFiltersDown
    }

    data class ElementBounds(val x0: Int, val y0: Int, val x1: Int, val y1: Int)

    private fun generateContainerElement(bounds: ElementBounds): Pair<ElementBounds, SimpleContainer> {
        return Pair(bounds, object : SimpleContainer(4) {
            override fun setChanged() {
                super.setChanged()
                this@MenuNode.slotsChanged(this)
            }
        })
    }

    private val guiMap = mapOf(
        GuiElement.PlayerInventory to Pair(ElementBounds(8, 133, 167, 184), null),
        GuiElement.PlayerHotbar to Pair(ElementBounds(8, 191, 167, 206), null),
        GuiElement.NodeFiltersUp to generateContainerElement(ElementBounds(33, 16, 66, 49)),
        GuiElement.NodeFiltersNorth to generateContainerElement(ElementBounds(71, 16, 104, 49)),
        GuiElement.NodeFiltersWest to generateContainerElement(ElementBounds(33, 54, 66, 87)),
        GuiElement.NodeFiltersEast to generateContainerElement(ElementBounds(109, 53, 142, 86)),
        GuiElement.NodeFiltersSouth to generateContainerElement(ElementBounds(71, 91, 104, 124)),
        GuiElement.NodeFiltersDown to generateContainerElement(ElementBounds(109, 91, 142, 124)),
    )

    private val player = playerInventory.player

    init {
        TODO("create class that handles items as filter inputs only and not real itemstacks")
        // bind filter slots
        guiMap.forEach { (element, value) ->
            val (bounds, container) = value
            container?.let {
                for (u in (0..1)) {
                    for (v in (0..1)) {
                        addSlot(Slot(it, element.ordinal, bounds.x0 + (u * 18), bounds.y0 + (v * 18)))
                    }
                }
            }
        }

        // bind player inventory
        for (row in (0..2)) {
            for (column in (0..8)) {
                addSlot(Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18))
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