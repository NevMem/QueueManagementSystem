#### POST `client/register`

Request: proto.client.RegisterRequest

Response: Empty

Регистрация нового пользователя(не администратора). Необходима уникальность логина.
При конфликте возвращается http код 409

#### POST `client/login`

Request: proto.client.ClientIdentity 
Response: proto.client.LoginResponse

#### GET `client/queues?organization=<id органицазции>`

Response: proto.queue_description.QueueDescriptionResponse

#### POST `client/enqueue`

Request: proto.client.EnqueueRequest

Response: proto.client.EnqueueResponse
