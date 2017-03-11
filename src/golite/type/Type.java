package golite.type;

import golite.node.*;

public enum Type
{
    INT, 
    FLOAT64, 
    BOOL,
    RUNE,
    STRING, 
    STRUCT,
    INVALID;

    public static Type stringToType(String s)
    {
        switch (s)
        {
            case "int":
                return Type.INT;
            case "float64":
                return Type.FLOAT64;
            case "bool":
                return Type.BOOL;
            case "rune":
                return Type.RUNE;
            case "string":
                return Type.STRING;
            default:
                return Type.INVALID;
        }
    } 
}