# swagger2postman
Create a Postman collection from Swagger documentation

##Motivation

[Swagger UI](https://github.com/swagger-api/swagger-ui) provides a nice way to interact with an API documented by the [Swagger specification](https://github.com/swagger-api/swagger-spec).  
But if you're working with an identical API across multiple environments, or you want to test edge cases that Swagger UI doesn't support 
(like omitting a required query parameter), Swagger UI comes up a bit short.  

[Postman](https://www.getpostman.com/) to the rescue!  Using Postman you can define variables for multiple environments and have more control over request generation. 
This library takes Swagger documentation served in JSON and converts it into a JSON collection which can be imported directly into Postman.

##Demo

Try out an online version at [http://app.josephpconley.com/swagger2postman](http://app.josephpconley.com/swagger2postman)
 
##Command line

`sbt run <host> <collectionName> [<key=value> ... ]`

##Multiple environments

To take advantage of multi-environment testing, simply pass a handlebars variable like `{{host}}` as the hostname.
Then, create environments in Postman that define a value for the config key `host`.  Toggling these environments with your imported collection will let you seamlessly test 
your API in different environments.
 
You can also take this approach for authentication.  If your API uses a header for authentication, then pass a `headerKey`=`{{headerValue}}`
so that all endpoints get a global authentication header with an environment-sensitive value.

##Next Steps
This currently supports Swagger 1.2.  There appears to be Swagger 2.0 import functionality in the current version of Postman, 
though I haven't gotten it to work, so if there's enough demand for a Swagger 2.0 version, let me know in the issues section (or add it yourself!)