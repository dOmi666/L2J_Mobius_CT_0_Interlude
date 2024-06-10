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
package ai.others.NewbieHelper;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

import ai.AbstractNpcAI;
import quests.Q00255_Tutorial.Q00255_Tutorial;

/**
 * @author Mobius
 */
public class NewbieHelper extends AbstractNpcAI
{
	private static final int SOULSHOT_NOVICE = 5789;
	private static final int SPIRITSHOT_NOVICE = 5790;
	private static final int TOKEN = 8542;
	private static final int SCROLL = 8594;
	private static final int SCROLL_REWARD_CHANCE = 100; // 0 to disable.
	
	public NewbieHelper()
	{
		addStartNpc(30598, 30599, 30600, 30601, 30602);
		addTalkId(30598, 30599, 30600, 30601, 30602);
		addFirstTalkId(30598, 30599, 30600, 30601, 30602);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (!Config.DISABLE_TUTORIAL)
		{
			final QuestState qs1 = getQuestState(player, true);
			if (!qs1.isCompleted() && (player.getLevel() < 18))
			{
				final QuestState qs2 = player.getQuestState(Q00255_Tutorial.class.getSimpleName());
				if (((qs2 != null) && (qs2.getInt("Ex") == 4)))
				{
					final boolean isMage = player.isMageClass();
					final boolean isOrcMage = player.getClassId().getId() == 49;
					qs1.playTutorialVoice(isMage && !isOrcMage ? "tutorial_voice_027" : "tutorial_voice_026");
					giveItems(player, isMage && !isOrcMage ? SPIRITSHOT_NOVICE : SOULSHOT_NOVICE, isMage && !isOrcMage ? 100 : 200);
					giveItems(player, TOKEN, 12);
					if (getRandom(100) < SCROLL_REWARD_CHANCE) // Old C6 had this at 50%.
					{
						giveItems(player, SCROLL, 2);
					}
					qs1.setState(State.COMPLETED);
				}
			}
		}
		npc.showChatWindow(player);
		return null;
	}
	
	public static void main(String[] args)
	{
		new NewbieHelper();
	}
}