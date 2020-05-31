package com.itactic.core.constants;

public class BootConstants {

	public static final String SESSION_INFO = "user";

	public interface AJAX_STATUS {
		public final static Integer success = 0;
		public final static Integer error = 1;
		public final static Integer nologin = -1;
		public final static Integer noauthor = -2;
		public final static Integer refresh = 2;
		public final static Integer repeat = 3;
	}

}
