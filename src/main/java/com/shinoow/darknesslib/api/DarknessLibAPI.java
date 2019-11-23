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
package com.shinoow.darknesslib.api;

import java.util.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shinoow.darknesslib.api.cap.DynamicLightsCapabilityProvider;
import com.shinoow.darknesslib.api.cap.IDynamicLightsCapability;
import com.shinoow.darknesslib.api.internal.DummyMethodHandler;
import com.shinoow.darknesslib.api.internal.IInternalMethodHandler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Main API class for DarknessLib, contains data and utility methods.
 *
 * @author shinoow
 *
 */
public class DarknessLibAPI {

	/**
	 * String used to specify the API version in the "package-info.java" classes
	 */
	public static final String API_VERSION = "1.0.0";

	private static final DarknessLibAPI INSTANCE = new DarknessLibAPI();

	private final Map<ItemStack, Integer> DYNAMIC_LIGHTS_MAP = new HashMap<>();

	private final List<Class<? extends Entity>> VEHICLES = new ArrayList<>();

	private IInternalMethodHandler internalMethodHandler = new DummyMethodHandler();

	private Logger LOGGER = LogManager.getLogger("DarknessLibAPI");
	
	private DarknessLibAPI() {}

	public static DarknessLibAPI getInstance() {
		return INSTANCE;
	}

	/**
	 * Adds an ItemStack with a specified light level to the dynamic lights list
	 * @param stack ItemStack to add
	 * @param lightLevel Light level
	 */
	public void addLightsource(ItemStack stack, int lightLevel) {
		if(!stack.isEmpty() && lightLevel > 0 && lightLevel < 16) {
			DYNAMIC_LIGHTS_MAP.put(stack, lightLevel);
			LOGGER.log(Level.INFO, "{} has been added to the Dynamic Lights List with Light level {}!", stack.getItem().getRegistryName(), lightLevel);
		} else LOGGER.log(Level.ERROR, "{} is either a invalid item, or {} is a invalid light level", stack.getItem().getRegistryName(), lightLevel);
	}

	/**
	 * Adds the entity to the list of vehicles (boats, minecarts etc), guilty of blocking out light for a player sitting in them
	 * @param vehicle A class (but preferably a superclass) of a vehicle entity
	 */
	public void addVehicle(Class<? extends Entity> vehicle) {
		VEHICLES.add(vehicle);
	}

	/**
	 * Attempts to calculate the light at the entity's position
	 * @param entityIn Entity to check the light by
	 * @param strict Whether or not to account for the entity possibly standing inside a block (ex. soul sand), which would throw off the calculation entirely
	 * @return The current light level
	 */
	public int getLight(Entity entityIn, boolean strict) {
		BlockPos blockpos = new BlockPos(entityIn.posX, entityIn.getEntityBoundingBox().minY, entityIn.posZ);
		if(strict && !entityIn.world.isBlockFullCube(blockpos) && !entityIn.world.getBlockState(blockpos).getBlock().isReplaceable(entityIn.world, blockpos))
			blockpos = blockpos.up();
		return entityIn.world.getLightFromNeighbors(blockpos);
	}

	/**
	 * Attempts to calculate the light at the player's position, including dynamic lights if enabled
	 * @param player Player to check
	 * @param strict Whether or not to account for the player possibly standing inside a block (ex. soul sand), which would throw off the calculation entirely
	 * @return
	 */
	public int getLightWithDynLights(EntityPlayer player, boolean strict) {

		int light = getLight(player, strict);

		if(hasDynamicLights(player)) {
			int main_light = getLight(player.getHeldItem(EnumHand.MAIN_HAND));
			int off_light = getLight(player.getHeldItem(EnumHand.OFF_HAND));
			int dynamic_light = Math.max(main_light, off_light);
			light = Math.max(light, dynamic_light);
		}

		return light;
	}

	/**
	 * Checks if the player has any dynamic lights mods installed, and if dynamic lights mode is enabled
	 * @param player Player to check
	 * @return Returns whether or not dynamic lights are enabled
	 */
	public boolean hasDynamicLights(EntityPlayer player) {

		IDynamicLightsCapability cap = player.getCapability(DynamicLightsCapabilityProvider.DYNAMIC_LIGHTS, null);

		return internalMethodHandler.isDynLightsModeEnabled() && cap.hasDynamicLights();
	}

	/**
	 * Attempts to get the light level of an ItemStack
	 * @param stack ItemStack to check
	 * @return The light level (if any), or 0
	 */
	public int getLight(ItemStack stack) {

		if(stack.isEmpty())
			return 0;

		Optional<Integer> val = DYNAMIC_LIGHTS_MAP.entrySet().stream().filter(e -> stacksEqual(stack, e.getKey())).map(e -> e.getValue()).findFirst();

		return val.orElse(0);
	}

	private boolean stacksEqual(ItemStack stack, ItemStack stack1) {
		return stack1.getItem() == stack.getItem() && (stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE ||
				stack1.getItemDamage() == stack.getItemDamage());
	}

	/**
	 * Somewhat legacy method that basically just checks if the player has dynamic lights enabled and is holding a light source in either hand.<br>
	 * Could be useful if you don't care about the actual light level of the held item, but just the fact that it gives off light.
	 * @param player Player to check
	 * @return True if the player has dynamic lights enabled and is holding at least one light source
	 */
	public boolean isIlluminatedDynamically(EntityPlayer player) {

		if(hasDynamicLights(player))
			return getLight(player.getHeldItem(EnumHand.MAIN_HAND)) > 0 || getLight(player.getHeldItem(EnumHand.OFF_HAND)) > 0;

			return false;
	}

	/**
	 * Checks if the Entity in question is a vehicle (boat, minecart, etc), which blocks out light
	 * @param entity Entity to check
	 * @return Whether or not said Entity is a vehicle
	 */
	public boolean isVehicle(Entity entity) {
		return VEHICLES.stream().anyMatch(c -> c.isInstance(entity));
	}

	/**
	 * Used by DarknessLib to set the Internal Method Handler<br>
	 * If any other mod tries to use this method, nothing will happen.
	 * @param handler Handler instance
	 */
	public void setInternalMethodHandler(IInternalMethodHandler handler) {
		if(internalMethodHandler.getClass().getName().equals(DummyMethodHandler.class.getName())
				&& Loader.instance().getLoaderState() == LoaderState.PREINITIALIZATION
				&& Loader.instance().activeModContainer().getModId().equals("darknesslib"))
			internalMethodHandler = handler;
	}
}
