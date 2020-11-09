package com.github.ponclure.simplenpcframework.internal;

public enum MinecraftVersion {

    V1_8_R2(0),
    V1_8_R3(1),
    V1_9_R1(2),
    V1_9_R2(3),
    V1_10_R1(4),
    V1_11_R1(5),
    V1_12_R1(6),
    V1_13_R1(7),
    V1_13_R2(8),
    V1_14_R1(9),
    V1_15_R1(10),
    v1_16_R3(11);
	
	private final int ordinal;
	private MinecraftVersion(int order) {
		this.ordinal = order;
	}
	
	public int getOrdinal() {
		return ordinal;
	}

    public boolean isAboveOrEqual(MinecraftVersion other) {
        return getOrdinal() >= other.getOrdinal() && isSupported(this);
    }
    
    public boolean isSupported(MinecraftVersion version) {
    	return version.getOrdinal() >= 10;
    }
}
