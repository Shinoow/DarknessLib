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
package com.shinoow.darknesslib.common.handlers;

import com.shinoow.darknesslib.DarknessLib;
import com.shinoow.darknesslib.api.internal.DummyMethodHandler;

public class InternalMethodHandler extends DummyMethodHandler {

	@Override
	public boolean isDynLightsModeEnabled() {
		return DarknessLib.DYNAMIC_LIGHTS_MODE;
	}
}
