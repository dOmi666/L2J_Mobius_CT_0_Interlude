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

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.commons.network.WritablePacket;
import org.l2jmobius.commons.util.CommonUtil;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ConnectionState;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.PacketLogger;

/**
 * @author Mobius
 */
public abstract class ServerPacket extends WritablePacket<GameClient>
{
	@Override
	protected boolean write(GameClient client, WritableBuffer buffer)
	{
		final GameClient c = client;
		if ((c == null) || c.isDetached() || (c.getConnectionState() == ConnectionState.DISCONNECTED))
		{
			return true; // Disconnected client.
		}
		
		try
		{
			writeImpl(c, buffer);
			return true;
		}
		catch (Exception e)
		{
			PacketLogger.warning("Error writing packet " + this + " to client (" + e.getMessage() + ") " + c + "]]");
			PacketLogger.warning(CommonUtil.getStackTrace(e));
		}
		return false;
	}
	
	public void runImpl(Player player)
	{
	}
	
	protected abstract void writeImpl(GameClient client, WritableBuffer buffer) throws Exception;
}
