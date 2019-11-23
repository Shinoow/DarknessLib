/*******************************************************************************
 * DarknessLib
 * Copyright (c) 2019 - 2019 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation
 ******************************************************************************/
package com.shinoow.darknesslib.client.handler;

import com.shinoow.darknesslib.DarknessLib;
import com.shinoow.darknesslib.common.network.PacketDispatcher;
import com.shinoow.darknesslib.common.network.server.DynamicLightsMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = Side.CLIENT, modid = "darknesslib")
public class DarknessLibClientEventHandler {

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityPlayer){
			if(!DarknessLib.DYNAMIC_LIGHTS_MODE) return;
			PacketDispatcher.sendToServer(new DynamicLightsMessage(Loader.isModLoaded("dynamiclights") || Loader.isModLoaded("sel") || isOFDynLightsEnabled()));
		}
	}

	private boolean isOFDynLightsEnabled() {
		try {
			Class<?> optifineConfig = Class.forName("Config", false, Loader.instance().getModClassLoader());
			Object test = optifineConfig.getMethod("isDynamicLights").invoke(null);
			return (boolean) test;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
