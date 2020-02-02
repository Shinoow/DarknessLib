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
package com.shinoow.darknesslib.common.network.server;

import java.io.IOException;

import com.shinoow.darknesslib.api.cap.DynamicLightsCapabilityProvider;
import com.shinoow.darknesslib.api.cap.IDynamicLightsCapability;
import com.shinoow.darknesslib.common.network.AbstractMessage.AbstractServerMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class DynamicLightsMessage extends AbstractServerMessage<DynamicLightsMessage> {

	private boolean modPresent;

	public DynamicLightsMessage() {}

	public DynamicLightsMessage(boolean modPresent){
		this.modPresent = modPresent;
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		modPresent = buffer.readBoolean();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeBoolean(modPresent);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		IDynamicLightsCapability capability = player.getCapability(DynamicLightsCapabilityProvider.DYNAMIC_LIGHTS, null);
		capability.setHasDynamicLights(modPresent);
	}
}
