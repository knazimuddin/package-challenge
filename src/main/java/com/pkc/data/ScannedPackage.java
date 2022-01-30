package com.pkc.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScannedPackage {
	private int maxWeight;
    private List<Package> packages;
}
