package com.themoviedb.movietest;

import static io.restassured.RestAssured.given;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import baseclasses.ExcelUtility_SingleDataSet;
import baseclasses.FormatingResponse;
import baseclasses.ReusableMethods.Constants;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class Validate_People_Details_Test {
	Properties prop = new Properties();
	String sheetname = "People";

	@BeforeClass(alwaysRun = true)
	public void setUp() throws Exception {
		ExcelUtility_SingleDataSet.setExcelFile(Constants.File, sheetname);

	}

	@BeforeTest
	public void getEnvironmentData() throws Exception {
		FileInputStream fis = new FileInputStream("./env.properties");
		prop.load(fis);
	}

	@SuppressWarnings("resource")
	@Test
	public void Validate_People_Details() throws Exception {
		XSSFWorkbook ExcelWBook;
		XSSFSheet ExcelWSheet;
		XSSFCell cell;
		String path = "./TheMovieDB.xlsx";
		FileInputStream ExcelFile = new FileInputStream(path);
		ExcelWBook = new XSSFWorkbook(ExcelFile);
		ExcelWSheet = ExcelWBook.getSheet("People");
		System.out.println("\n********Test case to person information based on ID*******");

		for (int i = 0; i <= ExcelWSheet.getLastRowNum(); i++) {

			RestAssured.baseURI = prop.getProperty("Host");
			cell = ExcelWSheet.getRow(i).getCell(0);
			String id = cell.getStringCellValue();

			Response res = given().param("api_key", prop.getProperty("KEY"))
					.queryParam("language", prop.getProperty("Language")).log().uri().get("/3/person/" + id).then()
					.assertThat().statusCode(200).and().contentType(ContentType.JSON).and().extract().response();

			JsonPath js = FormatingResponse.rawToJson(res);
			String name = js.get("name");
			cell = ExcelWSheet.getRow(i).getCell(1);
			String Artist_name = cell.getStringCellValue();
			if (name.equals(Artist_name)) {
				System.out.println("Test case Passed: Artist Name = " + name + ". When person ID = " + id + "\n");
				String timeStamp = new SimpleDateFormat("EEE, d MMM yyyy h:mm:ss a").format(new Date());
				ExcelUtility_SingleDataSet.setCellData("Test Case Passed", i, 2);
				ExcelUtility_SingleDataSet.setCellData(timeStamp, i, 3);
			} else {
				System.out.println("Test case Failed: Artist name = " + name + " is not Not matching the person ID = "
						+ id + "\n");
				String timeStamp = new SimpleDateFormat("EEE, d MMM yyyy h:mm:ss a").format(new Date());
				ExcelUtility_SingleDataSet.setCellData("Test Case Failed", i, 2);
				ExcelUtility_SingleDataSet.setCellData(timeStamp, i, 3);
			}

		}

	}

}
