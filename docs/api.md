#### POST `client/register`

Request: proto.client.RegisterRequest

Response: Empty

Регистрация нового пользователя(не администратора). Необходима уникальность логина.
При конфликте возвращается http код 409

#### POST `client/login`

Request: proto.client.ClientIdentity 
Response: proto.client.LoginResponse

#### POST `client/enqueue`

Request: proto.client.EnqueueRequest

Response: proto.client.EnqueueResponse

#### POST `admin/update_queue`

Обновить очередь. При пустом поле id создается новая очередь

#### POST `admin/next_client?queue_id=<id очереди>`

Request: None

Response: proto.user.User

#### POST `admin/update_organization`

Обновить информацию об организации
