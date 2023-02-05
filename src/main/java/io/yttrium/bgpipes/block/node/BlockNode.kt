/*
 * @author "Hannah Brooke <hannah@mail.yttrium.io>" a.k.a hotel, HotelCalifornia, hotel_california
 *
 * Copyright (c) 2023.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.yttrium.bgpipes.block.node

import io.yttrium.bgpipes.gui.node.MenuNode
import io.yttrium.bgpipes.util.ext.filterNotNullValues
import io.yttrium.bgpipes.util.ext.offset

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks

class BlockNode : Block(Properties.of(Material.METAL)), EntityBlock, MenuProvider {
    private var inventoryList = emptyMap<Direction, BlockEntity>()
    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity {
        return BlockEntityNode(pPos, pState)
    }

    override fun setPlacedBy(
        pLevel: Level, pPos: BlockPos, pState: BlockState, pPlacer: LivingEntity?, pStack: ItemStack
    ) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack)
        inventoryList = Direction.values().asIterable()
            .associateBy({ it }, { pLevel.getBlockEntity(BlockPos(pPos.offset(it.step()))) }).filterNotNullValues()
    }

    @Deprecated(
        "interface method marked deprecated in source", ReplaceWith(
            "super.use(pState, pLevel, pPos, pPlayer, pHand, pHit)", "net.minecraft.world.level.block.Block"
        )
    )
    override fun use(
        pState: BlockState, pLevel: Level, pPos: BlockPos, pPlayer: Player, pHand: InteractionHand, pHit: BlockHitResult
    ): InteractionResult {
        if (!pLevel.isClientSide) {
            when (pPlayer) {
                is ServerPlayer -> NetworkHooks.openScreen(pPlayer, pState.getMenuProvider(pLevel, pPos))
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide)
    }

    @Deprecated(
        "interface method marked deprecated in source",
        ReplaceWith("super.getMenuProvider(pState, pLevel, pPos)", "net.minecraft.world.level.block.Block")
    )
    override fun getMenuProvider(pState: BlockState, pLevel: Level, pPos: BlockPos): MenuProvider {
        return SimpleMenuProvider(this::createMenu, displayName)
    }

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return MenuNode(pContainerId, pPlayerInventory)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("menu.title.bgpipes.node")
    }
}


