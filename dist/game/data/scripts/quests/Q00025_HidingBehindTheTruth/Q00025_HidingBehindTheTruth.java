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
package quests.Q00025_HidingBehindTheTruth;

import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import quests.Q00024_InhabitantsOfTheForestOfTheDead.Q00024_InhabitantsOfTheForestOfTheDead;

/**
 * @author Mobius
 * @note Based on python script
 */
public class Q00025_HidingBehindTheTruth extends Quest
{
	// NPCs
	private static final int AGRIPEL = 31348;
	private static final int BENEDICT = 31349;
	private static final int WIZARD = 31522;
	private static final int TOMBSTONE = 31531;
	private static final int LIDIA = 31532;
	private static final int BOOKSHELF = 31533;
	private static final int BOOKSHELF2 = 31534;
	private static final int BOOKSHELF3 = 31535;
	private static final int COFFIN = 31536;
	private static final int TRIOL = 27218;
	// Items
	private static final int CONTRACT = 7066;
	private static final int DRESS = 7155;
	private static final int SUSPICIOUS_TOTEM = 7156;
	private static final int GEMSTONE_KEY = 7157;
	private static final int TOTEM_DOLL = 7158;
	
	public Q00025_HidingBehindTheTruth()
	{
		super(25);
		addStartNpc(BENEDICT);
		addTalkId(AGRIPEL, BENEDICT, BOOKSHELF, BOOKSHELF2, BOOKSHELF3, WIZARD, LIDIA, TOMBSTONE, COFFIN);
		addKillId(TRIOL);
		registerQuestItems(SUSPICIOUS_TOTEM, GEMSTONE_KEY, TOTEM_DOLL, DRESS);
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		final QuestState qs = player.getQuestState(getName());
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "31349-02.htm":
			{
				qs.startQuest();
				break;
			}
			case "31349-03.htm":
			{
				if (getQuestItemsCount(player, SUSPICIOUS_TOTEM) > 0)
				{
					htmltext = "31349-05.htm";
				}
				else
				{
					qs.setCond(2, true);
				}
				break;
			}
			case "31349-10.htm":
			{
				qs.setCond(4, true);
				break;
			}
			case "31348-02.htm":
			{
				takeItems(player, SUSPICIOUS_TOTEM, -1);
				break;
			}
			case "31348-07.htm":
			{
				qs.setCond(5, true);
				giveItems(player, GEMSTONE_KEY, 1);
				break;
			}
			case "31522-04.htm":
			{
				qs.setCond(6, true);
				break;
			}
			case "31535-03.htm":
			{
				if (qs.getInt("step") == 0)
				{
					qs.set("step", "1");
					final Npc triol = addSpawn(TRIOL, 59712, -47568, -2712, -1, false, 300000);
					triol.broadcastSay(ChatType.GENERAL, "That box was sealed by my master. Don't touch it!");
					triol.setRunning();
					((Attackable) triol).addDamageHate(player, 0, 999);
					triol.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
					qs.setCond(7, true);
				}
				else if (qs.getInt("step") == 2)
				{
					htmltext = "31535-04.htm";
				}
				break;
			}
			case "31535-05.htm":
			{
				giveItems(player, CONTRACT, 1);
				takeItems(player, GEMSTONE_KEY, -1);
				qs.setCond(9, true);
				break;
			}
			case "31532-02.htm":
			{
				takeItems(player, CONTRACT, -1);
				break;
			}
			case "31532-06.htm":
			{
				qs.setCond(11, true);
				break;
			}
			case "31531-02.htm":
			{
				qs.setCond(12, true);
				addSpawn(COFFIN, 60104, -35820, -664, -1, false, 20000);
				break;
			}
			case "31532-18.htm":
			{
				qs.setCond(15, true);
				break;
			}
			case "31522-12.htm":
			{
				qs.setCond(16, true);
			}
				break;
			case "31348-10.htm":
			{
				takeItems(player, TOTEM_DOLL, -1);
				break;
			}
			case "31348-15.htm":
			{
				qs.setCond(17, true);
				break;
			}
			case "31348-16.htm":
			{
				qs.setCond(18, true);
				break;
			}
			case "31532-20.htm":
			{
				giveItems(player, 905, 2);
				giveItems(player, 874, 1);
				takeItems(player, 7063, -1);
				addExpAndSp(player, 572277, 53750);
				qs.unset("cond");
				qs.exitQuest(true, true);
				break;
			}
			case "31522-15.htm":
			{
				giveItems(player, 936, 1);
				giveItems(player, 874, 1);
				takeItems(player, 7063, -1);
				addExpAndSp(player, 572277, 53750);
				qs.unset("cond");
				qs.exitQuest(true, true);
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		final int npcId = npc.getId();
		final int id = qs.getState();
		final int cond = qs.getCond();
		if (id == State.COMPLETED)
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		else if (id == State.CREATED)
		{
			if (npcId == BENEDICT)
			{
				final QuestState qs2 = player.getQuestState(Q00024_InhabitantsOfTheForestOfTheDead.class.getSimpleName());
				if (qs2 != null)
				{
					if ((qs2.getState() == State.COMPLETED) && (player.getLevel() >= 66))
					{
						htmltext = "31349-01.htm";
					}
					else
					{
						htmltext = "31349-00.htm";
					}
				}
			}
		}
		else if (id == State.STARTED)
		{
			if (npcId == BENEDICT)
			{
				if (cond == 1)
				{
					htmltext = "31349-02.htm";
				}
				else if ((cond == 2) || (cond == 3))
				{
					htmltext = "31349-04.htm";
				}
				else if (cond == 4)
				{
					htmltext = "31349-10.htm";
				}
			}
			else if (npcId == WIZARD)
			{
				if (cond == 2)
				{
					htmltext = "31522-01.htm";
					qs.setCond(3, true);
					giveItems(player, SUSPICIOUS_TOTEM, 1);
				}
				else if (cond == 3)
				{
					htmltext = "31522-02.htm";
				}
				else if (cond == 5)
				{
					htmltext = "31522-03.htm";
				}
				else if (cond == 6)
				{
					htmltext = "31522-04.htm";
				}
				else if (cond == 9)
				{
					htmltext = "31522-05.htm";
					qs.setCond(10, true);
				}
				else if (cond == 10)
				{
					htmltext = "31522-05.htm";
				}
				else if (cond == 15)
				{
					htmltext = "31522-06.htm";
				}
				else if (cond == 16)
				{
					htmltext = "31522-13.htm";
				}
				else if (cond == 17)
				{
					htmltext = "31522-16.htm";
				}
				else if (cond == 18)
				{
					htmltext = "31522-14.htm";
				}
			}
			else if (npcId == AGRIPEL)
			{
				if (cond == 4)
				{
					htmltext = "31348-01.htm";
				}
				else if (cond == 5)
				{
					htmltext = "31348-08.htm";
				}
				else if (cond == 16)
				{
					htmltext = "31348-09.htm";
				}
				else if (cond == 17)
				{
					htmltext = "31348-17.htm";
				}
				else if (cond == 18)
				{
					htmltext = "31348-18.htm";
				}
			}
			else if (npcId == BOOKSHELF)
			{
				if (cond == 6)
				{
					htmltext = "31533-01.htm";
				}
			}
			else if (npcId == BOOKSHELF2)
			{
				if (cond == 6)
				{
					htmltext = "31534-01.htm";
				}
			}
			else if (npcId == BOOKSHELF3)
			{
				if ((cond >= 6) && (cond <= 8))
				{
					htmltext = "31535-01.htm";
				}
				else if (cond == 9)
				{
					htmltext = "31535-06.htm";
				}
			}
			else if (npcId == LIDIA)
			{
				if (cond == 10)
				{
					htmltext = "31532-01.htm";
				}
				else if ((cond == 11) || (cond == 12))
				{
					htmltext = "31532-06.htm";
				}
				else if (cond == 13)
				{
					htmltext = "31532-07.htm";
					qs.setCond(14);
					takeItems(player, DRESS, -1);
				}
				else if (cond == 14)
				{
					htmltext = "31532-08.htm";
				}
				else if (cond == 15)
				{
					htmltext = "31532-18.htm";
				}
				else if (cond == 17)
				{
					htmltext = "31532-19.htm";
				}
				else if (cond == 18)
				{
					htmltext = "31532-21.htm";
				}
			}
			else if (npcId == TOMBSTONE)
			{
				if ((cond == 11) || (cond == 12))
				{
					htmltext = "31531-01.htm";
				}
				else if (cond == 13)
				{
					htmltext = "31531-03.htm";
				}
			}
			else if (npcId == COFFIN)
			{
				if (cond == 12)
				{
					htmltext = "31536-01.htm";
					giveItems(player, DRESS, 1);
					qs.setCond(13, true);
					npc.deleteMe();
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs == null) || !qs.isStarted())
		{
			return null;
		}
		
		if (qs.isCond(7))
		{
			qs.setCond(8, true);
			npc.broadcastSay(ChatType.GENERAL, "You've ended my immortal life! You've protected by the feudal lord, aren't you?");
			giveItems(player, TOTEM_DOLL, 1);
			qs.set("step", "2");
		}
		
		return null;
	}
}
