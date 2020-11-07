/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.github.simplenpcframework.internal;

public enum MinecraftVersion {
	
    V1_14_R1,
    V1_15_R1,
    V1_16_R1;

    public boolean isAboveOrEqual(MinecraftVersion compare) {
        return ordinal() >= compare.ordinal();
    }
}
