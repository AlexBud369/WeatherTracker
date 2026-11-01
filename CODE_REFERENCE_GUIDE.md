# Навигатор по коду лабораторных работ (Шпаргалка для ответа и защиты)

Данный документ содержит точные указания, в каких файлах, классах и функциях находятся ключевые элементы и механизмы приложения, требуемые по темам лабораторных работ.

> **Стандарт оформления кода проекта:**
> - Код написан без комментариев (чистый самодокументируемый код).
> - Все функции декомпозированы и не превышают 15–20 строк.
> - Названия всех классов, функций и переменных написаны понятным языком в соответствии с их назначением.

---

## 📌 Лабораторная работа №8: «Intent и навигация между Activity» (ветка `lab8`)

| Требование / Тема работы | Файл | Класс / Функция / Разметка | Пояснение, что показать преподавателю |
|---|---|---|---|
| **Явный Intent (Explicit Intent)** | `MainActivity.kt` | `setupNavigationButtons()` | Создание `Intent(this, WeatherDetailActivity::class.java)` и вызов `startActivity(intent)`. |
| **Передача данных (Intent.putExtra)** | `MainActivity.kt`, `ReviewsActivity.kt` | `setupNavigationButtons()`, `openViewReviewActivity()` | Вызов `intent.putExtra(WeatherDetailActivity.EXTRA_CITY, currentCity)` для передачи города и данных отзыва. |
| **Извлечение переданных данных** | `WeatherDetailActivity.kt`, `ViewReviewActivity.kt` | `readCityFromIntent()`, `displayData()` | Извлечение через `intent.getStringExtra(EXTRA_CITY)` и `intent.getLongExtra(EXTRA_REVIEW_ID, 0L)`. |
| **Двунаправленная навигация (кнопка Назад)** | `WeatherDetailActivity.kt`, `MapActivity.kt` | `setupListeners()`, `btnBack.setOnClickListener` | Вызов метода `finish()` для закрытия текущей Activity и возврата к предыдущей в стеке. |
| **Запуск Activity с ожиданием результата** | `MainActivity.kt`, `ReviewsActivity.kt` | `addReviewLauncher = registerForActivityResult(...)` | Регистрация `ActivityResultLauncher` по современному контракту `StartActivityForResult()`. |
| **Возврат результата из дочерней Activity** | `AddReviewActivity.kt` | `returnResult()` | Создание `resultIntent` с данными отзыва, вызов `setResult(Activity.RESULT_OK, resultIntent)` и последующий `finish()`. |
| **Регистрация всех Activity в системе** | `AndroidManifest.xml` | Теги `<activity>` внутри `<application>` | Все 7 экранов объявлены через теги `<activity android:name=".MainActivity" .../>`, `<activity android:name=".WeatherDetailActivity" .../>` и т.д. |
| **Флаги Intent для оптимизации переходов** | `NavigationHelper.kt` | `bindTabClick()` | Использование `intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT` для быстрого переключения вкладок без создания дубликатов Activity. |

---

## 📌 Лабораторная работа №9: «Работа с фрагментами (Fragment)» (ветка `lab9`)

| Требование / Тема работы | Файл | Класс / Функция / Разметка | Пояснение, что показать преподавателю |
|---|---|---|---|
| **Контейнер для фрагментов FrameLayout** | `activity_main.xml` | `<FrameLayout android:id="@+id/fragment_container" .../>` | Посадочное место для динамической подмены фрагментов во весь экран над нижней навигацией. |
| **Single-Activity Architecture** | `AndroidManifest.xml`, `MainActivity.kt` | Тег `<activity android:name=".MainActivity">` | В манифесте осталась строго одна `MainActivity`. Все 7 экранов переведены на фрагменты `Fragment`. |
| **Динамическая замена (FragmentManager, replace, commit)** | `MainActivity.kt` | `openFragment(fragment, addToBackStack)` | Вызов `supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()`. |
| **Стек возврата (addToBackStack)** | `MainActivity.kt` | `openFragment()` | Строка `transaction.addToBackStack(null)`, сохраняющая переход в стеке для корректной работы кнопки «Назад». |
| **Возврат назад по стеку фрагментов** | `ForecastFragment.kt`, `AddReviewFragment.kt`, `ViewReviewFragment.kt`, `MapFragment.kt` | `setupListeners()`, `btnBack.setOnClickListener` | Вызов `parentFragmentManager.popBackStack()`, который возвращает предыдущий фрагмент без закрытия приложения. |
| **Анимации смены фрагментов (setCustomAnimations)** | `MainActivity.kt`, папка `res/anim/` | `openFragment()`, `switchTab()` | Метод `transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, ...)` с кастомными XML-анимациями. |
| **Передача аргументов через Bundle во фрагмент** | `ForecastFragment.kt`, `ViewReviewFragment.kt`, `AddReviewFragment.kt` | Фабричный метод `companion object { fun newInstance(...) }` | Создание `Bundle()`, запись `putString(...)` и привязка к `fragment.arguments = args`. |
| **Чтение аргументов Bundle во фрагменте** | `ForecastFragment.kt`, `ViewReviewFragment.kt` | `readCityArgument()`, `loadReviewFromArguments()` | Чтение через `arguments?.getString(ARG_CITY)` при создании View фрагмента. |
| **Fragment Result API (отправка результата)** | `AddReviewFragment.kt` | `sendFragmentResult()` | Отправка данных из дочернего фрагмента: `parentFragmentManager.setFragmentResult("review_added", resultBundle)`. |
| **Fragment Result API (прием результата)** | `WeatherMainFragment.kt`, `ReviewsListFragment.kt` | `setupResultListener()` | Подписка на результат: `parentFragmentManager.setFragmentResultListener("review_added", viewLifecycleOwner) { _, bundle -> ... }`. |
| **Синхронизация нижней панели с BackStack** | `MainActivity.kt` | `setupNavigationListeners()` | Слушатель `supportFragmentManager.addOnBackStackChangedListener { updateTabForTopFragment() }`, подсвечивающий нужную вкладку при возвратах. |
| **Глобальное сохранение города между экранами** | `WeatherAppState.kt` | Объект `WeatherAppState.currentCity` | Синглтон состояния: город не сбрасывается при переходе между фрагментами и вкладками. |

---

## 📌 Будущие лабораторные работы (шпаргалка для расширения)

### Лабораторная работа №10: «Меню, Toolbar и диалоговые окна (AlertDialog, DatePickerDialog)»
- **Toolbar:** Будет настроен в `activity_main.xml` (`androidx.appcompat.widget.Toolbar`) и подключен через `setSupportActionBar(toolbar)` в `MainActivity`.
- **XML-меню:** Файл `res/menu/main_menu.xml`, методы `onCreateOptionsMenu()` и `onOptionsItemSelected()`.
- **AlertDialog:** Уже используется в `ReviewsListFragment.kt` (функция `confirmDeleteReview()`) для подтверждения удаления отзыва.
- **DatePickerDialog и TimePickerDialog:** Уже используются в `AddReviewFragment.kt` (функция `setupDateTimePickers()`) для выбора даты и времени отзыва.

### Лабораторная работа №11: «Хранение данных: SharedPreferences»
- **AppPreferences:** Вспомогательный класс для сохранения настроек и сериализованных списков через `getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)`.
- **Сохранение примитивов:** `prefs.edit().putString(...).apply()`.
- **Сохранение коллекций (Gson):** Сериализация `List<Review>` в JSON-строку и десериализация при старте приложения.
