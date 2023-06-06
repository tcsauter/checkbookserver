docker build -t checkbookserver:v1 .
docker run -d -p 8080:8080 checkbookserver:v1
