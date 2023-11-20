package com.example.frats;

import java.util.ArrayList;

class EncryptMessage {

    private ArrayList<letter> letterMap = new ArrayList<>(10);
    private ArrayList padVals = new ArrayList<>(10);

    private int pointer;
    private String cipherText = new String("");
    private String originalMessage = new String("");

    public EncryptMessage()
    {
        int count = 0;
        for( int u = 65, l = 97; count < 26; l++, u++, count++ )
        {
            letterMap.add( new letter( (char) u, (char) l, count));
        }

        padVals = getPad();
        pointer = 0;
    }

    public String encrypt( String message )
    {
        pointer = 0;
        cipherText = "";

        char[] charArray = message.toCharArray();

        for( char c : charArray )
        {
            if( containsLower(c) )
            {
                cipherText += getLowerCipher(c, getPadValue());
            }
            else
            if( containsUpper( c ) )
            {
                cipherText += getUpperCipher(c, getPadValue());
            }
            else
            {
                cipherText += new String("" + c);
            }
        }

        return cipherText;
    }

    public String decrypt( String cipher)
    {
        pointer = 0;
        originalMessage = "";

        char[] charArray = cipher.toCharArray();

        for( char c : charArray )
        {
            if( containsLower(c) )
            {
                originalMessage += getLowerOriginal(c, getPadValue());
            }
            else
            if( containsUpper( c ) )
            {
                originalMessage += getUpperOriginal(c, getPadValue());
            }
            else
            {
                originalMessage += new String("" + c);
            }
        }

        return originalMessage;
    }

    private String getLowerCipher(char original, int pad)
    {
        int c = ( pad + (int)original);

        if(  c > 122)
            c = (c % 122) + 96;

        String s = new String("" + (char) c);

        return s;
    }

    private String getUpperCipher( char original, int pad )
    {
        int c = ( pad + (int)original);

        if(  c > 90)
            c = (c % 90) + 64;

        String s = new String("" + (char)c);


        return s;
    }

    private String getLowerOriginal(char original, int pad)
    {
        int c = ((int)original - pad);

        if(  c < 97)
            c = 123 - (97 - c);

        String s = new String("" + (char)c);

        return s;
    }

    private String getUpperOriginal( char original, int pad )
    {
        int c = ((int)original - pad);

        if(  c < 65)
            c = 91 - (65 - c);

        String s = new String("" + (char)c);


        return s;
    }

    private boolean containsLower(char original )
    {
        for( letter l : letterMap)
        {
            if( l.lowerCase == original)
                return true;
        }

        return false;
    }

    private boolean containsUpper(char original)
    {
        for( letter l : letterMap )
        {
            if( l.upperCase == original)
                return true;
        }
        return false;
    }

    public ArrayList getPad()
    {
        ArrayList vals = new ArrayList(10);
        boolean isPrime;
        int count = 0;
        for( int num = 1; num <= 1000; num++ )
        {
            if( count == 101 )
                break;

            isPrime = true;
            for( int div = 1; div <= num; div++)
            {
                if( div != num && div != 1 && num % div == 0 )
                {
                    isPrime = false;
                }
            }

            if( isPrime )
            {
                vals.add(num % 26);
                count++;
            }
        }

        return vals;
    }

    private int getPadValue()
    {
        if( pointer == padVals.size() )
        {
            pointer = 0;
        }

        int value = (int) padVals.get(pointer);

        pointer++;
        return value;
    }

}

class letter
{
    public char upperCase;
    public char lowerCase;
    public int index;

    public letter( char upperCase, char lowerCase, int index )
    {
        this.upperCase = upperCase;
        this.lowerCase = lowerCase;
        this.index = index;
    }
}

