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
| **Регистрация всех Activity в системе** | `AndroidManifest.xml` | Теги `<activity>` внутри `<application>` | Все экраны объявлены через теги `<activity android:name=".MainActivity" .../>`, `<activity android:name=".WeatherDetailActivity" .../>` и т.д. |
| **Флаги Intent для оптимизации переходов** | `NavigationHelper.kt` | `bindTabClick()` | Использование `intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT` для быстрого переключения вкладок без создания дубликатов Activity. |

---

## 📌 Лабораторная работа №9: «Работа с фрагментами (Fragment)» (ветка `lab9`)

| Требование / Тема работы | Файл | Класс / Функция / Разметка | Пояснение, что показать преподавателю |
|---|---|---|---|
| **Контейнер для фрагментов FrameLayout** | `activity_main.xml` | `<FrameLayout android:id="@+id/fragment_container" .../>` | Посадочное место для динамической подмены фрагментов во весь экран над нижней навигацией. |
| **Single-Activity Architecture** | `AndroidManifest.xml`, `MainActivity.kt` | Тег `<activity android:name=".MainActivity">` | В манифесте осталась строго одна `MainActivity`. Все экраны приложения переведены на фрагменты `Fragment`. |
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

## 📌 Лабораторная работа №10: «Меню, Toolbar и диалоговые окна (AlertDialog, DatePickerDialog)» (ветка `lab10`)

| Требование / Тема работы | Файл | Класс / Функция / Разметка | Пояснение, что показать преподавателю |
|---|---|---|---|
| **Отключение стандартного ActionBar** | `res/values/themes.xml` | `<style name="Theme.WeatherTracker" parent="Theme.MaterialComponents.DayNight.NoActionBar">` | Тема с суффиксом `NoActionBar` отключает системную шапку, чтобы использовать кастомный `Toolbar`. |
| **Разметка кастомного Toolbar** | `res/layout/activity_main.xml` | `<androidx.appcompat.widget.Toolbar android:id="@+id/toolbar" .../>` | Элемент `Toolbar` размещен в верхней части главного экрана над `fragment_container`. |
| **Инициализация Toolbar как ActionBar** | `MainActivity.kt` | `setupToolbar()` | `toolbar = findViewById(R.id.toolbar)` и `setSupportActionBar(toolbar)`. |
| **Ресурс XML-меню** | `res/menu/main_menu.xml` | `<menu> ... <item android:id="@+id/action_add" .../> ... </menu>` | Описание пунктов меню действий: Добавить отзыв (`showAsAction="ifRoom"`), Настройки, О приложении. |
| **Инфлейт меню действий (onCreateOptionsMenu)** | `MainActivity.kt` | `onCreateOptionsMenu(menu: Menu?): Boolean` | Метод инфлейтит меню: `menuInflater.inflate(R.menu.main_menu, menu)`. |
| **Обработка нажатий меню (onOptionsItemSelected)** | `MainActivity.kt` | `onOptionsItemSelected(item: MenuItem): Boolean` | Обработка кликов: `R.id.action_add`, `R.id.action_settings`, `R.id.action_about`, а также стрелки `android.R.id.home`. |
| **Динамическое обновление заголовка Toolbar** | `MainActivity.kt` | `updateToolbar(title, showBackButton)`, `configureToolbarForFragment()` | При смене фрагментов `supportActionBar?.title` обновляется (например, «Прогноз погоды», «Прогноз: Москва», «Оставить отзыв»). |
| **Стрелка «Назад» в Toolbar (Up Navigation)** | `MainActivity.kt` | `updateToolbar()`, `toolbar.setNavigationOnClickListener` | `supportActionBar?.setDisplayHomeAsUpEnabled(true)` и вызов `supportFragmentManager.popBackStack()`. |
| **Информационный диалог (AlertDialog)** | `MainActivity.kt` | `showAboutDialog()` | Создание `AlertDialog.Builder(this)` с заголовком «О приложении», текстом и кнопкой «Понятно». |
| **Диалог подтверждения удаления (AlertDialog)** | `ReviewsListFragment.kt` | `confirmDeleteReview(review: Review)` | Диалог `AlertDialog.Builder` с кнопками «Удалить» (удаляет конкретный отзыв из `ReviewRepository`) и «Отмена». |
| **Диалог выбора из списка (SingleChoice AlertDialog)** | `ReviewsListFragment.kt` | `showSortDialog()`, `showFilterDialog()` | `AlertDialog.Builder` с методом `setSingleChoiceItems()` для сортировки (Новые/Старые/По городу) и фильтрации (Все/Совпало/Не совпало). |
| **Диалог выбора даты (DatePickerDialog)** | `AddReviewFragment.kt` | `setupDateTimePickers()` | Создание `DatePickerDialog(requireContext(), { _, year, month, day -> ... }, ...).show()`. Месяц корректируется как `month + 1`. |
| **Диалог выбора времени (TimePickerDialog)** | `AddReviewFragment.kt` | `setupDateTimePickers()` | Создание `TimePickerDialog(requireContext(), { _, hour, minute -> ... }, ...).show()` с форматом `24 часа`. |

---

## 📌 Лабораторная работа №11: «Хранение данных: SharedPreferences» (ветка `lab11`)

| Требование / Тема работы | Файл | Класс / Функция / Разметка | Пояснение, что показать преподавателю |
|---|---|---|---|
| **Подключение библиотеки Gson** | `build.gradle.kts` | `dependencies { implementation("com.google.code.gson:gson:2.11.0") }` | Библиотека Gson для сериализации списков объектов в формат JSON и обратно. |
| **Инициализация SharedPreferences** | `AppPreferences.kt` | `init(context: Context)` | Создание экземпляра через `context.applicationContext.getSharedPreferences("weather_tracker_prefs", Context.MODE_PRIVATE)`. |
| **Вызов инициализации в точке входа** | `MainActivity.kt` | `onCreate()` | Строки `AppPreferences.init(this)` и `WeatherAppState.currentCity = AppPreferences.getLastCity()`. |
| **Сохранение примитивных типов (`apply()`)** | `AppPreferences.kt` | `saveLastCity()`, `saveDarkTheme()`, `saveNotifications()`, `saveLanguageIndex()` | Использование `preferences.edit().putString(...).apply()`, `putBoolean(...)`, `putInt(...)`. |
| **Чтение примитивных типов** | `AppPreferences.kt` | `getLastCity()`, `isDarkTheme()`, `isNotificationsEnabled()`, `getLanguageIndex()` | Использование `preferences.getString(...)`, `getBoolean(...)`, `getInt(...)` со значениями по умолчанию. |
| **Сериализация коллекции в JSON (Gson.toJson)** | `AppPreferences.kt` | `saveReviews(reviews: List<Review>)` | Превращение списка `List<Review>` в JSON-строку: `val jsonString = gson.toJson(reviews)` и сохранение в преференсы. |
| **Десериализация коллекции из JSON (TypeToken)** | `AppPreferences.kt` | `getReviews(): List<Review>` | Извлечение JSON-строки и десериализация через `object : TypeToken<List<Review>>() {}.type` и `gson.fromJson(...)`. |
| **Интеграция сохранения в репозиторий** | `ReviewRepository.kt` | `addReview()`, `removeReview()`, `updateReview()`, `ensureInitialized()` | Любое добавление, редактирование или удаление отзыва автоматически вызывает `persistReviews()` и сохраняется в SharedPreferences. |
| **Автоматическое сохранение выбранного города** | `WeatherAppState.kt` | Сеттер свойства `currentCity` | При вводе города в поиске срабатывает сеттер `set(value)`, который автоматически сохраняет город в `AppPreferences`. |
| **Сохранение настроек интерфейса** | `SettingsFragment.kt` | `saveCurrentPreferences()`, `loadSavedPreferences()` | Экран настроек загружает сохраненные параметры при открытии и сохраняет при изменении/нажатии «Сохранить». |

---

## 📌 Лабораторная работа №12: «Работа с базой данных Room» (ветка `lab12`)

| Требование / Тема работы | Файл | Класс / Функция / Разметка | Пояснение, что показать преподавателю |
|---|---|---|---|
| **Подключение зависимостей Room** | `build.gradle.kts` | `dependencies { implementation("androidx.room:room-runtime:2.6.1") ... }` | Подключение библиотек Room Runtime, Room KTX и процессора аннотаций Room Compiler. |
| **Сущность базы данных (Entity)** | `Review.java` | `@Entity(tableName = "reviews") public class Review` | Класс сущности с полями таблицы. Первичный ключ `@PrimaryKey(autoGenerate = true) private long id`. |
| **Интерфейс доступа к данным (DAO)** | `ReviewDao.java` | `@Dao public interface ReviewDao` | Интерфейс с CRUD-методами: `@Query`, `@Insert`, `@Update`, `@Delete`. |
| **Реактивный поток данных (Flow)** | `ReviewRepository.kt`, `ReviewsListFragment.kt` | `fun getReviewsFlow(): Flow<List<Review>>`, `observeReviewsFromDatabase()` | Использование `Flow`: UI автоматически перерисовывается при любых изменениях в БД через `Flow.collect`. |
| **Асинхронные suspend-функции** | `ReviewRepository.kt` | `suspend fun addReview(...)`, `suspend fun removeReview(...)` | Все операции записи и чтения выполняются вне главного потока через корутины в контексте `Dispatchers.IO`. |
| **Класс базы данных (RoomDatabase)** | `AppDatabase.java` | `@Database(entities = {Review.class}, version = 1) public abstract class AppDatabase` | Наследник `RoomDatabase` с абстрактным методом `reviewDao()` и потокобезопасным синглтоном `getDatabase()`. |
| **Инициализация базы данных** | `MainActivity.kt` | `onCreate()` | Вызов `ReviewRepository.init(this)`, создающий экземпляр `AppDatabase` через `Room.databaseBuilder`. |
| **Асинхронное сохранение из Fragment** | `AddReviewFragment.kt` | `saveReview()` | Запуск корутины `viewLifecycleOwner.lifecycleScope.launch` для асинхронного сохранения отзыва в Room перед закрытием экрана. |
| **Асинхронное удаление из Fragment** | `ReviewsListFragment.kt` | `deleteReviewFromDatabase()` | Удаление записи из Room в корутине `lifecycleScope.launch` с мгновенным реактивным обновлением UI. |

---

## 📌 Лабораторная работа №13: «Работа с сетью: Retrofit + JSON» (ветка `lab13`)

| Требование / Тема работы | Файл | Класс / Функция / Разметка | Пояснение, что показать преподавателю |
|---|---|---|---|
| **Разрешение на доступ в интернет** | `AndroidManifest.xml` | `<uses-permission android:name="android.permission.INTERNET" />` | Системное разрешение на выполнение HTTP/HTTPS сетевых запросов в Android. |
| **Подключение Retrofit и Converter-Gson** | `build.gradle.kts` | `implementation("com.squareup.retrofit2:retrofit:2.11.0")`, `converter-gson` | Библиотеки Retrofit для REST API и конвертер Gson для автоматического парсинга JSON. |
| **Модель данных сетевого ответа (DTO)** | `Post.kt` | `data class Post(userId, id, title, body)` | Модель данных, строго соответствующая структуре JSON-ответа с API JSONPlaceholder. |
| **Интерфейс API с эндпоинтом** | `JsonPlaceholderApi.kt` | `@GET("posts") suspend fun getPosts(): List<Post>` | Описание HTTP-запроса через аннотацию `@GET("posts")` с `suspend` для вызова из корутин. |
| **Синглтон инициализации Retrofit** | `ApiService.kt` | `createRetrofitApi()`, `baseUrl("https://jsonplaceholder.typicode.com/")` | Настройка Retrofit: базовый URL, фабрика `GsonConverterFactory.create()` и ленивое создание API. |
| **Асинхронный сетевой запрос в корутине** | `PostListFragment.kt` | `loadPosts()` | Запуск корутины `viewLifecycleOwner.lifecycleScope.launch`, вызов `ApiService.api.getPosts()` в блоке `try-catch`. |
| **Обработка состояний Loading / Error / Success** | `PostListFragment.kt` | `showLoadingState()`, `showErrorState()`, `showSuccessState()` | Показ `ProgressBar` при загрузке, сообщения об ошибке при сбое и списка при успешном ответе. |
| **Обработка отсутствия интернета** | `PostListFragment.kt` | `showErrorState(exception)` | Перехват `IOException` (режим «В самолёте», нет Wi-Fi/связи) с выводом кнопки «Повторить попытку». |
| **Отображение сетевых данных в списке** | `PostAdapter.kt`, `item_post.xml` | `PostViewHolder`, `bindPostData()`, `updateList()` | Кастомный адаптер и разметка `MaterialCardView` для отображения постов в `RecyclerView`. |
| **Интеграция сетевого экрана в навигацию** | `main_menu.xml`, `MainActivity.kt`, `WeatherMainFragment.kt` | `action_posts`, `btnNetworkPosts` | Доступ к экрану через меню шапки Toolbar и кнопку на главном экране с поддержкой возврата «Назад». |
