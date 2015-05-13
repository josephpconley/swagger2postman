# swagger2postman
Create a Postman collection from Swagger documentation

##Motivation

[Swagger UI](https://github.com/swagger-api/swagger-ui) provides a nice way to interact with an API documented by the [Swagger specification](https://github.com/swagger-api/swagger-spec).  
But if you're working with an identical API across multiple environments, or you want to test edge cases that Swagger UI doesn't support 
(like omitting a required query parameter), Swagger UI comes up a bit short.  

[Postman](https://www.getpostman.com/) to the rescue!  Using Postman you can define variables for multiple environments and have more control over request generation. 
This library takes Swagger documentation served in JSON and converts it into a JSON collection which can be imported directly into Postman.

##Demo

Try out a web version at [http://app.josephpconley.com/swagger2postman](http://app.josephpconley.com/swagger2postman)
 
##Command line

`sbt run <host> <collectionName> [<key=value> ... ]`

##Examples

A simple example would be to explicitly pass the 

##Blog post
