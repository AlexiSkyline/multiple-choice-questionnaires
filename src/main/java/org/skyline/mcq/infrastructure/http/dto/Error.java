package org.skyline.mcq.infrastructure.http.dto;

public record Error(String message, String param, String location) {
}
