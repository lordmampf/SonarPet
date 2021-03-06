/*
 * This file is part of EchoPet.
 *
 * EchoPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EchoPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EchoPet.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.echopet.compat.nms.v1_10_R1.entity.ai;

import com.dsh105.echopet.compat.api.ai.APetGoalFollowOwner;
import com.dsh105.echopet.compat.api.ai.PetGoalType;
import com.dsh105.echopet.compat.api.event.PetMoveEvent;
import com.dsh105.echopet.compat.api.plugin.EchoPet;
import com.dsh105.echopet.compat.nms.v1_10_R1.entity.EntityInsentientPet;
import com.dsh105.echopet.compat.nms.v1_10_R1.entity.type.EntityEnderDragonPet;
import com.dsh105.echopet.compat.nms.v1_10_R1.entity.type.EntityGhastPet;

import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.GenericAttributes;
import net.minecraft.server.v1_10_R1.Navigation;
import net.minecraft.server.v1_10_R1.PathEntity;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;


public class PetGoalFollowOwner extends APetGoalFollowOwner {

    private EntityInsentientPet pet;
    private Navigation nav;
    private int timer = 0;
    private double startDistance;
    private double stopDistance;
    private double teleportDistance;
    //private EntityPlayer owner;

    public PetGoalFollowOwner(EntityInsentientPet pet, double startDistance, double stopDistance, double teleportDistance) {
        this.pet = pet;
        this.nav = (Navigation) pet.getEntity().getNavigation();
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.teleportDistance = teleportDistance;
        //this.owner = ((CraftPlayer) pet.getPlayerOwner()).getHandle();
    }

    @Override
    public PetGoalType getType() {
        return PetGoalType.THREE;
    }

    @Override
    public String getDefaultKey() {
        return "FollowOwner";
    }

    @Override
    public boolean shouldStart() {
        if (!this.pet.getEntity().isAlive()) {
            return false;
        } else if (this.pet.getPlayerOwner() == null) {
            return false;
        } else if (this.pet.getPet().isOwnerRiding() || this.pet.getPet().isHat()) {
            return false;
        } else if (this.pet.getEntity().h(((CraftPlayer) this.pet.getPlayerOwner()).getHandle()) < this.startDistance) { // pet.distanceSquared(this.pet.getPlayerOwner()) < startDistance
            return false;
        } else if (this.pet instanceof EntityEnderDragonPet) {
            return false;
        } else {
            return !(this.pet.getEntity().getGoalTarget() != null && this.pet.getEntity().getGoalTarget().isAlive());
        }

    }

    @Override
    public boolean shouldContinue() {
        if (this.nav.f()) { // if is able enter doors
            return false;
        } else if (this.pet.getPlayerOwner() == null) {
            return false;
        } else if (this.pet.getPet().isOwnerRiding() || this.pet.getPet().isHat()) {
            return false;
        } else if (this.pet.getEntity().h(((CraftPlayer) this.pet.getPlayerOwner()).getHandle()) <= this.stopDistance) { // pet.distanceSquared(this.pet.getPlayerOwner()) < stopDistance
            return false;
        }
        //PetGoalMeleeAttack attackGoal = (PetGoalMeleeAttack) this.pet.petGoalSelector.getGoal("Attack");
        //return !(attackGoal != null && attackGoal.isActive);
        return true;
    }

    @Override
    public void start() {
        this.timer = 0;

        //Set pathfinding radius
        pet.getEntity().getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(this.teleportDistance);
    }

    @Override
    public void finish() {
        this.nav.n();
    }

    @Override
    public void tick() {
        //https://github.com/Bukkit/mc-dev/blob/master/net/minecraft/server/PathfinderGoalFollowOwner.java#L57
        EntityPlayer owner = ((CraftPlayer) this.pet.getPlayerOwner()).getHandle();
        /*
         * Make the pet look at the owner.
         * Changing up to 10 yaw in the pet's facing (max) per tick.
         * The speed to change the pitch at is requested from the entity.
         * Also done in PetGoalLookAtPlayer.tick()
         */
        this.pet.getEntity().getControllerLook().a(owner, 10.0F, (float) this.pet.getEntity().N());
        if (--this.timer <= 0) {
            this.timer = 10;
            if (this.pet.getPlayerOwner().isFlying()) {
                //Don't move pet when owner flying
                return;
            }

            double speed = 0.6F;
            if (this.pet.getEntity().h(owner) > (this.teleportDistance)  // distanceTo(owner) > teleportDistance
                    && ((CraftPlayer) this.pet.getPlayerOwner()).getHandle().onGround) {
                this.pet.getPet().teleportToOwner();
                return;
            }

            PetMoveEvent moveEvent = new PetMoveEvent(this.pet.getPet(), this.pet.getLocation(), this.pet.getPlayerOwner().getLocation());
            EchoPet.getPlugin().getServer().getPluginManager().callEvent(moveEvent);
            if (moveEvent.isCancelled()) {
                return;
            }

            if (pet.getEntity().getGoalTarget() == null) {
                PathEntity path;
                if (pet instanceof EntityGhastPet) {
                    path = pet.getEntity().getNavigation().a(pet.getPlayerOwner().getLocation().getBlockX(), pet.getPlayerOwner().getLocation().getBlockY() + 5, pet.getPlayerOwner().getLocation().getBlockZ()); // getPathToLocation
                } else {
                    path = pet.getEntity().getNavigation().a(owner); // getPathTo
                }

                //Smooth path finding to entity instead of location
                pet.getEntity().getNavigation().a(path, speed); // Set a new path for the entity to follow
            }
        }
    }
}
