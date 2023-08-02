package br.inatel.conversionmanager.model.dto;

public record ErrorDto(String type, String title, int status, String detail, String instance) {
}

