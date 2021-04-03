trap "true" TERM INT

mkdir -p /var/log/supervisor
/usr/local/bin/supervisord -c /etc/supervisor/supervisord.conf &
SUPERVISORD_PID=$!

while supervisorctl status | grep -q -v "RUNNING\|STOPPED\|STOPPING"; do
    (kill -0 $SUPERVISORD_PID 2> /dev/null) || (echo "Supervisord died unexpectedly"; exit 1)
    sleep 5
done

rm /etc/nginx/sites-enabled/default

if [[ -f "/run/secrets/SSL_KEY" ]]; then
    python3 configs_generator.py
    supervisorctl start uvicorn-nginx
    supervisorctl start nginx
else
    supervisorctl start uvicorn
fi


wait $SUPERVISORD_PID || :
supervisorctl shutdown
wait $SUPERVISORD_PID || :
