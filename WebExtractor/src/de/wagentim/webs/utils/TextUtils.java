package de.wagentim.webs.utils;

import java.awt.TexturePaint;

public final class TextUtils {
	
	public static final char REPLACE_SYMBOL = '%';

	public static boolean isEmpty(final CharSequence s) {
        if (s == null) {
            return true;
        }
        return s.length() == 0;
    }

    public static boolean isBlank(final CharSequence s) {
        if (s == null) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Replace place holder with the actual text. The place holder is marked with "%" symbol plus integer number
     * 
     * @param input
     * @param values
     * @return
     */
    public static String textReplace( final String input, final String...values )
    {
    	CharSequence original = Args.notEmpty(Args.notNull(input, "TextUtils#textReplace: arg input is null"), "TextUtils#textReplace: arg input is empty");
    	
    	StringBuffer result = new StringBuffer();
    	StringBuffer number = new StringBuffer();
    	
    	boolean start = false;
    	boolean finish = false;
		
		for( int i = 0; i < original.length(); i++ )
		{
			char c = input.charAt(i);
			
			if( c == REPLACE_SYMBOL )
			{
				
				if( !start )
				{
					start = true;
					number.delete(0, number.length());
					continue;
				}
			}
			
			if( start )
			{
				if( Character.isDigit(c) )
				{
					number.append(c);
					
					if( i == ( original.length() - 1 ) )
					{
						finish = true;
						c = REPLACE_SYMBOL;
					}
					
				}else
				{
					finish = true;
				}
				
				if( finish )
				{
					String text = "";
					
					int index = Integer.parseInt(number.toString());
					
					if( index <= values.length && index > 0 )
					{
						text = values[index - 1];
						
						if( null == text )
						{
							text = "";
						}
					}
					
					result.append(text);
					start = false;
					finish = false;

					if( c == REPLACE_SYMBOL )
					{
						start = true;
						number.delete(0, number.length());
					}else
					{
						result.append(c);
					}
				}
				
			}else
			{
				result.append(c);
			}
		}
		
		return result.toString();
    }
    
    public static void main(String[] args)
    {
    	System.out.println(TextUtils.textReplace("Bin12 %12 %2", "the", "hero"));
    }
}
