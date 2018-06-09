package br.com.technomori.ordermanager.resources.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class URL {

	public static String decodeParam(String str) {
		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static List<Integer> decodeIntList(String str) {
		if( str.isEmpty() ) {
			return new ArrayList<Integer>();
		}
		return Arrays.asList( str.split(",") )
				.stream()
				.map( token -> Integer.parseInt(token) )
				.collect( Collectors.toList() );
				
	}
}
