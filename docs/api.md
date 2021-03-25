#### Регистрация пользователя 
POST `client/register`

Request: proto.user.RegisterRequest

Response: google.protobuf.Empty

Регистрация нового пользователя(не администратора). Необходима уникальность логина.
При конфликте возвращается http код 409

#### Авторизация
POST `client/login` 

Request: proto.user.UserIdentity 

Response: google.protobuf.Empty

В случае успешного входа возвращает хедер `session`
Иначе 401.

#### Получение информации о пользователе

POST/GET `client/get_user` 

Request: google.protobuf.Empty

Response: proto.user.User 

Возвращает информацию об авторизованном пользоватеел

#### Обновления информации о пользователе

POST `/client/update_user`

Request: proto.user.User

Response: None

Обновляет информацию о пользователе, с токеном которого пришел запрос

#### Проверка уникальности пользователя

POST/GET `client/check_unique_user` 

Request: google.protobuf.UserIdentity

Response: proto.user.CheckUniqueUserResponse 

Возвращает информацию об авторизованном пользоватеел

#### Встать в очередь

POST `client/enter_queue`

Request: proto.queue.Queue

Response: google.protobuf.Empty

В случае, если уже стоим в какой-то очереди возвращает 409

#### Создать организацию

POST `admin/create_organisation`

Request: proto.organisation.OrganisationInfo

Response: google.protobuf.Empty

В случае, если организация с таким названием уже есть - возвращаем 409

#### Обновить организацию

POST `admin/update_organisation`

Request: proto.organisation.OrganisationInfo

Response: google.protobuf.Empty

Заливает в базу инфу as is

#### Список редактируемых организаций

POST/GET `admin/get_organisations_list`

Request:  google.protobuf.Empty

Response:  proto.organisation.OrganisationList

Возвращает список редактируемых организаций для авторизованного пользователя

#### Создать сервис

POST `admin/create_service`

Request: proto.service.ServiceInfo

Response: google.protobuf.Empty

В случае, если сервис с таким названием уже есть - возвращаем 409

#### Обновить сервис

POST `admin/update_service`

Request: proto.service.ServiceInfo

Response: google.protobuf.Empty

Заливает новую информацию про сервис в базу as is. Нельзя поменять id организации

#### Получить qr для сервиса или организации

GET `/admin/generate_qr?organization?<organization_id>&service=<service_id>`

Request: None 

Параметр service - опциональный

Response: QR code SVG


#### Создать очередь

POST `admin/create_queue`

Request:  proto.queue.Queue

Response:  google.protobuf.Empty

Создает очередь в выбранной организации


