package com.pearson.common.enums;

public enum Node {
	NODE_1("1"),
	NODE_2("2"),
	NODE_3("3"),
	NODE_4("4"),
	NODE_5("5"),
	ANY("ANY");
	
	final private static Node node = create();
	private String nodeNumber = null;

	private Node(String nodeNumber){
		this.nodeNumber = nodeNumber;
	}
	
	final public static String toStr(){
		return node.toString();
	}
	
	public static String getNodeNumber(){
		return node.nodeNumber;
	}
		
	public static Node create(){
		
		String str = System.getProperty("selenium.node") == null ? "any" : 
					 System.getProperty("selenium.node").toLowerCase().trim();

		if(str.equals("1")){
			return NODE_1;
		}
		else if(str.equals("2")){
			return NODE_2;
		}
		else if(str.equals("3")){
			return NODE_3;
		}else
			return ANY;	
	}
}
