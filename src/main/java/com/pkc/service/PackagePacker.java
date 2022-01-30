package com.pkc.service;

import java.util.stream.Collectors;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pkc.exceptions.ConstraintViolationException;
import com.pkc.util.PkcConstants;
import com.pkc.data.Package;
import com.pkc.data.ScannedPackage;


public class PackagePacker {
	public static String pack(String file) {
		//We start by initially reading file creating some objects
		List<ScannedPackage> packages = scanForPackages(file);
		String packageCombination = 
				packages.stream()
				.map(pkg -> getBestPackageCombination(pkg.getMaxWeight(), pkg.getPackages()))
				.collect(Collectors.joining("\n"));
		return packageCombination;
    }
	
	//core logic to derive the best package
	private static String getBestPackageCombination(int maxWeight, List<Package> packages) {
		int n = packages.size() + 1;//+ 1 just for feasibility
        int w = maxWeight + 1; //+1 for feasibility
        double[][] a = new double[n][w]; //two dimensional array to save combinations. Useful to derive best packages


        //rest of the logic is self explanatory
        for (int i = 1; i < n; i++) {
            Package pack = packages.get(i - 1);

            for (int j = 1; j < w; j++) {
                if (pack.getWeight() > j) {
                    a[i][j] = a[i - 1][j];
                } else {
                    a[i][j] = Math.max(a[i - 1][j], a[i - 1][j - pack.getWeight()] + pack.getCost());
                }
            }
        }

        List<Integer> indexes = new ArrayList<>();
        int j = maxWeight;
        double totalcost = a[n - 1][w - 1];
        for (; j > 0 && a[n - 1][j - 1] == totalcost; j--);

        for (int i = n - 1; i > 0; i--) {
            if (a[i][j] != a[i - 1][j]) {
                indexes.add(packages.get(i - 1).getIndex());
                j -= packages.get(i - 1).getWeight();
            }
        }

        //collect the result
        String result =
                indexes.stream()
                        .mapToInt(i -> i)
                        .sorted()
                        .mapToObj(index -> Integer.toString(index))
                        .collect(Collectors.joining(","));
        return result.isEmpty() ? "-" : result;
	}
	
	private static List<ScannedPackage> scanForPackages(String file) {
		FileInputStream fileStream = null;
		try {
			fileStream = new FileInputStream(file);//Reading file here
		}catch(FileNotFoundException fnfe) {
			throw new ConstraintViolationException(fnfe);//An exception class created to display cause
		}
		
		List<ScannedPackage> parsedLines = new ArrayList<>();
		try(Scanner scanner = new Scanner(fileStream)){
			for( int line=0; scanner.hasNext(); line++) {
				String currentLine = scanner.nextLine();//Reading file line by line
				ScannedPackage scannedPackage =  validateAndCreatePackage(currentLine, line);//Reading file and creating objects
				parsedLines.add(scannedPackage);
			}
		}catch(Exception e) {
			throw new ConstraintViolationException(e);
		}
		return parsedLines;
	}
	
	private static ScannedPackage validateAndCreatePackage(String currentLine, int lineSequence) {
		validateSingleColonPerLine(currentLine, lineSequence);//validation for line to have single colon character
		
		List<Package> packages = new ArrayList<>();
		String[] maxWeightAndPackagesSplitValues = currentLine.split(":");//splitting individual line for save required values
		Pattern pattern = Pattern.compile(PkcConstants.REGEX_FOR_PACKAGE);//pattern matching to collect required values
		Matcher matcher = pattern.matcher(maxWeightAndPackagesSplitValues[1]);
		
		while (matcher.find()) {
			try {
		        	
		    	int index = Integer.valueOf(matcher.group(PkcConstants.STRING_INDEX)); //get index value
		        int weight = (int) (Double.valueOf(matcher.group(PkcConstants.STRING_WEIGHT)) * 100); //get weight, here multiplying by 100 to get maximum weight
		        double cost = Double.valueOf(matcher.group(PkcConstants.STRING_COST));//get cost
	
		        validateIndexorMaxItemsInLine(index, currentLine, lineSequence);//validating max items in a line
		        validateMaxWeight(weight, currentLine, lineSequence);//validation max weight
		        validateMaxCost(cost, currentLine, lineSequence);//validating cost
		        
		        packages.add(new Package(index, weight, cost)); //package collection per line
	        }catch(Exception exception) {
	        	throw new ConstraintViolationException(exception);
	        }
		}
		
		int maxWeight = (int) (Double.valueOf(maxWeightAndPackagesSplitValues[0]) * 100); //maximum allowed weight per line
		return new ScannedPackage(maxWeight, packages);
	}
	
	private static void validateIndexorMaxItemsInLine(int indexValue, String currentLine, int lineSequence) {
		if( indexValue < 0 || indexValue > PkcConstants.MAX_ITEMS_IN_LINE ) {
			throw new ConstraintViolationException(String.format("Index value should be between (1, %d) ",PkcConstants.MAX_ITEMS_IN_LINE), currentLine, lineSequence);
		}
	}
	
	private static void validateMaxWeight(int weight, String currentLine, int lineSequence) {
		if( weight < 0 || weight > PkcConstants.MAX_WEIGHT) {
			throw new ConstraintViolationException(String.format(" Weight should be between (0, %f)", PkcConstants.MAX_WEIGHT), currentLine, lineSequence);
		}
	}
	
	private static void validateMaxCost(double cost, String currentLine, int lineSequence) {
		if( cost < 0 || cost > PkcConstants.MAX_COST) {
			throw new ConstraintViolationException(String.format(" Cost should be between (0, %f)", PkcConstants.MAX_COST), currentLine, lineSequence);
		}
	}
	
	private static void validateSingleColonPerLine(String currentLine, int lineSequence) {
		String[] maxWeightAndPackagesSplitValues = currentLine.split(":");
        if (maxWeightAndPackagesSplitValues.length != 2) {
            throw new ConstraintViolationException("Each line must contain only one colon >> : ", currentLine, lineSequence);
        }
        
       
	}
}
