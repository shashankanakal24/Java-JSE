package com.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class CRUDOperation {
	
	private   String url;
	private  String username;
	private  String password;
	
	public CRUDOperation(String url,String username,String password){
		this.url = url;
		this.username =username;
		this.password =password;
		
		run(this.url, this.username, this.password);
		
		
	}
	
	static Scanner sc = new Scanner(System.in);


	
	
	
	public void run(String url, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Loaded Successfully");
            System.out.println("------------------------------------------------------");

            System.out.print("Do you want to (C)reate a new database or (E)xisting one? (C/E): ");
            String choice = sc.nextLine().trim().toUpperCase();

            System.out.print("Enter the Database Name: ");
            String dbName = sc.nextLine();

            if (choice.equals("C")) {
                createDatabase(dbName, url, username, password);
                tableOperationsMenu(dbName, url, username, password, sc);
            } else if (choice.equals("E")) {
                if (connectToExistingDatabase(dbName, url, username, password)) {
                    System.out.println("Connected to existing database `" + dbName + "` successfully.");
                    tableOperationsMenu(dbName, url, username, password, sc);
                } else {
                    System.out.println("Database `" + dbName + "` not found.");
                    System.out.print("Do you want to create this database? (Y/N): ");
                    String createChoice = sc.nextLine().trim().toUpperCase();
                    if (createChoice.equals("Y")) {
                        createDatabase(dbName, url, username, password);
                        System.out.print("Do you want to create a table in database `" + dbName + "`? (Y/N): ");
                        String tableChoice = sc.nextLine().trim().toUpperCase();
                        if (tableChoice.equals("Y")) {
                            createTable(dbName, url, username, password, sc);
                        }
                    } else {
                        System.out.println("Exiting without creating database.");
                    }
                }
            } else {
                System.out.println("Invalid input. Please enter C or E.");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
	
	public  void tableOperationsMenu(String dbName, String url, String username, String password, Scanner sc) {
	    while (true) {
	        System.out.println("\nWhat would you like to do?");
	        System.out.println("1. View tables and their data");
	        System.out.println("2. Insert a row into a table");
	        System.out.println("3. Create a new table");
	        System.out.println("4. Add a new column to a table");
	        System.out.println("5. Change column data type");
	        System.out.println("6. Update data in a table");
	        System.out.println("7. Drop a table");
	        System.out.println("8. Drop a column from a table");
	        System.out.println("9. Exit to main menu");
	        System.out.print("Enter your choice (1-9): ");
	        int choice = Integer.parseInt(sc.nextLine());

	        switch (choice) {
	            case 1: viewTablesAndData(dbName, url, username, password, sc); break;
	            case 2: insertDataIntoTable(dbName, url, username, password, sc); break;
	            case 3: createNewTable(dbName, url, username, password, sc); break;
	            case 4: addColumnToTable(dbName, url, username, password, sc); break;
	            case 5: changeColumnType(dbName, url, username, password, sc); break;
	            case 6: updateTableData(dbName, url, username, password, sc); break;
	            case 7: dropTable(dbName, url, username, password, sc); break;
	            case 8: dropColumn(dbName, url, username, password, sc); break;
	            case 9: return;
	            default: System.out.println("Invalid choice. Try again.");
	        }
	    }
	}

	
	public  void insertDataIntoTable(String dbName, String url, String username, String password, Scanner sc) {
	    try (
	        Connection con = DriverManager.getConnection(url + "/" + dbName, username, password);
	        Statement stmt = con.createStatement()
	    ) {
	        ResultSet rs = stmt.executeQuery("SHOW TABLES");
	        List<String> tables = new ArrayList<>();
	        while (rs.next()) {
	            tables.add(rs.getString(1));
	        }

	        if (tables.isEmpty()) {
	            System.out.println("No tables found in the database.");
	            return;
	        }

	        System.out.println("Available tables:");
	        for (String t : tables) {
	            System.out.println("- " + t);
	        }

	        System.out.print("Enter table name: ");
	        String table = sc.nextLine();

	        rs = stmt.executeQuery("DESCRIBE " + table);
	        List<String> columns = new ArrayList<>();
	        while (rs.next()) {
	            columns.add(rs.getString(1));
	        }

	        if (columns.isEmpty()) {
	            System.out.println("No columns found in the table.");
	            return;
	        }

	        List<String> values = new ArrayList<>();
	        for (String col : columns) {
	            System.out.print("Enter value for column '" + col + "': ");
	            values.add("'" + sc.nextLine() + "'"); // Wrap each value in single quotes
	        }

	        // Construct column and value strings
	        StringBuilder colsBuilder = new StringBuilder();
	        StringBuilder valsBuilder = new StringBuilder();

	        for (int i = 0; i < columns.size(); i++) {
	            colsBuilder.append(columns.get(i));
	            valsBuilder.append(values.get(i));
	            if (i < columns.size() - 1) {
	                colsBuilder.append(", ");
	                valsBuilder.append(", ");
	            }
	        }

	        String sql = "INSERT INTO " + table + " (" + colsBuilder + ") VALUES (" + valsBuilder + ")";
	        stmt.executeUpdate(sql);
	        System.out.println("Data inserted successfully into table `" + table + "`.");

	    } catch (SQLException e) {
	        System.out.println("Error inserting data.");
	        e.printStackTrace();
	    }
	}

	
	public void dropColumn(String dbName, String url, String username, String password, Scanner sc) {
	    try (
	        Connection con = DriverManager.getConnection(url + "/" + dbName, username, password);
	        Statement stmt = con.createStatement()
	    ) {
	        System.out.print("Enter table name: ");
	        String table = sc.nextLine();

	        ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM " + table);
	        List<String> columns = new ArrayList<>();
	        while (rs.next()) columns.add(rs.getString(1));

	        if (columns.isEmpty()) {
	            System.out.println("No columns found in the table.");
	            return;
	        }

	        System.out.println("Available columns:");
	        for (String c : columns) System.out.println("- " + c);

	        System.out.print("Enter column name to drop: ");
	        String col = sc.nextLine();

	        if (!columns.contains(col)) {
	            System.out.println("Column does not exist.");
	            return;
	        }

	        stmt.executeUpdate("ALTER TABLE " + table + " DROP COLUMN " + col);
	        System.out.println("Column `" + col + "` dropped from `" + table + "`.");

	    } catch (SQLException e) {
	        System.out.println("Error dropping column.");
	        e.printStackTrace();
	    }
	}

	
	public  void dropTable(String dbName, String url, String username, String password, Scanner sc) {
	    try (
	        Connection con = DriverManager.getConnection(url + "/" + dbName, username, password);
	        Statement stmt = con.createStatement()
	    ) {
	        ResultSet rs = stmt.executeQuery("SHOW TABLES");
	        List<String> tables = new ArrayList<>();
	        while (rs.next()) tables.add(rs.getString(1));

	        if (tables.isEmpty()) {
	            System.out.println("No tables found in the database.");
	            return;
	        }

	        System.out.println("Available tables:");
	        for (String t : tables) System.out.println("- " + t);

	        System.out.print("Enter table name to drop: ");
	        String table = sc.nextLine();

	        if (!tables.contains(table)) {
	            System.out.println("Table does not exist.");
	            return;
	        }

	        stmt.executeUpdate("DROP TABLE " + table);
	        System.out.println("Table `" + table + "` dropped successfully.");

	    } catch (SQLException e) {
	        System.out.println("Error dropping table.");
	        e.printStackTrace();
	    }
	}

	
	public void createNewTable(String dbName, String url, String username, String password, Scanner sc) {
	    try (
	        Connection con = DriverManager.getConnection(url + "/" + dbName, username, password);
	        Statement stmt = con.createStatement()
	    ) {
	        System.out.print("Enter table name: ");
	        String tableName = sc.nextLine();

	        System.out.print("Enter number of columns: ");
	        int cols = Integer.parseInt(sc.nextLine());

	        StringBuilder query = new StringBuilder("CREATE TABLE " + tableName + " (");
	        for (int i = 1; i <= cols; i++) {
	            System.out.print("Enter column " + i + " name: ");
	            String colName = sc.nextLine();

	            System.out.print("Enter column " + i + " type (INT or VARCHAR): ");
	            String type = sc.nextLine().toUpperCase();

	            String size = "";
	            if (type.equals("VARCHAR")) {
	                System.out.print("Enter size for VARCHAR: ");
	                size = "(" + sc.nextLine() + ")";
	            }

	            query.append(colName).append(" ").append(type).append(size);
	            if (i != cols) query.append(", ");
	        }
	        query.append(")");
	        stmt.executeUpdate(query.toString());
	        System.out.println("Table `" + tableName + "` created successfully.");

	    } catch (SQLException e) {
	        System.out.println("Error creating table.");
	        e.printStackTrace();
	    }
	}



	public void viewTablesAndData(String dbName, String url, String username, String password, Scanner sc) {
		try (
				Connection con = DriverManager.getConnection(url + "/" + dbName, username, password);
				Statement stmt = con.createStatement()
				) {
			ResultSet rs = stmt.executeQuery("SHOW TABLES");

			List<String> tableList = new ArrayList<>();
			System.out.println("Tables in database:");
			while (rs.next()) {
				String tableName = rs.getString(1);
				tableList.add(tableName);
				System.out.println("- " + tableName);
			}

			if (tableList.isEmpty()) {
				System.out.println("No tables found in the database `" + dbName + "`.");
				return;
			}

			System.out.print("Enter table name to view its data: ");
			String table = sc.nextLine();

			if (!tableList.contains(table)) {
				System.out.println("Table `" + table + "` does not exist in the database.");
				return;
			}

			ResultSet desc = stmt.executeQuery("DESCRIBE " + table);
			List<String> columnNames = new ArrayList<>();

			while (desc.next()) {
				columnNames.add(desc.getString("Field"));
			}

			rs = stmt.executeQuery("SELECT * FROM " + table);
			System.out.println("\nData from table `" + table + "`:");

			boolean hasRows = false;
			while (rs.next()) {
				hasRows = true;
				for (String col : columnNames) {
					System.out.print(col + ": " + rs.getString(col) + "\t");
				}
				System.out.println();
			}

			if (!hasRows) {
				System.out.println("No data available in the table `" + table + "`.");
			}

		} catch (SQLException e) {
			System.out.println("Error fetching table data.");
			e.printStackTrace();
		}
	}




	public  void addColumnToTable(String dbName, String url, String username, String password, Scanner sc) {
	    try (
	        Connection con = DriverManager.getConnection(url + "/" + dbName, username, password);
	        Statement stmt = con.createStatement()
	    ) {
	        // Step 1: Show available tables
	        ResultSet tables = stmt.executeQuery("SHOW TABLES");
	        List<String> tableList = new ArrayList<>();
	        System.out.println("Available tables in database `" + dbName + "`:");
	        while (tables.next()) {
	            String tableName = tables.getString(1);
	            tableList.add(tableName);
	            System.out.println("- " + tableName);
	        }

	        // Step 2: Check if no tables exist
	        if (tableList.isEmpty()) {
	            System.out.println("No tables found in the database. Cannot add column.");
	            return;
	        }

	        // Step 3: Ask user to input a table name
	        System.out.print("Enter table name: ");
	        String table = sc.nextLine();

	        // Step 4: Validate table name
	        if (!tableList.contains(table)) {
	            System.out.println("Table `" + table + "` does not exist.");
	            return;
	        }

	        // Step 5: Ask for new column name and type
	        System.out.print("Enter new column name: ");
	        String col = sc.nextLine();

	        System.out.print("Enter type (INT or VARCHAR): ");
	        String type = sc.nextLine().toUpperCase();

	        String size = "";
	        if (type.equals("VARCHAR")) {
	            System.out.print("Enter size for VARCHAR: ");
	            size = "(" + sc.nextLine() + ")";
	        }

	        // Step 6: Construct and execute ALTER TABLE command
	        String sql = "ALTER TABLE " + table + " ADD COLUMN " + col + " " + type + size;
	        stmt.executeUpdate(sql);
	        System.out.println("✅ Column added successfully to table `" + table + "`.");

	    } catch (SQLException e) {
	        System.out.println("Error adding column.");
	        e.printStackTrace();
	    }
	}




	public void changeColumnType(String dbName, String url, String username, String password, Scanner sc) {

	    try (
	        Connection con = DriverManager.getConnection(url + "/" + dbName, username, password);
	        Statement stmt = con.createStatement()
	    ) {
	        ResultSet tables = stmt.executeQuery("SHOW TABLES");
	        List<String> tableList = new ArrayList<>();
	        System.out.println("Available tables in database `" + dbName + "`:");
	        while (tables.next()) {
	            String tableName = tables.getString(1);
	            tableList.add(tableName);
	            System.out.println("- " + tableName);
	        }

	        // Check for no tables
	        if (tableList.isEmpty()) {
	            System.out.println("No tables found in the database. Cannot change column type.");
	            return;
	        }

	        System.out.print("Enter table name: ");
	        String table = sc.nextLine();

	        // ❗ Optionally validate table name
	        if (!tableList.contains(table)) {
	            System.out.println("Table `" + table + "` does not exist.");
	            return;
	        }

	        System.out.print("Enter column name to change type: ");
	        String col = sc.nextLine();

	        System.out.print("Enter new type (INT or VARCHAR): ");
	        String type = sc.nextLine().toUpperCase();

	        String size = "";
	        if (type.equals("VARCHAR")) {
	            System.out.print("Enter new size for VARCHAR: ");
	            size = "(" + sc.nextLine() + ")";
	        }

	        String sql = "ALTER TABLE " + table + " MODIFY COLUMN " + col + " " + type + size;
	        stmt.executeUpdate(sql);
	        System.out.println("Column type changed successfully.");

	    } catch (SQLException e) {
	        System.out.println("Error changing column type.");
	        e.printStackTrace();
	    }
	}


	public  void updateTableData(String dbName, String url, String username, String password, Scanner sc) {
	    try (
	        Connection con = DriverManager.getConnection(url + "/" + dbName, username, password);
	        Statement stmt = con.createStatement()
	    ) {
	        ResultSet tables = stmt.executeQuery("SHOW TABLES");
	        List<String> tableList = new ArrayList<>();
	        System.out.println("Available tables in database `" + dbName + "`:");
	        while (tables.next()) {
	            String tableName = tables.getString(1);
	            tableList.add(tableName);
	            System.out.println("- " + tableName);
	        }

	        // ✅ Check for no tables
	        if (tableList.isEmpty()) {
	            System.out.println("No tables found in the database. Cannot update data.");
	            return;
	        }

	        System.out.print("Enter table name: ");
	        String table = sc.nextLine();

	        // ❗ Optionally validate table name
	        if (!tableList.contains(table)) {
	            System.out.println("Table `" + table + "` does not exist.");
	            return;
	        }

	        System.out.print("Enter column to update: ");
	        String col = sc.nextLine();

	        System.out.print("Enter new value: ");
	        String newVal = sc.nextLine();

	        System.out.print("Enter condition (e.g., id=1): ");
	        String condition = sc.nextLine();

	        String sql = "UPDATE " + table + " SET " + col + " = '" + newVal + "' WHERE " + condition;
	        int rows = stmt.executeUpdate(sql);
	        System.out.println(rows + " row(s) updated.");

	    } catch (SQLException e) {
	        System.out.println("Error updating data.");
	        e.printStackTrace();
	    }
	}





	public void createTable(String dbName, String url, String username, String password,Scanner sc) {
		// sc = new Scanner(System.in);
		Connection con = null;
		Statement stmt = null;

		try {
			String fullUrl = url + "/" + dbName;
			con = DriverManager.getConnection(fullUrl, username, password);
			stmt = con.createStatement();

			System.out.print("Enter the Table Name: ");
			String tableName = sc.nextLine();

			System.out.print("How many columns do you want in the table? ");
			int colCount = Integer.parseInt(sc.nextLine());

			StringBuilder query = new StringBuilder("CREATE TABLE " + tableName + " (");

			for (int i = 0; i < colCount; i++) {
				System.out.print("Enter column " + (i + 1) + " name: ");
				String colName = sc.nextLine();

				String type = "";
				while (!(type.equals("Number") || type.equals("String"))) {
					System.out.print("Select type for column `" + colName + "` (Number/String): ");
					type = sc.nextLine().trim();
				}

				if (i == 0 && !type.equals("Number")) {
					System.out.println("WARNING: First column must be Number type to be used as Primary Key. Setting to Number.");
					type = "Number";
				}

				String colDef;
				if (type.equals("String")) {
					System.out.print("Enter size for VARCHAR column `" + colName + "`: ");
					int size = Integer.parseInt(sc.nextLine());
					colDef = colName + " VARCHAR(" + size + ")";
				} else {
					colDef = colName + " INT";
				}

				if (i == 0) {
					colDef += " PRIMARY KEY";
				}

				query.append(colDef);

				if (i < colCount - 1) {
					query.append(", ");
				}
			}

			query.append(");");

			stmt.executeUpdate(query.toString());
			System.out.println("Table `" + tableName + "` created successfully in database `" + dbName + "`.");

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			System.out.println("Invalid number entered. Table creation aborted.");
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	public  boolean connectToExistingDatabase(String dbName, String url, String username, String password) {
		Connection con = null;
		try {
			// Append the database name to the base URL
			String fullUrl = url + "/" + dbName;

			// Try connecting to the specified database
			con = DriverManager.getConnection(fullUrl, username, password);
			return true;  //  Connection successful
		} catch (SQLException e) {
			return false; //  Connection failed
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	public void createDatabase(String dbName, String url, String username, String password) {
		Connection con = null;
		Statement stmt = null;

		try {
			con = DriverManager.getConnection(url, username, password);
			System.out.println("SQL Connected.");
			System.out.println("------------------------------------------------------");

			stmt = con.createStatement();
			String sql = "CREATE DATABASE IF NOT EXISTS " + dbName;
			stmt.executeUpdate(sql);
			System.out.println("Database `" + dbName + "` created or already exists.");


		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
				if (con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


}
