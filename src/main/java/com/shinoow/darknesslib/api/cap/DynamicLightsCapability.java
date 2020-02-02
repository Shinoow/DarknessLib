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

public class DynamicLightsCapability implements IDynamicLightsCapability {

	private boolean hasDynLights;

	@Override
	public void setHasDynamicLights(boolean hasDynLights) {
		this.hasDynLights = hasDynLights;
	}

	@Override
	public boolean hasDynamicLights() {

		return hasDynLights;
	}

	@Override
	public void copy(IDynamicLightsCapability cap) {
		hasDynLights = cap.hasDynamicLights();
	}
}
