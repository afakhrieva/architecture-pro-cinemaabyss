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

	http.HandleFunc("/api/movies/health", moviesHandler.MoviesHealth)

	// Общий обработчик для всех остальных /movies* путей
	http.HandleFunc("/api/movies", func(w http.ResponseWriter, r *http.Request) {
		moviesHandler.HandleMoviesMigration(w, r)
	})

	// Монолит обрабатывает всё остальное
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		log.Printf("[Monolith] %s %s", r.Method, r.URL.Path)
		moviesHandler.ProxyToMonolith(w, r)
	})

	fmt.Printf("API Gateway запущен на порту %s\n", cfg.Port)
	fmt.Printf("Миграция movies: %v, процент: %d%%\n",
		cfg.GradualMigration,
		cfg.MoviesMigrationPercent)

	log.Fatal(http.ListenAndServe(":"+cfg.Port, nil))
}
