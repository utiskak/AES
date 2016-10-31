/*/**
 * Created by Kadri on 24.10.2016.
 */

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;

public class Main {
    static final BigInteger textAdd = new BigInteger("100000", 16);
    static final BigInteger keyAdd = new BigInteger("100000000", 16);
    static final int cycleToIndex = 192; //Starts from 0, so for the 193rd bit we cycle till the 192nd index
    static long encryptions = 0;

    public static void main(String[] args) throws Exception {
        BigInteger openText = new BigInteger("0");
        boolean found;
        do {
            found = cycleKey(openText);
            openText = openText.add(textAdd);
        } while (!openText.testBit(cycleToIndex) && !found);
    }

    private static boolean cycleKey(BigInteger openText) throws Exception {
        BigInteger key = new BigInteger("0280", 16);
        boolean found = false;
        do {
            found = encryptAes(key, openText);
            encryptions++;
            key = key.add(keyAdd);
        } while (!key.testBit(cycleToIndex) && !found);
        return found;
    }

    static boolean encryptAes(BigInteger key, BigInteger text) throws Exception {
        byte[] byteText = String.format("%024d", text).getBytes();
        byte[] byteKey = String.format("%024d", key).getBytes();
        SecretKeySpec aesKey = new SecretKeySpec(byteKey, "AES");

        Cipher aesCipher = Cipher.getInstance("AES/CFB/NoPadding");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);

        byte[] byteCipherText = aesCipher.doFinal(byteText);
        boolean found = checkByteArray(new BigInteger(byteCipherText).toString(2), byteCipherText);

        if (found) {
            System.out.print("   Avateks:       ");
            printBinary(text);
            System.out.print("      Võti:       ");
            printBinary(key);
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            System.out.print("Decrypted :");
            printByteArray(cipher.doFinal(byteCipherText));
        }
        return found;
    }

    static boolean checkByteArray(String byteArray, byte[] bytes) {
        char[] chars = byteArray.toCharArray();
        for (int i = chars.length - 20; i < chars.length; i++) {
            if (chars[i] != '0') {
                return false;
            }
        }
        System.out.print("Lahendus leitud!");
        System.out.println(" Krüpteerimis katseid kokku: " + String.valueOf(encryptions));
        System.out.print("Krüptogramm: ");
        printByteArray(bytes);
        return true;
    }

    static void printByteArray(byte[] byteArray) {
        System.out.print("     ");
        for (byte b : byteArray) {
            System.out.print(Integer.toBinaryString(b & 255 | 256).substring(1));
        }
        System.out.println();
    }

    public static void printBinary(BigInteger integer) {
        System.out.println(String.format("%1$" + 192 + "s", integer.toString(2)).replace(' ', '0'));
    }
}