package handlers

import (
	"encoding/json"
	"net/http"
	"time"

	"proxy-service/config"
)

// HealthHandler обрабатывает запросы health check
type HealthHandler struct {
	startTime time.Time
	config    *config.Config
}

// NewHealthHandler создает новый HealthHandler
func NewHealthHandler(cfg *config.Config) *HealthHandler {
	return &HealthHandler{
		startTime: time.Now(),
		config:    cfg,
	}
}

// Handle обрабатывает запросы к /health
func (h *HealthHandler) Handle(w http.ResponseWriter, r *http.Request) {
	response := map[string]interface{}{
		"status":    "ok",
		"service":   "cinemaabyss-proxy",
		"version":   "1.0.0",
		"uptime":    time.Since(h.startTime).String(),
		"timestamp": time.Now().Unix(),
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(response)
}

// Status обрабатывает запросы к /proxy-status
func (h *HealthHandler) Status(w http.ResponseWriter, r *http.Request) {
	status := map[string]interface{}{
		"service": "cinemaabyss-proxy",
		"version": "1.0.0",
		"uptime":  time.Since(h.startTime).String(),
		"migration": map[string]interface{}{
			"gradual_migration": h.config.GradualMigration,
			"movies_percent":    h.config.MoviesMigrationPercent,
		},
		"endpoints": []string{
			"/health",
			"/proxy-status",
			"/api/movies/* (with migration)",
			"/* (monolith)",
		},
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(status)
}
