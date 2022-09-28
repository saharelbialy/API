package apiTests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class RestfulBookerApiTests {
    String accessToken;
    int bookingId;

    @BeforeClass
    public void loginToApp()
    {
        String endpoint = "https://restful-booker.herokuapp.com/auth";
        String body = """
                             {
                             
                              "username" : "admin",
                              "password" : "password123"
                          }""";
        Response response = (Response) given().body(body).header("Content-Type", "application/json")
                .log().all()
                .when()
                .post(endpoint)
                .then().extract().response();
        JsonPath jsonPath = response.jsonPath();
        accessToken = jsonPath.getString("token");
        System.out.println(accessToken);
    }
@Test (priority = 0)
    public void testCreateBooking(){
        String endpoint = "https://restful-booker.herokuapp.com/booking";
        String body = """ 
                {
                "firstname": "Jim",
                "lastname": "Brown",
                "totalprice": 111,
                "depositpaid": true,
                "bookingdates": {
                    "checkin": "2018-01-01",
                    "checkout": "2019-01-01"
                      },
                      "additionalneeds" : "Breakfast"
                     }""" ;



      var responseToValidate = given().body(body).header("Content-Type", "application/json")
             .log().all()
             .when().post(endpoint).then();
    responseToValidate.body("booking.firstname", equalTo("Jim"));
    responseToValidate.statusCode(200);

      Response response = responseToValidate.extract().response();
    JsonPath jsonPath = response.jsonPath();
   bookingId = jsonPath.getInt("bookingid");

responseToValidate.log().all();
}
   @Test(priority = 1)
   public void testEditBooking ()
   {
       String endpoint = "https://restful-booker.herokuapp.com/booking/" + bookingId;
       String body = """
                {
                    "firstname" : "James",
                    "lastname" : "Brown",
                    "totalprice" : 111,
                    "depositpaid" : true,
                    "bookingdates" : {
                        "checkin" : "2018-01-01",
                        "checkout" : "2019-01-01"
                    },
                    "additionalneeds" : "Breakfast"
                }""" ;

       var responseToValidate = given().body(body)
               .header("Content-Type" , "application/json")
               .header("Cookie" , "token="  + accessToken )
               .header("Authorisation" , "Basic")
               .log().all()
               .when().put(endpoint).then();
       responseToValidate.body("firstname" , equalTo("James"));
   }

    @Test(priority = 2)
    public void testGetBooking ()
    {
        String endpoint = "https://restful-booker.herokuapp.com/booking/" + bookingId;
        var responseToValidate = given()
                .header("Accept" , "application/json")
                .log().all()
                .when()
                .get(endpoint)
                .then();
        responseToValidate.body("firstname" , equalTo("James"));
    }
    @Test(priority = 3)
    public void testDeleteBooking ()
    {

        String endpoint = "https://restful-booker.herokuapp.com/booking/" + bookingId;
        var responseToValidate  = given()
                .header("Content-Type" , "application/json")
                .header("Cookie" , "token="  + accessToken)
                .header("Authorisation" , "Basic")
                .log().all()
                .when()
                .delete(endpoint)
                .then();

        Response response = responseToValidate.extract().response();
        responseToValidate.statusCode(201);

        Assert.assertEquals(response.asString() , "Created", "Created should be displayed") ;
    }
}
