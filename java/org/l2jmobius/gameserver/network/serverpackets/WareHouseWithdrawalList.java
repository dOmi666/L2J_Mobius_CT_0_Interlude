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

import java.util.Collection;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.ServerPackets;

public class WareHouseWithdrawalList extends ServerPacket
{
	public static final int PRIVATE = 1;
	public static final int CLAN = 2;
	public static final int CASTLE = 3; // not sure
	public static final int FREIGHT = 4;
	
	private long _playerAdena;
	private Collection<Item> _items;
	/**
	 * <ul>
	 * <li>0x01-Private Warehouse</li>
	 * <li>0x02-Clan Warehouse</li>
	 * <li>0x03-Castle Warehouse</li>
	 * <li>0x04-Warehouse</li>
	 * </ul>
	 */
	private int _whType;
	
	public WareHouseWithdrawalList(Player player, int type)
	{
		if (player.getActiveWarehouse() == null)
		{
			PacketLogger.warning("Error while sending withdraw request to: " + player.getName());
			return;
		}
		_playerAdena = player.getAdena();
		_items = player.getActiveWarehouse().getItems();
		_whType = type;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.WAREHOUSE_WITHDRAW_LIST.writeId(this, buffer);
		buffer.writeShort(_whType);
		buffer.writeInt((int) _playerAdena);
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
			buffer.writeShort(0);
			buffer.writeShort(item.getCustomType2());
			buffer.writeInt(item.getObjectId());
			if (item.isAugmented())
			{
				buffer.writeInt(0x0000FFFF & item.getAugmentation().getAugmentationId());
				buffer.writeInt(item.getAugmentation().getAugmentationId() >> 16);
			}
			else
			{
				buffer.writeLong(0);
			}
		}
	}
}
