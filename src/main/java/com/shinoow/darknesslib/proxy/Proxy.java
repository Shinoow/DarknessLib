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
package com.shinoow.darknesslib.proxy;

import java.util.Arrays;

import org.apache.logging.log4j.Level;

import com.shinoow.darknesslib.DarknessLib;
import com.shinoow.darknesslib.api.DarknessLibAPI;
import com.shinoow.darknesslib.api.cap.DynamicLightsCapability;
import com.shinoow.darknesslib.api.cap.DynamicLightsCapabilityStorage;
import com.shinoow.darknesslib.api.cap.IDynamicLightsCapability;
import com.shinoow.darknesslib.common.handlers.InternalMethodHandler;
import com.shinoow.darknesslib.common.network.PacketDispatcher;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public class Proxy {

	public void preInit() {
		CapabilityManager.INSTANCE.register(IDynamicLightsCapability.class, DynamicLightsCapabilityStorage.INSTANCE, DynamicLightsCapability::new);
		PacketDispatcher.registerPackets();

		DarknessLibAPI.getInstance().setInternalMethodHandler(new InternalMethodHandler());
		DarknessLibAPI.getInstance().addLightProvider((player) -> {
			if(DarknessLibAPI.getInstance().hasDynamicLights(player)) {
				int main_light = DarknessLibAPI.getInstance().getLight(player.getHeldItem(EnumHand.MAIN_HAND));
				int off_light = DarknessLibAPI.getInstance().getLight(player.getHeldItem(EnumHand.OFF_HAND));
				return Math.max(main_light, off_light);
			}
			return 0;
		});
	}

	public void init() {
		DarknessLibAPI.getInstance().addVehicle(EntityBoat.class);
		DarknessLibAPI.getInstance().addVehicle(EntityMinecart.class);
		if(Loader.isModLoaded("vehicle"))
			try {
				Class clazz = Class.forName("com.mrcrayfish.vehicle.entity.EntityVehicle");
				if(Entity.class.isAssignableFrom(clazz))
					DarknessLibAPI.getInstance().addVehicle(clazz);
			} catch(Exception e) {
				DarknessLib.LOGGER.log(Level.ERROR, "Unable to load Class {}: {}", "com.mrcrayfish.vehicle.entity.EntityVehicle", e.getStackTrace());
			}
	}

	public void postInit() {
		ForgeRegistries.BLOCKS.forEach(b -> {
			try {
				int light_level = b.getDefaultState().getLightValue(null, null);
				if(light_level > 0)
					DarknessLibAPI.getInstance().addLightsource(new ItemStack(b), light_level);
			} catch(Exception e) {

			}
		});
		Arrays.stream(DarknessLib.DYNAMIC_LIGHTS_LIST).filter(s -> s.length() > 0).forEach(str -> {
			String[] data = str.split(";");
			String[] stuff = data[0].split(":");
			if(stuff.length <= 2) {
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(stuff[0], stuff[1]));
				if(item != null)
					DarknessLibAPI.getInstance().addLightsource(new ItemStack(item, 1, stuff.length == 3 ? Integer.valueOf(stuff[2]) : OreDictionary.WILDCARD_VALUE), data.length == 2 ? Integer.valueOf(data[1]) : 15);
				else DarknessLib.LOGGER.log(Level.ERROR, "{} is not a valid Item!", str);
			} else DarknessLib.LOGGER.log(Level.ERROR, "{} is not a valid Item!", str);
		});
	}

	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return ctx.getServerHandler().player;
	}

	public IThreadListener getThreadFromContext(MessageContext ctx) {
		return ctx.getServerHandler().player.getServer();
	}
}
