package com.dh.kakaopay.packet;

public class CardPacket {

	public static final int PACKET_LENGTH = 450;

	private StringBuffer outPacketString;
	private String intPacketString;
	private int offset;

	public CardPacket() {
		outPacketString = new StringBuffer();
		intPacketString = "";
		offset = 0;
	}
	
	public void addString(String value, int length) {
		outPacketString.append(makeStringR(value, length));
	}
	
	public void addNumber(int value, int length, String type) {
		
		switch (type) {
		case "O":
			outPacketString.append(makeNumber(String.valueOf(value), length));
			break;
			
		case "L":
			outPacketString.append(makeStringR(String.valueOf(value), length));
			break;

		default:
			outPacketString.append(makeStringL(String.valueOf(value), length));
			break;
		}
		
	}
	
	public void addNumber(long value, int length, String type) {
		
		switch (type) {
		case "O":
			outPacketString.append(makeNumber(String.valueOf(value), length));
			break;
			
		case "L":
			outPacketString.append(makeStringR(String.valueOf(value), length));
			break;

		default:
			outPacketString.append(makeStringL(String.valueOf(value), length));
			break;
		}
		
	}

	public String outPacket() {
		return outPacketString.toString();
	}

	private String makeStringL(String value, int length) {
		if (value.length() < length) {
			char[] temp = new char[length];
			int i = 0;
			int j = 0;

			while (i < length - value.length()) {
				temp[i] = ' ';
				i++;
			}

			while (i < length) {
				temp[i] = value.charAt(j);
				i++;
				j++;
			}

			value = new String(temp);
		}

		return value;
	}
	
	private String makeStringR(String value, int length) {
		if (value.length() < length) {
			char[] temp = new char[length];
			int i = 0;

			while (i < value.length()) {
				temp[i] = value.charAt(i);
				i++;
			}

			while (i < length) {
				temp[i] = ' ';
				i++;
			}

			value = new String(temp);
		}

		return value;
	}
	
	private String makeNumber(String value, int length) {
		if (value.length() < length) {
			char[] temp = new char[length];
			int i = 0;
			int j = 0;

			while (i < length - value.length()) {
				temp[i] = '0';
				i++;
			}

			while (i < length) {
				temp[i] = value.charAt(j);
				i++;
				j++;
			}

			value = new String(temp);
		}

		return value;
	}

	public String getIntPacketString() {
		return intPacketString;
	}

	public void setIntPacketString(String intPacketString) {
		this.intPacketString = intPacketString;
	}

	public String getString(int i) {
		String value = intPacketString.substring(offset, offset+i).trim();
		offset = offset + i;
		return value;
	}

}
