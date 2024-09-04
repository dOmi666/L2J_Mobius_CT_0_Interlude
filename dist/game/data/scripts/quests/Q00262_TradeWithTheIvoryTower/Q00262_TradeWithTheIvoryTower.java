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
package quests.Q00262_TradeWithTheIvoryTower;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00262_TradeWithTheIvoryTower extends Quest
{
	// Item
	private static final int FUNGUS_SAC = 707;
	
	public Q00262_TradeWithTheIvoryTower()
	{
		super(262);
		registerQuestItems(FUNGUS_SAC);
		addStartNpc(30137); // Vollodos
		addTalkId(30137);
		addKillId(20400, 20007);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("30137-03.htm"))
		{
			st.startQuest();
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() < 8) ? "30137-01.htm" : "30137-02.htm";
				break;
			}
			case State.STARTED:
			{
				if (getQuestItemsCount(player, FUNGUS_SAC) < 10)
				{
					htmltext = "30137-04.htm";
				}
				else
				{
					htmltext = "30137-05.htm";
					takeItems(player, FUNGUS_SAC, -1);
					giveAdena(player, 3000, true);
					st.exitQuest(true, true);
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState st = getQuestState(player, false);
		if ((st == null) || !st.isCond(1))
		{
			return null;
		}
		
		if (getRandom(10) < (npc.getId() == 20400 ? 4 : 3))
		{
			giveItems(player, FUNGUS_SAC, 1);
			
			if (getQuestItemsCount(player, FUNGUS_SAC) >= 10)
			{
				st.setCond(2, true);
			}
			else
			{
				playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		
		return null;
	}
}
