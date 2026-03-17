package utils

import (
	"strings"
)

// IsMoviesPath проверяет, относится ли путь к фильмам
func IsMoviesPath(path string) bool {
	return strings.HasPrefix(path, "/api/movies") || strings.HasPrefix(path, "/movies")
}

// BuildTargetURL строит целевой URL для проксирования
func BuildTargetURL(baseURL, path, rawQuery string) string {
	target := baseURL + path
	if rawQuery != "" {
		target += "?" + rawQuery
	}
	return target
}
