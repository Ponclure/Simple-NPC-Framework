/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.github.ponclure.simplenpcframework.internal;

public enum MinecraftVersion {
	
    V1_14_R1,
    V1_15_R1,
    v1_16_R3;

    public boolean isAboveOrEqual(MinecraftVersion compare) {
        return ordinal() >= compare.ordinal();
    }
}
