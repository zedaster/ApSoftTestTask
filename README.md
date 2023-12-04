# Test task for AP Soft

## How to run

### Locally
1. Run `mvn install` to install all needed dependencies
2. Run `./mvnw spring-boot:run` to start the server

### Using Docker image
1. Run `mvn install` to install all needed dependencies
2. Run `mvn package` to compile the project into a jar
3. Run `docker build -t ap-soft-test .` to build the image

After creating Docker image you can create and run a container using 
`docker run -d -p 8080:8080 ap-soft-test`

## Endpoints

The project uses 8080 port to serve

#### POST `/chapters/parse`
Request data: 
* `file` - UTF-8 text file (must be less than 1 MB)

Response example:
```
{
    "title": "GREATEST MAN IN ALIVE",
    "content": "This is a story about the greatest main in alive",
    "children": [
        {
            "title": "Chapter one",
            "content": "this story about awesome dude that call name is Jack",
            "children": [
                {
                    "title": "Jack's characteristics",
                    "content": null,
                    "children": [
                        {
                            "title": "height: 71 inch",
                            "content": null,
                            "children": []
                        },
                        {
                            "title": "weight: 190 pounds",
                            "content": null,
                            "children": []
                        }
                    ]
                }
            ]
        }
    ]
}
``` 

Response code: `200 OK`