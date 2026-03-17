package utils

import (
	"crypto/md5"
	"encoding/binary"
)

// ShouldRouteToNewService определяет, должен ли запрос идти в новый сервис
func ShouldRouteToNewService(percent int, path, method string) bool {
	if percent <= 0 {
		return false
	}
	if percent >= 100 {
		return true
	}

	// Создаем ключ для консистентного хеширования
	key := path + ":" + method

	// Вычисляем хеш
	hash := md5.Sum([]byte(key))
	value := binary.BigEndian.Uint64(hash[:8])

	// Определяем, попадает ли запрос в процент миграции
	return value%100 < uint64(percent)
}
