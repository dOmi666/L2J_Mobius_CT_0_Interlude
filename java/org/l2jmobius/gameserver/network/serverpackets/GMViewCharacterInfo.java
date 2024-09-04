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
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.itemcontainer.Inventory;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

public class GMViewCharacterInfo extends ServerPacket
{
	private final Player _player;
	private final int _runSpd;
	private final int _walkSpd;
	private final int _swimRunSpd;
	private final int _swimWalkSpd;
	private final int _flyRunSpd;
	private final int _flyWalkSpd;
	private final double _moveMultiplier;
	
	public GMViewCharacterInfo(Player player)
	{
		_player = player;
		_moveMultiplier = player.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(player.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(player.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(player.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(player.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = player.isFlying() ? _runSpd : 0;
		_flyWalkSpd = player.isFlying() ? _walkSpd : 0;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.GM_VIEW_CHARACTER_INFO.writeId(this, buffer);
		buffer.writeInt(_player.getX());
		buffer.writeInt(_player.getY());
		buffer.writeInt(_player.getZ());
		buffer.writeInt(_player.getHeading());
		buffer.writeInt(_player.getObjectId());
		buffer.writeString(_player.getName());
		buffer.writeInt(_player.getRace().ordinal());
		buffer.writeInt(_player.getAppearance().isFemale());
		buffer.writeInt(_player.getClassId().getId());
		buffer.writeInt(_player.getLevel());
		buffer.writeLong(_player.getExp());
		buffer.writeInt(_player.getSTR());
		buffer.writeInt(_player.getDEX());
		buffer.writeInt(_player.getCON());
		buffer.writeInt(_player.getINT());
		buffer.writeInt(_player.getWIT());
		buffer.writeInt(_player.getMEN());
		buffer.writeInt(_player.getMaxHp());
		buffer.writeInt((int) _player.getCurrentHp());
		buffer.writeInt(_player.getMaxMp());
		buffer.writeInt((int) _player.getCurrentMp());
		buffer.writeInt((int) _player.getSp());
		buffer.writeInt(_player.getCurrentLoad());
		buffer.writeInt(_player.getMaxLoad());
		buffer.writeInt(_player.getPkKills());
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_UNDER));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_REAR));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEAR));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_NECK));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RFINGER));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LFINGER));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HEAD));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RHAND));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_GLOVES));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_CHEST));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEGS));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_FEET));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_CLOAK));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RHAND));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HAIR));
		buffer.writeInt(_player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_FACE));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_UNDER));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_REAR));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEAR));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_NECK));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RFINGER));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LFINGER));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HEAD));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LHAND));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_GLOVES));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_CHEST));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEGS));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_FEET));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_CLOAK));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
		buffer.writeInt(_player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_FACE));
		// c6 new h's
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		// end of c6 new h's
		buffer.writeInt((int) _player.getPAtk(null));
		buffer.writeInt((int) _player.getPAtkSpd());
		buffer.writeInt((int) _player.getPDef(null));
		buffer.writeInt(_player.getEvasionRate(null));
		buffer.writeInt(_player.getAccuracy());
		buffer.writeInt(_player.getCriticalHit(null, null));
		buffer.writeInt((int) _player.getMAtk(null, null));
		buffer.writeInt(_player.getMAtkSpd());
		buffer.writeInt((int) _player.getPAtkSpd());
		buffer.writeInt((int) _player.getMDef(null, null));
		buffer.writeInt(_player.getPvpFlag()); // 0-non-pvp 1-pvp = violett name
		buffer.writeInt(_player.getKarma());
		buffer.writeInt(_runSpd);
		buffer.writeInt(_walkSpd);
		buffer.writeInt(_swimRunSpd);
		buffer.writeInt(_swimWalkSpd);
		buffer.writeInt(_flyRunSpd);
		buffer.writeInt(_flyWalkSpd);
		buffer.writeInt(_flyRunSpd);
		buffer.writeInt(_flyWalkSpd);
		buffer.writeDouble(_moveMultiplier);
		buffer.writeDouble(_player.getAttackSpeedMultiplier()); // 2.9); //
		buffer.writeDouble(_player.getCollisionRadius()); // scale
		buffer.writeDouble(_player.getCollisionHeight()); // y offset ??!? fem dwarf 4033
		buffer.writeInt(_player.getAppearance().getHairStyle());
		buffer.writeInt(_player.getAppearance().getHairColor());
		buffer.writeInt(_player.getAppearance().getFace());
		buffer.writeInt(_player.isGM()); // builder level
		buffer.writeString(_player.getTitle());
		buffer.writeInt(_player.getClanId()); // pledge id
		buffer.writeInt(_player.getClanCrestId()); // pledge crest id
		buffer.writeInt(_player.getAllyId()); // ally id
		buffer.writeByte(_player.getMountType().ordinal()); // mount type
		buffer.writeByte(_player.getPrivateStoreType().getId());
		buffer.writeByte(_player.hasDwarvenCraft());
		buffer.writeInt(_player.getPkKills());
		buffer.writeInt(_player.getPvpKills());
		buffer.writeShort(_player.getRecomLeft());
		buffer.writeShort(_player.getRecomHave()); // Blue value for name (0 = white, 255 = pure blue)
		buffer.writeInt(_player.getClassId().getId());
		buffer.writeInt(0); // special effects? circles around player...
		buffer.writeInt(_player.getMaxCp());
		buffer.writeInt((int) _player.getCurrentCp());
		buffer.writeByte(_player.isRunning()); // changes the Speed display on Status Window
		buffer.writeByte(321);
		buffer.writeInt(_player.getPledgeClass()); // changes the text above CP on Status Window
		buffer.writeByte(_player.isNoble());
		buffer.writeByte(_player.isHero());
		buffer.writeInt(_player.getAppearance().getNameColor());
		buffer.writeInt(_player.getAppearance().getTitleColor());
	}
}
