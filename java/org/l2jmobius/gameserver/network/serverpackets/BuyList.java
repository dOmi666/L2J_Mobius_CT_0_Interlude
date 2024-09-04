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

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.model.buylist.BuyListHolder;
import org.l2jmobius.gameserver.model.buylist.Product;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

public class BuyList extends ServerPacket
{
	private final int _listId;
	private final Collection<Product> _list;
	private final long _money;
	private double _taxRate = 0;
	
	public BuyList(BuyListHolder list, long currentMoney, double taxRate)
	{
		_listId = list.getListId();
		_list = list.getProducts();
		_money = currentMoney;
		_taxRate = taxRate;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.BUY_LIST.writeId(this, buffer); // writeC(7) ?
		buffer.writeInt((int) _money); // current money
		buffer.writeInt(_listId);
		buffer.writeShort(_list.size());
		for (Product product : _list)
		{
			if ((product.getCount() > 0) || !product.hasLimitedStock())
			{
				buffer.writeShort(product.getItem().getType1()); // item type1
				buffer.writeInt(0); // objectId
				buffer.writeInt(product.getItemId());
				buffer.writeInt(product.getCount() < 0 ? 0 : product.getCount());
				buffer.writeShort(product.getItem().getType2());
				buffer.writeShort(0); // isEquipped
				if (product.getItem().getType1() != ItemTemplate.TYPE1_ITEM_QUESTITEM_ADENA)
				{
					buffer.writeInt(product.getItem().getBodyPart());
					buffer.writeShort(0); // item enchant level
					buffer.writeShort(0); // ?
					buffer.writeShort(0);
				}
				else
				{
					buffer.writeInt(0);
					buffer.writeShort(0);
					buffer.writeShort(0);
					buffer.writeShort(0);
				}
				if ((product.getItemId() >= 3960) && (product.getItemId() <= 4026))
				{
					buffer.writeInt((int) (product.getPrice() * Config.RATE_SIEGE_GUARDS_PRICE * (1 + _taxRate)));
				}
				else
				{
					buffer.writeInt((int) (product.getPrice() * (1 + _taxRate)));
				}
			}
		}
	}
}
