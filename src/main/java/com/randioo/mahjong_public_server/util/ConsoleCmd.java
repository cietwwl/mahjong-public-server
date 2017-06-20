package com.randioo.mahjong_public_server.util;

import java.util.Scanner;

public class ConsoleCmd {
	private Scanner in = null;

	public ConsoleCmd() {
		in = new Scanner(System.in);
	}

	public int getInt() {
		String data = in.nextLine();

		int value = Integer.parseInt(data);
		return value;
	}

	public boolean getBoolean() {
		String data = in.nextLine();

		boolean value = Boolean.parseBoolean(data);
		return value;
	}

	public String getString() {
		return in.nextLine();
	}
}
