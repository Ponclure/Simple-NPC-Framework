/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.github.ponclure.simplenpcframework.api.utilities;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public class Logger {

	private final String prefix;

	private boolean enabled = true;

	public Logger(final String prefix) {
		this.prefix = prefix + " ";
	}

	public void disable() {
		this.enabled = false;
	}

	public void info(final String info) {
		log(Level.INFO, info);
	}

	public void warning(final String warning) {
		log(Level.WARNING, warning);
	}

	public void warning(final String warning, final Throwable throwable) {
		log(Level.WARNING, warning, throwable);
	}

	public void severe(final String severe) {
		log(Level.SEVERE, severe);
	}

	public void severe(final String severe, final Throwable throwable) {
		log(Level.SEVERE, severe, throwable);
	}

	public void log(final Level level, final String message) {
		if (enabled) {
			Bukkit.getLogger().log(level, prefix + message);
		}
	}

	public void log(final Level level, final String message, final Throwable throwable) {
		if (enabled) {
			Bukkit.getLogger().log(level, prefix + message, throwable);
		}
	}
}
