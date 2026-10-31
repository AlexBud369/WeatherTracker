# Отчёт по Лабораторной работе №9: «Работа с фрагментами (Fragment)»

**Предметная область:** Мобильное приложение «Прогноз погоды» (`WeatherTracker`)  
**Ветка в Git:** `lab9`  
**Архитектурный паттерн:** Single-Activity Architecture (Хост-Activity + независимые UI-фрагменты)

---

## 1. Цель лабораторной работы

- Освоить архитектурный компонент `Fragment` в Android и его жизненный цикл.
- Освоить управление фрагментами через `FragmentManager` и `FragmentTransaction`.
- Реализовать динамическую замену фрагментов в контейнере `FrameLayout` с анимацией переходов (`setCustomAnimations`).
- Организовать управление стеком переходов (`addToBackStack`, `popBackStack`).
- Настроить обмен данными между фрагментами с помощью аргументов (`Bundle`) и современного API результатов (`Fragment Result API`).
- Провести рефакторинг приложения от многоактивночной модели (Лаб. 8) к модульной архитектуре Single-Activity, адаптировав все 6 экранов предметной области.

---

## 2. Что требовалось по методичке vs Что реализовано в проекте

| Требование методички (Шаги 1–6) | Реализация в проекте «Прогноз погоды» |
|---|---|
| **Шаг 1. Подготовка проекта** (подключение библиотеки Fragment) | Использованы библиотеки `androidx.fragment:fragment` и `androidx.appcompat`, обеспечивающие поддержку `FragmentContainerView`, `FragmentManager` и `Fragment Result API`. |
| **Шаг 2. Первый фрагмент (Список/Главный экран)** | Реализован `WeatherMainFragment` (Главный экран погоды) и `ReviewsListFragment` (Список отзывов через RecyclerView с адаптером `ReviewCardAdapter`). |
| **Шаг 3. Второй фрагмент (Добавление/Детали)** | Реализованы `ForecastFragment` (Экран детального прогноза погоды), `AddReviewFragment` (Экран создания/редактирования отзыва), `ViewReviewFragment` (Экран просмотра отзыва), `SettingsFragment` (Экран настроек) и `MapFragment` (Увеличенная карта). |
| **Шаг 4. Управление фрагментами в Activity** | В `MainActivity` размещён контейнер `FrameLayout (id: fragment_container)`. Создан метод `openFragment(fragment, addToBackStack)`, вызывающий `supportFragmentManager.beginTransaction().replace(...).commit()`. |
| **Шаг 5. Передача данных между фрагментами** | 1) **Входные аргументы**: фабричный метод `newInstance(...)` с передачей параметров через `arguments = Bundle()`.<br>2) **Возврат результата**: `parentFragmentManager.setFragmentResult("review_added", bundle)` в `AddReviewFragment` и прослушивание через `setFragmentResultListener` в `WeatherMainFragment` и `ReviewsListFragment`. |
| **Шаг 6. Анимации переходов (`setCustomAnimations`)** | Созданы XML-ресурсы анимаций в папке `res/anim/` (`slide_in_right`, `slide_out_left`, `slide_in_left`, `slide_out_right`, `fade_in`, `fade_out`). Применяются в `FragmentTransaction`. |
| **Очистка архитектуры** | Удалены устаревшие дочерние Activity (`WeatherDetailActivity`, `ReviewsActivity`, `AddReviewActivity`, `ViewReviewActivity`, `SettingsActivity`, `MapActivity`) и их разметки. В `AndroidManifest.xml` осталась строго одна `MainActivity`. |

---

## 3. Архитектура приложения и экраны (Все 6 экранов + Карта)

1. **Экран 1: `WeatherMainFragment` (Главный экран):**
   - Поисковая строка города.
   - Сводка текущей погоды (температура, ощущение, статус осадков, влажность, давление, скорость ветра).
   - Интерактивная карточка мини-карты (клик открывает `MapFragment`).
   - Кнопка «Показать подробнее» (открывает `ForecastFragment` с передачей выбранного города).
   - Кнопка «Оставить отзыв» (открывает `AddReviewFragment`).
   - Плашка последнего добавленного отзыва (получаемая через `Fragment Result API`, исчезает по клику или таймеру через 4 сек).

2. **Экран 2: `ForecastFragment` (Подробнее о погоде):**
   - Поисковая строка для быстрой смены города.
   - Горизонтальная прокручивающаяся лента с почасовым прогнозом (иконка, время, температура).
   - Вертикальный список карточек прогноза на 7 дней вперед (осадки, ветер, влажность, давление).
   - Кнопка возврата назад через `parentFragmentManager.popBackStack()`.

3. **Экран 3: `ReviewsListFragment` (Мои отзывы):**
   - Панель фильтрации («Все», «Совпало», «Не совпало»), сортировки (новые, старые, по городу) и сброса.
   - `RecyclerView` с карточками отзывов (`ReviewCardAdapter`).
   - Кнопка «+ Отзыв» (открывает `AddReviewFragment`).
   - При клике на карточку — открытие детального просмотра `ViewReviewFragment`.
   - При клике на кнопку карандаша — открытие редактирования отзыва.
   - При клике на корзину — диалог подтверждения удаления с удалением из `ReviewRepository`.
   - Панель пагинации страниц.

4. **Экран 4: `AddReviewFragment` (Оставить / редактировать отзыв):**
   - Поле ввода заголовка отзыва.
   - Выпадающий список выбора города с поиском (`AutoCompleteTextView`).
   - Выбор даты (`DatePickerDialog`) и времени (`TimePickerDialog`).
   - Переключатель совпадения прогноза («Совпало» / «Не совпало»).
   - Текстовый комментарий.
   - Кнопки «Сохранить» (сохранение в `ReviewRepository`, передача в `setFragmentResult`, `popBackStack`) и «Отмена».

5. **Экран 5: `ViewReviewFragment` (Просмотр отзыва):**
   - Отображение полного текста отзыва, города, даты и цветного бейджа статуса.
   - Кнопка «Редактировать отзыв» (переход в `AddReviewFragment` в режиме редактирования).
   - Кнопка возврата назад в стек `popBackStack()`.

6. **Экран 6: `SettingsFragment` (Настройки приложения):**
   - Выбор языка (`Spinner`).
   - Переключатель светлой / тёмной темы.
   - Радио-группы единиц измерения: температура (°C / °F), скорость ветра (м/с / км/ч), атмосферное давление (мм рт. ст. / гПа).
   - Переключатель push-уведомлений о непогоде.
   - Кнопка сохранения настроек.

7. **Экран интерактивной карты: `MapFragment`:**
   - Радар осадков и облачности для выбранного города во весь экран.
   - Кнопки зума и кнопка закрытия карты с возвратом на главный экран.

---

## 4. Ключевые фрагменты кода для отчёта

### 4.1. Контейнер фрагментов и хост-Activity (`MainActivity.kt`)

```kotlin
// Динамическая замена фрагмента с добавлением в BackStack и кастомной анимацией
fun openFragment(fragment: Fragment, addToBackStack: Boolean = true) {
    val transaction = supportFragmentManager.beginTransaction()

    // Лаб 9: Анимации переходов
    transaction.setCustomAnimations(
        R.anim.slide_in_right,
        R.anim.slide_out_left,
        R.anim.slide_in_left,
        R.anim.slide_out_right
    )

    // Замена содержимого контейнера
    transaction.replace(R.id.fragment_container, fragment)

    if (addToBackStack) {
        transaction.addToBackStack(null)
    }

    transaction.commit()
}
```

### 4.2. Передача входных данных во фрагмент через `Bundle` (`ForecastFragment.kt`)

```kotlin
companion object {
    private const val ARG_CITY = "arg_city"

    fun newInstance(city: String = "Москва"): ForecastFragment {
        val fragment = ForecastFragment()
        val args = Bundle().apply {
            putString(ARG_CITY, city)
        }
        fragment.arguments = args
        return fragment
    }
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    // Извлечение аргументов
    currentCity = arguments?.getString(ARG_CITY) ?: "Москва"
    updateCityView(currentCity)
}
```

### 4.3. Возврат результата через `Fragment Result API` (`AddReviewFragment.kt` -> `WeatherMainFragment.kt`)

**Отправка результата из дочернего фрагмента:**
```kotlin
// Сохранение и возврат данных
val resultBundle = Bundle().apply {
    putString("title", title)
    putString("city", city)
    putString("status", status)
    putString("comment", comment)
}
parentFragmentManager.setFragmentResult("review_added", resultBundle)
parentFragmentManager.popBackStack()
```

**Прием результата в вызывающем фрагменте:**
```kotlin
parentFragmentManager.setFragmentResultListener(
    "review_added",
    viewLifecycleOwner
) { _, bundle ->
    val title = bundle.getString("title") ?: "Отзыв"
    val city = bundle.getString("city") ?: currentCity
    val status = bundle.getString("status") ?: "Совпало"

    tvLatestReviewNotice.apply {
        visibility = View.VISIBLE
        text = "✓ Добавлен отзыв: «$title» для города $city ($status)  ✕"
    }
}
```

---

## 5. Что продемонстрировать на скриншотах в отчёте

1. **Скриншот 1 (Главный экран):**
   - Отображение текущей погоды в городе Москва, карточка осадков, кнопки «Показать подробнее» и «Оставить отзыв».
2. **Скриншот 2 (Экран «Подробнее о погоде»):**
   - Отображение почасовой прокручивающейся ленты с иконками (солнце, облако, дождь) и списка прогноза на 7 дней.
3. **Скриншот 3 (Экран «Мои отзывы»):**
   - Список карточек отзывов из `RecyclerView` с цветными бейджами («Совпало» / «Не совпало»), панель сортировки/фильтрации и кнопка «+ Отзыв».
4. **Скриншот 4 (Экран добавления отзыва):**
   - Заполненная форма отзыва: заголовок, выбор города из выпадающего списка, выбранная дата через диалог, статус и текст отзыва.
5. **Скриншот 5 (Экран детального просмотра отзыва):**
   - Карточка просмотра с текстом комментария, датой и кнопками «Редактировать» / «Вернуться к списку».
6. **Скриншот 6 (Экран настроек):**
   - Спиннер выбора языка, переключатель темы, единицы измерения и свитч уведомлений.
7. **Скриншот 7 (Демонстрация передачи данных и возврата назад):**
   - Главный экран с появившейся зеленой плашкой `✓ Добавлен отзыв: «...»`, подтверждающей успешное получение данных через `Fragment Result API`.

---

## 6. Ответы на контрольные вопросы Лабораторной работы №9

### 1. Что такое `Fragment` и чем он отличается от `Activity`?
`Fragment` (фрагмент) — это модульная, переиспользуемая часть пользовательского интерфейса внутри `Activity`.  
**Главные отличия:**
- `Activity` является независимым компонентом приложения, регистрируется в `AndroidManifest.xml` и управляется операционной системой Android.
- `Fragment` не может существовать самостоятельно — он всегда привязан к хост-`Activity` и управляется её `FragmentManager`.
- `Fragment` имеет более расширенный жизненный цикл, связанный с жизненным циклом своего представления (`onCreateView`, `onViewCreated`, `onDestroyView`).
- Фрагменты позволяют создавать гибкие динамические интерфейсы (замена фрагментов в контейнере, адаптация под планшеты с несколькими фрагментами на одном экране).

### 2. Какие основные методы жизненного цикла есть у `Fragment`?
- `onAttach(context)` — фрагмент связывается с контекстом хост-активити.
- `onCreate(savedInstanceState)` — инициализация не-UI логики и данных фрагмента.
- `onCreateView(inflater, container, savedInstanceState)` — создание и инфлейт иерархии View из XML-макета.
- `onViewCreated(view, savedInstanceState)` — вызывается сразу после создания View; основное место для связывания UI-компонентов (`findViewById`), установки слушателей и подписок на `Fragment Result API`.
- `onStart()` / `onResume()` — фрагмент становится видимым и готовым к интерактивному взаимодействию с пользователем.
- `onPause()` / `onStop()` — фрагмент теряет фокус или скрывается.
- `onDestroyView()` — уничтожение ресурсов разметки (View), связанное с кэшированием фрагмента в BackStack.
- `onDestroy()` — полное уничтожение состояния фрагмента.
- `onDetach()` — отсоединение фрагмента от хост-активити.

### 3. Как работает замена фрагментов в контейнере? Чем отличаются `add()` и `replace()`?
- Замена выполняется через транзакцию:  
  `supportFragmentManager.beginTransaction().replace(containerId, fragment).commit()`.
- `add()` добавляет новый фрагмент поверх существующих в контейнере. Предыдущий фрагмент остается активным и видимым в разметке под новым фрагментом.
- `replace()` сначала удаляет (`remove()`) все существующие фрагменты в указанном контейнере, а затем добавляет новый фрагмент. Это освобождает память и предотвращает наложение слоев интерфейса.

### 4. Что делает метод `addToBackStack()` и как обрабатывается нажатие кнопки «Назад»?
- Метод `transaction.addToBackStack(name)` сохраняет текущее состояние транзакции в специальный стек возврата `FragmentManager BackStack`.
- При нажатии системной кнопки «Назад» (или вызове `parentFragmentManager.popBackStack()`):
  - Если в BackStack есть сохраненные транзакции, `FragmentManager` автоматически откатывает последнюю транзакцию, восстанавливая предыдущий фрагмент (вызывая `onCreateView`/`onViewCreated`).
  - Если BackStack пуст, событие «Назад» передается самой `Activity`, что приводит к закрытию приложения или выходу на рабочий стол.

### 5. Какие способы передачи данных между фрагментами существуют в современном Android?
1. **Передача аргументов при создании (от родителя к потомку):** через `Bundle` и метод `fragment.arguments = bundle`. Гарантирует сохранение данных при пересоздании фрагмента системой (при смене конфигурации или нехватке памяти).
2. **Fragment Result API (от дочернего фрагмента к вызывающему):**  
   - Установка слушателя: `setFragmentResultListener(requestKey, lifecycleOwner) { key, bundle -> ... }`.  
   - Передача результата: `setFragmentResult(requestKey, bundle)`.  
   Это современная безопасная замена устаревшим интерфейсам-колбэкам и `setTargetFragment`.
3. **Общая `Shared ViewModel` (в области видимости хост-Activity):**  
   `by activityViewModels()`, где данные хранятся в реактивных потоках (`StateFlow`/`LiveData`), доступных всем фрагментам одной Activity.
