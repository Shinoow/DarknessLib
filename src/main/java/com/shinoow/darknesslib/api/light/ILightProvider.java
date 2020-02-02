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
package com.shinoow.darknesslib.api.light;

import net.minecraft.entity.player.EntityPlayer;

/**
 * A handler that allows you to "add" light to the players current light level
 *
 * @author shinoow
 *
 */
public interface ILightProvider {

	/**
	 * Fetches the additional light based on a condition related to the player
	 * @param player Current Player
	 * @return A number representing the light level (0 - 15) provided
	 */
	public int getAdditionalLight(EntityPlayer player);
}
