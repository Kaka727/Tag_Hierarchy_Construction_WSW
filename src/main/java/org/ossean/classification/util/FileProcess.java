package org.ossean.classification.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileProcess {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File f = new File("f:/rs_copy");
		Scanner sc = null;
		try {
			 sc= new Scanner(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> list = new ArrayList<String>();
		String tmp;
		while(sc.hasNextLine()){
			tmp = sc.nextLine();
			if(!tmp.equals("")){
				list.add(tmp);
			}
		}
		for(String str:list){
			System.out.println(str);
		}
	}

}
