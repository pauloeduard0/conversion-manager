package br.inatel.conversionmanager.controller;

import br.inatel.conversionmanager.model.dto.ConversionDto;
import br.inatel.conversionmanager.model.dto.ErrorDto;
import br.inatel.conversionmanager.model.dto.ExchangeRateResponse;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyJsonComponent {

    // Serializer for ConversionDto
    public static class ConversionDtoSerializer extends JsonSerializer<ConversionDto> {

        @Override
        public void serialize(ConversionDto value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
            jgen.writeStartObject();
            jgen.writeStringField("baseCurrency", value.baseCurrency());
            jgen.writeNumberField("amount", value.amount());
            jgen.writeStringField("to", value.to());
            jgen.writeNumberField("convertedAmount", value.convertedAmount());
            jgen.writeStringField("date", value.date().toString());
            jgen.writeEndObject();
        }
    }

    // Deserializer for ConversionDto
    public static class ConversionDtoDeserializer extends JsonDeserializer<ConversionDto> {

        @Override
        public ConversionDto deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = jsonParser.getCodec();
            JsonNode tree = codec.readTree(jsonParser);
            String baseCurrency = tree.get("baseCurrency").textValue();
            BigDecimal amount = tree.get("amount").decimalValue();
            String to = tree.get("to").textValue();
            BigDecimal convertedAmount = tree.get("convertedAmount").decimalValue();
            LocalDate date = LocalDate.parse(tree.get("date").textValue());
            return new ConversionDto(baseCurrency, amount, to, convertedAmount, date);
        }
    }

    // Serializer for ExchangeRateResponse
    public static class ExchangeRateResponseSerializer extends JsonSerializer<ExchangeRateResponse> {

        @Override
        public void serialize(ExchangeRateResponse value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
            jgen.writeStartObject();
            jgen.writeNumberField("timestamp", value.timestamp());
            jgen.writeStringField("base", value.base());
            jgen.writeBooleanField("success", value.success());
            jgen.writeObjectField("rates", value.rates());
            jgen.writeStringField("date", value.date());
            jgen.writeBooleanField("historical", value.historical());
            jgen.writeEndObject();
        }
    }

    // Deserializer for ExchangeRateResponse
    public static class ExchangeRateResponseDeserializer extends JsonDeserializer<ExchangeRateResponse> {

        @Override
        public ExchangeRateResponse deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = jsonParser.getCodec();
            JsonNode tree = codec.readTree(jsonParser);
            long timestamp = tree.get("timestamp").longValue();
            String base = tree.get("base").textValue();
            boolean success = tree.get("success").booleanValue();
            JsonNode ratesNode = tree.get("rates");
            Map<String, BigDecimal> rates = new HashMap<>();
            for (Iterator<Map.Entry<String, JsonNode>> it = ratesNode.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                rates.put(entry.getKey(), entry.getValue().decimalValue());
            }
            String date = tree.get("date").textValue();
            boolean historical = tree.get("historical").booleanValue();
            return new ExchangeRateResponse(timestamp, base, success, rates, date, historical);
        }
    }

    // Serializer for ErrorDto
    public static class ErrorDtoSerializer extends JsonSerializer<ErrorDto> {

        @Override
        public void serialize(ErrorDto value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
            jgen.writeStartObject();
            jgen.writeStringField("type", value.type());
            jgen.writeStringField("title", value.title());
            jgen.writeNumberField("status", value.status());
            jgen.writeStringField("detail", value.detail());
            jgen.writeStringField("instance", value.instance());
            jgen.writeEndObject();
        }
    }

    // Deserializer for ErrorDto
    public static class ErrorDtoDeserializer extends JsonDeserializer<ErrorDto> {

        @Override
        public ErrorDto deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = jsonParser.getCodec();
            JsonNode tree = codec.readTree(jsonParser);
            String type = tree.get("type").textValue();
            String title = tree.get("title").textValue();
            int status = tree.get("status").intValue();
            String detail = tree.get("detail").textValue();
            String instance = tree.get("instance").textValue();
            return new ErrorDto(type, title, status, detail, instance);
        }
    }
}