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

package com.dsh105.echopet.compat.nms.v1_9_R1.entity.type;

import com.dsh105.echopet.compat.api.entity.*;
import com.dsh105.echopet.compat.api.entity.type.nms.IEntityCreeperPet;
import com.dsh105.echopet.compat.nms.v1_9_R1.NMS;
import com.dsh105.echopet.compat.nms.v1_9_R1.entity.EntityPet;

import net.minecraft.server.v1_9_R1.DataWatcherObject;
import net.minecraft.server.v1_9_R1.World;

import org.bukkit.Sound;

@EntitySize(width = 0.6F, height = 1.9F)
@EntityPetType(petType = PetType.CREEPER)
public class   EntityCreeperPet extends EntityPet implements IEntityCreeperPet {

    public static final DataWatcherObject<Integer> CREEPER_STATE_METADATA = NMS.createMetadata(11, NMS.MetadataType.VAR_INT);
    public static final DataWatcherObject<Boolean> CREEPER_IS_POWERED_METADATA = NMS.createMetadata(12, NMS.MetadataType.BOOLEAN);
    public static final DataWatcherObject<Boolean> CREEPER_IS_IGNITED_METADATA = NMS.createMetadata(13, NMS.MetadataType.BOOLEAN);


    public EntityCreeperPet(World world) {
        super(world);
    }

    public EntityCreeperPet(World world, IPet pet) {
        super(world, pet);
    }

    @Override
    public void setPowered(boolean flag) {
        this.datawatcher.set(CREEPER_IS_POWERED_METADATA, flag);
    }

    @Override
    public void setIgnited(boolean flag) {
        this.datawatcher.set(CREEPER_IS_IGNITED_METADATA, flag);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(CREEPER_STATE_METADATA, (byte) -1);
        this.datawatcher.register(CREEPER_IS_POWERED_METADATA, false);
        this.datawatcher.register(CREEPER_IS_IGNITED_METADATA, false);
    }

    @Override
    protected Sound getIdleSound() {
        return null;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_CREEPER_DEATH;
    }

    @Override
    public SizeCategory getSizeCategory() {
        return SizeCategory.REGULAR;
    }
}
