/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Mobius
 */
public class SellList extends ServerPacket
{
	private final int _money;
	private final List<Item> _items = new ArrayList<>();
	
	public SellList(Player player)
	{
		_money = player.getAdena();
		for (Item item : player.getInventory().getItems())
		{
			if (!item.isEquipped() && item.getTemplate().isSellable() && ((player.getSummon() == null) || (item.getObjectId() != player.getSummon().getControlObjectId())))
			{
				_items.add(item);
			}
		}
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.SELL_LIST.writeId(this, buffer);
		buffer.writeInt(_money);
		buffer.writeInt(0);
		buffer.writeShort(_items.size());
		for (Item item : _items)
		{
			buffer.writeShort(item.getTemplate().getType1());
			buffer.writeInt(item.getObjectId());
			buffer.writeInt(item.getId());
			buffer.writeInt(item.getCount());
			buffer.writeShort(item.getTemplate().getType2());
			buffer.writeShort(item.getCustomType1());
			buffer.writeInt(item.getTemplate().getBodyPart());
			buffer.writeShort(item.getEnchantLevel());
			buffer.writeShort(item.getCustomType2());
			buffer.writeShort(0);
			buffer.writeInt(item.getTemplate().getReferencePrice() / 2);
		}
	}
}
