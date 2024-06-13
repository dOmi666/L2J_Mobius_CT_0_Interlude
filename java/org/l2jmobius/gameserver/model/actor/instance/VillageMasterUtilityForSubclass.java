/*
 * Utility class for all subclass functionalities that village
 * master should have so that other Folks can use them
 */
package org.l2jmobius.gameserver.model.actor.instance;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.data.xml.CategoryData;
import org.l2jmobius.gameserver.data.xml.ClassListData;
import org.l2jmobius.gameserver.enums.CategoryType;
import org.l2jmobius.gameserver.enums.ClassId;
import org.l2jmobius.gameserver.enums.Race;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.templates.NpcTemplate;
import org.l2jmobius.gameserver.model.holders.SubClassHolder;
import org.l2jmobius.gameserver.model.olympiad.Olympiad;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

public class VillageMasterUtilityForSubclass
{
	public static final Logger LOGGER = Logger.getLogger(VillageMaster.class.getName());
	
	public static final Set<ClassId> mainSubclassSet;
	public static final Set<ClassId> neverSubclassed = EnumSet.of(ClassId.OVERLORD, ClassId.WARSMITH);
	public static final Set<ClassId> subclasseSet1 = EnumSet.of(ClassId.DARK_AVENGER, ClassId.PALADIN, ClassId.TEMPLE_KNIGHT, ClassId.SHILLIEN_KNIGHT);
	public static final Set<ClassId> subclasseSet2 = EnumSet.of(ClassId.TREASURE_HUNTER, ClassId.ABYSS_WALKER, ClassId.PLAINS_WALKER);
	public static final Set<ClassId> subclasseSet3 = EnumSet.of(ClassId.HAWKEYE, ClassId.SILVER_RANGER, ClassId.PHANTOM_RANGER);
	public static final Set<ClassId> subclasseSet4 = EnumSet.of(ClassId.WARLOCK, ClassId.ELEMENTAL_SUMMONER, ClassId.PHANTOM_SUMMONER);
	public static final Set<ClassId> subclasseSet5 = EnumSet.of(ClassId.SORCERER, ClassId.SPELLSINGER, ClassId.SPELLHOWLER);
	public static final EnumMap<ClassId, Set<ClassId>> subclassSetMap = new EnumMap<>(ClassId.class);
	static
	{
		final Set<ClassId> subclasses = CategoryData.getInstance().getCategoryByType(CategoryType.THIRD_CLASS_GROUP).stream().map(ClassId::getClassId).collect(Collectors.toSet());
		subclasses.removeAll(neverSubclassed);
		mainSubclassSet = subclasses;
		subclassSetMap.put(ClassId.DARK_AVENGER, subclasseSet1);
		subclassSetMap.put(ClassId.PALADIN, subclasseSet1);
		subclassSetMap.put(ClassId.TEMPLE_KNIGHT, subclasseSet1);
		subclassSetMap.put(ClassId.SHILLIEN_KNIGHT, subclasseSet1);
		subclassSetMap.put(ClassId.TREASURE_HUNTER, subclasseSet2);
		subclassSetMap.put(ClassId.ABYSS_WALKER, subclasseSet2);
		subclassSetMap.put(ClassId.PLAINS_WALKER, subclasseSet2);
		subclassSetMap.put(ClassId.HAWKEYE, subclasseSet3);
		subclassSetMap.put(ClassId.SILVER_RANGER, subclasseSet3);
		subclassSetMap.put(ClassId.PHANTOM_RANGER, subclasseSet3);
		subclassSetMap.put(ClassId.WARLOCK, subclasseSet4);
		subclassSetMap.put(ClassId.ELEMENTAL_SUMMONER, subclasseSet4);
		subclassSetMap.put(ClassId.PHANTOM_SUMMONER, subclasseSet4);
		subclassSetMap.put(ClassId.SORCERER, subclasseSet5);
		subclassSetMap.put(ClassId.SPELLSINGER, subclasseSet5);
		subclassSetMap.put(ClassId.SPELLHOWLER, subclasseSet5);
	}

	public static final Set<ClassId> getSubclasses(Player player, int classId)
	{
		Set<ClassId> subclasses = null;
		final ClassId pClass = ClassId.getClassId(classId);
		if (CategoryData.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, classId) || (CategoryData.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, classId)))
		{
			subclasses = EnumSet.copyOf(mainSubclassSet);
			subclasses.remove(pClass);
			
			if (player.getRace() == Race.ELF)
			{
				for (ClassId cid : ClassId.values())
				{
					if (cid.getRace() == Race.DARK_ELF)
					{
						subclasses.remove(cid);
					}
				}
			}
			else if (player.getRace() == Race.DARK_ELF)
			{
				for (ClassId cid : ClassId.values())
				{
					if (cid.getRace() == Race.ELF)
					{
						subclasses.remove(cid);
					}
				}
			}
			
			final Set<ClassId> unavailableClasses = subclassSetMap.get(pClass);
			if (unavailableClasses != null)
			{
				subclasses.removeAll(unavailableClasses);
			}
		}
		
		if (subclasses != null)
		{
			final ClassId currClassId = player.getClassId();
			for (ClassId tempClass : subclasses)
			{
				if (currClassId.equalsOrChildOf(tempClass))
				{
					subclasses.remove(tempClass);
				}
			}
		}
		return subclasses;
	}

		/**
	 * Returns list of available subclasses Base class and already used subclasses removed
	 * @param player
	 * @return
	 */
	public static final Set<ClassId> getAvailableSubClasses(Player player)
	{
		// get player base class
		final int currentBaseId = player.getBaseClass();
		final ClassId baseCID = ClassId.getClassId(currentBaseId);
		
		// we need 2nd occupation ID
		final int baseClassId;
		if (baseCID.level() > 2)
		{
			baseClassId = baseCID.getParent().getId();
		}
		else
		{
			baseClassId = currentBaseId;
		}
		
		/**
		 * If the race of your main class is Elf or Dark Elf, you may not select each class as a subclass to the other class. If the race of your main class is Kamael, you may not subclass any other race If the race of your main class is NOT Kamael, you may not subclass any Kamael class You may not
		 * select Overlord and Warsmith class as a subclass. You may not select a similar class as the subclass. The occupations classified as similar classes are as follows: Treasure Hunter, Plainswalker and Abyss Walker Hawkeye, Silver Ranger and Phantom Ranger Paladin, Dark Avenger, Temple Knight
		 * and Shillien Knight Warlocks, Elemental Summoner and Phantom Summoner Elder and Shillien Elder Swordsinger and Bladedancer Sorcerer, Spellsinger and Spellhowler Also, Kamael have a special, hidden 4 subclass, the inspector, which can only be taken if you have already completed the other
		 * two Kamael subclasses
		 */
		final Set<ClassId> availSubs = getSubclasses(player, baseClassId);
		if ((availSubs != null) && !availSubs.isEmpty())
		{
			for (Iterator<ClassId> availSub = availSubs.iterator(); availSub.hasNext();)
			{
				final ClassId pclass = availSub.next();
				
				// check for the village master
				if (!checkVillageMaster(pclass))
				{
					availSub.remove();
					continue;
				}
				
				// scan for already used subclasses
				final int availClassId = pclass.getId();
				final ClassId cid = ClassId.getClassId(availClassId);
				SubClassHolder prevSubClass;
				ClassId subClassId;
				for (Iterator<SubClassHolder> subList = iterSubClasses(player); subList.hasNext();)
				{
					prevSubClass = subList.next();
					subClassId = ClassId.getClassId(prevSubClass.getClassId());
					if (subClassId.equalsOrChildOf(cid))
					{
						availSub.remove();
						break;
					}
				}
			}
		}
		
		return availSubs;
	}

	public static Iterator<SubClassHolder> iterSubClasses(Player player)
	{
		return player.getSubClasses().values().iterator();
	}

	/**
	 * Check new subclass classId for validity (villagemaster race/type, is not contains in previous subclasses, is contains in allowed subclasses) Base class not added into allowed subclasses.
	 * @param player
	 * @param classId
	 * @return
	 */
	public static final boolean isValidNewSubClass(Player player, int classId)
	{
		if (!checkVillageMaster(classId))
		{
			return false;
		}
		
		final ClassId cid = ClassId.getClassId(classId);
		SubClassHolder sub;
		ClassId subClassId;
		for (Iterator<SubClassHolder> subList = iterSubClasses(player); subList.hasNext();)
		{
			sub = subList.next();
			subClassId = ClassId.getClassId(sub.getClassId());
			if (subClassId.equalsOrChildOf(cid))
			{
				return false;
			}
		}
		
		// get player base class
		final int currentBaseId = player.getBaseClass();
		final ClassId baseCID = ClassId.getClassId(currentBaseId);
		
		// we need 2nd occupation ID
		final int baseClassId;
		if (baseCID.level() > 2)
		{
			baseClassId = baseCID.getParent().getId();
		}
		else
		{
			baseClassId = currentBaseId;
		}
		
		final Set<ClassId> availSubs = getSubclasses(player, baseClassId);
		if ((availSubs == null) || availSubs.isEmpty())
		{
			return false;
		}
		
		boolean found = false;
		for (ClassId pclass : availSubs)
		{
			if (pclass.getId() == classId)
			{
				found = true;
				break;
			}
		}
		return found;
	}

	public static void onBypassFeedback(int villageMasterObjectID, Player player, String command) {
		// Subclasses may not be changed while a skill is in use.
		if (player.isCastingNow() || player.isAllSkillsDisabled())
		{
			player.sendPacket(SystemMessageId.SUBCLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE);
			return;
		}
		
		if (Olympiad.getInstance().isRegisteredInComp(player))
		{
			Olympiad.getInstance().unRegisterNoble(player);
		}
		final NpcHtmlMessage html = new NpcHtmlMessage(villageMasterObjectID);
		
		// Subclasses may not be changed while a summon is active.
		if (player.hasSummon())
		{
			html.setFile(player, "data/html/villagemaster/SubClass_NoSummon.htm");
			player.sendPacket(html);
			return;
		}
		// Subclasses may not be changed while you have exceeded your inventory limit.
		if (!player.isInventoryUnder90(true))
		{
			player.sendPacket(SystemMessageId.A_SUB_CLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT);
			return;
		}
		// Subclasses may not be changed while a you are over your weight limit.
		if (player.getWeightPenalty() >= 2)
		{
			player.sendPacket(SystemMessageId.A_SUB_CLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT);
			return;
		}
		
		int cmdChoice = 0;
		int paramOne = 0;
		int paramTwo = 0;
		try
		{
			cmdChoice = Integer.parseInt(command.substring(9, 10).trim());
			int endIndex = command.indexOf(' ', 11);
			if (endIndex == -1)
			{
				endIndex = command.length();
			}
			
			if (command.length() > 11)
			{
				paramOne = Integer.parseInt(command.substring(11, endIndex).trim());
				if (command.length() > endIndex)
				{
					paramTwo = Integer.parseInt(command.substring(endIndex).trim());
				}
			}
		}
		catch (Exception nfe)
		{
			LOGGER.warning(VillageMaster.class.getName() + ": Wrong numeric values for command " + command);
		}
		
		Set<ClassId> subsAvailable = null;
		switch (cmdChoice)
		{
			case 0: // Subclass change menu
			{
				html.setFile(player, getSubClassMenu(player.getRace()));
				break;
			}
			case 1: // Add Subclass - Initial
			{
				// Avoid giving player an option to add a new sub class, if they have max sub-classes already.
				if (player.getTotalSubClasses() >= Config.MAX_SUBCLASS)
				{
					html.setFile(player, getSubClassFail());
					break;
				}
				
				subsAvailable = getAvailableSubClasses(player);
				if ((subsAvailable != null) && !subsAvailable.isEmpty())
				{
					html.setFile(player, "data/html/villagemaster/SubClass_Add.htm");
					final StringBuilder content1 = new StringBuilder(200);
					for (ClassId subClass : subsAvailable)
					{
						content1.append("<a action=\"bypass -h npc_%objectId%_Subclass 4 " + subClass.getId() + "\" msg=\"1268;" + ClassListData.getInstance().getClass(subClass.getId()).getClassName() + "\">" + ClassListData.getInstance().getClass(subClass.getId()).getClientCode() + "</a><br>");
					}
					html.replace("%list%", content1.toString());
				}
				else
				{
					if ((player.getRace() == Race.ELF) || (player.getRace() == Race.DARK_ELF))
					{
						html.setFile(player, "data/html/villagemaster/SubClass_Fail_Elves.htm");
						player.sendPacket(html);
					}
					else
					{
						// TODO: Retail message
						player.sendMessage("There are no sub classes available at this time.");
					}
					return;
				}
				break;
			}
			case 2: // Change Class - Initial
			{
				if (player.getSubClasses().isEmpty())
				{
					html.setFile(player, "data/html/villagemaster/SubClass_ChangeNo.htm");
				}
				else
				{
					final StringBuilder content2 = new StringBuilder(200);
					if (checkVillageMaster(player.getBaseClass()))
					{
						content2.append("<a action=\"bypass -h npc_%objectId%_Subclass 5 0\">" + ClassListData.getInstance().getClass(player.getBaseClass()).getClientCode() + "</a><br>");
					}
					
					for (Iterator<SubClassHolder> subList = iterSubClasses(player); subList.hasNext();)
					{
						final SubClassHolder subClass = subList.next();
						if (checkVillageMaster(subClass.getClassDefinition()))
						{
							content2.append("<a action=\"bypass -h npc_%objectId%_Subclass 5 " + subClass.getClassIndex() + "\">" + ClassListData.getInstance().getClass(subClass.getClassId()).getClientCode() + "</a><br>");
						}
					}
					
					if (content2.length() > 0)
					{
						html.setFile(player, "data/html/villagemaster/SubClass_Change.htm");
						html.replace("%list%", content2.toString());
					}
					else
					{
						html.setFile(player, "data/html/villagemaster/SubClass_ChangeNotFound.htm");
					}
				}
				break;
			}
			case 3: // Change/Cancel Subclass - Initial
			{
				if ((player.getSubClasses() == null) || player.getSubClasses().isEmpty())
				{
					html.setFile(player, "data/html/villagemaster/SubClass_ModifyEmpty.htm");
					break;
				}
				
				// custom value
				if (player.getTotalSubClasses() > 3)
				{
					html.setFile(player, "data/html/villagemaster/SubClass_ModifyCustom.htm");
					final StringBuilder content3 = new StringBuilder(200);
					int classIndex = 1;
					for (Iterator<SubClassHolder> subList = iterSubClasses(player); subList.hasNext();)
					{
						final SubClassHolder subClass = subList.next();
						content3.append("Sub-class " + classIndex++ + "<br><a action=\"bypass -h npc_%objectId%_Subclass 6 " + subClass.getClassIndex() + "\">" + ClassListData.getInstance().getClass(subClass.getClassId()).getClientCode() + "</a><br>");
					}
					html.replace("%list%", content3.toString());
				}
				else
				{
					// retail html contain only 3 subclasses
					html.setFile(player, "data/html/villagemaster/SubClass_Modify.htm");
					if (player.getSubClasses().containsKey(1))
					{
						html.replace("%sub1%", ClassListData.getInstance().getClass(player.getSubClasses().get(1).getClassId()).getClientCode());
					}
					else
					{
						html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 1\">%sub1%</a><br>", "");
					}
					
					if (player.getSubClasses().containsKey(2))
					{
						html.replace("%sub2%", ClassListData.getInstance().getClass(player.getSubClasses().get(2).getClassId()).getClientCode());
					}
					else
					{
						html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 2\">%sub2%</a><br>", "");
					}
					
					if (player.getSubClasses().containsKey(3))
					{
						html.replace("%sub3%", ClassListData.getInstance().getClass(player.getSubClasses().get(3).getClassId()).getClientCode());
					}
					else
					{
						html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 3\">%sub3%</a><br>", "");
					}
				}
				break;
			}
			case 4: // Add Subclass - Action (Subclass 4 x[x])
			{
				/**
				 * If the character is less than level 75 on any of their previously chosen classes then disallow them to change to their most recently added sub-class choice.
				 */
				if (!player.getClient().getFloodProtectors().canChangeSubclass())
				{
					return;
				}
				
				boolean allowAddition = true;
				if (player.getTotalSubClasses() >= Config.MAX_SUBCLASS)
				{
					allowAddition = false;
				}
				
				if (player.getLevel() < 75)
				{
					allowAddition = false;
				}
				
				if (allowAddition && !player.getSubClasses().isEmpty())
				{
					for (Iterator<SubClassHolder> subList = iterSubClasses(player); subList.hasNext();)
					{
						final SubClassHolder subClass = subList.next();
						if (subClass.getLevel() < 75)
						{
							allowAddition = false;
							break;
						}
					}
				}
				
				/**
				 * If quest checking is enabled, verify if the character has completed the Mimir's Elixir (Path to Subclass) and Fate's Whisper (A Grade Weapon) quests by checking for instances of their unique reward items. If they both exist, remove both unique items and continue with adding
				 * the sub-class.
				 */
				if (allowAddition && !Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS)
				{
					allowAddition = checkQuests(player);
				}
				
				if (allowAddition && isValidNewSubClass(player, paramOne))
				{
					if (!player.addSubClass(paramOne, player.getTotalSubClasses() + 1))
					{
						return;
					}
					
					player.setActiveClass(player.getTotalSubClasses());
					
					html.setFile(player, "data/html/villagemaster/SubClass_AddOk.htm");
					player.sendPacket(SystemMessageId.THE_NEW_SUBCLASS_HAS_BEEN_ADDED); // Subclass added.
				}
				else
				{
					html.setFile(player, getSubClassFail());
				}
				break;
			}
			case 5: // Change Class - Action
			{
				/**
				 * If the character is less than level 75 on any of their previously chosen classes then disallow them to change to their most recently added sub-class choice. Note: paramOne = classIndex
				 */
				if (!player.getClient().getFloodProtectors().canChangeSubclass())
				{
					return;
				}
				
				if (player.getClassIndex() == paramOne)
				{
					html.setFile(player, "data/html/villagemaster/SubClass_Current.htm");
					break;
				}
				
				if (paramOne == 0)
				{
					if (!checkVillageMaster(player.getBaseClass()))
					{
						return;
					}
				}
				else
				{
					try
					{
						if (!checkVillageMaster(player.getSubClasses().get(paramOne).getClassDefinition()))
						{
							return;
						}
					}
					catch (NullPointerException e)
					{
						return;
					}
				}
				
				player.setActiveClass(paramOne);
				player.sendPacket(SystemMessageId.YOU_HAVE_SUCCESSFULLY_SWITCHED_TO_YOUR_SUBCLASS); // Transfer completed.
				return;
			}
			case 6: // Change/Cancel Subclass - Choice
			{
				// validity check
				if ((paramOne < 1) || (paramOne > Config.MAX_SUBCLASS))
				{
					return;
				}
				
				subsAvailable = getAvailableSubClasses(player);
				// another validity check
				if ((subsAvailable == null) || subsAvailable.isEmpty())
				{
					// TODO: Retail message
					player.sendMessage("There are no sub classes available at this time.");
					return;
				}
				
				final StringBuilder content6 = new StringBuilder(200);
				for (ClassId subClass : subsAvailable)
				{
					content6.append("<a action=\"bypass -h npc_%objectId%_Subclass 7 " + paramOne + " " + subClass.getId() + "\" msg=\"1445;\">" + ClassListData.getInstance().getClass(subClass.getId()).getClientCode() + "</a><br>");
				}
				
				switch (paramOne)
				{
					case 1:
					{
						html.setFile(player, "data/html/villagemaster/SubClass_ModifyChoice1.htm");
						break;
					}
					case 2:
					{
						html.setFile(player, "data/html/villagemaster/SubClass_ModifyChoice2.htm");
						break;
					}
					case 3:
					{
						html.setFile(player, "data/html/villagemaster/SubClass_ModifyChoice3.htm");
						break;
					}
					default:
					{
						html.setFile(player, "data/html/villagemaster/SubClass_ModifyChoice.htm");
					}
				}
				html.replace("%list%", content6.toString());
				break;
			}
			case 7: // Change Subclass - Action
			{
				/**
				 * Warning: the information about this subclass will be removed from the subclass list even if false!
				 */
				if (!player.getClient().getFloodProtectors().canChangeSubclass())
				{
					return;
				}
				
				if (!isValidNewSubClass(player, paramTwo))
				{
					return;
				}
				
				if (player.modifySubClass(paramOne, paramTwo))
				{
					player.abortCast();
					player.stopAllEffectsExceptThoseThatLastThroughDeath(); // all effects from old subclass stopped!
					player.stopAllEffectsNotStayOnSubclassChange();
					player.stopCubics();
					player.setActiveClass(paramOne);
					
					html.setFile(player, "data/html/villagemaster/SubClass_ModifyOk.htm");
					html.replace("%name%", ClassListData.getInstance().getClass(paramTwo).getClientCode());
					player.sendPacket(SystemMessageId.THE_NEW_SUBCLASS_HAS_BEEN_ADDED); // Subclass added.
				}
				else
				{
					/**
					 * This isn't good! modifySubClass() removed subclass from memory we must update _classIndex! Else IndexOutOfBoundsException can turn up some place down the line along with other seemingly unrelated problems.
					 */
					player.setActiveClass(0); // Also updates _classIndex plus switching _classid to baseclass.
					player.sendMessage("The sub class could not be added, you have been reverted to your base class.");
					return;
				}
				break;
			}
		}
		html.replace("%objectId%", String.valueOf(villageMasterObjectID));
		player.sendPacket(html);
	}

	public static boolean checkVillageMasterRace(ClassId pClass)
	{
		return true;
	}
	
	public static boolean checkVillageMasterTeachType(ClassId pClass)
	{
		return true;
	}
	
	/**
	 * Returns true if this classId allowed for master
	 * @param classId
	 * @return
	 */
	public static boolean checkVillageMaster(int classId)
	{
		return checkVillageMaster(ClassId.getClassId(classId));
	}
	
	/**
	 * Returns true if this PlayerClass is allowed for master
	 * @param pclass
	 * @return
	 */
	public static boolean checkVillageMaster(ClassId pclass)
	{
		if (Config.ALT_GAME_SUBCLASS_EVERYWHERE)
		{
			return true;
		}
		return checkVillageMasterRace(pclass) && checkVillageMasterTeachType(pclass);
	}

	public static String getSubClassMenu(Race race)
	{
		if (Config.ALT_GAME_SUBCLASS_EVERYWHERE)
		{
			return "data/html/villagemaster/SubClass.htm";
		}
		return "data/html/villagemaster/SubClass_NoOther.htm";
	}

	public static String getSubClassFail()
	{
		return "data/html/villagemaster/SubClass_Fail.htm";
	}

	public static boolean checkQuests(Player player)
	{
		// Noble players can add Sub-Classes without quests
		if (player.isNoble())
		{
			return true;
		}
		
		QuestState qs = player.getQuestState("Q00234_FatesWhisper");
		if ((qs == null) || !qs.isCompleted())
		{
			return false;
		}
		
		qs = player.getQuestState("Q00235_MimirsElixir");
		if ((qs == null) || !qs.isCompleted())
		{
			return false;
		}
		
		return true;
	}	
}