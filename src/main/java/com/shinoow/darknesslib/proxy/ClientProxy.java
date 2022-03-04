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

import com.shinoow.darknesslib.client.render.entity.layers.LayerStarSpawnTentacles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends Proxy {

	@Override
	public void init() {
		super.init();
		if(!Loader.isModLoaded("abyssalcraft")){
			RenderPlayer render1 = Minecraft.getMinecraft().getRenderManager().getSkinMap().get("default");
			render1.addLayer(new LayerStarSpawnTentacles(render1));
			RenderPlayer render2 = Minecraft.getMinecraft().getRenderManager().getSkinMap().get("slim");
			render2.addLayer(new LayerStarSpawnTentacles(render2));
		}
	}

	@Override
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntity(ctx);
	}

	@Override
	public IThreadListener getThreadFromContext(MessageContext ctx) {
		return ctx.side.isClient() ? Minecraft.getMinecraft() : super.getThreadFromContext(ctx);
	}
}
