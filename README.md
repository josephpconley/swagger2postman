# swagger2postman

[![Join the chat at https://gitter.im/josephpconley/swagger2postman](https://badges.gitter.im/josephpconley/swagger2postman.svg)](https://gitter.im/josephpconley/swagger2postman?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
Create a Postman collection from Swagger documentation

##Motivation

[Swagger UI](https://github.com/swagger-api/swagger-ui) provides a nice way to interact with an API documented by the [Swagger specification](https://github.com/swagger-api/swagger-spec).  
But if you're working with an identical API across multiple environments, or you want to test edge cases that Swagger UI doesn't support 
(like omitting a required query parameter), Swagger UI comes up a bit short.  

[Postman](https://www.getpostman.com/) to the rescue!  Using Postman you can define variables for multiple environments and have more control over request generation. 
This library takes Swagger documentation served in JSON and converts it into a JSON collection which can be imported directly into Postman.

##Command line

To convert a Swagger 2.0 JSON file to a valid Postman collection:

    sbt runMain com.josephpconley.swagger2postman.app.v2.Swagger2PostmanApp <filename> <collectionName> [<headerKey=headerValue> ... ]

To convert a Swagger 1.2 hosted endpoint to a valid Postman collection:

    sbt runMain com.josephpconley.swagger2postman.app.v12.Swagger2PostmanApp <host> <collectionName> [<headerKey=headerValue> ... ]


##Demo

Try out an online version at [http://app.josephpconley.com/swagger2postman](http://app.josephpconley.com/swagger2postman)

Or using `curl` to convert a Swagger 2.0 document into a Postman JSON import file:
    
    curl -X POST --data "@v2petstore-swagger.json" "http://app.josephpconley.com/swagger20?name=my_collection&header_key=header_value" --header "Content-Type:application/json" > my_collection.json 


##Multiple environments

To take advantage of multi-environment testing, I would first run swagger2postman against a hosted Swagger doc.
Then I do a simple Find/Replace, replacing the target host with a handlebars variable like `{{host}}`.
Then I create environments in Postman that define a value for the config key `host`.
Toggling these environments with your imported collection will let you seamlessly test your API in different environments.
 
You can also use environment variables for authentication.  If your API uses a header for authentication, then pass a `headerKey`=`{{headerValue}}`
so that all endpoints get a global authentication header with an environment-dependent value.

##Release Notes
### 1.1
- Initial support for Swagger 2.0

### 1.0
- Initial support for Swagger 1.2