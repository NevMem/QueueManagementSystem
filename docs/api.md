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



#### Добавить пользователя в организацию/сервис

POST `admin/add_user`

Request:  proto.permissions.AddUserRequest

Response:  google.protobuf.Empty

Добавляет пользователя в организацию


#### Удалить пользователя из организации/сервиса

POST `admin/remove_user`

Request:  proto.permissions.RemoveUserRequest

Response:  google.protobuf.Empty

Удалить пользователя из организации


#### Обновить права пользователя в организации/сервисе

POST `admin/update_user_privilege`

Request:  proto.permissions.AddUserRequest

Response:  google.protobuf.Empty

Удалить пользователя из организации


#### Список тикетов в очереди в организации

POST `admin/queue_tickets`

Request:  proto.organization.OrganizationInfo

Response:  proto.ticket.TicketList

Возвращает список всех незакрытых тикетов в организации


#### Текущий статус в очереди

POST `/client/get_current_queue_info`

Request:  google.protobuf.Empty

Response:  proto.ticket.TicketInfo

Возвращает статус по последнему тикету


#### История тикетов для юзера

POST `/client/tickets_history`

Request:  google.protobuf.Empty

Response:  proto.ticket.TicketList

Возвращает список всех завершенных тикетов для пользователя


#### История тикетов для сервиса

POST `/admin/service_tickets_history`

Request:  google.protobuf.Empty

Response:  proto.ticket.TicketList

Возвращает список всех завершенных тикетов для сервиса


#### Покинуть очередь

POST `/client/left_queue`

Request:  google.protobuf.Empty

Response:  google.protobuf.Empty

Покинуть очередь. Не сработает, если нас уже обрабатывают



#### Получить текущий обрабатываемый тикет

POST `/admin/get_current_ticket`

Request:  google.protobuf.Empty

Response:  proto.ticket.Ticket

Получает текущий обрабатваемый менеджером тикет (на случай рестарта страницы допустим)


#### Обработать следующего клиента

POST `/admin/service_next_user`

Request:  proto.management.NextUserRequest

Response:  proto.ticket.Ticket

Выдает следующий тикет из набора сервисов, если такой есть (404 иначе)
Важно! Завершает обработку предыдущего тикета, если она не была завершена


#### Закончить обработку клиента

POST `/admin/end_servicing`

Request:  proto.management.EndServicingRequest

Response:  google.protobuf.Empty

Заканчивает обработку пользователя. Дефолтная резолюция SERVICED, нельзя поставить NONE


#### Оповестить, когда сменится статус тикета
#### Долгий запрос!

POST `/client/notify_ticket_state_changed`

Request:  proto.ticket.TicketInfo

Response:  proto.ticket.TicketInfo

На вход подается proto TicketInfo с полями ticket.state и people_in_front_count (по версии клиента)
Если текущее состояние тикета отличается от заданного - моментально вернется ответ с текущим состоянием.
Иначе запрос поставится на удержание до изменения статуса.
