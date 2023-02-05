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

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import io.yttrium.bgpipes.BGPipes
import io.yttrium.bgpipes.util.ItemTransferDirection
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class ScreenNode(pMenu: MenuNode, pPlayerInventory: Inventory) : AbstractContainerScreen<MenuNode>(
    pMenu, pPlayerInventory, Component.translatable("bgpipes.gui.node.title")
) {
    class PushPullConfigurationButton(
        pX: Int,
        pY: Int,
        pMessage: Component,
        pOnPress: OnPress,
    ) : Button(pX, pY, 12, 12, pMessage, pOnPress, DEFAULT_NARRATION) {
        private var direction = ItemTransferDirection.Pull

        fun toggle() {
            direction = when (direction) {
                ItemTransferDirection.Push -> ItemTransferDirection.Pull
                ItemTransferDirection.Pull -> ItemTransferDirection.Push
            }
        }

        private fun getBlitVOffset(): Int {
            return if (direction == ItemTransferDirection.Pull) 0 else 12
        }

        private fun getBlitUOffset(): Int {
            return if (isHoveredOrFocused) 188 else 176
        }

        override fun renderButton(pPoseStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader)
            RenderSystem.setShaderTexture(0, Texture)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha)
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.enableDepthTest()
            blit(pPoseStack, x, y, getBlitUOffset(), getBlitVOffset(), width, height)
        }
    }

    init {
        imageWidth = 176
        imageHeight = 215
    }

    companion object {
        val Texture = ResourceLocation(BGPipes.ModID, "textures/gui/gui_node.png")
    }

    private fun generatePushPullConfiguratorButton(side: Direction, x: Int, y: Int): Button {
        return PushPullConfigurationButton(x, y, Component.translatable("bgpipes.gui.node.button.pushpull")) {
            it.message = Component.literal(side.toString())
            (it as PushPullConfigurationButton).toggle()
        }
    }

    override fun init() {
        super.init()

        addRenderableWidget(generatePushPullConfiguratorButton(Direction.UP, 69, 52))
        addRenderableWidget(generatePushPullConfiguratorButton(Direction.NORTH, 80, 52))
        addRenderableWidget(generatePushPullConfiguratorButton(Direction.WEST, 69, 63))
        addRenderableWidget(generatePushPullConfiguratorButton(Direction.EAST, 96, 68))
        addRenderableWidget(generatePushPullConfiguratorButton(Direction.SOUTH, 85, 79))
        addRenderableWidget(generatePushPullConfiguratorButton(Direction.DOWN, 96, 79))
    }

    override fun renderBg(pPoseStack: PoseStack, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, Texture)
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2
        blit(pPoseStack, x, y, 0, 0, imageHeight, imageWidth)
        // TODO: render filter slot overlays based on side push/pull configuration
//        pPoseStack.pushPose()
//        blit(pPoseStack, )
    }

    override fun render(pPoseStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        renderBackground(pPoseStack)
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick)
        renderTooltip(pPoseStack, pMouseX, pMouseY)
    }
}