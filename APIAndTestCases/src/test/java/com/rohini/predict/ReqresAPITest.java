package com.rohini.predict;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ReqresAPITest {

	private static final String BASE_URI = "https://reqres.in/";
	private static final String user = "https://reqres.in/api/users"; // "2"
	private static final String registerUser = "https://reqres.in/api/register";
	private static final String loginUser = "https://reqres.in/api/login"; // "/2"

	// Configuration conf = Configuration.defaultConfiguration();
	private static String userPayload = null;
	private static String registerUserPayload = null;

	public static String loginUserPayload = null;
	public static String registeredUserJsonObject = null;

	@BeforeClass
	public void beforeClass() throws IOException {
		userPayload = generatePayLoad("src/test/resources/requestPayloads/createOrUpdateUserPayload.json");
		registerUserPayload = generatePayLoad("src/test/resources/requestPayloads/registerUserPayload.json");
	}

	public String generatePayLoad(String filepath) throws IOException {
		String payload = Files.readString(Paths.get(filepath));
		System.out.println("\n.....Request payload.....\n" + payload);
		return payload;
	}

	@Test(enabled = true, description = "Update User")
	public void updateUser() throws IOException {

		RestAssured.baseURI = BASE_URI;

		// Get the RequestSpecification of the request to be sent to the server.
		RequestSpecification httpRequest = RestAssured.given();

		// Add a header stating the Request body is a JSON
		httpRequest.header("Content-Type", "application/json"); // Add the Json to the body of the request

		// Get the json payload and update the data to perform POST.
		String payload = userPayload;
		payload = JsonPath.parse(payload).set("$.name", "Updated morpheus").jsonString();
		payload = JsonPath.parse(payload).set("$.job", "Updated zion resident").jsonString();
		System.out.println(".....Payload......After Update.....\n" + payload);

		httpRequest = RestAssured.given();
		httpRequest.header("Content-Type", "application/json"); // Add the Json to the body of the request
		httpRequest.body(payload); // Post the request and check the response

		// Passing the payload to call POST api.
		Response response = httpRequest.put(user + "/2");

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 200,
				".....Response Code did NOT match...." + response.asString());

		System.out.println(".....Response....." + response.prettyPrint());

		// Converting the response body to string object
		String rbdy = response.asString();

		/*
		 * validate - 1. name, job (Updated data) 2. id is not null 3. created is not
		 * null
		 */
		Assert.assertEquals(JsonPath.read(rbdy, "$.name"), "Updated morpheus", ".....Name data did NOT match....");
		Assert.assertEquals(JsonPath.read(rbdy, "$.job"), "Updated zion resident", ".....Job data did NOT match....");
		Assert.assertNotNull(JsonPath.read(rbdy, "$.updatedAt"), ".....updatedAt data should NOT be NULL....");

	}

	@Test(enabled = true, description = "Create User")
	public void createUser() throws IOException {

		// Get the json payload and update the data to perform POST.
		String payload = userPayload;
		payload = JsonPath.parse(payload).set("$.name", "Rohini Kumari").jsonString();
		payload = JsonPath.parse(payload).set("$.job", "QA").jsonString();
		System.out.println(".....Payload......After Update.....\n" + payload);

		// Get the RequestSpecification of the request to be sent to the server.
		RequestSpecification httpRequest = RestAssured.given();

		// Add a header stating the Request body is a JSON
		httpRequest.header("Content-Type", "application/json"); // Add the Json to the body of the request
		httpRequest.body(payload); // Post the request and check the response

		// Passing the payload to call POST api.
		Response response = httpRequest.post(user);

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 201,
				".....Response Code did NOT match...." + response.asString());

		System.out.println(".....Response....." + response.prettyPrint());

		// Converting the response body to string object
		String rbdy = response.asString();

		/*
		 * validate - 1. name, job are same 2. id is not null 3. created is not null
		 */
		Assert.assertEquals(JsonPath.read(rbdy, "$.name"), "Rohini Kumari", ".....Name data did NOT match....");
		Assert.assertEquals(JsonPath.read(rbdy, "$.job"), "QA", ".....Job data did NOT match....");
		Assert.assertNotNull(JsonPath.read(rbdy, "$.id"), ".....ID data should NOT be NULL....");
		Assert.assertNotNull(JsonPath.read(rbdy, "$.createdAt"), ".....createdAt data should NOT be NULL....");

	}

	@Test(enabled = true, description = "Retrieve User")
	public void retrieveGvnUser() throws IOException {

		RestAssured.baseURI = BASE_URI;

		// Get the RequestSpecification of the request to be sent to the server.
		RequestSpecification httpRequest = RestAssured.given();

		// Add a header stating the Request body is a JSON
		httpRequest.header("Content-Type", "application/json"); // Add the Json to the body of the request

		// Passing the payload to call POST api.
		Response response = httpRequest.get(user + "/2");
		System.out.println(".....Response.....\n" + response.prettyPrint());

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 200,
				".....Response Code did NOT match...." + response.asString());
		Assert.assertEquals(response.getStatusLine(), "HTTP/1.1 200 OK", "......StatusLine did NOT match......");

		// Converting the response body to string object
		String rbdy = response.asString();

		/*
		 * validate
		 */
		Assert.assertEquals(JsonPath.read(rbdy, "$.data.id"), 2, ".....id data did NOT match....");
		Assert.assertEquals(JsonPath.read(rbdy, "$.data.first_name"), "Janet",
				".....first_name data did NOT match....");
		Assert.assertEquals(JsonPath.read(rbdy, "$.data.last_name"), "Weaver", ".....last_name data did NOT match....");
		Assert.assertNotNull(JsonPath.read(rbdy, "$.support.url"), ".....url data should NOT be NULL....");
		Assert.assertNotNull(JsonPath.read(rbdy, "$.support.text"), ".....text data should NOT be NULL....");

	}

	@Test(enabled = true, description = "Retrieve List of User")
	public void retrieveListOfUsers() throws IOException {

		RestAssured.baseURI = BASE_URI;

		// Get the RequestSpecification of the request to be sent to the server.
		RequestSpecification httpRequest = RestAssured.given();

		// call get api.
		Response response = httpRequest.get(user);
		System.out.println(".....Response.....\n" + response.prettyPrint());

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 200,
				".....Response Code did NOT match...." + response.asString());
		Assert.assertEquals(response.getStatusLine(), "HTTP/1.1 200 OK", "......StatusLine did NOT match......");

		/* Getting all users's data */
		List<Object> data = response.jsonPath().get("data");
		Assert.assertTrue(data.size() >= 1, ".....No data found......");

		for (int i = 0; i < data.size(); i++) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> dataMap = (HashMap<String, Object>) data.get(i);
			Assert.assertNotNull(dataMap.get("id"), ".....ID data should NOT be NULL....");
			Assert.assertNotNull(dataMap.get("email"), ".....email data should NOT be NULL....");
			Assert.assertNotNull(dataMap.get("first_name"), ".....first_name data should NOT be NULL....");
			Assert.assertNotNull(dataMap.get("last_name"), ".....last_name data should NOT be NULL....");
			Assert.assertNotNull(dataMap.get("avatar"), ".....avatar data should NOT be NULL....");
		}
	}

	@Test(enabled = true, description = "Delete User")
	public void deleteUser() throws IOException {

		RestAssured.baseURI = BASE_URI;

		// Get the RequestSpecification of the request to be sent to the server.
		RequestSpecification httpRequest = RestAssured.given();

		// Add a header stating the Request body is a JSON
		httpRequest.header("Content-Type", "application/json"); // Add the Json to the body of the request

		// call delete api.
		Response response = httpRequest.delete(user + "/2");
		System.out.println(".....Response.....\n" + response.prettyPrint());

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 204,
				".....Response Code did NOT match...." + response.asString());
	}

	@Test(enabled = true, description = "Register User Unsuccessfully")
	public void registerUserFailure() throws IOException {

		// Get the json payload and update the data to perform POST.
		String payload = registerUserPayload;
		payload = JsonPath.parse(payload).set("$.email", "krirohini@gmail.com").jsonString();

		// Get the RequestSpecification of the request to be sent to the server.
		RequestSpecification httpRequest = RestAssured.given();

		// Add a header stating the Request body is a JSON
		httpRequest.header("Content-Type", "application/json"); // Add the Json to the body of the request
		httpRequest.body(payload); // Post the request and check the response

		// Passing the payload to call POST api.
		Response response = httpRequest.post(registerUser);

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 400,
				".....Response Code did NOT match...." + response.asString());

		System.out.println(".....Response....." + response.prettyPrint());
		String rbody = response.asString();

		/*
		 * validate - 1. For missing "password"
		 */
		Assert.assertNotNull(JsonPath.read(rbody, "$.error"), ".....Missing password....");
	}

	@Test(enabled = true, description = "Register User Successfully")
	public void registerUser() throws IOException {

		// Get the json payload and update the data to perform POST.
		String payload = registerUserPayload;
		payload = JsonPath.parse(payload).set("$.email", "eve.holt@reqres.in").jsonString();
		payload = JsonPath.parse(payload).set("$.password", "pistol").jsonString();
		System.out.println(".....Payload......After Update.....\n" + payload);

		// Get the RequestSpecification of the request to be sent to the server.
		RequestSpecification httpRequest = RestAssured.given();

		// Add a header stating the Request body is a JSON
		httpRequest.header("Content-Type", "application/json"); // Add the Json to the body of the request
		httpRequest.body(payload); // Post the request and check the response

		// Passing the payload to call POST api.
		Response response = httpRequest.post(registerUser);

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 200,
				".....Response Code did NOT match...." + response.asString());

		System.out.println(".....Response....." + response.prettyPrint());

		// response body after registering the user.
		registeredUserJsonObject = response.asString();

		/*
		 * validate - 1. id is not null 2. token is not null
		 */
		Assert.assertNotNull(JsonPath.read(registeredUserJsonObject, "$.token"),
				".....token data should NOT be NULL....");

		// Assigning the register request body for login
		loginUserPayload = payload;
	}

	@Test(enabled = true, description = "Login User Unsuccessfully")
	public void loginUserFailure() throws IOException {

		// Get the json payload and update the data to perform POST.
		String payload = registerUserPayload;
		payload = JsonPath.parse(payload).set("$.email", "peter@klaven").jsonString();

		// Get the RequestSpecification of the request to be sent to the server.
		RequestSpecification httpRequest = RestAssured.given();

		// Add a header stating the Request body is a JSON
		httpRequest.header("Content-Type", "application/json"); // Add the Json to the body of the request
		httpRequest.body(payload); // Post the request and check the response

		// Passing the payload to call POST api.
		Response response = httpRequest.post(registerUser);

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 400,
				".....Response Code did NOT match...." + response.asString());

		System.out.println(".....Response....." + response.prettyPrint());
		String rbody = response.asString();

		/*
		 * validate - 1. For missing "password"
		 */
		Assert.assertNotNull(JsonPath.read(rbody, "$.error"), ".....Missing password....");
	}

	@Test(enabled = true, dependsOnMethods = "registerUser", description = "Login User Successfully after register the user")
	public void loginUser() throws IOException {

		// Get the RequestSpecification of the request to be sent to the server.
		RequestSpecification httpRequest = RestAssured.given();

		// Add a header stating the Request body is a JSON
		httpRequest.header("Content-Type", "application/json"); // Add the Json to the body of the request
		httpRequest.body(loginUserPayload); // Post the request and check the response
		// System.out.println(".....loginUserPayload.....\n" + loginUserPayload);

		// Passing the payload to call POST api.
		Response response = httpRequest.post(loginUser);

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 200,
				".....Response Code did NOT match...." + response.asString());
		// System.out.println(".....Response.....\n" + response.prettyPrint());

		/*
		 * validate - 1. "token" is same, after registration.
		 */
		Assert.assertEquals(JsonPath.read((response.asString()), "$.token"),
				JsonPath.read(registeredUserJsonObject, "$.token"), ".....token data did NOT match....");

	}

	@Test(enabled = true, description = "Validate User flows")
	public void validateUserRegistrationLoginFlows() {
		// Get the json payload and update the data to perform POST.
		String payload = registerUserPayload;
		payload = JsonPath.parse(payload).set("$.email", "eve.holt@reqres.in").jsonString();
		payload = JsonPath.parse(payload).set("$.password", "pistol").jsonString();
		System.out.println(".....Payload......After Update.....\n" + payload);

		// Get the RequestSpecification of the request to be sent to the server.
		RequestSpecification httpRequest = RestAssured.given();

		// Add a header stating the Request body is a JSON
		httpRequest.header("Content-Type", "application/json"); // Add the Json to the body of the request
		httpRequest.body(payload); // Post the request and check the response

		// Passing the payload to call POST api.
		Response response = httpRequest.post(registerUser);

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 200,
				".....Response Code did NOT match...." + response.asString());

		System.out.println(".....Response....." + response.prettyPrint());

		// response body after registering the user.
		registeredUserJsonObject = response.asString();

		/*
		 * validate - 1. id is not null 2. token is not null
		 */
		Assert.assertNotNull(JsonPath.read(registeredUserJsonObject, "$.token"),
				".....token data should NOT be NULL....");

		// Assigning the register request body for login
		loginUserPayload = payload;

		// Get the RequestSpecification of the request to be sent to the server.
		httpRequest = RestAssured.given();

		// Add a header stating the Request body is a JSON
		httpRequest.header("Content-Type", "application/json"); // Add the Json to the body of the request
		httpRequest.body(loginUserPayload); // Post the request and check the response
		// System.out.println(".....loginUserPayload.....\n" + loginUserPayload);

		// Passing the payload to call POST api.
		response = httpRequest.post(loginUser);

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 200,
				".....Response Code did NOT match...." + response.asString());
		// System.out.println(".....Response.....\n" + response.prettyPrint());

		/*
		 * validate - 1. "token" is same, after registration.
		 */
		Assert.assertEquals(JsonPath.read((response.asString()), "$.token"),
				JsonPath.read(registeredUserJsonObject, "$.token"), ".....token data did NOT match....");

	}

	@Test(enabled = true, description = "Create an User and then get list of users.")
	public void createUserAndListAllUsers() {

		// Get the json payload and update the data to perform POST.
		String payload = userPayload;
		payload = JsonPath.parse(payload).set("$.name", "Rohini Kumari").jsonString();
		payload = JsonPath.parse(payload).set("$.job", "QA").jsonString();
		System.out.println(".....Payload......After Update.....\n" + payload);

		// Get the RequestSpecification of the request to be sent to the server.
		RequestSpecification httpRequest = RestAssured.given();

		// Add a header stating the Request body is a JSON
		httpRequest.header("Content-Type", "application/json"); // Add the Json to the body of the request
		httpRequest.body(payload); // Post the request and check the response

		// Passing the payload to call POST api.
		Response response = httpRequest.post(user);

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 201,
				".....Response Code did NOT match...." + response.asString());

		System.out.println(".....Response....." + response.prettyPrint());
		// Converting the response body to string object
		String rbdy = response.asString();

		/*
		 * validate - 1. name, job are same 2. id is not null 3. created is not null
		 */
		Assert.assertEquals(JsonPath.read(rbdy, "$.name"), "Rohini Kumari", ".....Name data did NOT match....");
		Assert.assertEquals(JsonPath.read(rbdy, "$.job"), "QA", ".....Job data did NOT match....");
		Assert.assertNotNull(JsonPath.read(rbdy, "$.id"), ".....ID data should NOT be NULL....");
		Assert.assertNotNull(JsonPath.read(rbdy, "$.createdAt"), ".....createdAt data should NOT be NULL....");

		// Get the RequestSpecification of the request to be sent to the server.
		httpRequest = RestAssured.given();

		// call get api.
		response = httpRequest.get(user);
		System.out.println(".....Response.....\n" + response.prettyPrint());

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 200,
				".....Response Code did NOT match...." + response.asString());
		Assert.assertEquals(response.getStatusLine(), "HTTP/1.1 200 OK", "......StatusLine did NOT match......");

		/* Getting all users's data */
		List<Object> data = response.jsonPath().get("data");
		Assert.assertTrue(data.size() >= 1, ".....No data found......");

		for (int i = 0; i < data.size(); i++) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> dataMap = (HashMap<String, Object>) data.get(i);
			Assert.assertNotNull(dataMap.get("id"), ".....ID data should NOT be NULL....");
			Assert.assertNotNull(dataMap.get("email"), ".....email data should NOT be NULL....");
			Assert.assertNotNull(dataMap.get("first_name"), ".....first_name data should NOT be NULL....");
			Assert.assertNotNull(dataMap.get("last_name"), ".....last_name data should NOT be NULL....");
			Assert.assertNotNull(dataMap.get("avatar"), ".....avatar data should NOT be NULL....");
		}
	}

	@Test(enabled = true, description = "https://reqres.in/api/users?delay=3")
	public void userDelayed() throws IOException {

		// Get the RequestSpecification of the request to be sent to the server.
		RequestSpecification httpRequest = RestAssured.given();

		// Get the RequestSpecification of the request to be sent to the server.
		httpRequest = RestAssured.given();
		
		// Passing the query param for "delay"
		Response response = httpRequest.queryParam("delay", "3").get(user);
		System.out.println(".....Response.....\n" + response.prettyPrint());

		// Assert that correct status code is returned.
		Assert.assertEquals(response.getStatusCode(), 200,
				".....Response Code did NOT match...." + response.asString());
		Assert.assertEquals(response.getStatusLine(), "HTTP/1.1 200 OK", "......StatusLine did NOT match......");

		/* Getting all users's data */
		List<Object> data = response.jsonPath().get("data");
		Assert.assertTrue(data.size() >= 1, ".....No data found......");

	}

}
