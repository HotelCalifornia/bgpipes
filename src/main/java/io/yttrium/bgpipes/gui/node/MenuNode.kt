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

fun slotArray(number: Int, gridDimensions: Pair<Int, Int>, baseUV: Pair<Int, Int>): Array<Pair<Int, Int>> {
    val (gridWidth, gridHeight) = gridDimensions
    val (u, v) = baseUV

    return Array(number) { i -> Pair(u + ((i % gridWidth) * 18), v + ((i / gridHeight) * 18)) }
}

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

    private val player = playerInventory.player

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

    init {
        TODO("create class that handles items as filter inputs only and not real itemstacks")
        // bind filter slots
        guiMap.forEach { (_, value) ->
            val (bounds, container) = value
            container?.let {
                slotArray(4, Pair(2, 2), Pair(bounds.x0, bounds.y0)).forEachIndexed { i, (u, v) ->
                    addSlot(Slot(it, i, u, v))
                }
            }
        }

        // bind player inventory
        slotArray(27, Pair(9, 3), Pair(8, 133)).forEachIndexed { i, (u, v) ->
            addSlot(Slot(playerInventory, i, u, v))
        }

        // bind player hotbar
        slotArray(9, Pair(9, 1), Pair(8, 142)).forEachIndexed { i, (u, v) ->
            addSlot(Slot(playerInventory, 27 + i, u, v))
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