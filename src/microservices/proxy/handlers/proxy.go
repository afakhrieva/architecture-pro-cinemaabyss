package handlers

import (
	"io"
	"log"
	"net/http"

	"proxy-service/config"
	"proxy-service/transformer"
	"proxy-service/utils"
)

// MoviesHandler обрабатывает все прокси-запросы
type MoviesHandler struct {
	config      *config.Config
	client      *http.Client
	transformer *transformer.MovieTransformer
}

// NewMoviesHandler создает новый MoviesHandler
func NewMoviesHandler(cfg *config.Config) *MoviesHandler {
	return &MoviesHandler{
		config:      cfg,
		client:      &http.Client{},
		transformer: transformer.NewMovieTransformer(),
	}
}

// HandleMoviesMigration обрабатывает запросы к movies с миграцией
func (h *MoviesHandler) HandleMoviesMigration(w http.ResponseWriter, r *http.Request) {
	// Определяем, куда направить запрос
	targetService := h.determineTarget(r.URL.Path, r.URL.RawQuery, r.Method)

	var targetURL string
	var shouldTransform bool

	if targetService == "movies-service" {
		targetURL = BuildTargetURL(h.config.MoviesServiceURL, r.URL.Path, r.URL.RawQuery)
		shouldTransform = true
		log.Printf("[Movies Migration] -> новый сервис (%d%%): %s", h.config.MoviesMigrationPercent, targetURL)
	} else {
		targetURL = BuildTargetURL(h.config.MonolithURL, r.URL.Path, r.URL.RawQuery)
		shouldTransform = false
		log.Printf("[Movies Migration] -> монолит (%d%%): %s", 100-h.config.MoviesMigrationPercent, targetURL)
	}

	// Единый метод прокси с параметром трансформации
	h.proxyRequest(w, r, targetURL, shouldTransform)

	// Добавляем заголовок с информацией о маршрутизации
	w.Header().Set("X-Service-Used", targetService)
	w.Header().Set("X-Migration-Percent", string(rune(h.config.MoviesMigrationPercent)))
}

// MoviesHealth обрабатывает запросы к /movies/health
func (h *MoviesHandler) MoviesHealth(w http.ResponseWriter, r *http.Request) {
	targetURL := BuildTargetURL(h.config.MoviesServiceURL, r.URL.Path, r.URL.RawQuery)
	log.Printf("[Movies Health] -> (%d%%): %s", h.config.MoviesMigrationPercent, targetURL)

	h.proxyRequest(w, r, targetURL, false)
}

// ProxyToMonolith проксирует любые запросы в монолит (без миграции)
func (h *MoviesHandler) ProxyToMonolith(w http.ResponseWriter, r *http.Request) {
	targetURL := BuildTargetURL(h.config.MonolithURL, r.URL.Path, r.URL.RawQuery)
	log.Printf("[Monolith] %s %s -> %s", r.Method, r.URL.Path, targetURL)

	// Все запросы в монолит идут без трансформации
	h.proxyRequest(w, r, targetURL, false)

	w.Header().Set("X-Service-Used", "monolith")
}

// determineTarget определяет целевой сервис для movies migration
func (h *MoviesHandler) determineTarget(path, rawQuery, method string) string {
	if !h.config.GradualMigration {
		return "monolith"
	}

	// Только для GET запросов применяем процентную миграцию
	if method == http.MethodGet && utils.ShouldRouteToNewService(h.config.MoviesMigrationPercent, path, rawQuery, method) {
		return "movies-service"
	}

	// POST, PUT, DELETE пока в монолите
	return "monolith"
}

// proxyRequest единый метод для всех прокси-запросов
func (h *MoviesHandler) proxyRequest(w http.ResponseWriter, r *http.Request, targetURL string, shouldTransform bool) {
	// Создаем запрос
	proxyReq, err := http.NewRequest(r.Method, targetURL, r.Body)
	if err != nil {
		http.Error(w, "Error creating proxy request", http.StatusInternalServerError)
		log.Printf("Error creating proxy request: %v", err)
		return
	}

	// Копируем заголовки
	utils.CopyHeaders(proxyReq.Header, r.Header)
	proxyReq.Header.Set("X-Proxy-By", "cinemaabyss-proxy")

	if shouldTransform {
		proxyReq.Header.Set("X-Transform", "true")
	}

	// Отправляем запрос
	resp, err := h.client.Do(proxyReq)
	if err != nil {
		http.Error(w, "Service unavailable", http.StatusServiceUnavailable)
		log.Printf("Error proxying request to %s: %v", targetURL, err)
		return
	}
	defer resp.Body.Close()

	// Копируем заголовки ответа
	utils.CopyHeaders(w.Header(), resp.Header)

	// Применяем трансформацию если нужно
	if shouldTransform {
		log.Printf("[Proxy] Applying transformation for %s %s", r.Method, r.URL.Path)
		if err := h.transformer.TransformStream(resp.Body, w); err != nil {
			log.Printf("Error in transformer: %v", err)
			// В случае ошибки пробуем отдать оригинал
			w.WriteHeader(resp.StatusCode)
			return
		}
	} else {
		// Просто копируем тело без трансформации
		w.WriteHeader(resp.StatusCode)
		if _, err := io.Copy(w, resp.Body); err != nil {
			log.Printf("Error copying response: %v", err)
		}
	}

	log.Printf("[Proxy] %s %s -> %s %d (transform=%v)",
		r.Method, r.URL.Path, targetURL, resp.StatusCode, shouldTransform)
}

// BuildTargetURL строит целевой URL для проксирования
func BuildTargetURL(baseURL, path, rawQuery string) string {
	target := baseURL + path
	if rawQuery != "" {
		target += "?" + rawQuery
	}
	return target
}
