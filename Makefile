.PHONY: build test up down logs clean restart

build:
	mvn clean package -DskipTests

test:
	mvn test

up:
	docker-compose up -d --build

down:
	docker-compose down

logs:
	docker-compose logs -f library-app

restart: down up

clean:
	mvn clean