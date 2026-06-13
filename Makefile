.PHONY: up down logs clean

# Поднять базу данных в фоновом режиме
up:
	docker-compose up -d

# Остановить базу данных
down:
	docker-compose down

# Посмотреть логи базы данных
logs:
	docker-compose logs -f postgres

# Удалить контейнеры и очистить данные (volume)
clean:
	docker-compose down -v