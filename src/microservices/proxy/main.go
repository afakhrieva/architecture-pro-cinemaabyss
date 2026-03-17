package main

import (
	"fmt"
	"log"
	"net/http"

	"proxy-service/config"
	"proxy-service/handlers"
)

func main() {
	// Загружаем конфигурацию
	cfg := config.Load()

	// Создаем обработчики
	healthHandler := handlers.NewHealthHandler(cfg)
	moviesHandler := handlers.NewMoviesHandler(cfg)

	// Регистрируем маршруты
	http.HandleFunc("/health", healthHandler.Handle)
	http.HandleFunc("/proxy-status", healthHandler.Status)

	// Обработчик для всех запросов
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		log.Printf("[Request] %s %s", r.Method, r.URL.Path)

		// Все запросы к movies обрабатываем специально
		if handlers.IsMoviesPath(r.URL.Path) {
			moviesHandler.Handle(w, r)
			return
		}

		// Все остальное идет в монолит
		moviesHandler.ProxyToMonolith(w, r)
	})

	fmt.Printf("API Gateway запущен на порту %s\n", cfg.Port)
	fmt.Printf("Миграция movies: %v, процент: %d%%\n",
		cfg.GradualMigration,
		cfg.MoviesMigrationPercent)

	log.Fatal(http.ListenAndServe(":"+cfg.Port, nil))
}
