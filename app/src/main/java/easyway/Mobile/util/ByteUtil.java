package easyway.Mobile.util;

public class ByteUtil {
	
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	public static byte[] byteSub(byte[] byte_1, int index, byte byte_2) {
		if (byte_1.length > index) {
			int i = index;
			for(; i < byte_1.length; i++) {
				if (byte_1[i] == byte_2)
					break;
			}
			byte[] byte_3 = new byte[i - index];
			System.arraycopy(byte_1, index, byte_3, 0, byte_3.length);
			return byte_3;
		} else {
			return new byte[0];
		}
	}
	
	public static byte[] byteSub(byte[] byte_1, int index) {
		if (byte_1.length > index) {
			byte[] byte_2 = new byte[byte_1.length - index];
			System.arraycopy(byte_1, index, byte_2, 0, byte_2.length);
			return byte_2;
		} else {
			return new byte[0];
		}
	}

	public static byte[] byteSub(byte[] byte_1, int start, int length) {
		if (byte_1.length > start) {
			if (byte_1.length <= start + length) {
				byte[] byte_2 = new byte[byte_1.length - start];
				System.arraycopy(byte_1, start, byte_2, 0, byte_2.length);

				return byte_2;
			} else {
				byte[] byte_2 = new byte[length];
				System.arraycopy(byte_1, start, byte_2, 0, byte_2.length);

				return byte_2;
			}

		} else {
			return new byte[0];
		}
	}
}
