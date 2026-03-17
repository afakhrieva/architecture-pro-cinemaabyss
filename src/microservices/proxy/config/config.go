package config

import (
	"os"
	"strconv"
)

// ServiceConfig представляет конфигурацию сервиса
type ServiceConfig struct {
	Prefix string
	Target string
}

// Config содержит всю конфигурацию приложения
type Config struct {
	Port                   string
	MonolithURL            string
	MoviesServiceURL       string
	EventsServiceURL       string
	GradualMigration       bool
	MoviesMigrationPercent int
}

// Load загружает конфигурацию из переменных окружения

func Load() *Config {
	cfg := &Config{
		Port:                   getEnv("PORT", "8000"),
		MonolithURL:            getEnv("MONOLITH_URL", "http://monolith:8080"),
		MoviesServiceURL:       getEnv("MOVIES_SERVICE_URL", "http://movies-service:8081"),
		EventsServiceURL:       getEnv("EVENTS_SERVICE_URL", "http://events-service:8082"),
		GradualMigration:       getEnvAsBool("GRADUAL_MIGRATION", false),
		MoviesMigrationPercent: getEnvAsInt("MOVIES_MIGRATION_PERCENT", 0),
	}

	// Ограничиваем процент миграции
	if cfg.MoviesMigrationPercent < 0 {
		cfg.MoviesMigrationPercent = 0
	} else if cfg.MoviesMigrationPercent > 100 {
		cfg.MoviesMigrationPercent = 100
	}

	return cfg
}

// Helper functions
func getEnv(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}

func getEnvAsBool(key string, defaultValue bool) bool {
	if value := os.Getenv(key); value != "" {
		if boolVal, err := strconv.ParseBool(value); err == nil {
			return boolVal
		}
	}
	return defaultValue
}

func getEnvAsInt(key string, defaultValue int) int {
	if value := os.Getenv(key); value != "" {
		if intVal, err := strconv.Atoi(value); err == nil {
			return intVal
		}
	}
	return defaultValue
}
