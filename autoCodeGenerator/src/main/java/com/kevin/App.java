package com.kevin;

import com.kevin.generator.AutoCodeGenerator;

/**
 * 程序入口
 * 
 */
public class App {
	public static void main(String[] args) throws Exception {
		AutoCodeGenerator autoCodeGenerator = new AutoCodeGenerator();
		autoCodeGenerator.init();
		autoCodeGenerator.generate();
	}
}
