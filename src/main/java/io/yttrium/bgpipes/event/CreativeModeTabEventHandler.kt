/*
 * @author "Hannah Brooke <hannah@mail.yttrium.io>" a.k.a hotel, HotelCalifornia, hotel_california
 *
 * Copyright (c) 2023.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package io.yttrium.bgpipes.event

import io.yttrium.bgpipes.BGPipes
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.event.CreativeModeTabEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = BGPipes.ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
object CreativeModeTabEventHandler {
    @JvmStatic
    @SubscribeEvent
    fun buildContents(event: CreativeModeTabEvent.BuildContents) {
        when (event.tab) {
            CreativeModeTabs.FUNCTIONAL_BLOCKS -> BGPipes.Blocks.filterValues { v ->
                v.get().asItem() != net.minecraft.world.item.Items.AIR
            }.forEach { (_, v) -> event.accept(v) }

            CreativeModeTabs.TOOLS_AND_UTILITIES -> BGPipes.Items.filterValues { it.get() !is BlockItem }
                .forEach { (_, v) -> event.accept(v) }
        }
    }
}