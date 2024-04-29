/**
 * @file Hash.java
 * @summary Contains the Hash class. This class is used to hash a string into a m bit integer.
 * 
 * @author Jamison Grudem (grude013)
 * @grace_days Using 2 grace days
 */
package src;

/**
 * @class Hash
 * @summary Contains a static method to hash a string into a m bit integer.
 */
public class Hash {

    private static final int FNV_32_INIT = 0x811c9dc5;   // 2166136261
    private static final int FNV_32_PRIME = 0x01000193;  // 16777619;

    /**
     * Hash a string into a m bit integer.
     * @param Key The string to hash
     * @return
     */
    public static int hash32(String Key) {   // FNV-1a Hash
        byte[] kBytes = Key.getBytes();
        int hash = FNV_32_INIT;
        final int len = kBytes.length;
        for(int i = 0; i < len; i++) {
            hash ^= kBytes[i];
            hash *= FNV_32_PRIME;
        }

        if (hash < 0) {  
           if (hash == Integer.MIN_VALUE) {
               hash = Integer.MAX_VALUE;
           }
           else hash = Math.abs(hash);  
        }
        return hash;
   }
}

