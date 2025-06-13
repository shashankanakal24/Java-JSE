package com.JDBC;

public class Main {
	

	public static void main(String[] args) {
		
		String url = "jdbc:mysql://localhost:3306";
		String username ="root";
		String password ="########";
		
		
		//CRUD operation of the SQL Using JDBC
		CRUDOperation c =new CRUDOperation(url,username,password);
		

		
	}




}



