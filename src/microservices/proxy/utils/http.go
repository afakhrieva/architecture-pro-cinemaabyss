package utils

import (
	"io"
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

// CopyResponse копирует тело ответа
func CopyResponse(w io.Writer, body io.ReadCloser) (int64, error) {
	defer body.Close()
	return io.Copy(w, body)
}
