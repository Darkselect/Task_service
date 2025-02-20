CRUD приложение + логи через аспекты
1. Создать простой RESTful сервис для управления задачами
Task(id, title, description, userId)

API для работы с задачами
POST /tasks – создание новой задачи. 
GET /tasks/{id} – получение задачи по ID. 
PUT /tasks/{id} – обновление задачи. 
DELETE /tasks/{id} – удаление задачи. 
GET /tasks – получение списка всех задач. 

2. Реализуйте класс аспектов с advice:
Before
AfterThrowing
AfterReturning
Around (замер выполнения)

В приложении должна быть реализована логика для каждого advice – свой метод, можно сделать через аннотации.
