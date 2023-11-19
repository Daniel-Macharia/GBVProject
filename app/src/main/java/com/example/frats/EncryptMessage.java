package com.example.frats;

import java.util.ArrayList;

class EncryptMessage {

    private ArrayList<letter> letterMap = new ArrayList<>(10);

    private int pad = 12;
    private String cipherText = new String("");
    private String originalMessage = new String("");

    public EncryptMessage()
    {
        int count = 0;
        for( int u = 65, l = 97; count < 26; l++, u++, count++ )
        {
            letterMap.add( new letter( (char) u, (char) l, count));
        }
    }

    public String encrypt( String message )
    {
        cipherText = "";

        char[] charArray = message.toCharArray();

        for( char c : charArray )
        {
            if( containsLower(c) )
            {
                cipherText += getLowerCipher(c, pad);
            }
            else
            if( containsUpper( c ) )
            {
                cipherText += getUpperCipher(c, pad);
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

        originalMessage = "";

        char[] charArray = cipher.toCharArray();

        for( char c : charArray )
        {
            if( containsLower(c) )
            {
                originalMessage += getLowerOriginal(c, pad);
            }
            else
            if( containsUpper( c ) )
            {
                originalMessage += getUpperOriginal(c, pad);
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

