package net.trileg.sha1;

public class SHA1 {
  private int[] H = {0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476, 0xC3D2E1F0};


  public void outputByteArray(String text, byte[] input) {
    System.out.print(String.format(text + ": len=%d, ", input.length));
    for (byte i : input) {
      System.out.print(String.format("%02x ", i));
    }
    System.out.println();
  }

  private byte[] getLengthArray(long len) {
    // ビットシフトでいらないものを捨てつつ，先頭バイトから順にbyte配列に詰めていく
    byte[] tmp = new byte[8];
    tmp[0] = (byte) (len >> 56);
    tmp[1] = (byte) (len >> 48);
    tmp[2] = (byte) (len >> 40);
    tmp[3] = (byte) (len >> 32);
    tmp[4] = (byte) (len >> 24);
    tmp[5] = (byte) (len >> 16);
    tmp[6] = (byte) (len >> 8);
    tmp[7] = (byte) (len);
    return tmp;
  }

  public byte[] padding(byte[] message) {
    int original_byte_len = message.length;
    long original_bit_len_long = original_byte_len * 8;

    byte[] append_one = new byte[original_byte_len + 1];
    System.arraycopy(message, 0, append_one, 0, original_byte_len);
    append_one[append_one.length - 1] = (byte) 0x80;
    int append_one_bit_length = append_one.length * 8;

    while (append_one_bit_length % 512 != 448) {
      append_one_bit_length += 8;
    }

    byte[] append_zeros = new byte[append_one_bit_length / 8];
    System.arraycopy(append_one, 0, append_zeros, 0, append_one.length);

    for (int i = append_zeros.length - 1; i > append_one.length - 1; i--) {
      append_zeros[i] = (byte) 0x00;
    }

    byte[] lengthArray = getLengthArray(original_bit_len_long);
    byte[] padded = new byte[append_zeros.length + lengthArray.length];
    System.arraycopy(append_zeros, 0, padded, 0, append_zeros.length);
    System.arraycopy(lengthArray, 0, padded, append_zeros.length, lengthArray.length);

    return padded;
  }

  private int rotl(int x, int n) {
    return ((x << n) | (x >>> (32 - n)));
  }

  private String hexString(int input) {
    byte[] tmp = new byte[4];
    tmp[0] = (byte) (input >>> 24);
    tmp[1] = (byte) (input >>> 16);
    tmp[2] = (byte) (input >>> 8);
    tmp[3] = (byte) (input);
    return hexString(tmp);
  }

  private String hexString(byte[] input) {
    final String hexChar = "0123456789ABCDEF";

    StringBuilder stringBuilder = new StringBuilder();
    for (byte i : input) {
      stringBuilder.append(hexChar.charAt((i >> 4) & 0x0F));
      stringBuilder.append(hexChar.charAt(i & 0x0F));
    }

    return stringBuilder.toString();
  }

  public String getHash(byte[] message) {
    System.out.println("---   getHash   ---");
    outputByteArray("padded: ", message);


    for (int i = 0; i < message.length; i = i + 64) {
      int[] w = new int[80];

      for (int j = 0; j < 16; j++) {
        w[j] = ((message[i + (j * 4) + 0] & 0xFF) << 24) + ((message[i + (j * 4) + 1] & 0xFF) << 16) + ((message[i + (j * 4) + 2] & 0xFF) << 8) + ((message[i + (j * 4) + 3] & 0xFF));
      }

      for (int j = 16; j < 80; j++) {
        w[j] = rotl(w[j - 3] ^ w[j - 8] ^ w[j - 14] ^ w[j - 16], 1);
      }

      int a = H[0];
      int b = H[1];
      int c = H[2];
      int d = H[3];
      int e = H[4];

      for (int j = 0; j < 80; j++) {
        int K = 0, F = 0;
        if (0 <= j && j <= 19) {
          F = (b & c) ^ ((~ b) & d);
          K = 0x5A827999;
        } else if(20 <= j && j <= 39) {
          F = b ^ c ^ d;
          K = 0x6ED9EBA1;
        } else if(40 <= j && j <= 59) {
          F = (b & c) ^ (b & d) ^ (c & d);
          K = 0x8F1BBCDC;
        } else if(60 <= j && j <= 79) {
          F = b ^ c ^ d;
          K = 0xCA62C1D6;
        }

        int T = rotl(a, 5) + F + e + w[j] + K;
        e = d;
        d = c;
        c = rotl(b, 30);
        b = a;
        a = T;
      }

      H[0] = H[0] + a;
      H[1] = H[1] + b;
      H[2] = H[2] + c;
      H[3] = H[3] + d;
      H[4] = H[4] + e;
    }

    return "" + hexString(H[0]) + hexString(H[1]) + hexString(H[2]) + hexString(H[3]) + hexString(H[4]);
  }
}
