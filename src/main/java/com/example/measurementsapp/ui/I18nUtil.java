package com.example.measurementsapp.ui;

import com.vaadin.flow.internal.JsonSerializer;
import elemental.json.JsonFactory;
import elemental.json.JsonValue;
import elemental.json.impl.JreJsonFactory;

public class I18nUtil {

  public static <V> V getI18n(String stringJson, Class<V> type) {
    return jsonToClass(getI18nJsonContent(stringJson, type), type);
  }

  public static <V> JsonValue getI18nJsonContent(String stringJson, Class<V> type) {
    final JsonFactory JSON_FACTORY = new JreJsonFactory();
    JsonValue jsonI18n = JSON_FACTORY.parse(stringJson);
    return jsonI18n;
  }

  public static <V> V jsonToClass(JsonValue jsonValue, Class<V> type) {
    return JsonSerializer.toObject(type, jsonValue);
  }
}
