package com.pkc.util;

public class PkcConstants {
	public static final String STRING_INDEX = "index";
	public static final String STRING_WEIGHT = "weight";
	public static final String STRING_COST = "cost";
	public static final int MAX_ITEMS_IN_LINE = 15;
    public static final int MAX_WEIGHT = 100 * 100;
    public static final int MAX_COST = 100 * 100;
    public static final String REGEX_FOR_PACKAGE = 
    		new StringBuilder("\\((?<")
    		.append(STRING_INDEX)
    		.append(">\\d+)\\,(?<")
    		.append(STRING_WEIGHT)
    		.append(">\\d+(\\.\\d{1,2})?)\\,€(?<")
    		.append(STRING_COST)
    		.append(">\\d+(\\.\\d{1,2})?)\\)").toString();
}
