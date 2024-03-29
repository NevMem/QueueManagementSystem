import sys
sys.path.append('/usr/lib/qms')
import server.app.main # noqa
from server.app.utils.web import _routes
import jinja2

NGINX_TEMPLATE = jinja2.Template("""\
upstream uvicorn {
    server localhost:9824;
}

server {
  listen 80;
  listen 443 ssl;
  server_name         "qms-back.nikitonsky.tk";
  ssl_certificate     /run/secrets/SSL_CERT;
  ssl_certificate_key /run/secrets/SSL_KEY;

  client_max_body_size 4G;

{% for route in routes %}
  location "{{ route }}" {
    if ($request_method = 'OPTIONS') {
        add_header 'Access-Control-Max-Age' 1728000;
        add_header 'Content-Type' 'text/plain charset=UTF-8';
        add_header 'Content-Length' 0;
        add_header Access-Control-Allow-Origin "*" always;
        add_header Access-Control-Expose-Headers session always;
        add_header Access-Control-Allow-Methods "GET, POST, OPTIONS" always;
        add_header Access-Control-Allow-Headers * always;

        return 204;
    }

    proxy_set_header Host $http_host;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header Upgrade $http_upgrade;
    add_header Access-Control-Allow-Origin "*" always;
    add_header Access-Control-Expose-Headers session always;
    add_header Access-Control-Allow-Methods "GET, POST, OPTIONS" always;
    add_header Access-Control-Allow-Headers * always;

    proxy_redirect off;
    proxy_buffering off;
    proxy_pass http://uvicorn;
  }
{% endfor %}
}
""")


def generate_nginx():
    handles = [route.path for route in _routes]
    with open('/etc/nginx/sites-enabled/qms.conf', 'w') as config:
        config.write(NGINX_TEMPLATE.render(routes=handles))


def main():
    generate_nginx()


if __name__ == '__main__':
    main()
