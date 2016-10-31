/*/**
 * Created by Kadri on 24.10.2016.
 */
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;

public class Main {
    static final BigInteger textAdd = new BigInteger("100000", 16);
    static final BigInteger keyAdd = new BigInteger("100000000", 16);
    static final int cycleToIndex = 32; //Starts from 0, so for the 193rd bit we cycle till the 192nd index
    static long encryptions = 0;

    public static void main(String[] args) throws Exception {
        BigInteger openText = new BigInteger("0");
        boolean found = false;
        do {
            found = cycleKey(openText);
            openText = openText.add(textAdd);
        } while(!openText.testBit(cycleToIndex) || !found);
    }

    private static boolean cycleKey(BigInteger openText) throws Exception {
        BigInteger key = new BigInteger("0280", 16);
        boolean found = false;
        do {
            found = encryptAes(key, openText);
            encryptions++;
            key = key.add(keyAdd);
        } while(!key.testBit(cycleToIndex) || !found);
        return found;
    }

    static boolean encryptAes(BigInteger key, BigInteger text) throws Exception {
        Cipher AesCipher = Cipher.getInstance("AES/CFB/NoPadding");

        byte[] byteText = String.format("%024d", text).getBytes();
        byte[] byteKey = String.format("%024d", key).getBytes();
        SecretKeySpec aesKey = new SecretKeySpec(byteKey, "AES");

        AesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] byteCipherText = AesCipher.doFinal(byteText);

        boolean found = checkByteArray(new BigInteger(byteCipherText).toString(2), byteCipherText);
        if(found){
            System.out.print("Text:");
            printByteArray(String.format("%024d", text).getBytes());
            System.out.print("Key :");
            printByteArray(String.format("%024d", key).getBytes());
        }
        return found;
    }

    static boolean checkByteArray(String byteArray, byte[] bytes){
        char[] chars = byteArray.toCharArray();
        for (int i = chars.length-20; i < chars.length; i++) {
            if(chars[i] != '0'){
                return false;
            }
        }
        System.out.print("Found solution!");
        System.out.println(" Encryption attempts: "+ String.valueOf(encryptions));
        printByteArray(bytes);
        return true;
    }

    static void printByteArray(byte[] byteArray){
        System.out.print("    ");
        for (byte b : byteArray) {
            System.out.print(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        System.out.println();
    }

    public static void printBinary(BigInteger integer){
        System.out.println(integer.toString(2));
    }
}