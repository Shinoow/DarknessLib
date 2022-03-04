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
package com.shinoow.darknesslib.api.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class DynamicLightsCapabilityProvider implements ICapabilityProvider, INBTSerializable<NBTBase> {

	@CapabilityInject(IDynamicLightsCapability.class)
	public static final Capability<IDynamicLightsCapability> DYNAMIC_LIGHTS = null;

	private IDynamicLightsCapability capability;

	public DynamicLightsCapabilityProvider(){
		capability = new DynamicLightsCapability();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {

		return capability == DYNAMIC_LIGHTS;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {

		if(capability == DYNAMIC_LIGHTS)
			return (T) this.capability;

		return null;
	}

	@Override
	public NBTBase serializeNBT() {
		return DynamicLightsCapabilityStorage.INSTANCE.writeNBT(DYNAMIC_LIGHTS, capability, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		DynamicLightsCapabilityStorage.INSTANCE.readNBT(DYNAMIC_LIGHTS, capability, null, nbt);
	}
}
