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
	proxyHandler := handlers.NewProxyHandler(cfg)

	// Регистрируем маршруты
	http.HandleFunc("/health", healthHandler.Handle)
	http.HandleFunc("/proxy-status", healthHandler.Status)

	http.HandleFunc("/api/movies/health", proxyHandler.MoviesHealth)
	// Общий обработчик для всех остальных /movies* путей
	http.HandleFunc("/api/movies", proxyHandler.HandleMoviesMigration)
	http.HandleFunc("/api/movies/", proxyHandler.HandleMoviesMigration)

	// Запросы в events-service
	http.HandleFunc("/api/events/", proxyHandler.ProxyToEvents)

	// Монолит обрабатывает всё остальное
	http.HandleFunc("/", proxyHandler.ProxyToMonolith)

	fmt.Printf("API Gateway запущен на порту %s\n", cfg.Port)
	fmt.Printf("Миграция movies: %v, процент: %d%%\n",
		cfg.GradualMigration,
		cfg.MoviesMigrationPercent)

	log.Fatal(http.ListenAndServe(":"+cfg.Port, nil))
}
