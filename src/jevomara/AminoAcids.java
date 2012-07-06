package jevomara;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.Arrays;
import java.util.List;

/**
 *
 * @author andreas
 */
public class AminoAcids {

    private static final List<String> aminoAcidAlphabet = Arrays.asList(new String[] {"I", "F", "V", "L", "W", "M", "A", "G", "C", "Y", "P", "T", "S", "H", "Q", "N", "E", "D", "K", "R"});

    public static boolean[] toBoolean(int[] intArray) {
        boolean[] bolArray = new boolean[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            if (intArray[i] > 0) {
                bolArray[i] = true;
            } else {
                bolArray[i] = false;
            }
        }
        return bolArray;
    }

    public static boolean[] singleLetterCodetoBinary(String singleLetterSeq) {
        int[] bitArray = new int[singleLetterSeq.length() * 5];
        for (int i = 0; i < singleLetterSeq.length(); i++) {
            switch (singleLetterSeq.charAt(i)) {
                // Sorted by hydrophobicity index
                // Eisenberg et al. (1984) Journal of Molecular Biology, 179, No. 1, 125-142.
                case 'I':
                    System.arraycopy(new int[] {0,0,0,0,0}, 0, bitArray, i * 5, 5);
                    break;
                case 'F':
                    System.arraycopy(new int[] {0,0,0,0,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'V':
                    System.arraycopy(new int[] {0,0,0,1,0}, 0, bitArray, i * 5, 5);
                    //System.arraycopy(new int[] {0,0,0,1,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'L':
                    System.arraycopy(new int[] {0,0,1,0,0}, 0, bitArray, i * 5, 5);
                    //System.arraycopy(new int[] {0,0,1,0,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'W':
                    System.arraycopy(new int[] {0,0,1,1,0}, 0, bitArray, i * 5, 5);
                    break;
                case 'M':
                    System.arraycopy(new int[] {0,0,1,1,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'A':
                    System.arraycopy(new int[] {0,1,0,0,0}, 0, bitArray, i * 5, 5);
                    //return toBoolean(new int[] {0,1,0,0,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'G':
                    System.arraycopy(new int[] {0,1,0,1,0}, 0, bitArray, i * 5, 5);
                    //return toBoolean(new int[] {0,1,0,1,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'C':
                    System.arraycopy(new int[] {0,1,1,0,0}, 0, bitArray, i * 5, 5);
                    break;
                case 'Y':
                    System.arraycopy(new int[] {0,1,1,0,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'P':
                    System.arraycopy(new int[] {0,1,1,1,0}, 0, bitArray, i * 5, 5);
                    //System.arraycopy(new int[] {0,1,1,1,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'T':
                    System.arraycopy(new int[] {1,0,0,0,0}, 0, bitArray, i * 5, 5);
                    //System.arraycopy(new int[] {1,0,0,0,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'S':
                    System.arraycopy(new int[] {1,0,0,1,0}, 0, bitArray, i * 5, 5);
                    //System.arraycopy(new int[] {1,0,0,1,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'H':
                    System.arraycopy(new int[] {1,0,1,0,0}, 0, bitArray, i * 5, 5);
                    break;
                case 'Q':
                    System.arraycopy(new int[] {1,0,1,0,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'N':
                    System.arraycopy(new int[] {1,0,1,1,0}, 0, bitArray, i * 5, 5);
                    //System.arraycopy(new int[] {1,0,1,1,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'E':
                    System.arraycopy(new int[] {1,1,0,0,0}, 0, bitArray, i * 5, 5);
                    //System.arraycopy(new int[] {1,1,0,0,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'D':
                    System.arraycopy(new int[] {1,1,0,1,0}, 0, bitArray, i * 5, 5);
                    //System.arraycopy(new int[] {1,1,0,1,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'K':
                    System.arraycopy(new int[] {1,1,1,0,0}, 0, bitArray, i * 5, 5);
                    //System.arraycopy(new int[] {1,1,1,0,1}, 0, bitArray, i * 5, 5);
                    break;
                case 'R':
                    System.arraycopy(new int[] {1,1,1,1,0}, 0, bitArray, i * 5, 5);
                    //System.arraycopy(new int[] {1,1,1,1,1}, 0, bitArray, i * 5, 5);
                    break;
            }
        }
        return AminoAcids.toBoolean(bitArray);
    }

    public static String binaryToSingleLetterCode(boolean[] bitArray) {
        StringBuilder seq = new StringBuilder();
        for (int i = 0; i < bitArray.length; i+=5) {
            boolean[] gene = new boolean[5];
            System.arraycopy(bitArray, i, gene, 0, 5);
            // Sorted by hydrophobicity index
            // Eisenberg et al. (1984) Journal of Molecular Biology, 179, No. 1, 125-142.
            // Frequent amino acids have two alleles. Frequency according to:
            // http://www.tiem.utk.edu/bioed/webmodules/aminoacid.htm
            if (Arrays.equals(toBoolean(new int[] {0,0,0,0,0}), gene))
                seq.append('I');
            else if (Arrays.equals(toBoolean(new int[] {0,0,0,0,1}), gene))
                seq.append('F');
            else if (Arrays.equals(toBoolean(new int[] {0,0,0,1,0}), gene) || Arrays.equals(toBoolean(new int[] {0,0,0,1,1}), gene))
                seq.append('V');
            else if (Arrays.equals(toBoolean(new int[] {0,0,1,0,0}), gene) || Arrays.equals(toBoolean(new int[] {0,0,1,0,1}), gene))
                seq.append('L');
            else if (Arrays.equals(toBoolean(new int[] {0,0,1,1,0}), gene))
                seq.append('W');
            else if (Arrays.equals(toBoolean(new int[] {0,0,1,1,1}), gene))
                seq.append('M');
            else if (Arrays.equals(toBoolean(new int[] {0,1,0,0,0}), gene) || Arrays.equals(toBoolean(new int[] {0,1,0,0,1}), gene))
                seq.append('A');
            else if (Arrays.equals(toBoolean(new int[] {0,1,0,1,0}), gene) || Arrays.equals(toBoolean(new int[] {0,1,0,1,1}), gene))
                seq.append('G');
            else if (Arrays.equals(toBoolean(new int[] {0,1,1,0,0}), gene))
                seq.append('C');
            else if (Arrays.equals(toBoolean(new int[] {0,1,1,0,1}), gene))
                seq.append('Y');
            else if (Arrays.equals(toBoolean(new int[] {0,1,1,1,0}), gene) || Arrays.equals(toBoolean(new int[] {0,1,1,1,1}), gene))
                seq.append('P');
            else if (Arrays.equals(toBoolean(new int[] {1,0,0,0,0}), gene) || Arrays.equals(toBoolean(new int[] {1,0,0,0,1}), gene))
                seq.append('T');
            else if (Arrays.equals(toBoolean(new int[] {1,0,0,1,0}), gene) || Arrays.equals(toBoolean(new int[] {1,0,0,1,1}), gene))
                seq.append('S');
            else if (Arrays.equals(toBoolean(new int[] {1,0,1,0,0}), gene))
                seq.append('H');
            else if (Arrays.equals(toBoolean(new int[] {1,0,1,0,1}), gene))
                seq.append('Q');
            else if (Arrays.equals(toBoolean(new int[] {1,0,1,1,0}), gene) || Arrays.equals(toBoolean(new int[] {1,0,1,1,1}), gene))
                seq.append('N');
            else if (Arrays.equals(toBoolean(new int[] {1,1,0,0,0}), gene) || Arrays.equals(toBoolean(new int[] {1,1,0,0,1}), gene))
                seq.append('E');
            else if (Arrays.equals(toBoolean(new int[] {1,1,0,1,0}), gene) || Arrays.equals(toBoolean(new int[] {1,1,0,1,1}), gene))
                seq.append('D');
            else if (Arrays.equals(toBoolean(new int[] {1,1,1,0,0}), gene) || Arrays.equals(toBoolean(new int[] {1,1,1,0,1}), gene))
                seq.append('K');
            else if (Arrays.equals(toBoolean(new int[] {1,1,1,1,0}), gene) || Arrays.equals(toBoolean(new int[] {1,1,1,1,1}), gene))
                seq.append('R');
        }
        return seq.toString();
    }

    public static List<String> getAminoAcidAlphabet() {
        return aminoAcidAlphabet;
    }

}
