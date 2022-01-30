package com.pkc.main;

import com.pkc.service.PackagePacker;

public class Main {
	 public static void main(String[] args) {
	        if (args.length != 1){
	            System.err.println("Please provide input file as argument");
	            System.exit(1);
	        }

	        System.out.println(PackagePacker.pack(args[0]));
	    }
}
