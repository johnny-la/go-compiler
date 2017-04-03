package golite.type;

import golite.node.*;

public class Dimension
{
    public boolean isArray;
    public int size;

    public Dimension(boolean isArray, int size) {
    	this.isArray = isArray;
    	this.size = size;
    }

    public String toString() {
    	String output = "";
    	output += "[";
    	if (isArray) {
    		output += size;
    	}
    	output += "]";
    	return output;
    }


}