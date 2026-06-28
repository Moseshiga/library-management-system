.PHONY: help build test run-dev up down logs clean restart

help:
	@echo "Available commands:"
	@echo "  make build     - Build application jar"
	@echo "  make test      - Run tests"
	@echo "  make run-dev   - Run application locally with dev profile and H2"
	@echo "  make up        - Start PostgreSQL and application with Docker Compose"
	@echo "  make down      - Stop Docker Compose services"
	@echo "  make logs      - Follow application logs"
	@echo "  make clean     - Clean Maven target and remove Docker volumes"
	@echo "  make restart   - Restart Docker Compose services"

build:
	cd library-management && ./mvnw clean package -DskipTests

test:
	cd library-management && ./mvnw test

run-dev:
	cd library-management && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

up:
	docker compose up -d --build

down:
	docker compose down

logs:
	docker compose logs -f library-app

restart: down up

clean:
	cd library-management && ./mvnw clean
	docker compose down -v