# JSON Parser & Serializer

## Описание

Данный проект реализует JSON-парсер и сериализатор, не используя сторонние библиотеки. Основной функционал включает:

- Парсинг JSON-строк в Java объекты.
- Преобразование JSON в структуры `Map<String, Object>`.
- Преобразование JSON в объекты заданного класса.
- Сериализацию Java объектов обратно в формат JSON.

Поддерживаемые типы данных:
- Примитивные типы, объекты-обёртки (boxing types), `null`.
- Массивы и коллекции.
- Вложенные объекты.

Ограничения (по ТЗ):
- Циклические зависимости не поддерживаются.
- Типы, которые не могут быть представлены в JSON, не поддерживаются.

## Задание (ТЗ)

1. **JSON Parser**:
   - **Не использовать внешние библиотеки** для парсинга.
   - **Читать строку JSON** и преобразовывать её в:
     - Java объект.
     - `Map<String, Object>`.
     - Объект указанного класса.
   - **Конвертировать Java объект в строку JSON**.

2. **Поддержка**:
   - Классы с полями (примитивы, обёртки, `null`, массивы, вложенные классы).
   - Массивы и коллекции.

3. **Ограничения** (можно пропустить реализацию):
   - Циклические зависимости.
   - Типы, которые не могут быть представлены в JSON.

## Структура проекта

### 1. **JsonParser.java**

Класс для разбора JSON, который принимает список токенов (`List<JsonToken>`) и предоставляет методы:

- `parse()`: разбирает JSON-объект и возвращает его в виде `Map<String, Object>`.
- `parse(Class<T>)`: разбирает JSON-объект и возвращает объект указанного класса.
- `parseArray()`: разбирает JSON-массив.
- Поддержка работы с числами, строками, логическими значениями, null, объектами и массивами.

### 2. **JsonSerializer.java**

Класс для преобразования Java объектов в строку JSON. Предоставляет методы:

- `serializeToJson(Object obj)`: сериализует объект в строку JSON.
- Поддержка массивов, коллекций, вложенных объектов и примитивных типов.

### 3. **JsonToken.java**

Запись, представляющая токен JSON. Содержит типы токенов, такие как:

- `Number`, `String`, `Null`, `True`, `False`, `BracketObjectLeft`, `BracketObjectRight`, `BracketArrayLeft`, `BracketArrayRight`, `Comma`, `Colon`.

### 4. **ObjectMapper.java**

Класс для маппинга JSON в объекты заданного класса. Основные методы:

- `mapToObject(Map<String, Object>, Class<T>)`: маппинг JSON-карты в объект класса `T`.
- Поддержка примитивных типов, списков и вложенных объектов.

### 5. **JsonParserTest.java**

Тесты для проверки корректности работы `JsonParser`. Покрывают следующие сценарии:

- Парсинг пустого объекта.
- Парсинг простого объекта (ключ-значение).
- Парсинг массивов.
- Парсинг вложенных объектов.
- Парсинг логических значений и `null`.
- Проверка на ошибки при некорректном JSON.

## Как использовать

### Пример использования парсера:

```java
List<JsonToken> tokens = List.of(
    new JsonToken(JsonToken.Type.BracketObjectLeft, "{"),
    new JsonToken(JsonToken.Type.String, "key"),
    new JsonToken(JsonToken.Type.Colon, ":"),
    new JsonToken(JsonToken.Type.String, "value"),
    new JsonToken(JsonToken.Type.BracketObjectRight, "}")
);

JsonParser parser = new JsonParser(tokens);
Map<String, Object> result = (Map<String, Object>) parser.parse();
System.out.println(result.get("key")); // Output: value
```
### Пример использования сериализатора:
```java
MyClass myObject = new MyClass();
String jsonString = JsonSerializer.serializeToJson(myObject);
System.out.println(jsonString);
```
### Тесты:
Для запуска тестов используйте JUnit. Пример тестов можно найти в файле `JsonParserTest.java`.
