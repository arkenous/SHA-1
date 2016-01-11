package net.trileg.sha1;

public class Main {

  public static void main(String[] args) {
    SHA1 sha1 = new SHA1();
    byte[] message = {(byte)0xFF, (byte)0xFF};
    String result = sha1.getHash(sha1.padding(message));
    System.out.println(result);
  }
}
