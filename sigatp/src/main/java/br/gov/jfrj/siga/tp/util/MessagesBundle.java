package br.gov.jfrj.siga.tp.util;

import br.com.caelum.vraptor.core.Localization;

public class MessagesBundle {
	private static final ThreadLocal<Localization> THREAD_LOCAL = new ThreadLocal<Localization>();

	public static void set(Localization localization) {
		THREAD_LOCAL.set(localization);
	}

	public static String getMessage(String key, String... args) {
		return THREAD_LOCAL.get().getMessage(key, args);
	}

	public static void remove() {
		THREAD_LOCAL.remove();
	}
}