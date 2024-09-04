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
package quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.instancemanager.QuestManager;
import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.serverpackets.MagicSkillUse;
import org.l2jmobius.gameserver.network.serverpackets.NpcSay;

public class SagasSuperClass extends Quest
{
	public int[] _npc = {};
	public int[] _items = {};
	public int[] _mob = {};
	public int[] _classId = {};
	public int[] _prevClass = {};
	public int[] _x = {};
	public int[] _y = {};
	public int[] _z = {};
	public String[] _text = {};
	private static final Map<Npc, Integer> SPAWN_LIST = new HashMap<>();
	// @formatter:off
	private static final int[][] QUEST_CLASSES =
	{
		{ 0x7f }, { 0x80, 0x81 }, { 0x82 }, { 0x05 }, { 0x14 }, { 0x15 },
		{ 0x02 }, { 0x03 }, { 0x2e }, { 0x30 }, { 0x33 }, { 0x34 }, { 0x08 },
		{ 0x17 }, { 0x24 }, { 0x09 }, { 0x18 }, { 0x25 }, { 0x10 }, { 0x11 },
		{ 0x1e }, { 0x0c }, { 0x1b }, { 0x28 }, { 0x0e }, { 0x1c }, { 0x29 },
		{ 0x0d }, { 0x06 }, { 0x22 }, { 0x21 }, { 0x2b }, { 0x37 }, { 0x39 }
	};
	// @formatter:on
	private static final int[] ARCHON_HALISHA_NORM =
	{
		18212,
		18214,
		18215,
		18216,
		18218
	};
	
	public SagasSuperClass(int questId)
	{
		super(questId);
	}
	
	public void registerNPCs()
	{
		addStartNpc(_npc[0]);
		addAttackId(_mob[2]);
		addAttackId(_mob[1]);
		addSkillSeeId(_mob[1]);
		addFirstTalkId(_npc[4]);
		for (int npc : _npc)
		{
			addTalkId(npc);
		}
		for (int mobid : _mob)
		{
			addKillId(mobid);
		}
		final int[] questItemIds = _items.clone();
		questItemIds[0] = 0;
		questItemIds[2] = 0; // remove Ice Crystal and Divine Stone of Wisdom
		registerQuestItems(questItemIds);
		for (int archonMinion = 21646; archonMinion < 21652; archonMinion++)
		{
			addKillId(archonMinion);
		}
		for (int element : ARCHON_HALISHA_NORM)
		{
			addKillId(element);
		}
		for (int guardianAngel = 27214; guardianAngel < 27217; guardianAngel++)
		{
			addKillId(guardianAngel);
		}
	}
	
	public void Cast(Npc npc, Creature target, int skillId, int level)
	{
		target.broadcastPacket(new MagicSkillUse(target, target, skillId, level, 6000, 1));
		target.broadcastPacket(new MagicSkillUse(npc, npc, skillId, level, 6000, 1));
	}
	
	public void addSpawn(QuestState st, Npc mob)
	{
		SPAWN_LIST.put(mob, st.getPlayer().getObjectId());
	}
	
	public Npc findSpawn(Player player, Npc npc)
	{
		if (SPAWN_LIST.containsKey(npc) && (SPAWN_LIST.get(npc) == player.getObjectId()))
		{
			return npc;
		}
		return null;
	}
	
	public void deleteSpawn(Npc npc)
	{
		if (SPAWN_LIST.containsKey(npc))
		{
			SPAWN_LIST.remove(npc);
			npc.deleteMe();
		}
	}
	
	public QuestState findRightState(Npc npc)
	{
		Player player = null;
		QuestState st = null;
		if (SPAWN_LIST.containsKey(npc))
		{
			player = (Player) World.getInstance().findObject(SPAWN_LIST.get(npc));
			if (player != null)
			{
				st = player.getQuestState(getName());
			}
		}
		return st;
	}
	
	public void giveHallishaMark(QuestState st2)
	{
		if (st2.getInt("spawned") == 0)
		{
			final Player player = st2.getPlayer();
			if (getQuestItemsCount(player, _items[3]) >= 700)
			{
				takeItems(player, _items[3], 20);
				final int xx = st2.getPlayer().getX();
				final int yy = st2.getPlayer().getY();
				final int zz = st2.getPlayer().getZ();
				final Npc archon = addSpawn(_mob[1], xx, yy, zz, -1);
				addSpawn(st2, archon);
				st2.set("spawned", "1");
				startQuestTimer("Archon Hellisha has despawned", 600000, archon, st2.getPlayer());
				autoChat(archon, _text[13].replace("PLAYERNAME", st2.getPlayer().getName()));
				((Attackable) archon).addDamageHate(st2.getPlayer(), 0, 99999);
				archon.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, st2.getPlayer(), null);
			}
			else
			{
				giveItems(player, _items[3], getRandom(1, 4));
			}
		}
	}
	
	public QuestState findQuest(Player player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st != null)
		{
			final int id = st.getQuest().getId();
			if (id != 68)
			{
				if (player.getClassId().getId() == QUEST_CLASSES[id - 67][0])
				{
					return st;
				}
			}
			else
			{
				for (int q = 0; q < 2; q++)
				{
					if (player.getClassId().getId() == QUEST_CLASSES[1][q])
					{
						return st;
					}
				}
			}
		}
		return null;
	}
	
	public int getClassId(Player player)
	{
		if (player.getClassId().getId() == 0x81)
		{
			return _classId[1];
		}
		return _classId[0];
	}
	
	public int getPrevClass(Player player)
	{
		if (player.getClassId().getId() == 0x81)
		{
			if (_prevClass.length == 1)
			{
				return -1;
			}
			return _prevClass[1];
		}
		return _prevClass[0];
	}
	
	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		final QuestState st = player.getQuestState(getName());
		String htmltext = "";
		if (st != null)
		{
			if (event.equals("0-011.htm") || event.equals("0-012.htm") || event.equals("0-013.htm") || event.equals("0-014.htm") || event.equals("0-015.htm"))
			{
				htmltext = event;
			}
			else if (event.equals("accept"))
			{
				st.startQuest();
				giveItems(player, _items[10], 1);
				htmltext = "0-03.htm";
			}
			else if (event.equals("0-1"))
			{
				if (player.getLevel() < 76)
				{
					htmltext = "0-02.htm";
					if (st.getState() == State.CREATED)
					{
						st.exitQuest(true);
					}
				}
				else
				{
					htmltext = "0-05.htm";
				}
			}
			else if (event.equals("0-2"))
			{
				if (player.getLevel() >= 76)
				{
					st.setCond(0);
					st.exitQuest(false);
					htmltext = "0-07.htm";
					takeItems(player, _items[10], -1);
					addExpAndSp(player, 2299404, 0);
					giveAdena(player, 5000000, true);
					giveItems(player, 6622, 1);
					final int playerClass = getClassId(player);
					final int prevClass = getPrevClass(player);
					player.setClassId(playerClass);
					if (!player.isSubClassActive() && (player.getBaseClass() == prevClass))
					{
						player.setBaseClass(playerClass);
					}
					player.rewardSkills();
					player.broadcastUserInfo();
					Cast(npc, player, 4339, 1);
					
					final Quest q = QuestManager.getInstance().getQuest("SkillTransfer");
					if (q != null)
					{
						q.startQuestTimer("givePormanders", 1, npc, player);
					}
				}
				else
				{
					takeItems(player, _items[10], -1);
					st.setCond(20, true);
					htmltext = "0-08.htm";
				}
			}
			else if (event.equals("1-3"))
			{
				st.setCond(3);
				htmltext = "1-05.htm";
			}
			else if (event.equals("1-4"))
			{
				st.setCond(4);
				takeItems(player, _items[0], 1);
				if (_items[11] != 0)
				{
					takeItems(player, _items[11], 1);
				}
				giveItems(player, _items[1], 1);
				htmltext = "1-06.htm";
			}
			else if (event.equals("2-1"))
			{
				st.setCond(2);
				htmltext = "2-05.htm";
			}
			else if (event.equals("2-2"))
			{
				st.setCond(5);
				takeItems(player, _items[1], 1);
				giveItems(player, _items[4], 1);
				htmltext = "2-06.htm";
			}
			else if (event.equals("3-5"))
			{
				htmltext = "3-07.htm";
			}
			else if (event.equals("3-6"))
			{
				st.setCond(11);
				htmltext = "3-02.htm";
			}
			else if (event.equals("3-7"))
			{
				st.setCond(12);
				htmltext = "3-03.htm";
			}
			else if (event.equals("3-8"))
			{
				st.setCond(13);
				takeItems(player, _items[2], 1);
				giveItems(player, _items[7], 1);
				htmltext = "3-08.htm";
			}
			else if (event.equals("4-1"))
			{
				htmltext = "4-010.htm";
			}
			else if (event.equals("4-2"))
			{
				giveItems(player, _items[9], 1);
				st.setCond(18, true);
				htmltext = "4-011.htm";
			}
			else if (event.equals("4-3"))
			{
				giveItems(player, _items[9], 1);
				st.setCond(18, true);
				autoChat(npc, _text[13].replace("PLAYERNAME", player.getName()));
				st.set("Quest0", "0");
				cancelQuestTimer("Mob_2 has despawned", npc, player);
				deleteSpawn(npc);
				return null;
			}
			else if (event.equals("5-1"))
			{
				st.setCond(6, true);
				takeItems(player, _items[4], 1);
				Cast(npc, player, 4546, 1);
				htmltext = "5-02.htm";
			}
			else if (event.equals("6-1"))
			{
				st.setCond(8, true);
				takeItems(player, _items[5], 1);
				Cast(npc, player, 4546, 1);
				htmltext = "6-03.htm";
			}
			else if (event.equals("7-1"))
			{
				if (st.getInt("spawned") == 1)
				{
					htmltext = "7-03.htm";
				}
				else if (st.getInt("spawned") == 0)
				{
					final Npc mob1 = addSpawn(_mob[0], _x[0], _y[0], _z[0], -1);
					st.set("spawned", "1");
					startQuestTimer("Mob_1 Timer 1", 500, mob1, player);
					startQuestTimer("Mob_1 has despawned", 300000, mob1, player);
					addSpawn(st, mob1);
					htmltext = "7-02.htm";
				}
				else
				{
					htmltext = "7-04.htm";
				}
			}
			else if (event.equals("7-2"))
			{
				st.setCond(10, true);
				takeItems(player, _items[6], 1);
				Cast(npc, player, 4546, 1);
				htmltext = "7-06.htm";
			}
			else if (event.equals("8-1"))
			{
				st.setCond(14, true);
				takeItems(player, _items[7], 1);
				Cast(npc, player, 4546, 1);
				htmltext = "8-02.htm";
			}
			else if (event.equals("9-1"))
			{
				st.setCond(17, true);
				takeItems(player, _items[8], 1);
				Cast(npc, player, 4546, 1);
				htmltext = "9-03.htm";
			}
			else if (event.equals("10-1"))
			{
				if (st.getInt("Quest0") == 0)
				{
					final Npc mob3 = addSpawn(_mob[2], _x[1], _y[1], _z[1], -1);
					final Npc mob2 = addSpawn(_npc[4], _x[2], _y[2], _z[2], -1);
					addSpawn(st, mob3);
					addSpawn(st, mob2);
					st.set("Mob_2", String.valueOf(mob2.getObjectId()));
					st.set("Quest0", "1");
					st.set("Quest1", "45");
					startQuestTimer("Mob_3 Timer 1", 500, mob3, player, true);
					startQuestTimer("Mob_3 has despawned", 59000, mob3, player);
					startQuestTimer("Mob_2 Timer 1", 500, mob2, player);
					startQuestTimer("Mob_2 has despawned", 60000, mob2, player);
					htmltext = "10-02.htm";
				}
				else if (st.getInt("Quest1") == 45)
				{
					htmltext = "10-03.htm";
				}
				else
				{
					htmltext = "10-04.htm";
				}
			}
			else if (event.equals("10-2"))
			{
				st.setCond(19);
				takeItems(player, _items[9], 1);
				Cast(npc, player, 4546, 1);
				htmltext = "10-06.htm";
			}
			else if (event.equals("11-9"))
			{
				st.setCond(15);
				htmltext = "11-03.htm";
			}
			else if (event.equals("Mob_1 Timer 1"))
			{
				autoChat(npc, _text[0].replace("PLAYERNAME", player.getName()));
				return null;
			}
			else if (event.equals("Mob_1 has despawned"))
			{
				autoChat(npc, _text[1].replace("PLAYERNAME", player.getName()));
				st.set("spawned", "0");
				deleteSpawn(npc);
				return null;
			}
			else if (event.equals("Archon Hellisha has despawned"))
			{
				autoChat(npc, _text[6].replace("PLAYERNAME", player.getName()));
				st.set("spawned", "0");
				deleteSpawn(npc);
				return null;
			}
			else if (event.equals("Mob_3 Timer 1"))
			{
				final Npc mob2 = findSpawn(player, (Npc) World.getInstance().findObject(st.getInt("Mob_2")));
				if (World.getInstance().getVisibleObjects(npc, Npc.class).contains(mob2))
				{
					((Attackable) npc).addDamageHate(mob2, 0, 99999);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, mob2, null);
					mob2.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc, null);
					autoChat(npc, _text[14].replace("PLAYERNAME", player.getName()));
					cancelQuestTimer("Mob_3 Timer 1", npc, player);
				}
				return null;
			}
			else if (event.equals("Mob_3 has despawned"))
			{
				autoChat(npc, _text[15].replace("PLAYERNAME", player.getName()));
				st.set("Quest0", "2");
				deleteSpawn(npc);
				return null;
			}
			else if (event.equals("Mob_2 Timer 1"))
			{
				autoChat(npc, _text[7].replace("PLAYERNAME", player.getName()));
				startQuestTimer("Mob_2 Timer 2", 1500, npc, player);
				if (st.getInt("Quest1") == 45)
				{
					st.set("Quest1", "0");
				}
				return null;
			}
			else if (event.equals("Mob_2 Timer 2"))
			{
				autoChat(npc, _text[8].replace("PLAYERNAME", player.getName()));
				startQuestTimer("Mob_2 Timer 3", 10000, npc, player);
				return null;
			}
			else if (event.equals("Mob_2 Timer 3"))
			{
				if (st.getInt("Quest0") == 0)
				{
					startQuestTimer("Mob_2 Timer 3", 13000, npc, player);
					if (getRandomBoolean())
					{
						autoChat(npc, _text[9].replace("PLAYERNAME", player.getName()));
					}
					else
					{
						autoChat(npc, _text[10].replace("PLAYERNAME", player.getName()));
					}
				}
				return null;
			}
			else if (event.equals("Mob_2 has despawned"))
			{
				st.set("Quest1", String.valueOf(st.getInt("Quest1") + 1));
				if ((st.getInt("Quest0") == 1) || (st.getInt("Quest0") == 2) || (st.getInt("Quest1") > 3))
				{
					st.set("Quest0", "0");
					if (st.getInt("Quest0") == 1)
					{
						autoChat(npc, _text[11].replace("PLAYERNAME", player.getName()));
					}
					else
					{
						autoChat(npc, _text[12].replace("PLAYERNAME", player.getName()));
					}
					deleteSpawn(npc);
				}
				else
				{
					startQuestTimer("Mob_2 has despawned", 1000, npc, player);
				}
				return null;
			}
		}
		else
		{
			return null;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player talker)
	{
		String htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPCs minimum quest requirements.</body></html>";
		final QuestState st = talker.getQuestState(getName());
		if (st != null)
		{
			final int npcId = npc.getId();
			if ((st.getState() == State.COMPLETED) && (npcId == _npc[0]))
			{
				htmltext = "<html><body>You have already completed this quest!</body></html>";
			}
			else if (st.getPlayer().getClassId().getId() == getPrevClass(st.getPlayer()))
			{
				switch (st.getCond())
				{
					case 0:
					{
						if (npcId == _npc[0])
						{
							htmltext = "0-01.htm";
						}
						break;
					}
					case 1:
					{
						if (npcId == _npc[0])
						{
							htmltext = "0-04.htm";
						}
						else if (npcId == _npc[2])
						{
							htmltext = "2-01.htm";
						}
						break;
					}
					case 2:
					{
						if (npcId == _npc[2])
						{
							htmltext = "2-02.htm";
						}
						else if (npcId == _npc[1])
						{
							htmltext = "1-01.htm";
						}
						break;
					}
					case 3:
					{
						if ((npcId == _npc[1]) && (getQuestItemsCount(st.getPlayer(), _items[0]) != 0))
						{
							htmltext = "1-02.htm";
							if ((_items[11] == 0) || (getQuestItemsCount(st.getPlayer(), _items[11]) != 0))
							{
								htmltext = "1-03.htm";
							}
						}
						break;
					}
					case 4:
					{
						if (npcId == _npc[1])
						{
							htmltext = "1-04.htm";
						}
						else if (npcId == _npc[2])
						{
							htmltext = "2-03.htm";
						}
						break;
					}
					case 5:
					{
						if (npcId == _npc[2])
						{
							htmltext = "2-04.htm";
						}
						else if (npcId == _npc[5])
						{
							htmltext = "5-01.htm";
						}
						break;
					}
					case 6:
					{
						if (npcId == _npc[5])
						{
							htmltext = "5-03.htm";
						}
						else if (npcId == _npc[6])
						{
							htmltext = "6-01.htm";
						}
						break;
					}
					case 7:
					{
						if (npcId == _npc[6])
						{
							htmltext = "6-02.htm";
						}
						break;
					}
					case 8:
					{
						if (npcId == _npc[6])
						{
							htmltext = "6-04.htm";
						}
						else if (npcId == _npc[7])
						{
							htmltext = "7-01.htm";
						}
						break;
					}
					case 9:
					{
						if (npcId == _npc[7])
						{
							htmltext = "7-05.htm";
						}
						break;
					}
					case 10:
					{
						if (npcId == _npc[7])
						{
							htmltext = "7-07.htm";
						}
						else if (npcId == _npc[3])
						{
							htmltext = "3-01.htm";
						}
						break;
					}
					case 11:
					case 12:
					{
						if (npcId == _npc[3])
						{
							if (getQuestItemsCount(st.getPlayer(), _items[2]) > 0)
							{
								htmltext = "3-05.htm";
							}
							else
							{
								htmltext = "3-04.htm";
							}
						}
						break;
					}
					case 13:
					{
						if (npcId == _npc[3])
						{
							htmltext = "3-06.htm";
						}
						else if (npcId == _npc[8])
						{
							htmltext = "8-01.htm";
						}
						break;
					}
					case 14:
					{
						if (npcId == _npc[8])
						{
							htmltext = "8-03.htm";
						}
						else if (npcId == _npc[11])
						{
							htmltext = "11-01.htm";
						}
						break;
					}
					case 15:
					{
						if (npcId == _npc[11])
						{
							htmltext = "11-02.htm";
						}
						else if (npcId == _npc[9])
						{
							htmltext = "9-01.htm";
						}
						break;
					}
					case 16:
					{
						if (npcId == _npc[9])
						{
							htmltext = "9-02.htm";
						}
						break;
					}
					case 17:
					{
						if (npcId == _npc[9])
						{
							htmltext = "9-04.htm";
						}
						else if (npcId == _npc[10])
						{
							htmltext = "10-01.htm";
						}
						break;
					}
					case 18:
					{
						if (npcId == _npc[10])
						{
							htmltext = "10-05.htm";
						}
						break;
					}
					case 19:
					{
						if (npcId == _npc[10])
						{
							htmltext = "10-07.htm";
						}
						else if (npcId == _npc[0])
						{
							htmltext = "0-06.htm";
						}
						break;
					}
					case 20:
					{
						if (npcId == _npc[0])
						{
							final Player player = st.getPlayer();
							if (player.getLevel() >= 76)
							{
								htmltext = "0-09.htm";
								if ((getClassId(player) < 131) || (getClassId(player) > 135)) // in Kamael quests, npc wants to chat for a bit before changing class
								{
									st.setCond(0);
									st.exitQuest(false);
									addExpAndSp(player, 2299404, 0);
									giveAdena(player, 5000000, true);
									giveItems(player, 6622, 1);
									final int playerClass = getClassId(player);
									final int prevClass = getPrevClass(player);
									player.setClassId(playerClass);
									if (!player.isSubClassActive() && (player.getBaseClass() == prevClass))
									{
										player.setBaseClass(playerClass);
									}
									player.rewardSkills();
									player.broadcastUserInfo();
									Cast(npc, player, 4339, 1);
									
									final Quest q = QuestManager.getInstance().getQuest("SkillTransfer");
									if (q != null)
									{
										q.startQuestTimer("givePormanders", 1, npc, st.getPlayer());
									}
								}
							}
							else
							{
								htmltext = "0-010.htm";
							}
						}
						break;
					}
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = "";
		final QuestState st = player.getQuestState(getName());
		final int npcId = npc.getId();
		if (st != null)
		{
			final int cond = st.getCond();
			if (npcId == _npc[4])
			{
				if (cond == 17)
				{
					final QuestState st2 = findRightState(npc);
					if (st2 != null)
					{
						player.setLastQuestNpcObject(npc.getObjectId());
						if (st == st2)
						{
							if (st.getInt("Tab") == 1)
							{
								if (st.getInt("Quest0") == 0)
								{
									htmltext = "4-04.htm";
								}
								else if (st.getInt("Quest0") == 1)
								{
									htmltext = "4-06.htm";
								}
							}
							else
							{
								if (st.getInt("Quest0") == 0)
								{
									htmltext = "4-01.htm";
								}
								else if (st.getInt("Quest0") == 1)
								{
									htmltext = "4-03.htm";
								}
							}
						}
						else
						{
							if (st.getInt("Tab") == 1)
							{
								if (st.getInt("Quest0") == 0)
								{
									htmltext = "4-05.htm";
								}
								else if (st.getInt("Quest0") == 1)
								{
									htmltext = "4-07.htm";
								}
							}
							else
							{
								if (st.getInt("Quest0") == 0)
								{
									htmltext = "4-02.htm";
								}
							}
						}
					}
				}
				else if (cond == 18)
				{
					htmltext = "4-08.htm";
				}
			}
		}
		if (htmltext.equals(""))
		{
			npc.showChatWindow(player);
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(Npc npc, Player player, int damage, boolean isPet)
	{
		final QuestState st2 = findRightState(npc);
		if (st2 == null)
		{
			return super.onAttack(npc, player, damage, isPet);
		}
		
		final int cond = st2.getCond();
		final QuestState st = player.getQuestState(getName());
		final int npcId = npc.getId();
		if ((npcId == _mob[2]) && (st == st2) && (cond == 17))
		{
			st.set("Quest0", String.valueOf(st.getInt("Quest0") + 1));
			if (st.getInt("Quest0") == 1)
			{
				autoChat(npc, _text[16].replace("PLAYERNAME", player.getName()));
			}
			if (st.getInt("Quest0") > 15)
			{
				st.set("Quest0", "1");
				autoChat(npc, _text[17].replace("PLAYERNAME", player.getName()));
				cancelQuestTimer("Mob_3 has despawned", npc, st2.getPlayer());
				st.set("Tab", "1");
				deleteSpawn(npc);
			}
		}
		else if ((npcId == _mob[1]) && (cond == 15) && ((st != st2) || player.isInParty()))
		{
			autoChat(npc, _text[5].replace("PLAYERNAME", player.getName()));
			cancelQuestTimer("Archon Hellisha has despawned", npc, st2.getPlayer());
			st2.set("spawned", "0");
			deleteSpawn(npc);
		}
		return super.onAttack(npc, player, damage, isPet);
	}
	
	@Override
	public String onSkillSee(Npc npc, Player player, Skill skill, List<WorldObject> targets, boolean isSummon)
	{
		if (SPAWN_LIST.containsKey(npc) && (SPAWN_LIST.get(npc) != player.getObjectId()))
		{
			final Player questPlayer = (Player) World.getInstance().findObject(SPAWN_LIST.get(npc));
			if (questPlayer == null)
			{
				return null;
			}
			for (WorldObject obj : skill.getTargetList(player))
			{
				if ((obj == questPlayer) || (obj == npc))
				{
					final QuestState st2 = findRightState(npc);
					if (st2 == null)
					{
						return null;
					}
					autoChat(npc, _text[5].replace("PLAYERNAME", player.getName()));
					cancelQuestTimer("Archon Hellisha has despawned", npc, st2.getPlayer());
					st2.set("spawned", "0");
					deleteSpawn(npc);
				}
			}
		}
		return null;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		final int npcId = npc.getId();
		for (int archonMinion = 21646; archonMinion < 21652; archonMinion++)
		{
			if (npcId == archonMinion)
			{
				final Party party = player.getParty();
				if (party != null)
				{
					final List<QuestState> partyQuestMembers = new ArrayList<>();
					for (Player player1 : party.getMembers())
					{
						final QuestState st1 = findQuest(player1);
						if ((st1 != null) && st1.isCond(15))
						{
							partyQuestMembers.add(st1);
						}
					}
					if (!partyQuestMembers.isEmpty())
					{
						final QuestState st2 = partyQuestMembers.get(getRandom(partyQuestMembers.size()));
						giveHallishaMark(st2);
					}
				}
				else
				{
					final QuestState st1 = findQuest(player);
					if ((st1 != null) && st1.isCond(15))
					{
						giveHallishaMark(st1);
					}
				}
				return super.onKill(npc, player, isPet);
			}
		}
		
		for (int element : ARCHON_HALISHA_NORM)
		{
			if (npcId == element)
			{
				final QuestState st1 = findQuest(player);
				if ((st1 != null) && st1.isCond(15))
				{
					// This is just a guess....not really sure what it actually says, if anything
					autoChat(npc, _text[4].replace("PLAYERNAME", st1.getPlayer().getName()));
					giveItems(player, _items[8], 1);
					takeItems(player, _items[3], -1);
					st1.setCond(16, true);
				}
				return super.onKill(npc, player, isPet);
			}
		}
		
		for (int guardianAngel = 27214; guardianAngel < 27217; guardianAngel++)
		{
			if (npcId == guardianAngel)
			{
				final QuestState st1 = findQuest(player);
				if ((st1 != null) && st1.isCond(6))
				{
					if (st1.getInt("kills") < 9)
					{
						st1.set("kills", String.valueOf(st1.getInt("kills") + 1));
					}
					else
					{
						giveItems(player, _items[5], 1);
						st1.setCond(7, true);
					}
				}
				return super.onKill(npc, player, isPet);
			}
		}
		
		QuestState st = player.getQuestState(getName());
		if ((st != null) && (npcId != _mob[2]))
		{
			final QuestState st2 = findRightState(npc);
			if (st2 == null)
			{
				return super.onKill(npc, player, isPet);
			}
			if ((npcId == _mob[0]) && st.isCond(8))
			{
				if (!player.isInParty() && (st == st2))
				{
					autoChat(npc, _text[12].replace("PLAYERNAME", player.getName()));
					giveItems(player, _items[6], 1);
					st.setCond(9, true);
				}
				cancelQuestTimer("Mob_1 has despawned", npc, st2.getPlayer());
				st2.set("spawned", "0");
				deleteSpawn(npc);
			}
			else if ((npcId == _mob[1]) && st.isCond(15))
			{
				if (!player.isInParty())
				{
					if (st == st2)
					{
						autoChat(npc, _text[4].replace("PLAYERNAME", player.getName()));
						giveItems(player, _items[8], 1);
						takeItems(player, _items[3], -1);
						st.setCond(16, true);
					}
					else
					{
						autoChat(npc, _text[5].replace("PLAYERNAME", player.getName()));
					}
				}
				cancelQuestTimer("Archon Hellisha has despawned", npc, st2.getPlayer());
				st2.set("spawned", "0");
				deleteSpawn(npc);
			}
		}
		else
		{
			if (npcId == _mob[0])
			{
				st = findRightState(npc);
				if (st != null)
				{
					cancelQuestTimer("Mob_1 has despawned", npc, st.getPlayer());
					st.set("spawned", "0");
					deleteSpawn(npc);
				}
			}
			else if (npcId == _mob[1])
			{
				st = findRightState(npc);
				if (st != null)
				{
					cancelQuestTimer("Archon Hellisha has despawned", npc, st.getPlayer());
					st.set("spawned", "0");
					deleteSpawn(npc);
				}
			}
		}
		return super.onKill(npc, player, isPet);
	}
	
	private void autoChat(Npc npc, String text)
	{
		npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.GENERAL, npc.getId(), text));
	}
}
