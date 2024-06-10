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
package org.l2jmobius.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.templates.NpcTemplate;
import org.l2jmobius.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.model.actor.Npc;

/**
 * @author Trance, Bluur
 * @adapted for L2jmobius Reanimation
 */
public final class Buffer extends Npc
{
	public Buffer(NpcTemplate template)
	{
		super(template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		
		int buffid = 0, bufflevel = 1;
		if (st.countTokens() == 2)
		{
			buffid = Integer.valueOf(st.nextToken());
			bufflevel = Integer.valueOf(st.nextToken());
		}
		else if (st.countTokens() == 1)
		{
			buffid = Integer.valueOf(st.nextToken());
		}
		
		if (actualCommand.equalsIgnoreCase("getbuff"))
		{
			SkillData.getInstance().getSkill(buffid, bufflevel).applyEffects(this, player);
			broadcastPacket(new MagicSkillUse(this, player, buffid, bufflevel, 100, 0));
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player, getHtmlPath(getId(), 1));
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("restore"))
		{
			player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());
			
			broadcastPacket(new MagicSkillUse(this, player, 1258, 4, 500, 0));
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player, getHtmlPath(getId(), 1));
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("cancel"))
		{
			player.stopAllEffects();
			broadcastPacket(new MagicSkillUse(this, player, 1056, 12, 500, 0));
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player, getHtmlPath(getId(), 1));
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
	
	@Override
	public String getHtmlPath(int npcId, int value)
	{
		String filename = "";
		if (value == 0)
		{
			filename = Integer.toString(npcId);
		}
		else
		{
			filename = npcId + "-" + value;
		}
		return "data/html/mods/Buffer/" + filename + ".htm";
	}
}