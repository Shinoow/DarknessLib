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
package com.shinoow.darknesslib.client.config;

import com.shinoow.darknesslib.DarknessLib;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class DarknessLibConfigGUI extends GuiConfig {

	public DarknessLibConfigGUI(GuiScreen parentScreen) {
		super(parentScreen, new ConfigElement(DarknessLib.CFG.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), "darknesslib", false, false, "DarknessLib");
	}
}
