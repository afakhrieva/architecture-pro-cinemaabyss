package utils

import (
	"math/rand/v2"
)

// ShouldRouteToNewService определяет, должен ли запрос идти в новый сервис
func ShouldRouteToNewService(percent int) bool {
	if percent <= 0 {
		return false
	}
	if percent >= 100 {
		return true
	}

	value := rand.IntN(100)

	// Определяем, попадает ли запрос в процент миграции
	return value%100 < percent
}