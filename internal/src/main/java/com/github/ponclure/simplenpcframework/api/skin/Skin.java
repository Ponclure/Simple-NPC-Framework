package com.github.ponclure.simplenpcframework.api.skin;

public class Skin {

	private final String value;
	private final String signature;

	public Skin(final String value, final String signature) {
		this.value = value;
		this.signature = signature;
	}

	public String getValue() {
		return value;
	}

	public String getSignature() {
		return signature;
	}
}
