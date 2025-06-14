package com.tom.storagemod.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import com.tom.storagemod.Content;
import com.tom.storagemod.network.NetworkHandler;
import com.tom.storagemod.util.DataSlots;
import com.tom.storagemod.util.TagFilter;

public class TagItemFilterMenu extends AbstractFilteredMenu {
	private TagFilter filter;
	public boolean allowList;
	private boolean synced;

	public TagItemFilterMenu(int wid, Inventory pinv, TagFilter filter) {
		this(wid, pinv);
		this.filter = filter;
	}

	public TagItemFilterMenu(int wid, Inventory pinv) {
		super(Content.tagItemFilterConatiner.get(), wid, pinv);
		this.addSlot(new PhantomSlot(new SimpleContainer(1), 0, 8, 15));

		for(int k = 0; k < 3; ++k) {
			for(int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(pinv, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		for(int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(pinv, l, 8 + l * 18, 142));
		}
		addDataSlot(DataSlots.create(v -> allowList = v == 1, () -> filter.isAllowList() ? 1 : 0));
	}

	@Override
	public boolean clickMenuButton(Player p_38875_, int btn) {
		boolean v = (btn & 1) != 0;
		btn >>= 1;
		if(btn == 0) {
			filter.setAllowList(v);
		}
		return false;
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		if(!synced) {
			ListTag list = new ListTag();
			filter.getTags().forEach(t -> list.add(StringTag.valueOf(t.location().toString())));
			CompoundTag tag = new CompoundTag();
			tag.put("l", list);
			NetworkHandler.sendTo((ServerPlayer) pinv.player, tag);
			synced = true;
		}
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			if (index < 1) {
			} else {
				ItemStack is = slot.getItem().copy();
				is.setCount(1);
				Slot sl = this.slots.get(0);
				if(ItemStack.isSameItemSameTags(sl.getItem(), is))return ItemStack.EMPTY;
				if(sl.getItem().isEmpty()) {
					sl.set(is);
					return ItemStack.EMPTY;
				}
			}
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		if(filter != null)filter.flush();
	}

	@Override
	public void receive(CompoundTag tag) {
		super.receive(tag);
		if(tag.contains("l")) {
			ListTag list = tag.getList("l", Tag.TAG_STRING);
			try {
				List<ResourceLocation> tags = new ArrayList<>();
				for (int i = 0; i < list.size(); i++) {
					tags.add(new ResourceLocation(list.getString(i)));
				}
				filter.setTags(tags);
			} catch (Exception e) {
			}
		}
	}
}
