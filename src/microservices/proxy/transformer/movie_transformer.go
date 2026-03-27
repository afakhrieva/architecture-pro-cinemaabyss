package transformer

import (
	"encoding/json"
	"io"
	"log"

	"proxy-service/models"
)

// MovieTransformer - заглушка для демонстрации трансформации
type MovieTransformer struct{}

// NewMovieTransformer создает новый трансформер
func NewMovieTransformer() *MovieTransformer {
	return &MovieTransformer{}
}

// TransformStream - заглушка, которая просто передает данные
func (t *MovieTransformer) TransformStream(body io.ReadCloser, w io.Writer) error {
	defer body.Close()

	data, err := io.ReadAll(body)
	if err != nil {
		return err
	}

	// Проверяем, что данные - валидный JSON
	var movies []models.Movie
	if err := json.Unmarshal(data, &movies); err != nil {
		var movie models.Movie
		if err2 := json.Unmarshal(data, &movie); err2 != nil {
			log.Printf("[Transformer] Данные не являются фильмом, передаем как есть")
		}
	}

	// Просто передаем данные без изменений
	_, err = w.Write(data)
	return err
}
