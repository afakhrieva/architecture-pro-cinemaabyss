package ru.yandex.practicum.arch.cinema.events.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.yandex.practicum.arch.cinema.events.dto.HealthResponse

@RestController
@RequestMapping("/api/events")
class HealthController {

    @GetMapping("/health")
    fun health(): HealthResponse {
        return HealthResponse(status = true)
    }
}