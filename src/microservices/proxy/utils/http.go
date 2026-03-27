package utils

import (
	"net/http"
)

// CopyHeaders копирует заголовки
func CopyHeaders(dst, src http.Header) {
	for key, values := range src {
		for _, value := range values {
			dst.Add(key, value)
		}
	}
}
