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
package ai.areas.ForgeOfTheGods;

import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;

import ai.AbstractNpcAI;

/**
 * @author Mobius
 */
public class MagmaDrake extends AbstractNpcAI
{
	// NPCs
	private static final int MAGMA_DRAKE = 21657;
	private static final int MAGMA_DRAKE_MINION = 21393;
	
	private MagmaDrake()
	{
		addKillId(MAGMA_DRAKE);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Creature attacker = isSummon ? killer.getSummon() : killer;
		for (int i = 0; i < 5; i++)
		{
			final Attackable newNpc = (Attackable) addSpawn(MAGMA_DRAKE_MINION, npc.getX(), npc.getY(), npc.getZ() + 20, npc.getHeading(), false, 0, true);
			newNpc.setRunning();
			newNpc.addDamageHate(attacker, 0, 500);
			newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new MagmaDrake();
	}
}