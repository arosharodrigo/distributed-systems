package byzantine;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * Created by user on 4/21/14.
 */
public class Util
{
    public static final String COMMAND_ATTACK = "ATTACK";
    public static final String COMMAND_RETREAT = "RETREAT";


    public static String  getOppositeCommand( String command )
    {
        if( command.equals( COMMAND_ATTACK ) )
        {
            return COMMAND_RETREAT;
        }
        else
        {
            return COMMAND_ATTACK;
        }
    }

    public static String getRandomCommand( )
    {
        Random random = new Random();
        int i = random.nextInt( 2 );

        if ( i == 0 )
        {
            return COMMAND_ATTACK;
        }
        else
        {
            return COMMAND_RETREAT;
        }
    }

    public static String getMaxOccurrenceCmd( Collection<String> values )
    {
        int attackCount = Collections.frequency( values, Util.COMMAND_ATTACK );
        int retreatCount = Collections.frequency( values, Util.COMMAND_RETREAT );

        if ( attackCount > retreatCount )
        {
            return COMMAND_ATTACK;
        }
        else
        {
           return COMMAND_RETREAT;
        }
    }
}
