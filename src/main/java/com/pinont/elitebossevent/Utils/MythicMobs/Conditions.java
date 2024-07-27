package com.pinont.elitebossevent.Utils.MythicMobs;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;

public class Conditions implements IEntityCondition {

    public Conditions(MythicLineConfig config) {

    }

    @Override
    public boolean check(AbstractEntity target) {
        return target.isPlayer();
    }
}