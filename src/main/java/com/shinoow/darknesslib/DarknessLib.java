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
package com.shinoow.darknesslib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.shinoow.darknesslib.api.DarknessLibAPI;
import com.shinoow.darknesslib.proxy.Proxy;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = DarknessLib.MODID, name = DarknessLib.NAME, version = DarknessLib.VERSION, dependencies = "required-after:forge@[FORGEVERSION,)", acceptedMinecraftVersions = "[1.12.2]", guiFactory = "com.shinoow.darknesslib.client.config.DarknessLibGuiFactory", updateJSON = "https://raw.githubusercontent.com/Shinoow/DarknessLib/master/version.json", useMetadata = false, certificateFingerprint = "@CERT_FINGERPRINT@")
public class DarknessLib {

	public static final String VERSION = "@DLIB_VERSION@";
	public static final String MODID = "darknesslib";
	public static final String NAME = "DarknessLib";

	@Metadata(MODID)
	public static ModMetadata METADATA;

	@Instance(MODID)
	public static DarknessLib INSTANCE;

	@SidedProxy(clientSide = "com.shinoow.darknesslib.proxy.ClientProxy",
			serverSide = "com.shinoow.darknesslib.proxy.CommonProxy")
	public static Proxy PROXY;

	public static Configuration CFG;

	public static String[] DYNAMIC_LIGHTS_LIST;
	public static boolean DYNAMIC_LIGHTS_MODE;

	public static Logger LOGGER = LogManager.getLogger("DarknessLib");

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		METADATA = event.getModMetadata();
		METADATA.description += "\n\n\u00a76Supporters: "+getSupporterList()+"\u00a7r";
		MinecraftForge.EVENT_BUS.register(this);

		CFG = new Configuration(event.getSuggestedConfigurationFile());
		syncConfig();

		PROXY.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		PROXY.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		PROXY.postInit();
	}

	@EventHandler
	public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		LOGGER.log(Level.WARN, "Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
	}

	@EventHandler
	public void handleIMC(FMLInterModComms.IMCEvent event){
		event.getMessages().forEach(m -> {
			if(m.key.equals("addVehicle")) {
				if(m.isStringMessage())
					try {
						Class clazz = Class.forName(m.getStringValue());
						if(Entity.class.isAssignableFrom(clazz))
							DarknessLibAPI.getInstance().addVehicle(clazz);
					} catch(Exception e) {
						LOGGER.log(Level.ERROR, "Failed to add vehicle Class {} from {}: {}", m.getStringValue(), m.getSender(), e.getStackTrace());
					}
			} else if(m.key.equals("addLightsource"))
				if(m.isStringMessage()) {
					String[] data = m.getStringValue().split(";");
					String[] stuff = data[0].split(":");
					if(stuff.length <= 2) {
						Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(stuff[0], stuff[1]));
						if(item != null)
							DarknessLibAPI.getInstance().addLightsource(new ItemStack(item, 1, stuff.length == 3 ? Integer.valueOf(stuff[2]) : OreDictionary.WILDCARD_VALUE), data.length == 2 ? Integer.valueOf(data[1]) : 15);
						else LOGGER.log(Level.ERROR, "{} from {} is not a valid Item!", m.getStringValue(), m.getSender());
					} else LOGGER.log(Level.ERROR, "{} from {} is not a valid Item!", m.getStringValue(), m.getSender());

				} else if(m.isItemStackMessage())
					DarknessLibAPI.getInstance().addLightsource(m.getItemStackValue(), 15);
		});
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if(eventArgs.getModID().equals(MODID))
			syncConfig();
	}

	private static void syncConfig(){
		DYNAMIC_LIGHTS_MODE = CFG.get(Configuration.CATEGORY_GENERAL, "Dynamic Lights Mode", false, "If this is enabled (client and server), handheld light sources will be treated as actual light while AtomicStryker's Dynamic Lights is present, or Optifine if its Dynamic Lights is enabled, or Smooth Entity Light is present.").getBoolean();
		DYNAMIC_LIGHTS_LIST = CFG.get(Configuration.CATEGORY_GENERAL, "Dynamic Lights List", new String[0], "Items/Blocks added to this list will be regarded as handheld light sources while AtomicStryker's Dynamic Lights is present, or Optifine if its Dynamic Lights is enabled "
				+ "(and Dynamic Lights Mode is enabled), or if Smooth Entity Light is present. Most light-emitting Blocks are automatically detected as light sources, so you might not have to add them to this list (check the log as it lists all that it finds)\nFormat: modid:name:meta;light_level, where meta and light_level are optional (light level is automatically set to 15 if not specified).\n"+TextFormatting.RED+"[Minecraft Restart Required]"+TextFormatting.RESET).getStringList();

		if(CFG.hasChanged())
			CFG.save();
	}

	private String getSupporterList(){
		BufferedReader nameFile;
		String names = "";
		try {
			nameFile = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/Shinoow/AbyssalCraft/master/supporters.txt").openStream()));

			names = nameFile.readLine();
			nameFile.close();

		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "Failed to fetch supporter list, using local version!");
			names = "Tedyhere";
		}

		return names;
	}
}
