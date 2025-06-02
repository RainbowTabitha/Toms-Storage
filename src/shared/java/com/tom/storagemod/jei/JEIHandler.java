package com.tom.storagemod.jei;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.tom.storagemod.Content;
import com.tom.storagemod.StorageMod;
import com.tom.storagemod.gui.AbstractFilteredScreen;
import com.tom.storagemod.gui.CraftingTerminalScreen;
import com.tom.storagemod.util.IAutoFillTerminal;
import com.tom.storagemod.util.IAutoFillTerminal.ISearchHandler;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

@JeiPlugin
public class JEIHandler implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(StorageMod.modid, "jei");
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(CraftingTerminalScreen.class, 83, 125, 28, 23, new RecipeType[] { RecipeTypes.CRAFTING });
		registration.addGhostIngredientHandler(AbstractFilteredScreen.class, new JeiGhostIngredientHandler());
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		CraftingTerminalTransferHandler.registerTransferHandlers(registration);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(Content.craftingTerminal.get()), new RecipeType[] { RecipeTypes.CRAFTING });
	}
	private static IJeiRuntime jeiRuntime;
	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		JEIHandler.jeiRuntime = jeiRuntime;
	}

	static {
		IAutoFillTerminal.updateSearch.add(new ISearchHandler() {

			@Override
			public void setSearch(String text) {
				if (jeiRuntime != null) {
					if (jeiRuntime.getIngredientFilter() != null) {
						jeiRuntime.getIngredientFilter().setFilterText(text);
					}
				}
			}

			@Override
			public String getSearch() {
				if (jeiRuntime != null) {
					if (jeiRuntime.getIngredientFilter() != null) {
						return jeiRuntime.getIngredientFilter().getFilterText();
					}
				}
				return "";
			}

			@Override
			public String getName() {
				return "JEI";
			}
		});
	}
}
