/*******************************************************************************
 * DarknessLib
 * Copyright (c) 2019 - 2020 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation
 ******************************************************************************/
package com.shinoow.darknesslib.api.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class DynamicLightsCapabilityStorage implements IStorage<IDynamicLightsCapability> {

	public static IStorage<IDynamicLightsCapability> INSTANCE = new DynamicLightsCapabilityStorage();

	@Override
	public NBTBase writeNBT(Capability<IDynamicLightsCapability> capability, IDynamicLightsCapability instance, EnumFacing side) {
		NBTTagCompound properties = new NBTTagCompound();

		properties.setBoolean("DynLightsMode", instance.hasDynamicLights());

		return properties;
	}

	@Override
	public void readNBT(Capability<IDynamicLightsCapability> capability, IDynamicLightsCapability instance, EnumFacing side, NBTBase nbt) {
		NBTTagCompound properties = (NBTTagCompound)nbt;

		instance.setHasDynamicLights(properties.getBoolean("DynLightsMode"));
	}
}
