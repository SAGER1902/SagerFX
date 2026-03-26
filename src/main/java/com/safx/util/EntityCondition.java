package safx.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import wmlib.common.living.EntityWMVehicleBase;
/**
 * Used for Sounds and ParticleSystems, which will stop when the condition is not met.
 */
public enum EntityCondition {
	NONE(0),
	CHARGING_WEAPON(1),
	ENTITY_ALIVE(2),
	ENTITY_PLANE(3);
	
	public byte id = 0;
	private EntityCondition(int id) {
		this.id = (byte)id;
	}
	
	public static EntityCondition fromByte(byte id) {
		for (EntityCondition e : EntityCondition.values()) {
			if (e.id == id) return e;
		}
		return NONE;
	}
	
	public boolean evaluate(Entity entity) {
		switch (this) {
		case CHARGING_WEAPON:
			/*if (entity instanceof Player) {
				SAExtendedPlayer txp = SAExtendedPlayer.get((Player)entity);
				return txp.isChargingWeapon();
			}*/
			return false;
		case ENTITY_ALIVE:
			return entity.isAlive();
		case ENTITY_PLANE:
			//return entity.isAlive();
			if(ModList.get().isLoaded("wmlib")){
				if(entity instanceof EntityWMVehicleBase){
					EntityWMVehicleBase vehicle = (EntityWMVehicleBase)entity;
					if(vehicle.onGround()||vehicle.getMovePitch()>1F||
					vehicle.getMovePitch()<0F&&vehicle.getMovePitch()>-0.001F||vehicle.movePower<6F){
						return false;
					}else{
						return entity.isAlive();
					}
				}else{
					return false;
				}
			}else{
				return false;
			}
		case NONE:
		default: //If no condition is set, never stop a sound/particle.
			return true;
		}
	}
}
