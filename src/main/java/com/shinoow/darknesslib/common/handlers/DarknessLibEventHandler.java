/*******************************************************************************
 * DarknessLib
 * Copyright (c) 2019 - 2022 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation
 ******************************************************************************/
package com.shinoow.darknesslib.common.handlers;

import com.shinoow.darknesslib.api.cap.DynamicLightsCapabilityProvider;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = "darknesslib")
public class DarknessLibEventHandler {

	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event){
		if(event.getObject() instanceof EntityPlayer)
			event.addCapability(new ResourceLocation("darknesslib", "dynamiclights"), new DynamicLightsCapabilityProvider());
	}

	@SubscribeEvent
	public static void onClonePlayer(PlayerEvent.Clone event) {
		if(event.isWasDeath())
			event.getEntityPlayer().getCapability(DynamicLightsCapabilityProvider.DYNAMIC_LIGHTS, null).copy(event.getOriginal().getCapability(DynamicLightsCapabilityProvider.DYNAMIC_LIGHTS, null));
	}
}
