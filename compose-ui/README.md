# ui-kit

Этот модуль предназначен для всего что связано с UI-китом.
Сюда стоит складывать стили, темы, цвета, иконки (кроме специфичных для проекта), кастомные вьюхи (которые можно переиспользовать на других проектах).

Специфичные для приложения стили, настройки тем и кастомные вьюхи всё так же следует помещать в модуль `app`.

## Демо

Чтобы разработчики видели все доступные UI-компоненты и могли посмотреть как они выглядят в текущей теме, реализована функция демонстрации UI-kit'а.
Чтобы посмотреть демо, нужно запустить `UiKitDemoActivity`:

```kotlin
startActivity(UiKitDemoActivity.newIntent(context))
```

По умолчанию в демо есть два пункта: Типографика и Кнопки.
Остальные необходимые пункты нужно добавлять самостоятельно.
Чтобы добавить новый пункт, нужно:

1. Создать экран с демонстрационными данными на Compose.
2. Добавить название пункта меню в `debug/res/strings.xml`
3. Добавить созданный экран в граф навигации `UiKitGraph` по примеру существующих.
4. Добавить в метод `getItems()` экрана `TocScreen` пункт для отображения и навигации на новый экран демонстрационных данных.


[mtb]: https://github.com/material-components/material-components-android-examples/tree/develop/MaterialThemeBuilder
[shapes]: https://material.io/design/shape/about-shape.html#shape-customization-tool
[colors]: https://material.io/design/color/the-color-system.html#color-theme-creation
[e2e]: https://developer.android.com/training/gestures/edge-to-edge
